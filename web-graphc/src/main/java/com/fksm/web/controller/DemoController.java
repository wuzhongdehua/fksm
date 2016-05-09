package com.fksm.web.controller;

import com.fksm.web.service.common.DemoService;
import com.fksm.web.service.global.dto.response.ResponseCode;
import com.fksm.web.service.global.dto.response.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/5/7.
 */

@Controller
@RequestMapping("/api/demo")
public class DemoController {

    private static Logger logger = LoggerFactory.getLogger(DemoController.class);

    @Resource
    private DemoService demoService;


    /**
     * 描述: 保存 .
     * @return
     * @throws Exception
     */
    @RequestMapping("/list")
    @ResponseBody
    public ResponseDto<List<HashMap>> list
    (
            @RequestParam(value="startTime",required=true) Long startTime,
            @RequestParam(value="endTime",required=true) Long endTime,
            @RequestParam(value="serverIp",required=true) String serverIp,
            @RequestParam(value="serviceId",required=true) String serviceId
    ) throws Exception {
        ResponseDto<List<HashMap>> response = new ResponseDto<List<HashMap>>();
        List<HashMap> res = demoService.getCallers(startTime, endTime, serverIp, serviceId);
        logger.debug("get {} data from redis.", res.size());
        if (res.size() == 0){
            logger.warn("get null data from redis.");
            response.setCode(ResponseCode.OPERATE_NO_DATA);
            return response;
        }
        response.setData(res);
        return response;
    }
}
