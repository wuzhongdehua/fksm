package com.fksm.storm.bolt;

import com.alibaba.fastjson.JSON;
import com.fksm.common.util.Constants;
import com.fksm.utils.ConfigPropertieUtil;
import com.fksm.utils.RedisUtil;
import com.typesafe.config.Config;
import org.apache.commons.collections.map.HashedMap;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseWindowedBolt;
import redis.clients.jedis.Tuple;

import java.util.*;

/**
 * Redis连接
 */
public abstract class BaseRedisWindowBolt extends BaseWindowedBolt {

    private static Long step = ConfigPropertieUtil.getInstance().getLong("storm.topology.watermark.interval") * 1000;

    protected RedisUtil redisUtil;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);
        Config config = ConfigPropertieUtil.getInstance();
        String redisHost = config.getString("bonus.redis.host");
        int redisPort =  config.getInt("bonus.redis.port");
        redisUtil = new RedisUtil(redisHost, redisPort);
    }

    protected List<String> getRedisServicesByServerId(String server_ip){
        return redisUtil.hvals(Constants.REDIS_SERVER_SERVICE_PREFIX + server_ip);
    }

    protected List<String> getRedisRequestsByServiceId(String service_id){
        return redisUtil.hvals(Constants.REDIS_SERVICE_REQUEST_PREFIX + service_id);
    }

    /**
     *
     * @param key
     * @param start
     * @param end
     */
    protected Map<String, String> getRedisValueByScoreRange(String key, Long start, Long end){
        Set<Tuple> data = redisUtil.zrangeByScoreWithScores(key, start.toString(), end.toString());
        //score count
        Map<String, String> res = new HashedMap(data.size());
        Iterator<Tuple> datas = data.iterator();
        while (datas.hasNext()){
            Tuple d = datas.next();
            String v = d.getElement();
            String k = Double.toString(d.getScore());
            res.put(k, v);
        }
        return res;
    }

    /**
     *
     * @param key
     * @param timeScore
     * @param count
     */
    protected boolean removeAndSetRedisValueByScore(String key, Long timeScore, String count){
        Long res1 = redisUtil.zremrangeByScore(key, timeScore, timeScore);
        Long res2 = redisUtil.zadd(key, timeScore.doubleValue(), count);
        return (res1 > 0) && (res2 > 0);
    }

    protected boolean saveHashRedisValues(String key, String... datas){
        return  redisUtil.sadd(key, datas) > 0 ? true : false;
    }

    /**
     * 保存访问时间
     * @param time
     * @param server_ip
     * @param service_id
     * @param costs
     */
    protected void saveServerServiceCosts(Long time, String server_ip, String service_id, Long costs){
        String k = Constants.REDIS_SERVER_COSTS_PREFIX + server_ip + "_" + service_id;
        redisUtil.zadd(k, time.doubleValue(), costs.toString());
    }

    /**
     * 保存访问量
     * @param time
     * @param server_ip
     * @param service_id
     * @param cnt
     */
    protected void saveServerServiceRequests(Long time, String server_ip, String service_id, Integer cnt){
        String k = Constants.REDIS_SERVER_PREFIX + server_ip + "_" + service_id;
        redisUtil.zadd(k, time.doubleValue(), cnt.toString());
    }

    /**
     * 保存请求次数
     * @param time
     * @param server_ip
     * @param service_id
     * @param caller_request_map
     */
    protected void saveServerServiceCaller(Long time, String server_ip, String service_id, Map<String,Integer> caller_request_map){
        String k = Constants.REDIS_SERVER_CALLER_PREFIX + server_ip + "_" + service_id;
        redisUtil.zadd(k, time.doubleValue(), JSON.toJSONString(caller_request_map));
    }

    /**
     * 保存请求成功率
     * @param time
     * @param server_ip
     * @param service_id
     * @param code
     */
    protected void saveServerServiceRequestsCode(Long time, String server_ip, String service_id, Float code){
        String k = Constants.REDIS_SERVER_CODE_PREFIX + server_ip + "_" + service_id;
        redisUtil.zadd(k, time.doubleValue(), code.toString());
    }

    /**
     * 创建时间线
     * @param start
     * @param end
     * @return
     */
    protected List<Long> checkAndAppendTimeLine(Long start, Long end){
        int startIndex = -1;
        int endIndex = -1;
        //先获取timeLine
        List<Long> timeLine = getRedisTimeLine(Constants.REDIS_TIMELINE_KEY, start, end);
        if(timeLine.size() == 0){
            timeLine.add(0, start);
            startIndex = 0;
            Long d = (end - start) / step;
            for(int i = 0; i < d.intValue(); i++){
                Long time = start + step * i;
                timeLine.add(i, time);
            }
            endIndex = d.intValue();
        }
        Integer index = timeLine.size() - 1;
        Long tend = timeLine.get(timeLine.size() - 1);
        if(((start <= tend) && (end > tend)) || (start > tend)){
            Long d = (end - tend) / step;
            startIndex = index;
            for(int i = 0; i < d.intValue(); i++){
                Long time = tend + step * i;
                timeLine.add(index + i, time);
            }
            endIndex = index + d.intValue();
        }
        List<Long> res = timeLine.subList(startIndex, endIndex + 1);
        Map<String,Double> times = new HashMap<>();
        for (Long t:res){
            times.put(t.toString(),t.doubleValue());
        }
        redisUtil.zadd(Constants.REDIS_TIMELINE_KEY, times);
        return res;
    }

    /**
     * 从redis获取时间线
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    protected List<Long> getRedisTimeLine(String key, Long start, Long end){
        Set<String> data = redisUtil.zrevrange(key, start, end);
        List<Long> res = new ArrayList<>(data.size());
        Iterator<String> datas = data.iterator();
        while (datas.hasNext()){
            res.add(Long.valueOf(datas.next()));
        }
        return res;
    }
}
