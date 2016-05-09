/**
 * Copyright© 2003-2015 www.ymangroup.com, All Rights Reserved. <br/>
 * 描述: TODO <br/>
 * @author yushunwei005
 * @date 2015年8月3日
 * @version 1.0
 */

package com.fksm.web.service.global.dto.response;

import java.io.Serializable;

/** 
 * 描述: API接口数据传输对象 .<br>
 * @author yushunwei005
 * @date 2015年8月14日 
 * @param <T> 数据对象
 */
public class ResponseDto<T> implements Serializable{
	private static final long serialVersionUID = -7976564531903764830L;
	
	/**
	 * 操作结果编码
	 */
	private String code = ResponseCode.OPERATE_SUCCESS;
	
	/**
	 * 响应是否正常.
	 */
	private boolean responseSuccess = true;
	
	/**
	 * 响应数据.
	 */
	private T data;
	
	public ResponseDto(){
		
	}
	
	public ResponseDto(boolean responseSuccess){
		this.responseSuccess = responseSuccess;
	}
	
	public ResponseDto(String code){
		this.code = code;
	}
	
	public ResponseDto(String code,boolean responseSuccess){
		this.code = code;
		this.responseSuccess = responseSuccess;
	}
	
	public ResponseDto(String code,boolean responseSuccess,T data){
		this.code = code;
		this.responseSuccess = responseSuccess;
		this.data = data;
	}
	

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isResponseSuccess() {
		return responseSuccess;
	}

	public void setResponseSuccess(boolean responseSuccess) {
		this.responseSuccess = responseSuccess;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}

