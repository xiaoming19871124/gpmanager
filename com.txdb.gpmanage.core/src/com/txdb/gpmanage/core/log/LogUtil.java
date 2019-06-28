package com.txdb.gpmanage.core.log;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.txdb.gpmanage.core.Activator;

/**
 * 日志记录
 * 
 * @author ws
 *
 */
public class LogUtil {
	/**
	 * 错误级别
	 * 
	 * @param message
	 *            错误信息
	 * @param t
	 *            异常
	 */
	public static void error(String message, Throwable t) {
		error(Activator.PLUGIN_ID, message, t);
	}
	/**
	 * 错误级别
	 * 
	 * @param message
	 *            错误信息
	 * @param t
	 *            异常
	 */
	public static void error(String message) {
//		System.err.println(message);
		error(Activator.PLUGIN_ID, message, null);
	}
	/**
	 * 错误级别
	 * 
	 * @param pluginId
	 *            异常插件ID
	 * @param message
	 *            错误信息
	 * @param t
	 *            异常
	 */
	public static void error(String pluginId, String message, Throwable t) {
		ILog log = Activator.getDefault().getLog();
		Status status = new Status(IStatus.ERROR, pluginId, IStatus.ERROR,
				message + "\n", t);
		log.log(status);
	}

	/**
	 * 错误级别
	 * 
	 * @param pluginId
	 *            异常插件ID
	 * @param message
	 *            错误信息
	 */
	public static void error(String pluginId, String message) {
		ILog log = Activator.getDefault().getLog();
		Status status = new Status(IStatus.ERROR, pluginId, IStatus.ERROR,
				message + "\n", null);
		log.log(status);
	}

	/**
	 * 警告级别
	 * 
	 * @param pluginId
	 * @param message
	 */
	public static void warn(String pluginId, String message) {
		ILog log = Activator.getDefault().getLog();
		Status status = new Status(IStatus.WARNING, pluginId, IStatus.INFO,
				message + "\n", null);
		log.log(status);
	}

	/**
	 * 警告级别
	 * 
	 * @param pluginId
	 * @param message
	 */
	public static void warn(String message) {
//		System.out.println("<! " + message + " >");
		warn(Activator.PLUGIN_ID, message);
	}

	/**
	 * 信息级别
	 * 
	 * @param message
	 */
	public static void info(String message) {
//		System.out.println("< " + message + " >");
		info(Activator.PLUGIN_ID, message);
	}

	/**
	 * 信息级别
	 * 
	 * @param pluginId
	 * @param message
	 */
	public static void info(String pluginId, String message) {
		ILog log = Activator.getDefault().getLog();
		Status status = new Status(IStatus.INFO, pluginId, IStatus.INFO,
				message + "\n", null);
		log.log(status);
	}
}
