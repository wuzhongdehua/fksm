package com.fksm.common.dto;

import java.util.Map;

/**
 * Created by Administrator on 2016/5/7.
 */
public class CodeStatics implements Comparable {
    private Long time;
    private Double score;

    public CodeStatics() {
    }

    public CodeStatics(Long time, Double score) {
        this.setTime(time);
        this.setScore(score);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CodeStatics){
            CodeStatics info  = (CodeStatics)obj;
            return  info.getTime() == this.getTime() ? true : false;
        }
        return super.equals(obj);
    }

    public int compareTo(Object o) {
        if (o instanceof CodeStatics){
            CodeStatics info  = (CodeStatics)o;
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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
