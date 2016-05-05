package com.fksm.storm.bolt;

import com.fksm.utils.CuratorLockAcquire;
import com.fksm.utils.RedisKeysQueueManager;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * remove data from redis
 */
public class RemoveRedisBolt extends BaseRedisBasicBolt {

    private static final Logger logger = LoggerFactory.getLogger(RemoveRedisBolt.class);

    private CuratorLockAcquire curatorLockAcquire;

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);

        curatorLockAcquire = new CuratorLockAcquire();

        Timer timer = new Timer();
        timer.schedule(new SaveRedisToMysqlTimer(), nextExecTime(), 5 * 60 * 1000);
    }

    @Override
    public void execute(Tuple tuple) {
        String key = tuple.getString(0);
        RedisKeysQueueManager.add(key);
    }

    /**
     * 获取下一次五分钟执行时间
     */
    private Date nextExecTime() {
        try {
            Calendar ca = Calendar.getInstance();
            int currentMinute = ca.get(Calendar.MINUTE);
            int currentFiveMinute = currentMinute - (currentMinute % 5);
            ca.set(Calendar.MINUTE, currentFiveMinute);
            ca.add(Calendar.MINUTE, 5);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            Date execTime = sdf.parse(sdf.format(ca.getTime()));
            logger.debug("removeFromRedis timer next exec time is {}.", execTime);

            return execTime;
        } catch (ParseException e) {
            logger.error("Init removeFromRedis timer error.", e);
        }

        return null;
    }

    private void removeFromRedis() {
        InterProcessMutex lock = null;
        try{
            lock = new InterProcessMutex(curatorLockAcquire.getCuratorClient(), "/locks/bonus");
            while (RedisKeysQueueManager.isEmpty()){
                String key = RedisKeysQueueManager.pop();
                redisUtil.del(key);
            }
        } catch (Exception ex) {
            logger.error("remove redis data error.", ex);
        } finally {
            try {
                if (null != lock) {
                    lock.release();
                }
            } catch (Exception ex) {
                logger.warn("close bonus connection error.", ex);
            }
        }
    }

    class SaveRedisToMysqlTimer extends TimerTask {
        @Override
        public void run() {
            logger.debug("removeFromRedisTimer begin.");
            removeFromRedis();
            logger.debug("removeFromRedisTimer end.");
        }
    }
}
