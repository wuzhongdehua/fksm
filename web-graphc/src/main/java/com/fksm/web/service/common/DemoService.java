package com.fksm.web.service.common;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/5/7.
 */
public interface DemoService {
    /**
     * getCallers
     * @return
     * @throws Exception
     */
    public List<HashMap> getCallers(Long startTime, Long endTime, String server_ip, String service_id) throws Exception;
}
