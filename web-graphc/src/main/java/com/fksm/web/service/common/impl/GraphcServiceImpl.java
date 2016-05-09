package com.fksm.web.service.common.impl;

import com.alibaba.fastjson.JSON;
import com.fksm.common.dto.CallersStatics;
import com.fksm.common.dto.CodeStatics;
import com.fksm.common.dto.CostsStatics;
import com.fksm.common.dto.RequestStatics;
import com.fksm.common.util.Constants;
import com.fksm.web.service.common.GraphcService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection.*;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by Administrator on 2016/5/7.
 */
@Service("graphcService")
public class GraphcServiceImpl implements GraphcService {

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    @Resource(name = "stringRedisTemplate")
    private RedisTemplate<String, String> stringRedisTemplate;

    @Override
    public Map<String, Set<String>> getServerServices() throws Exception {
        Map<String, Set<String>> res = stringRedisTemplate.execute(new RedisCallback<Map<String, Set<String>>>() {
            public Map<String, Set<String>> doInRedis(RedisConnection connection) throws DataAccessException {
                Map<String, Set<String>> res = new HashMap<>();
                StringRedisConnection conn = ((StringRedisConnection)connection);
                Set<String> servers = conn.sMembers(Constants.REDIS_SERVER_KEY);
                for (String server: servers){
                    String server_services_key = Constants.REDIS_SERVER_SERVICE_PREFIX + server;
                    Set<String> services = conn.sMembers(server_services_key);
                    res.put(server, services);
                }
                return res;
            }
        });
        return res;
    }

    @Override
    public List<CallersStatics> getServerServiceCallers(final String serverIp, final String serviceId) throws Exception {
        return stringRedisTemplate.execute(new RedisCallback<List<CallersStatics>>() {
            public List<CallersStatics> doInRedis(RedisConnection connection) throws DataAccessException {
                String k = Constants.REDIS_SERVER_CALLER_PREFIX + serverIp + "_" + serviceId;
                StringRedisConnection conn = ((StringRedisConnection)connection);
                Set<StringTuple> callers = conn.zRangeByScoreWithScores(k, Long.MIN_VALUE, Long.MAX_VALUE);
                if (callers.isEmpty()){
                    return Collections.EMPTY_LIST;
                }
                List<CallersStatics> res = new ArrayList<CallersStatics>();
                for (StringTuple tuple:callers){
                    Double score = tuple.getScore();
                    String value = tuple.getValueAsString();
                    CallersStatics statics = new CallersStatics();
                    statics.setTime(score.longValue());
                    statics.setCallers(JSON.parseObject(value, HashMap.class));
                    res.add(statics);
                }
                Collections.sort(res);
                return res;
            }
        });
    }

    @Override
    public List<CodeStatics> getServerServiceCodes(final String serverIp, final String serviceId) throws Exception {
        return stringRedisTemplate.execute(new RedisCallback<List<CodeStatics>>() {
            public List<CodeStatics> doInRedis(RedisConnection connection) throws DataAccessException {
                String k = Constants.REDIS_SERVER_CODE_PREFIX + serverIp + "_" + serviceId;
                StringRedisConnection conn = ((StringRedisConnection)connection);
                Set<StringTuple> callers = conn.zRangeByScoreWithScores(k, Long.MIN_VALUE, Long.MAX_VALUE);
                if (callers.isEmpty()){
                    return Collections.EMPTY_LIST;
                }
                List<CodeStatics> res = new ArrayList<CodeStatics>();
                for (StringTuple tuple:callers){
                    Double score = tuple.getScore();
                    String value = tuple.getValueAsString();
                    CodeStatics statics = new CodeStatics();
                    statics.setTime(score.longValue());
                    statics.setScore(Double.parseDouble(value));
                    res.add(statics);
                }
                Collections.sort(res);
                return res;
            }
        });
    }

    @Override
    public List<CostsStatics> getServerServiceCosts(final String serverIp, final String serviceId) throws Exception {
        return stringRedisTemplate.execute(new RedisCallback<List<CostsStatics>>() {
            public List<CostsStatics> doInRedis(RedisConnection connection) throws DataAccessException {
                String k = Constants.REDIS_SERVER_COSTS_PREFIX + serverIp + "_" + serviceId;
                StringRedisConnection conn = ((StringRedisConnection)connection);
                Set<StringTuple> callers = conn.zRangeByScoreWithScores(k, Long.MIN_VALUE, Long.MAX_VALUE);
                if (callers.isEmpty()){
                    return Collections.EMPTY_LIST;
                }
                List<CostsStatics> res = new ArrayList<CostsStatics>();
                for (StringTuple tuple:callers){
                    Double score = tuple.getScore();
                    String value = tuple.getValueAsString();
                    CostsStatics statics = new CostsStatics();
                    statics.setTime(score.longValue());
                    statics.setCosts(Long.parseLong(value));
                    res.add(statics);
                }
                Collections.sort(res);
                return res;
            }
        });
    }

    @Override
    public List<RequestStatics> getServerServiceRequests(final String serverIp, final String serviceId) throws Exception {
        return stringRedisTemplate.execute(new RedisCallback<List<RequestStatics>>() {
            public List<RequestStatics> doInRedis(RedisConnection connection) throws DataAccessException {
                String k = Constants.REDIS_SERVER_PREFIX + serverIp + "_" + serviceId;
                StringRedisConnection conn = ((StringRedisConnection)connection);
                Set<StringTuple> callers = conn.zRangeByScoreWithScores(k, Long.MIN_VALUE, Long.MAX_VALUE);
                if (callers.isEmpty()){
                    return Collections.EMPTY_LIST;
                }
                List<RequestStatics> res = new ArrayList<RequestStatics>();
                for (StringTuple tuple:callers){
                    Double score = tuple.getScore();
                    String value = tuple.getValueAsString();
                    RequestStatics statics = new RequestStatics();
                    statics.setTime(score.longValue());
                    statics.setCount(Integer.parseInt(value));
                    res.add(statics);
                }
                Collections.sort(res);
                return res;
            }
        });
    }
}
