package com.fksm.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2016/4/25 0025.
 */
public class ServersTest {

    private static Logger logger = LoggerFactory.getLogger(ServersTest.class);

    public static void main(String[] args){
        String[] ips = SimulationDataUtil.getCaller_ip();
    }
}
