/**
 * Copyright© 2003-2015 www.ymangroup.com, All Rights Reserved. <br/>
 * 描述: TODO <br/>
 * @author yushunwei005
 * @date 2015年7月27日
 * @version 1.0
 */

package com.fksm.web.service.global.exception;

/** 
 * 描述: 自定义异常 .<br>
 * @author yushunwei005
 * @date 2015年8月14日  
 */
public class BusinessException extends Exception {
	private static final long serialVersionUID = 5336347006760270388L;

	/**
	 * 异常编码.
	 */
	private String exceptionCode;
	
	/**
	 * 异常描述.
	 */
	private String detailMsg;
	
	public BusinessException(String exceptionCode,String extraMsg){
		super();
		this.setDetailMsg(exceptionCode);
		this.setExtraMsg(extraMsg);
	}
	
	public BusinessException(String exceptionCode) {
		super();
	    this.setDetailMsg(exceptionCode);
	}

	public String getExceptionCode() {
		return exceptionCode;
	}

	public void setExceptionCode(String exceptionCode) {
		this.exceptionCode = exceptionCode;
	}

	public String getDetailMsg() {
		return detailMsg;
	}

	private void setExtraMsg(String extraMsg) {
		this.detailMsg += BusinessExceptionCode.EXTRA_EXCEPTION_MSG_SPLITER + extraMsg;
	}
	
	private void setDetailMsg(String exceptionCode){
	    this.exceptionCode = exceptionCode;
	    this.detailMsg = BusinessExceptionCode.CODE_MAP.get(exceptionCode);
	}

	@Override
	public String getMessage() {
		return this.detailMsg;
	}
}

