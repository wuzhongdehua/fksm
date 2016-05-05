package com.fksm.common.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 16-4-23.
 */
public class ServiceInformation extends BaseMassageDto implements Serializable {
    private String service_path;

    public String getService_path() {
        return service_path;
    }

    public void setService_path(String service_path) {
        this.service_path = service_path;
    }

    /**
     *
     * @param service_id
     * @param request_id
     * @param server_ip
     * @return
     */
    public static ServiceInformation build(String service_id, String request_id, String server_ip, Long begin_time){
        ServiceInformation info = new ServiceInformation();
        info.setServer_ip(server_ip);
        info.setRequest_id(request_id);
        info.setService_id(service_id);
        info.setBegin_time(begin_time);
        return info;
    }
}
