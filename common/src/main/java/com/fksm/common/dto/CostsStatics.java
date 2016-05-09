package com.fksm.common.dto;

import java.util.Map;

/**
 * Created by Administrator on 2016/5/7.
 */
public class CostsStatics implements Comparable {
    private Long time;
    private Long costs;

    public CostsStatics() {
    }

    public CostsStatics(Long time, Long costs) {
        this.setTime(time);
        this.setCosts(costs);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CostsStatics){
            CostsStatics info  = (CostsStatics)obj;
            return  info.getTime() == this.getTime() ? true : false;
        }
        return super.equals(obj);
    }

    public int compareTo(Object o) {
        if (o instanceof CostsStatics){
            CostsStatics info  = (CostsStatics)o;
            if (info.time > this.time)
                return 1;
            else if (info.time < this.time)
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

    public Long getCosts() {
        return costs;
    }

    public void setCosts(Long costs) {
        this.costs = costs;
    }
}
