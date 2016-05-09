package com.fksm.storm.bolt;

import com.alibaba.fastjson.JSONObject;
import com.fksm.common.util.Constants;
import com.fksm.utils.CuratorLockAcquire;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by root on 16-4-23.
 */
public class ServerServiceWindowBolt extends BaseRedisWindowBolt {

    private static final Logger logger = LoggerFactory.getLogger(ServerServiceWindowBolt.class);

    private OutputCollector collector;
    //分布式锁
    private CuratorLockAcquire curatorLockAcquire;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);
        this.collector = collector;
        curatorLockAcquire = new CuratorLockAcquire();
    }

    @Override
    public void execute(TupleWindow inputWindow) {
        logger.debug("start deal ServerServiceWindowBolt ...");
        String server_ip = "";
        String service_id = "";
        String server_service = "";
        List<Long> time = new ArrayList<>();
        //此段时间内的请求的id
        Set<String> requests = new HashSet<>();
        //调用者调用次数
        Map<String, Integer> caller_count = new HashMap<>();
        //响应时间最大值
        Long maxLong = 0L;
        //响应成功次数
        Integer respCount = 0;
        for(Tuple tuple: inputWindow.get()) {
            //添加到时间
            time.add(tuple.getLongByField("timestamp"));
            server_service = tuple.getStringByField("server_service");
            String[] ss = server_service.split("_");
            server_ip = ss[0];
            service_id = ss[1];
            logger.debug("get server_ip KafkaJsonAnalysis : {}", server_ip);
            logger.debug("get service_id KafkaJsonAnalysis : {}", service_id);
            int type = tuple.getIntegerByField("type");
            logger.debug("get type from KafkaJsonAnalysis : {}", type);
            JSONObject data = (JSONObject)tuple.getValueByField("data");
            requests.add(data.getString("request_id"));
            logger.debug("get data from KafkaJsonAnalysis : {}", data);
            switch (type){
                case Constants.TYPE_CALLER_INFO:
                    //调用者峰值
                    String caller_ip = data.getString("caller_ip");
                    Integer cnt = caller_count.get(caller_ip);
                    if(cnt == null){
                        caller_count.put(caller_ip, 1);
                    }else{
                        cnt ++;
                    }
                    break;
                case Constants.TYPE_RESP_TIME:
                    //响应时间
                    Long start_time = data.getLong("begin_time");
                    Long end_time = data.getLong("end_time");
                    Long cost = end_time - start_time;
                    maxLong = Math.max(maxLong, cost);
                    break;
                case Constants.TYPE_RESP_PARAM:
                    Integer response_code = data.getInteger("response_code");
                    if (response_code == Constants.CODE_SUCESS){
                        respCount ++;
                    }
            }
        }
        List<Long> timeLine = null;
        //得到这个分组下这段时间的所有数据,这是就可以进行统计操作了。
        //获取锁
        InterProcessMutex lock = null;
        try{
            lock = new InterProcessMutex(curatorLockAcquire.getCuratorClient(), "/locks/redis_timeline");
            timeLine = checkAndAppendTimeLine(time.get(0), time.get(time.size() - 1));
        } catch (Exception ex) {
            logger.error("get time lines lock error : {}", ex);
        } finally {
            try {
                if (null != lock && lock.isAcquiredInThisProcess()) {
                    lock.release();
                }
            } catch (Exception ex) {
                logger.warn("close bonus connection error : {}", ex);
            }
        }
        Long startTime = timeLine.get(0);
        logger.debug("get statics time lines , start : {}, end : {}", startTime);
        //保存访问量
        saveServerServiceRequests(startTime, server_ip, service_id, requests.size());
        logger.debug("{} save ServerServiceRequests done", startTime);
        //保存请求次数
        saveServerServiceCaller(startTime, server_ip, service_id, caller_count);
        logger.debug("{} save ServerServiceCaller done", startTime);
        //保存访问时间
        saveServerServiceCosts(startTime, server_ip, service_id, maxLong);
        logger.debug("{} save ServerServiceCosts done", startTime);
        //保存请求成功率
        Float code = respCount.floatValue() / requests.size();
        saveServerServiceRequestsCode(startTime, server_ip, service_id, code);
        logger.debug("{} save ServerServiceRequestsCode done", startTime);
        // 提交确认，下一步是存储到hdfs.
        logger.debug("done deal ServerServiceWindowBolt ...");
        collector.emit(new Values(startTime, server_service));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("time","server_service"));
    }
}
