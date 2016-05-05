package com.fksm.log;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.Date;
import java.util.Random;

/**
 * Created by Administrator on 2016/4/25 0025.
 */
public class TimeUtil {

    public static String format = "yyyy-MM-dd HH:mm:ss.SSS";

    public static String getNearRandomTimeStr(){
        Random r=new Random();
        DateTime dateTime=new DateTime(new Date());
        //随机秒
        dateTime.plusSeconds(-r.nextInt(60));
        //随机分钟
        dateTime.plusMinutes(-r.nextInt(60));
        return dateTime.toString(format);
    }

    public static Long getNearRandomTimeLong(){
        Random r=new Random();
        DateTime dateTime=new DateTime(new Date());
        //随机秒
        dateTime.plusSeconds(-r.nextInt(60));
        //随机分钟
        dateTime.plusMinutes(-r.nextInt(60));
        return dateTime.getMillis();
    }

    public static String getRandomResponseTimeStr(){
        Random r=new Random();
        DateTime dateTime=new DateTime(new Date());
        //随机秒
        dateTime.plusSeconds(r.nextInt(60));
        //随机分钟
        dateTime.plusMinutes(r.nextInt(1));
        return dateTime.toString(format);
    }

    public static Long getRandomResponseTimeLong(){
        Random r=new Random();
        DateTime dateTime=new DateTime(new Date());
        //随机秒
        dateTime.plusSeconds(r.nextInt(60));
        //随机分钟
        dateTime.plusMinutes(r.nextInt(1));
        return dateTime.getMillis();
    }

    public static Integer getIntervalSeconds(String start, String end){
        DateTime startTime = DateTime.parse(start);
        DateTime endTime = DateTime.parse(end);
        return Seconds.secondsBetween(startTime, endTime).getSeconds();
    }

    public static Long getTimeNow(){
        DateTime now = new DateTime(new Date());
        return now.getMillis();
    }

    public static String getTimeNowStr(){
        DateTime now = new DateTime(new Date());
        return now.toString(format);
    }
}
