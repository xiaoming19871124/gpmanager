package com.txdb.gpmanage.core.exception;

/**
 * 自定义异常
 * 
 * @author ws
 *
 */
public class GpException extends RuntimeException {

	/**
	 * ID
	 */
	private static final long serialVersionUID = 1L;
	private IErrorCode errorCode;

	/**
	 * 构造一个基本异常
	 * 
	 * @param errorCode
	 *            错误码
	 */
	public GpException(IErrorCode errorCode) {
		super(errorCode.getErrorMessage());
		this.errorCode = errorCode;
	}

	/**
	 * 构造一个基本异常
	 * 
	 * @param errorCode
	 *            错误码
	 * @param cause
	 *            异常
	 */
	public GpException(IErrorCode errorCode, Throwable cause) {
		super(errorCode.getErrorMessage(), cause);
		this.errorCode = errorCode;
	}

	/**
	 * 构造一个基本异常
	 * 
	 * @param cause
	 *            异常
	 */
	public GpException(Throwable cause) {
		super(cause);
	}

	/**
	 * 获取错误码编号
	 * 
	 * @return 错误码编号
	 */
	public int getErrorCode() {
		return errorCode.getErrorCode();
	}

	/**
	 * 获取异常信息
	 * 
	 * @return 异常信息
	 */
	public String getErrorMassage() {
		return errorCode.getErrorMessage();
	}
}
