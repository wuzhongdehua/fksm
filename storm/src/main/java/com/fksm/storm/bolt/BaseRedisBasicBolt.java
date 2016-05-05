package com.fksm.storm.bolt;

import com.fksm.common.util.Constants;
import com.fksm.utils.ConfigPropertieUtil;
import com.fksm.utils.RedisUtil;
import com.typesafe.config.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.base.BaseRichBolt;

import java.util.ArrayList;
import java.util.Map;

/**
 * Redis连接
 */
public abstract class BaseRedisBasicBolt extends BaseRichBolt {

    protected RedisUtil redisUtil;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        Config config = ConfigPropertieUtil.getInstance();
        String redisHost = config.getString("bonus.redis.host");
        int redisPort =  config.getInt("bonus.redis.port");
        redisUtil = new RedisUtil(redisHost, redisPort);
    }

    /**
     * 保存server->services的映射关系
     * @param server_services
     */
    protected void saveServerServices(Map<String, ArrayList<String>> server_services){
        if(server_services.size() > 0){
            //保存server
            redisUtil.sadd(Constants.REDIS_SERVER_KEY, server_services.keySet().toArray(new String[server_services.size()]));
            for (String server: server_services.keySet()){
                ArrayList<String> services = server_services.get(server);
                String server_services_key = Constants.REDIS_SERVER_SERVICE_PREFIX + server;
                //保存server和service的映射
                redisUtil.sadd(server_services_key, services.toArray(new String[services.size()]));
            }
        }
    }

    protected void putServerServicesMap(String server_ip, String service_id, Map<String, ArrayList<String>> server_services){
        ArrayList<String> services = server_services.get(server_ip);
        if(services == null){
            ArrayList<String> d = new ArrayList<>();
            d.add(service_id);
            server_services.put(server_ip, d);
        }else{
            services.add(service_id);
        }
    }
}
