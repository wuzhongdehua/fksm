package com.fksm.flume.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/4/26 0026.
 */
public class MatcherTest {

    private static Pattern number = Pattern.compile("\\d{3}");

    //time 2
    //uuid 2
    //ip 2
    static String m1Str = "2016-04-26 15:43:51.029 INFO [pool-2-thread-1](LogSimulation.java:35) visitor : 171.8.232.206 start visit service : acb4f575-dd6a-4d05-a17a-9f832500b1de in server : 171.9.80.67 with request id : 5f2082bf-3d39-4f71-89e4-3f4996b9319f at time : 2016-04-26 15:43:51.029";
    //time 2
    //uuid 2
    //ip 0
    static String m2Str = "2016-04-26 15:43:51.031 INFO [pool-2-thread-1](LogSimulation.java:59) server : 171.9.80.67 response for request : 5f2082bf-3d39-4f71-89e4-3f4996b9319f at time : 2016-04-26 15:43:51.029 in service : acb4f575-dd6a-4d05-a17a-9f832500b1de at time : 2016-04-26 15:43:51:031 and the response code is : 400";
    //uuid
    static String m1 = "\\{?\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}\\}?";
    //ip
    static String m2 = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
    //time
    static String m3 = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}(?: [AP]M)?(?: [+-]\\d{4})?";

    private static void test1(){
        Pattern regex = Pattern.compile(m1,Pattern.MULTILINE);
        Matcher matcher = regex.matcher(m1Str);
        if (matcher.find()){
            System.out.println(";;;;;;;;;;;;;"+matcher.group());
        }
    }
    private static void test2(){
        Pattern regex = Pattern.compile(m2,Pattern.MULTILINE);
        Matcher matcher = regex.matcher(m1Str);
        if (matcher.find()){
            System.out.println(";;;;;;;;;;;;;"+matcher.group());
        }
    }

    private static void test3(){
        Pattern regex = Pattern.compile(m3,Pattern.MULTILINE);
        Matcher matcher = regex.matcher(m1Str);
        if (matcher.find()){
            System.out.println(";;;;;;;;;;;;;"+matcher.group());
        }
    }

    private static void test(){
        String mStr = m2Str;
        //String mStr = m2Str;
        Pattern uuid = Pattern.compile(m1);
        Matcher uuidMatcher = uuid.matcher(mStr);

        Pattern ip = Pattern.compile(m2);
        Matcher ipMatcher = ip.matcher(mStr);

        Pattern time = Pattern.compile(m3);
        Matcher timeMatcher = time.matcher(mStr);

        if (uuidMatcher.find() && timeMatcher.find()){
            if(mStr.indexOf("response for request") > 0) {
                String server_ip = "";
                if (ipMatcher.find()) {
                    //visitor ip
                    server_ip = ipMatcher.group();
                }
                //request_id
                String request_id = uuidMatcher.group();
                //service_id
                String service_id = "";
                if (uuidMatcher.find()) {
                    service_id = uuidMatcher.group();
                }
                //ResponseTime
                //request time
                String begin_time = timeMatcher.group();
                //response time
                String end_time = "";
                if(timeMatcher.find()){
                    end_time = timeMatcher.group();
                }
                //ResponseParameters
                //response code
                String response_code = "";
                Matcher matcher = number.matcher(mStr);
                int cnt = 0;
                while (matcher.find()) {
                    if (cnt == 10)
                        response_code = matcher.group();
                    cnt++;
                }
                System.out.println(server_ip);
                System.out.println(request_id);
                System.out.println(service_id);
                System.out.println(begin_time);
                System.out.println(end_time);
                System.out.println(response_code);
            }
        }
    }


    public static void main(String[] args) {
        test();
    }
}
