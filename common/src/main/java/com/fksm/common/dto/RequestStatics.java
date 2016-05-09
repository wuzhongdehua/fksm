package com.fksm.common.dto;

/**
 * Created by Administrator on 2016/5/7.
 */
public class RequestStatics implements Comparable {
    private Long time;
    private Integer count;

    public RequestStatics() {
    }

    public RequestStatics(Long time, Integer count) {
        this.setTime(time);
        this.setCount(count);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RequestStatics){
            RequestStatics info  = (RequestStatics)obj;
            return  info.getTime() == this.getTime() ? true : false;
        }
        return super.equals(obj);
    }

    public int compareTo(Object o) {
        if (o instanceof RequestStatics){
            RequestStatics info  = (RequestStatics)o;
            if (info.getTime() > this.getTime())
                return 1;
            else if (info.getTime() < this.getTime())
                return -1;
            else
                return 0;
        }
        return 0;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
