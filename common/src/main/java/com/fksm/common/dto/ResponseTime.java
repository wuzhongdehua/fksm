package com.fksm.common.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 16-4-23.
 */
public class ResponseTime  extends BaseMassageDto implements Serializable {
    private Long end_time;
    private Long total_cost;

    public Long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Long end_time) {
        this.end_time = end_time;
    }

    public Long getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(Long total_cost) {
        this.total_cost = total_cost;
    }

    /**
     * @param begin_time
     * @return
     */
    public static ResponseTime build(String server_ip, String service_id, String request_id, Long begin_time, Long end_time){
        ResponseTime responseTime = new ResponseTime();
        responseTime.setServer_ip(server_ip);
        responseTime.setService_id(service_id);
        responseTime.setRequest_id(request_id);
        responseTime.setBegin_time(begin_time);
        responseTime.setEnd_time(end_time);
        return responseTime;
    }
}
