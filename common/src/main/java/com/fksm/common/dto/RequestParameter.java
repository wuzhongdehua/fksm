package com.fksm.common.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 16-4-23.
 */
public class RequestParameter extends BaseMassageDto implements Serializable {
    private Map<String, String> request_param;

    public Map<String, String> getRequest_param() {
        return request_param;
    }

    public void setRequest_param(Map<String, String> request_param) {
        this.request_param = request_param;
    }

    /**
     *
     * @param data
     * @return
     */
    public static List<RequestParameter> build(String data){
        List<RequestParameter> info = new ArrayList<RequestParameter>();
        //TODO
        return info;
    }
}
