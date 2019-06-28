package com.txdb.gpmanage.core.exception;

/**
 * 错误码超类
 * 
 * @author ws
 *
 */
public interface IErrorCode {
	/**
	 * 获取异常编号
	 * 
	 * @return 异常编号
	 */
	public int getErrorCode();

	/**
	 * 获取异常信息
	 * 
	 * @return 异常信息
	 */
	public String getErrorMessage();
}
