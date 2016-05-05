package com.fksm.common.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 16-4-23.
 */
public class ResponseParameters  extends BaseMassageDto implements Serializable {
    //100,200,300,400
    //其中100表示响应成功,200表示请求参数异常,300表示业务异常,400 表示系统异常
    private Integer response_code;
    private Map<String, String> response_param;

    public Integer getResponse_code() {
        return response_code;
    }

    public void setResponse_code(Integer response_code) {
        this.response_code = response_code;
    }

    public Map<String, String> getResponse_param() {
        return response_param;
    }

    public void setResponse_param(Map<String, String> response_param) {
        this.response_param = response_param;
    }

    /**
     *
     * @param service_id
     * @param request_id
     * @param response_code
     * @return
     */
    public static ResponseParameters build(String server_ip, String service_id, String request_id, Integer response_code, Long begin_time){
        ResponseParameters info = new ResponseParameters();
        info.setServer_ip(server_ip);
        info.setService_id(service_id);
        info.setRequest_id(request_id);
        info.setResponse_code(response_code);
        info.setBegin_time(begin_time);
        return info;
    }
}
