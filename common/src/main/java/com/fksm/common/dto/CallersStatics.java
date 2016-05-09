package com.fksm.common.dto;

import java.util.Map;

/**
 * Created by Administrator on 2016/5/7.
 */
public class CallersStatics implements Comparable {
    private Long time;
    private Map<String, Integer> callers;

    public CallersStatics() {
    }

    public CallersStatics(Long time, Map<String, Integer> callers) {
        this.setTime(time);
        this.setCallers(callers);
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Map<String, Integer> getCallers() {
        return callers;
    }

    public void setCallers(Map<String, Integer> callers) {
        this.callers = callers;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CallersStatics){
            CallersStatics info  = (CallersStatics)obj;
            return  info.time == this.time ? true : false;
        }
        return super.equals(obj);
    }

    public int compareTo(Object o) {
        if (o instanceof CallersStatics){
            CallersStatics info  = (CallersStatics)o;
            if (info.time > this.time)
                return 1;
            else if (info.time < this.time)
                return -1;
            else
                return 0;
        }
        return 0;
    }
}
