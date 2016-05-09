package com.fksm.web.service.common;

import com.fksm.common.dto.CallersStatics;
import com.fksm.common.dto.CodeStatics;
import com.fksm.common.dto.CostsStatics;
import com.fksm.common.dto.RequestStatics;

import java.util.*;

/**
 * Created by Administrator on 2016/5/7.
 */
public interface GraphcService {
    /**
     * getServerServices
     * @return
     * @throws Exception
     */
    public Map<String, Set<String>> getServerServices() throws Exception;

    public List<CallersStatics> getServerServiceCallers(String serverIp, String serviceId) throws Exception;

    public List<CodeStatics> getServerServiceCodes(String serverIp, String serviceId) throws Exception;

    public List<CostsStatics> getServerServiceCosts(String serverIp, String serviceId) throws Exception;

    public List<RequestStatics> getServerServiceRequests(String serverIp, String serviceId) throws Exception;
}
