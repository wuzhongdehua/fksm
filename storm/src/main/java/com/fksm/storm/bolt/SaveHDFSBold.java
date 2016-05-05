package com.fksm.storm.bolt;

import com.alibaba.fastjson.JSONObject;
import com.fksm.common.util.Constants;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * Created by root on 16-4-28.
 */
public class SaveHDFSBold extends BaseRichBolt {

    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        Integer type = tuple.getIntegerByField("type");
        JSONObject data = (JSONObject)tuple.getValueByField("data");
        String res = buildCallerInfo(data);
        switch (type){
            case Constants.TYPE_CALLER_INFO:
                collector.emit(tuple, new Values(res, null, null , null));
                break;
            case Constants.TYPE_RESP_PARAM:
                collector.emit(tuple, new Values(null, res, null , null));
                break;
            case Constants.TYPE_RESP_TIME:
                collector.emit(tuple, new Values(null, null, res , null));
                break;
            case Constants.TYPE_SERVICE_INFO:
                collector.emit(tuple, new Values(null, null, null , res));
                break;
            default:
                collector.emit(tuple, new Values(null, null, null , null));
                break;
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("caller_info", "resp_param", "resp_time", "service_info"));
    }

    /**
     *
     * @param data
     * @return
     */
    private String buildCallerInfo(JSONObject data){
        StringBuilder sb = new StringBuilder();
        for (String key: data.keySet()){
            sb.append(data.getString(key)).append(":");
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }
}
