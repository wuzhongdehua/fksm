/**
 * Copyright© 2003-2015 www.ymangroup.com, All Rights Reserved. <br/>
 * 描述: TODO <br/>
 * @author yushunwei005
 * @date 2015年8月3日
 * @version 1.0
 */

package com.fksm.web.service.global.dto.response;

import java.util.HashMap;
import java.util.Map;

/** 
 * 描述: 后端响应码 供前端 .<br>
 * 命名规则: 2位模块代号 + 3位序号
 * @author yusunwei005
 * @date 2015年8月12日  
 */
public class ResponseCode {
	
	/**
	 * 通用编码：参数缺失 .
	 */
	public static final String COMMON_PARAMETER_MISSING = "CN001";
	
	/**
	 * 需要重新登录 .
	 */
	public static final String USER_NEED_LOGIN = "UR001";
	
	/**
	 * 用户名/密码错误 .
	 */
	public static final String USER_NORP_ERROR = "UR002";
	
	/**
	 * 户登录错误 .
	 */
	public static final String USER_LOGIN_ERROR = "UR003";
	
	/**
	 * 用户认证状态失效 .
	 */
	public static final String AUTH_USER_STATUS_ERROR = "AH002";

	/**
	 * 操作成功 .
	 */
	public static final String OPERATE_SUCCESS = "200";

	/**
	 * 操作成功 .
	 */
	public static final String OPERATE_NO_DATA = "NDATA";
//====================================================================================================================================

	public static Map<String, String> CODE_MAP = new HashMap<String,String>();
	
    // 用作自动生成前端 scripts/common/messages_zh-cn.js 文件
	static{
		CODE_MAP.put(COMMON_PARAMETER_MISSING, "参数缺失");
		CODE_MAP.put(USER_NEED_LOGIN,"会话已结束,需要重新登录");
		CODE_MAP.put(USER_NORP_ERROR, "用户名或者密码错误");
		CODE_MAP.put(USER_LOGIN_ERROR, "用户登录错误");
		CODE_MAP.put(AUTH_USER_STATUS_ERROR, "用户认证状态失效");
		CODE_MAP.put(OPERATE_SUCCESS, "操作成功！");
	}
}

