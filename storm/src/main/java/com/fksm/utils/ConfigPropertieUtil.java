package com.fksm.utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2016/4/21 0021.
 */
public class ConfigPropertieUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigPropertieUtil.class);

    private static Config config;

    static {
        config = ConfigFactory.load();
    }

    public static Config getInstance(){
        return config;
    }

    public static Config getInstance(String configFile){
        config = ConfigFactory.load(configFile);
        config.checkValid(ConfigFactory.defaultReference(), "simple-lib");
        return config;
    }
}