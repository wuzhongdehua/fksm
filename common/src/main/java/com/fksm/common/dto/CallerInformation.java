package com.fksm.common.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 16-4-23.
 */
public class CallerInformation extends BaseMassageDto implements Serializable {
    private String caller_ip;

    public String getCaller_ip() {
        return caller_ip;
    }

    public void setCaller_ip(String caller_ip) {
        this.caller_ip = caller_ip;
    }

    /**
     *
     * @param service_id
     * @param request_id
     * @param caller_ip
     * @return
     */
    public static CallerInformation build(String server_ip, String service_id, String request_id, String caller_ip, Long begin_time){
        CallerInformation information = new CallerInformation();
        information.setServer_ip(server_ip);
        information.setCaller_ip(caller_ip);
        information.setRequest_id(request_id);
        information.setService_id(service_id);
        information.setBegin_time(begin_time);
        return information;
    }
}
