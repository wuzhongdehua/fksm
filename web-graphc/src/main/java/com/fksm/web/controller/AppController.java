package com.fksm.web.controller;

import com.fksm.common.dto.CallersStatics;
import com.fksm.common.dto.CodeStatics;
import com.fksm.common.dto.CostsStatics;
import com.fksm.common.dto.RequestStatics;
import com.fksm.web.service.common.GraphcService;
import com.fksm.web.service.global.dto.response.ResponseCode;
import com.fksm.web.service.global.dto.response.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2016/5/7.
 */

@Controller
@RequestMapping("/api/app")
public class AppController {

    private static Logger logger = LoggerFactory.getLogger(AppController.class);

    @Resource
    private GraphcService graphcService;


    /**
     * 描述: 保存 .
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/index")
    public ModelAndView index() throws Exception {
        ModelAndView mav = new ModelAndView("/index");
        mav.addObject("menu", graphcService.getServerServices());
        return mav;
    }

    @RequestMapping(value="/graph1")
    @ResponseBody
    public ResponseDto<List<CallersStatics>> graph1(@RequestParam(value="serverIp",required=true) String serverIp, @RequestParam(value="serviceId",required=true) String serviceId) throws Exception {
        ResponseDto<List<CallersStatics>> responseDto = new ResponseDto<List<CallersStatics>>();
        List<CallersStatics> data =  graphcService.getServerServiceCallers(serverIp, serviceId);
        responseDto.setData(data);
        responseDto.setCode(ResponseCode.OPERATE_SUCCESS);
        return responseDto;
    }

    @RequestMapping(value="/graph2")
    @ResponseBody
    public ResponseDto<List<CodeStatics>> graph2(@RequestParam(value="serverIp",required=true) String serverIp, @RequestParam(value="serviceId",required=true) String serviceId) throws Exception {
        ResponseDto<List<CodeStatics>> responseDto = new ResponseDto<List<CodeStatics>>();
        List<CodeStatics> data =  graphcService.getServerServiceCodes(serverIp, serviceId);
        responseDto.setData(data);
        responseDto.setCode(ResponseCode.OPERATE_SUCCESS);
        return responseDto;
    }

    @RequestMapping(value="/graph3")
    @ResponseBody
    public ResponseDto<List<CostsStatics>> graph3(@RequestParam(value="serverIp",required=true) String serverIp, @RequestParam(value="serviceId",required=true) String serviceId) throws Exception {
        ResponseDto<List<CostsStatics>> responseDto = new ResponseDto<List<CostsStatics>>();
        List<CostsStatics> data =  graphcService.getServerServiceCosts(serverIp, serviceId);
        responseDto.setData(data);
        responseDto.setCode(ResponseCode.OPERATE_SUCCESS);
        return responseDto;
    }

    @RequestMapping(value="/graph4")
    @ResponseBody
    public ResponseDto<List<RequestStatics>> graph4(@RequestParam(value="serverIp",required=true) String serverIp, @RequestParam(value="serviceId",required=true) String serviceId) throws Exception {
        ResponseDto<List<RequestStatics>> responseDto = new ResponseDto<List<RequestStatics>>();
        List<RequestStatics> data =  graphcService.getServerServiceRequests(serverIp, serviceId);
        responseDto.setData(data);
        responseDto.setCode(ResponseCode.OPERATE_SUCCESS);
        return responseDto;
    }
}
