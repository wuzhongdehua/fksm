package com.fksm.common.util;

/**
 * Created by root on 16-4-23.
 */
public class Constants {
    /** The Constant REGEX. */
    public static final String REGEX_UUID = "regex.uuid";
    public static final String REGEX_IP = "regex.ip";
    public static final String REGEX_TIME = "regex.time";

    public static final int TYPE_CALLER_INFO = 1;
    public static final int TYPE_RESP_PARAM = 2;
    public static final int TYPE_REQ_PARAM = 3;
    public static final int TYPE_RESP_TIME = 4;
    public static final int TYPE_SERVICE_INFO = 5;

    public static final String REDIS_SERVER_KEY = "SERVER";

    public static final String REDIS_SERVER_SERVICE_PREFIX = "SERVER_SERVICE_";
    public static final String REDIS_SERVICE_REQUEST_PREFIX = "SERVICE_REQUEST_";
    public static final String REDIS_TIME_REQUESTS_PREFIX = "TIME_REQUESTS_";
    //server->service->requests
    public static final String REDIS_SERVER_SERVICE_REQUEST_PREFIX = "SERVER_SERVICE_REQUEST_";

    public static final String REDIS_SERVICE_COSTS_PREFIX = "SERVICE_COSTS_";
    public static final String REDIS_SERVICE_CALLERS_PREFIX = "SERVICE_CALLERS_";
    public static final String REDIS_SERVICE_CODES_PREFIX = "SERVICE_CODES_";

    public static final String REDIS_SERVICE_REQUEST_CALLER_PREFIX = "SERVICE_REQUEST_CALLER_";
    public static final String REDIS_SERVER_SERVICE_COST_PREFIX = "SERVER_SERVICE_COST_";
    public static final String REDIS_SERVICE_REQUEST_CODE_PREFIX = "SERVICE_REQUEST_CODE_";

    public static final String REDIS_SERVER_PREFIX = "S_SERVER_SERVICE_REQUEST_";
    public static final String REDIS_SERVER_COSTS_PREFIX = "S_SERVER_SERVICE_COSTS_";
    public static final String REDIS_SERVER_CALLER_PREFIX = "S_SERVER_SERVICE_CALLER_";
    public static final String REDIS_SERVER_CODE_PREFIX = "S_SERVER_SERVICE_CODE_";

    public static final String REDIS_TIMELINE_KEY = "TIMELINE_KEY";

    public static final Integer CODE_SUCESS = 100;
}
