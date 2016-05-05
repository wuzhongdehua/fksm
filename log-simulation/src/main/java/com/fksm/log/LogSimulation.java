package com.fksm.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/4/25 0025.
 */
public class LogSimulation {
    private static Logger logger = LoggerFactory.getLogger(LogSimulation.class);

    public static void main(String[] args){
        logger.debug("start simulation data...");
        final Integer numCallers = ConfigUtil.getConfig().getInt("data.num.callers");
        final Integer numService = ConfigUtil.getConfig().getInt("data.sim.maxServiceCount");
        Integer threadCount = ConfigUtil.getConfig().getInt("data.sim.threadCount");
        final Map<String, String> service_server = SimulationDataUtil.getService_server();
        ExecutorService callers = Executors.newFixedThreadPool(threadCount);
        final Random rd = new Random();
        callers.execute(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String[] callerIps = SimulationDataUtil.getRandomCallerIps(numCallers);
                    String[] services = SimulationDataUtil.getRandomServices(numService);
                    for (String service:services){
                        do1();
                        for (String caller:callerIps){
                            String request_id = SimulationDataUtil.getRandomUUID();
                            String timeNowStr = TimeUtil.getTimeNowStr();
                            logger.info("visitor : {} start visit service : {} in server : {} with request id : {} at time : {}",
                                    caller,
                                    service,
                                    service_server.get(service),
                                    request_id,
                                    timeNowStr
                            );
                            //deal
                            logger.info("i am doing something ... at {}", TimeUtil.getTimeNowStr());
                            //response
                            String resonseTime = TimeUtil.getRandomResponseTimeStr();
                            Integer code = SimulationDataUtil.getRandomResponseCode();
                            int type = rd.nextInt(2);
                            switch (type){
                                case 0:
                                    do1();
                                    break;
                                case 1:
                                    do2(caller, code);
                                    break;
                                default:
                                    do1();
                                    do2(caller, code);
                            }
                            logger.info("server : {} response for request : {} at time : {} in service : {} at time : {} and the response code is : {}",
                                    service_server.get(service),
                                    request_id,
                                    timeNowStr,
                                    service,
                                    resonseTime,
                                    code
                            );
                            try {
                                Thread.sleep(rd.nextInt(300));
                            } catch (InterruptedException e) {
                                logger.info("thread : {} throw excetion : {}", Thread.currentThread().getName(), e);
                            }
                        }
                    }
                }
            }
        });
    }

    public static void do1(){
        try{
            int a = 300;
            int b = 0;
            int res = a / b ;
            logger.debug(" a/b : {}", res);
        } catch (Exception e){
            logger.error(" a/b throw excetion: {}", e);
        }
    }

    public static void do2(String caller, Integer code){
        logger.info("{} - - [{} +0800] \"GET /sell HTTP/1.0\" {} 46227 \"http://wwwqa2.yoohouse.com/\" \"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.3) Gecko/20100401 Firefox/3.6.3 GTB7.1\" \"{}\" \"0.702\"",
                caller,
                TimeUtil.getTimeNowStr(),
                code,
                LogUtil.getRandomIp()
        );
    }
}
