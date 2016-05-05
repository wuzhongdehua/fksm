package com.fksm.utils;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by root on 16-4-21.
 */
public class RedisKeysQueueManager {
    private static ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();

    public static void add(String key){
        queue.add(key);
    }

    public static String pop(){
        if(!queue.isEmpty()) {
            return  queue.poll();
        }
        return null;
    }

    public static boolean isEmpty(){
        return queue.isEmpty();
    }

    public static int size(){
        return queue.size();
    }
}
