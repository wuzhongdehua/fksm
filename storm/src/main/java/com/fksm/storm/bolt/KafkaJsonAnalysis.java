package com.fksm.storm.bolt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/21 0021.
 */
public class KafkaJsonAnalysis extends BaseRedisBasicBolt {

    private static final Logger logger = LoggerFactory.getLogger(KafkaJsonAnalysis.class);

    private static final long serialVersionUID = 886149197481637894L;

    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        //初始化redis
        super.prepare(stormConf, context, collector);
        this.collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        String line = tuple.getString(0);
        Map<String, ArrayList<String>> server_services = new HashMap<>();
        ArrayList<JSONObject> datas = JSON.parseObject(line, ArrayList.class);
        for (JSONObject data: datas){
            Integer type = data.getInteger("type");
            JSONArray list = data.getJSONArray("data");
            for (Object o : list){
                JSONObject jObj = (JSONObject)o;
                String server_ip = jObj.getString("server_ip");
                String service_id = jObj.getString("service_id");
                //放入map中
                putServerServicesMap(server_ip, service_id, server_services);
                Long timestamp = jObj.getLong("begin_time");
                collector.emit(tuple, new Values(server_ip+"_"+service_id, type, timestamp, o));
            }
        }
        //保存server->service的关系到redis
        saveServerServices(server_services);
        logger.debug("save server->services done!");
        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("server_service", "type", "timestamp", "data"));
    }

}
