package com.fksm.storm.topology;


import com.fksm.storm.bolt.*;
import com.fksm.storm.hdfs.action.MoveStormToLogAction;
import com.fksm.utils.ConfigPropertieUtil;
import org.apache.hadoop.io.SequenceFile;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.hdfs.bolt.HdfsBolt;
import org.apache.storm.hdfs.bolt.SequenceFileBolt;
import org.apache.storm.hdfs.bolt.format.*;
import org.apache.storm.hdfs.bolt.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.FileSizeRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.FileSizeRotationPolicy.*;
import org.apache.storm.hdfs.bolt.sync.CountSyncPolicy;
import org.apache.storm.hdfs.bolt.sync.SyncPolicy;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt.*;
import org.apache.storm.tuple.Fields;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


/**
 * Created by Administrator on 2016/4/21 0021.
 */
public class KafkaTopology {

    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, InterruptedException, AuthorizationException {
        com.typesafe.config.Config config = ConfigPropertieUtil.getInstance();
        //zk地址
        String zks = config.getString("kafka.zk.hosts");
        //topic名称
        String topic = config.getString("kafka.app.topic");
        //kafka 对应的zk的目录地址
        String zkRoot = config.getString("kafka.zk.broker.path");
        //zk的ip
        String zkHosts = config.getString("kafka.zk.servers");
        //zk port
        Integer zkPort = config.getInt("kafka.zk.port");
        //storm worker数量
        Integer numWorkers = config.getInt("storm.topology.workers");
        //hdfs地址
        String hdfsUrl = config.getString("hdfs.url");

        //storm窗口一些设置
        Integer windowDuration = config.getInt("storm.topology.window.duration");
        Integer windowLag = config.getInt("storm.topology.window.lag");
        Integer watermarkInterval = config.getInt("storm.topology.watermark.interval");
        String id = config.getString("kafka.zk.kafka.group");

        BrokerHosts brokerHosts = new ZkHosts(zks);
        SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);
        spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
        spoutConf.zkServers = Arrays.asList(zkHosts.split(","));
        spoutConf.zkPort = zkPort;

        HdfsBolt hdfsCallerBolt = buildHdfsBolt(hdfsUrl, "log_caller_info_", new Fields("caller_info"));
        HdfsBolt hdfsRespParamBolt = buildHdfsBolt(hdfsUrl, "log_resp_param_", new Fields("resp_param"));
        HdfsBolt hdfsRespTimeBolt = buildHdfsBolt(hdfsUrl, "log_resp_time_", new Fields("resp_time"));
        HdfsBolt hdfsServiceInfoBolt = buildHdfsBolt(hdfsUrl, "log_service_info_", new Fields("service_info"));

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("kafka-reader", new KafkaSpout(spoutConf), 5);
        builder.setBolt("json-analysis", new KafkaJsonAnalysis(), 4).shuffleGrouping("kafka-reader");
        //流水窗口操作，按照日志的时间来操作，窗口延迟2s
        builder.setBolt("ss-sliding-window",
                new ServerServiceWindowBolt()
                        .withTumblingWindow(new Duration(windowDuration, TimeUnit.SECONDS))
                        .withWatermarkInterval(new Duration(watermarkInterval, TimeUnit.SECONDS))
                .withLag(new Duration(windowLag, TimeUnit.SECONDS)).withTimestampField("timestamp"), 4).fieldsGrouping("json-analysis", new Fields("server_service"));
//        builder.setBolt("json-analysis", new StatisticsTimeLineBold(), 5).shuffleGrouping("sliding-window");
        builder.setBolt("hdfs-bolt", new SaveHDFSBold(), 1).shuffleGrouping("json-analysis");
        builder.setBolt("hdfs-caller-bolt", hdfsCallerBolt, 1).fieldsGrouping("hdfs-bolt", new Fields("caller_info"));
        builder.setBolt("hdfs-resp-param-bolt", hdfsRespParamBolt, 1).fieldsGrouping("hdfs-bolt", new Fields("resp_param"));
        builder.setBolt("hdfs-resp-time-bolt", hdfsRespTimeBolt, 1).fieldsGrouping("hdfs-bolt", new Fields("resp_time"));
        builder.setBolt("hdfs-service-info-bolt", hdfsServiceInfoBolt, 1).fieldsGrouping("hdfs-bolt", new Fields("service_info"));

//        builder.setBolt("redis-remove-bolt", new RemoveRedisBolt(), 1);

        Config conf = new Config();
        //conf.setDebug(true);
        if (args != null && args.length > 0) {
            conf.setNumWorkers(numWorkers);
            StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
        } else {
            String name = KafkaTopology.class.getSimpleName();
            conf.setMaxTaskParallelism(numWorkers);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(name, conf, builder.createTopology());
            Thread.sleep(60000);
            cluster.shutdown();
        }
    }


    private static HdfsBolt buildHdfsBolt(String hdfsUrl,String prefix, Fields fields){
        // use "|" instead of "," for field delimiter
        RecordFormat format = new DelimitedRecordFormat()
                .withFieldDelimiter(" : ").withFields(fields);

        // sync the filesystem after every 1k tuples
        SyncPolicy syncPolicy = new CountSyncPolicy(1000);

        // rotate files
        FileRotationPolicy rotationPolicy = new FileSizeRotationPolicy(5.0f, Units.MB);

        FileNameFormat fileNameFormat = new DefaultFileNameFormat()
                .withPath("/storm/").withPrefix(prefix).withExtension(".seq");

        HdfsBolt hdfsBolt = new HdfsBolt()
                .withFsUrl(hdfsUrl)
                .withFileNameFormat(fileNameFormat)
                .withRecordFormat(format)
                .withRotationPolicy(rotationPolicy)
                .withSyncPolicy(syncPolicy)
                .withRetryCount(5)
                .addRotationAction(new MoveStormToLogAction().withDestination("/log"));

        return hdfsBolt;
    }
}
