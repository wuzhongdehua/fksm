package com.fksm.utils;

import com.alibaba.fastjson.JSON;
import com.fksm.common.util.Constants;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Administrator on 2016/4/24.
 */
public class CacheManager {

    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    //server_id -> service_ids
    private Map<String, ArrayList<String>> server_services = new HashMap<>();
    //time -> request_ids
    private Map<Long, ArrayList<String>> time_requests = new HashMap<>();
    //service_id -> request_ids
    private Map<String, ArrayList<String>> service_requests = new HashMap<>();
    //service_id -> callers_ip
    private Map<String, HashMap<String, String>> service_callers = new HashMap<>();
    //caller_ip -> request_ids
    private Map<String, ArrayList<String>> caller_requests = new HashMap<>();
    //service_id -> costs
    private Map<String, HashMap<String, Long>> service_costs = new HashMap<>();
    //service_id -> code
    private Map<String, HashMap<String, Integer>> service_codes = new HashMap<>();
    //timeLine
    private static List<Long> timeLine = Collections.synchronizedList(new ArrayList());

    private static Long step = ConfigPropertieUtil.getInstance().getLong("storm.topology.watermark.interval") * 1000;

    private static RedisUtil redisUtil;

    static {
        Config config = ConfigPropertieUtil.getInstance();
        String redisHost = config.getString("bonus.redis.host");
        int redisPort =  config.getInt("bonus.redis.port");
        redisUtil = new RedisUtil(redisHost, redisPort);
    }

    public Map<String, ArrayList<String>> getServerServicesCache(){
        return server_services;
    }

    public void putServerServicesCache(String server_ip, String service_id){
        ArrayList<String> services = server_services.get(server_ip);
        if(services == null){
            ArrayList<String> d = new ArrayList<>();
            d.add(service_id);
            server_services.put(server_ip, d);
        }else{
            services.add(service_id);
        }
    }

    public Map<Long, ArrayList<String>> getTimeRequestsCache(){
        return time_requests;
    }

    public void putTimeRequestsCache(Long startTime, String request_id){
        ArrayList<String> requests = time_requests.get(startTime);
        if(requests == null){
            ArrayList<String> d = new ArrayList<>();
            d.add(request_id);
            time_requests.put(startTime, d);
        }else{
            requests.add(request_id);
        }
    }

    public Map<String, ArrayList<String>> getServiceRequestsCache(){
        return service_requests;
    }

    public void putServiceRequestsCache(String service_id, String request_id){
        ArrayList<String> requests = service_requests.get(service_id);
        if(requests == null){
            ArrayList<String> d = new ArrayList<>();
            d.add(request_id);
            service_requests.put(service_id, d);
        }else{
            requests.add(request_id);
        }
    }

    public Map<String, HashMap<String, String>> getServiceCallersCache(){
        return service_callers;
    }

    public void putServiceCallersCache(String service_id, String request_id, String caller_ip){
        HashMap<String, String> requests = service_callers.get(service_id);
        if(requests == null){
            HashMap<String, String> d = new HashMap<String, String>();
            d.put(request_id, caller_ip);
            service_callers.put(service_id, d);
        }else{
            requests.put(request_id, caller_ip);
        }
    }

    private void saveServiceCallers(){
        if(service_callers.size() > 0) {
            for (String service:service_callers.keySet()){
                String key = Constants.REDIS_SERVICE_CODES_PREFIX + service;
                HashMap<String, String> requests = service_callers.get(service);
                //json保存
                redisUtil.sadd(Constants.REDIS_SERVICE_CODES_PREFIX + service, JSON.toJSONString(requests));
            }
        }
    }

    public Map<String, ArrayList<String>> getCallerRequestsCache(){
        return caller_requests;
    }

    public void putCallerRequestsCache(String caller, String request_id){
        ArrayList<String> requests = caller_requests.get(caller);
        if(requests == null){
            ArrayList<String> d = new ArrayList<>();
            d.add(request_id);
            caller_requests.put(caller, d);
        }else{
            requests.add(request_id);
        }
    }

    public Map<String, HashMap<String, Long>> getServiceCostsCache(){
        return service_costs;
    }

    public void putServiceCostsCache(String service_id, String request_id, Long costs){
        HashMap<String, Long> requests = service_costs.get(service_id);
        if(requests == null){
            HashMap<String, Long> d = new HashMap<String, Long>();
            d.put(request_id, costs);
            service_costs.put(service_id, d);
        }else{
            requests.put(request_id, costs);
        }
    }

    private void saveServiceCosts(){
        if(service_costs.size() > 0) {
            for (String service:service_costs.keySet()){
                String key = Constants.REDIS_SERVICE_COSTS_PREFIX + service;
                HashMap<String, Long> requests = service_costs.get(service);
                //json保存
                redisUtil.sadd(Constants.REDIS_SERVICE_COSTS_PREFIX + service, JSON.toJSONString(requests));
            }
        }
    }

    public Map<String, HashMap<String, Integer>> getServiceCodesCache(){
        return service_codes;
    }

    public void putServiceCodesCache(String service_id, String request_id, Integer code){
        HashMap<String, Integer> requests = service_codes.get(service_id);
        if(requests == null){
            HashMap<String, Integer> d = new HashMap<>();
            d.put(request_id, code);
            service_codes.put(service_id, d);
        }else{
            requests.put(request_id, code);
        }
    }

    private void saveServiceCodes(){
        if(service_codes.size() > 0) {
            for (String service:service_codes.keySet()){
                String key = Constants.REDIS_SERVICE_COSTS_PREFIX + service;
                HashMap<String, Integer> requests = service_codes.get(service);
                //json保存
                redisUtil.sadd(Constants.REDIS_SERVICE_COSTS_PREFIX + service, JSON.toJSONString(requests));
            }
        }
    }

    public List<Long> getTimeLineCache(){
        return timeLine;
    }

    public void saveRedis(){
        //
        saveServerServices();
        //
        saveTimeRequests();
        //
        saveServiceCosts();
        //
        saveServiceCallers();
    }

    private void saveServerServices(){
        if(server_services.size() > 0){
            //保存server
            redisUtil.sadd(Constants.REDIS_SERVER_KEY, server_services.keySet().toArray(new String[server_services.size()]));
            for (String server: server_services.keySet()){
                ArrayList<String> services = server_services.get(server);
                String server_services_key = Constants.REDIS_SERVER_SERVICE_PREFIX + server;
                //保存server和service的映射
                redisUtil.sadd(server_services_key, services.toArray(new String[services.size()]));
                for(String service:services){
                    ArrayList<String> requests = service_requests.get(service);
                    if (requests != null){
                        String server_service_requests_key = Constants.REDIS_SERVER_SERVICE_REQUEST_PREFIX + server + "_" + service;
                        //保存service和request的映射
                        redisUtil.sadd(server_service_requests_key, requests.toArray(new String[requests.size()]));
                    }
                }
            }
        }
    }

    private void saveTimeRequests(){
        Map<String, Double> scoreMembers = new HashMap<>();
        for(Long time:time_requests.keySet()){
            ArrayList<String> requests = time_requests.get(time);
            for(String request:requests){
                scoreMembers.put(request, time.doubleValue());
            }
        }
        redisUtil.zadd(Constants.REDIS_TIME_REQUESTS_PREFIX, scoreMembers);
    }

    /**
     * 创建时间线
     * @param start
     * @param end
     * @return
     */
    public static List<Long> checkAndAppendTimeLine(Long start, Long end){
        int startIndex = -1;
        int endIndex = -1;
        if(timeLine.size() == 0){
            //先获取timeLine
            List<Long> res = getRedisTimeLine(Constants.REDIS_TIMELINE_KEY, start, end);
            timeLine.addAll(res);
        }
        if(timeLine.size() == 0){
            timeLine.set(0, start);
            startIndex = 0;
            Long d = (end - start) / step;
            for(int i=1;i<=d.intValue();i++){
                Long time = start + step * i;
                timeLine.add(i, time);
            }
            endIndex = d.intValue();
        }
        Integer index = timeLine.size() - 1;
        Long tend = timeLine.get(timeLine.size());
        if(((start <= tend) && (end > tend)) || (start > tend)){
            Long d = (end - tend) / step;
            startIndex = index;
            for(int i=1;i<=d.intValue();i++){
                Long time = tend + step * i;
                timeLine.add(index + i, time);
            }
            endIndex = index + d.intValue();
        }
        List<Long> res = timeLine.subList(startIndex, endIndex);
        Map<String,Double> times = new HashMap<>();
        for (Long t:res){
            times.put(t.toString(),t.doubleValue());
        }
        redisUtil.zadd(Constants.REDIS_TIMELINE_KEY, times);
        return timeLine.subList(startIndex, endIndex);
    }

    /**
     * 从redis获取时间线
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    protected static List<Long> getRedisTimeLine(String key, Long start, Long end){
        Set<String> data = redisUtil.zrevrange(key, start, end);
        List<Long> res = new ArrayList<>(data.size());
        Iterator<String> datas = data.iterator();
        while (datas.hasNext()){
            res.add(Long.valueOf(datas.next()));
        }
        return res;
    }
}
