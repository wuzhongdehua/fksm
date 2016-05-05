package com.fksm.storm.bolt;

import com.alibaba.fastjson.JSON;
import com.fksm.common.util.Constants;
import com.fksm.utils.CacheManager;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

import java.util.*;

/**
 * Created by Administrator on 2016/4/24.
 * 统计bold
 */
public class StatisticsTimeLineBold extends BaseRedisBasicBolt {


    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);
    }

    @Override
    public void execute(Tuple input) {
        Long start = input.getLongByField("start");
        Long end = input.getLongByField("end");

        List<Long> timeLine = CacheManager.checkAndAppendTimeLine(start, end);
        for (int i=0;i<timeLine.size()-1;i++){
            Long sstart = timeLine.get(i);
            Long eend = timeLine.get(i+1);
            //server
            Set<String> server = redisUtil.smembers(Constants.REDIS_SERVER_KEY);
            Iterator<String> servers = server.iterator();
            while (servers.hasNext()){
                String s = servers.next();
                String server_services_key = Constants.REDIS_SERVER_SERVICE_PREFIX + s;
                Set<String> service = redisUtil.smembers(server_services_key);
                Iterator<String> services = service.iterator();
                while (services.hasNext()){
                    String ss = servers.next();
                    String server_service_requests_key = Constants.REDIS_SERVER_SERVICE_REQUEST_PREFIX + s + "_" + ss;
                    String service_costs_key = Constants.REDIS_SERVICE_COSTS_PREFIX + ss;
                    String service_caller_key = Constants.REDIS_SERVICE_CALLERS_PREFIX + ss;
                    String service_code_key = Constants.REDIS_SERVICE_CODES_PREFIX + ss;
                    //service的requests
                    Set<String> requestsByService = redisUtil.smembers(server_service_requests_key);
                    /////////////////////////////////////////////////////请求响应次数///////////////////////////////////////////////
                    //time的requests
                    Set<String> requestsByTime = redisUtil.zrangebyscore(Constants.REDIS_TIME_REQUESTS_PREFIX, sstart.toString(), eend.toString());
                    //求交集
                    requestsByTime.retainAll(requestsByService);
                    //保存
                    String k = Constants.REDIS_SERVER_PREFIX + s + "_" +ss;
                    redisUtil.zadd(k, sstart.doubleValue(), requestsByTime.size() + "");
                    /////////////////////////////////////////////////////响应时间峰值///////////////////////////////////////////////////////////
                    //service的costs
                    Set<String> requestsByCost = redisUtil.smembers(service_costs_key);
                    Set<String> requestsByCostSet = new HashSet<>();
                    HashMap<String,String> requests = new HashMap();
                    for(String request_costs: requestsByCost){
                        HashMap requests_cost = JSON.parseObject(request_costs, HashMap.class);
                        requestsByCostSet.addAll(requests_cost.keySet());
                        requests.putAll(requests_cost);
                    }
                    requestsByCostSet.retainAll(requestsByService);
                    //求响应时间峰值
                    Long max = 0L;
                    for(String key:requestsByCostSet){
                        max = Math.max(Long.valueOf(requests.get(key)),max);
                    }
                    //保存
                    String k2 = Constants.REDIS_SERVER_COSTS_PREFIX + s + "_" +ss;
                    redisUtil.zadd(k2, sstart.doubleValue(), max.toString());
                    ////////////////////////////////////////////////调用者峰值/////////////////////////////////////////////////////////////
                    //service的caller
                    Set<String> requestsByCaller = redisUtil.smembers(service_caller_key);
                    Set<String> requestsByCallerSet = new HashSet<>();
                    HashMap<String,String> request_caller_map = new HashMap();
                    HashMap<String,Integer> caller_request_map = new HashMap();
                    for(String request_caller: requestsByCaller){
                        HashMap requests_cost = JSON.parseObject(request_caller, HashMap.class);
                        requestsByCallerSet.addAll(requests_cost.keySet());
                        request_caller_map.putAll(requests_cost);
                    }
                    requestsByCallerSet.retainAll(requestsByService);
                    for (String key:requestsByCallerSet){
                        String caller = request_caller_map.get(key);
                        Integer cnt = caller_request_map.get(caller);
                        if(cnt == null){
                            caller_request_map.put(caller,1);
                        }else {
                            cnt ++;
                        }
                    }
                    //保存
                    String k3 = Constants.REDIS_SERVER_CALLER_PREFIX + s + "_" +ss;
                    redisUtil.zadd(k3, sstart.doubleValue(), JSON.toJSONString(caller_request_map));
                    ////////////////////////////////////////////////响应成功率/////////////////////////////////////////////////////////////
                    //service的code
                    Set<String> requestsByCode = redisUtil.smembers(service_code_key);
                    Set<String> requestsByCodeSet = new HashSet<>();
                    HashMap<String,String> request_code_map = new HashMap();
                    for(String request_code: requestsByCode){
                        HashMap requests_code = JSON.parseObject(request_code, HashMap.class);
                        requestsByCodeSet.addAll(requests_code.keySet());
                        request_code_map.putAll(requests_code);
                    }
                    requestsByCodeSet.retainAll(requestsByService);
                    int total = 0;
                    int sucess = 0;
                    Float res = 0f;
                    for (String key:requestsByCodeSet){
                        String code = request_code_map.get(key);
                        if(Integer.parseInt(code) == Constants.CODE_SUCESS)
                            sucess++;
                        total ++;
                    }
                    if(total > 0){
                        res = (float)sucess / total;
                    }
                    //保存
                    String k4 = Constants.REDIS_SERVER_CODE_PREFIX + s + "_" +ss;
                    redisUtil.zadd(k4, sstart.doubleValue(), res.toString());
                }
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
