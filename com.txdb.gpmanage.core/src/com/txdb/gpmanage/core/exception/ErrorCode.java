package com.txdb.gpmanage.core.exception;

/**
 * 错误码
 * 
 * @author ws
 *
 */
public enum ErrorCode implements IErrorCode {
	/** 参数为空 */
	EMPTY_PARAME(100001, "参数为空"),
	/** 参数错误 */
	ERROR_PARAME(100002, "参数错误");
	/**
	 * 错误码
	 */
	private int errorCode;
	/**
	 * 错误信息
	 */
	private String errorMessage;

	private ErrorCode(int errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	@Override
	public int getErrorCode() {
		return errorCode;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

}
