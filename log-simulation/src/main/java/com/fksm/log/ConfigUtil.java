package com.fksm.log;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by Administrator on 2016/4/25 0025.
 */
public class ConfigUtil {
    private static Config config;

    static {
        config = ConfigFactory.load();
    }

    public static Config getConfig() {
        return config;
    }
}
