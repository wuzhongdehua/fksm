package com.fksm.common;

/**
 * Redis操作枚举
 */
public interface RedisOp {
    String SET = "SET";
    String INCRBY = "INCRBY";
    String ZADD = "ZADD";
    String ZRANGE = "ZRANGE";
    String 	HVALS = "HVALS ";
    String HSETNX = "HSETNX";
}
