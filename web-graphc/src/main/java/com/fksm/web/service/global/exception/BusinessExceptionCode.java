/**
 * Copyright© 2003-2015 www.ymangroup.com, All Rights Reserved. <br/>
 * 描述: TODO <br/>
 * @author yushunwei005
 * @date 2015年7月27日
 * @version 1.0
 */

package com.fksm.web.service.global.exception;

import java.util.HashMap;
import java.util.Map;

/** 
 * 描述: 自定义异常编码 .<br>
 * @author yushunwei005
 * @date 2015年8月14日  
 */
public class BusinessExceptionCode {
    /**
     * 异常分隔符.
     */
    public static final String EXTRA_EXCEPTION_MSG_SPLITER = ": ";
    
    /**
     * 异常集合.
     */
    public static Map<String, String> CODE_MAP = new HashMap<String,String>();
    
    /**
     * 系统异常.
     */
    public static final String SYSTEM_EXCEPTION_CODE = "E001";
    
    /**
     * 邮件发送异常.
     */
    public static final String EMAIL_SEND_EXCEPTION_CODE = "E002";
    
    /**
     * SAAS服务DDL脚本执行异常 .
     */
    public static final String SAAS_DDL_SCRIPT_RUN_ERROR = "E003";
    
    /**
     * SAAS服务机构账户校验异常 .
     */
    public static final String SAAS_ACCOUNT_CHECK_ERROR = "E004";
 
    // 用作自动生成前端 scripts/common/messages_zh-cn.js 文件
    static{
        CODE_MAP = new HashMap<String, String>();
        CODE_MAP.put(SYSTEM_EXCEPTION_CODE,"系统异常");
        CODE_MAP.put(EMAIL_SEND_EXCEPTION_CODE, "邮件发送异常");
        CODE_MAP.put(SAAS_DDL_SCRIPT_RUN_ERROR, "机构数据生成异常");
        CODE_MAP.put(SAAS_ACCOUNT_CHECK_ERROR, "机构账户校验异常");
    }
}

