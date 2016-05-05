package com.fksm.storm.bolt;

import com.alibaba.fastjson.JSONObject;
import com.fksm.common.dto.CallerInformation;
import com.fksm.common.dto.ResponseParameters;
import com.fksm.common.dto.ResponseTime;
import com.fksm.common.dto.ServiceInformation;
import com.fksm.common.util.Constants;
import com.fksm.utils.CacheManager;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 16-4-23.
 */
public class SlidingWindowBolt extends BaseRedisWindowBolt {

    private static final Logger logger = LoggerFactory.getLogger(SlidingWindowBolt.class);

    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(TupleWindow inputWindow) {

        CacheManager cacheManager = new CacheManager();

        List<Long> timeLine = new ArrayList<>();
        for(Tuple tuple: inputWindow.get()) {
            int type = tuple.getIntegerByField("type");
            JSONObject data = (JSONObject)tuple.getValueByField("data");
            switch (type){
                case Constants.TYPE_CALLER_INFO:
                    CallerInformation info1 = data.toJavaObject(CallerInformation.class);
                    String key1 = info1.getRequest_id();
                    cacheManager.putServiceCallersCache(info1.getService_id(), key1, info1.getCaller_ip());
                    cacheManager.putServiceRequestsCache(info1.getService_id(), info1.getRequest_id());
                    break;
                case Constants.TYPE_RESP_TIME:
                    ResponseTime info2 = data.toJavaObject(ResponseTime.class);
                    String key2 = info2.getRequest_id();
                    cacheManager.putServiceCostsCache(info2.getService_id(), key2, info2.getTotal_cost());
                    cacheManager.putServiceRequestsCache(info2.getService_id(), info2.getRequest_id());
                    timeLine.add(info2.getBegin_time());
                    break;
                case Constants.TYPE_RESP_PARAM:
                    ResponseParameters info3 = data.toJavaObject(ResponseParameters.class);
                    String key3 = info3.getRequest_id();
                    cacheManager.putServiceCodesCache(info3.getService_id(), key3, info3.getResponse_code());
                    cacheManager.putServiceRequestsCache(info3.getService_id(), info3.getRequest_id());
                    break;
                case Constants.TYPE_SERVICE_INFO:
                    ServiceInformation info4 = data.toJavaObject(ServiceInformation.class);
                    cacheManager.putServerServicesCache(info4.getServer_ip(), info4.getService_id());
                    cacheManager.putServiceRequestsCache(info4.getService_id(), info4.getRequest_id());
                    break;
                default:
                    logger.warn("error type:{}",type);
                    break;
            }
        }
        //save to redis:
        cacheManager.saveRedis();
        Collections.sort(timeLine);
        Long start = timeLine.get(0);
        Long end = timeLine.get(timeLine.size()-1);
        // emit the results
        collector.emit(new Values(start, end));
    }
}
