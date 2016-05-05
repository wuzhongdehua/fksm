package com.fksm.flume.interceptor;


import com.alibaba.fastjson.JSON;
import com.fksm.common.dto.*;
import com.fksm.common.util.Constants;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CleanAndToJsonInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(CleanAndToJsonInterceptor.class);

    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private Pattern uuid;
    private Pattern ip;
    private Pattern time;

    private static Pattern number = Pattern.compile("\\d{3}");

    private CleanAndToJsonInterceptor(Pattern uuid,Pattern ip, Pattern time) {
        this.uuid = uuid;
        this.ip = ip;
        this.time = time;
    }

    @Override
    public void initialize() {
        // no-op
    }

    @Override
    public Event intercept(Event event) {
        if (event == null) {
            return null;
        }
        try {
            String e_b = new String(event.getBody(), Charsets.UTF_8);
            List<MessageDto> data = doReg(e_b);
            if (data == null || data.size() <= 0) {
                logger.warn("get none match data !!!");
                return null;
            }
            //toJson
            String res = JSON.toJSONString(data);
            logger.info("get json result : {} from log.", e_b);
            event.setBody(res.getBytes(Charsets.UTF_8));
            return event;
        } catch (Exception e) {
            logger.error("error get result : {} from log.", e);
            return null;
        }
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        List<Event> out = Lists.newArrayList();
        for (Event event : events) {
            Event outEvent = intercept(event);
            if (outEvent != null) {
                out.add(outEvent);
            }
        }
        return out;
    }

    @Override
    public void close() {
        // no-op
    }

    public static class Builder implements Interceptor.Builder {

        private Pattern uuid;
        private Pattern ip;
        private Pattern time;

        @Override
        public void configure(Context context) {
            uuid = Pattern.compile(context.getString(Constants.REGEX_UUID, ""), Pattern.MULTILINE);
            ip = Pattern.compile(context.getString(Constants.REGEX_IP, ""), Pattern.MULTILINE);
            time = Pattern.compile(context.getString(Constants.REGEX_TIME, ""), Pattern.MULTILINE);
        }

        @Override
        public Interceptor build() {
            logger.info(String.format("Creating CleanAndToJsonInterceptor: uuid=%s,ip=%s, time=%s", uuid, ip, time));
            return new CleanAndToJsonInterceptor(uuid, ip, time);
        }
    }

    /**
     * do rege
     * visitor : 123.233.220.245 start visit service : 30a09c46-7d2c-485c-b9e0-1633674b2b04 in server : 171.9.80.67 with request id : 2eac609b-e9cf-41c7-9dcd-c28eba2b6c5c at time : 2016-04-26 10:42:40:243
     * server : 171.9.80.67 response for request : 2eac609b-e9cf-41c7-9dcd-c28eba2b6c5c at time : 2016-04-26 10:42:40:244 and the response code is : 300
     * @param body
     * @return
     */
    public List<MessageDto> doReg(String body){
        List<MessageDto> dtos = new ArrayList<>();
        //request_id -> time
        List<CallerInformation> callerInformations = new ArrayList<>();
        List<ServiceInformation> serviceInformations = new ArrayList<>();
        List<ResponseTime> responseTimes = new ArrayList<>();
        List<ResponseParameters> responseParameterses = new ArrayList<>();
        String[] lines = body.split("\n");
        logger.debug("read : {} from the logs.", lines.length);
        for (String line: lines){
            logger.debug("the line : {} ", line);
            System.out.println("--------------------uuidStr------------------------->" + uuid.pattern());
            System.out.println("--------------------ipStr------------------------->" + ip.pattern());
            System.out.println("--------------------uuidStr------------------------->" + time.pattern());
            Matcher uuidMatcher = uuid.matcher(line);
            Matcher ipMatcher = ip.matcher(line);
            Matcher timeMatcher = time.matcher(line);
            if (uuidMatcher.find() && timeMatcher.find()){
                // is response
                if(line.indexOf("response for request") > 0){
                    String server_ip = "";
                    if (ipMatcher.find()) {
                        //visitor ip
                        server_ip = ipMatcher.group();
                    }
                    //request_id
                    String request_id = uuidMatcher.group();
                    //service_id
                    String service_id = "";
                    if(uuidMatcher.find()){
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
                    Matcher matcher = number.matcher(line);
                    int cnt = 0;
                    while (matcher.find()){
                        if(cnt==10)
                            response_code = matcher.group();
                        cnt ++;
                    }
                    responseParameterses.add(ResponseParameters.build(server_ip, service_id, request_id, Integer.parseInt(response_code), DateTime.parse(begin_time, formatter).getMillis()));
                    responseTimes.add(ResponseTime.build(server_ip, service_id, request_id, DateTime.parse(begin_time, formatter).getMillis(), DateTime.parse(end_time, formatter).getMillis()));
                } else {// is request
                    String caller_ip = "";
                    if (ipMatcher.find()) {
                        //visitor ip
                        caller_ip = ipMatcher.group();
                    }
                    //ServiceInformation
                    //server ip
                    String server_ip = "";
                    if(ipMatcher.find()){
                        server_ip = ipMatcher.group();
                    }
                    //service_id
                    String service_id = uuidMatcher.group();
                    //request_id
                    String request_id = "";
                    if(uuidMatcher.find()){
                        request_id = uuidMatcher.group();
                    }
                    //request time
                    String begin_time = timeMatcher.group();
                    callerInformations.add(CallerInformation.build(server_ip, service_id, request_id, caller_ip, DateTime.parse(begin_time, formatter).getMillis()));
                    serviceInformations.add(ServiceInformation.build(service_id, request_id, server_ip, DateTime.parse(begin_time, formatter).getMillis()));
                }
            }
        }
        if(callerInformations.size()>0)
            dtos.add(MessageDto.build(Constants.TYPE_CALLER_INFO,callerInformations));
        if(serviceInformations.size()>0)
            dtos.add(MessageDto.build(Constants.TYPE_SERVICE_INFO,serviceInformations));
        if(responseParameterses.size()>0)
            dtos.add(MessageDto.build(Constants.TYPE_RESP_PARAM,responseParameterses));
        if(responseTimes.size()>0)
            dtos.add(MessageDto.build(Constants.TYPE_RESP_TIME,responseTimes));
        return dtos;
    }
}

