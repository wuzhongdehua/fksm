package com.fksm.web.service.common.impl;

import com.alibaba.fastjson.JSON;
import com.fksm.common.util.Constants;
import com.fksm.web.service.common.DemoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by Administrator on 2016/5/7.
 */
@Service("demoService")
public class DemoServiceImpl implements DemoService {

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public List<HashMap> getCallers(Long startTime, Long endTime, String server_ip, String service_id) throws Exception {
        String k = Constants.REDIS_SERVER_CALLER_PREFIX + server_ip + "_" + service_id;
        Set<TypedTuple<String>> result =  redisTemplate.boundZSetOps(k).rangeByScoreWithScores(startTime, endTime);
        if (result.isEmpty()){
            return Collections.EMPTY_LIST;
        }
        List<HashMap> res = new ArrayList<>();
        Iterator<TypedTuple<String>> dd =  result.iterator();
        while (dd.hasNext()){
            TypedTuple<String> d2 = dd.next();
            res.add(JSON.parseObject(d2.getValue(), HashMap.class));
        }
        return res;
    }
}
