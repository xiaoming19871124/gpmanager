package com.txdb.gpmanage.core.gp.service;

import java.util.Map;
import java.util.Properties;

import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;

public interface IGpEnvService {
	
	public static final String G_CONF_SYSCTL = "/etc/sysctl.conf";
	public static final String G_CONF_LIMITS = "/etc/security/limits.conf";
	public static final String G_CONF_NPROC  = "/etc/security/limits.d/90-nproc.conf";
	
	void initialize(IExecuteDao dao);
	
	// 检查必要软件安装情况
	GPResultSet checkRequiredSoftware();
	
	// 修改/etc/sysconfig/network
	GPResultSet updateHostname(String hostname);
	
	// 修改/etc/resolv.conf
	GPResultSet updateResolvConf(String nameserverIp);

	// 修改/etc/hosts
	GPResultSet updateHosts(Map<String, String[]> hostMap);

	// 关闭防火墙
	GPResultSet closeIptables();

	// 关闭SELINUX
	GPResultSet closeSeLinux();

	// 内存环境 && 软限制
	GPResultSet updateSysctl_limits_90_nproc(Properties sysctlProps, Properties limitsProps, Properties nprocProps);
	
	// 查询系统环境参数
	Properties getSysctlParams();
	Properties getSysctlParams(String key);

	// 磁盘
	GPResultSet blockDev(int number);

	// 用户建立
	GPResultSet createOrReplaceGpUser(String userName, String password);
	
	// 时钟同步
	GPResultSet clockSynchronize(String hostfile);
	
	/**
	 * 建立用户
	 * @param userName
	 */
	GPResultSet createUser(String userName);
	
	/**
	 * 删除用户
	 * @param userName
	 */
	GPResultSet deleteUser(String userName);
	
	/**
	 * 检查用户是否存在
	 * @param userName
	 */
	boolean userExist(String userName);
	
	/**
	 * 设置用户密码
	 * @param userName
	 * @param password
	 */
	GPResultSet userPassword(String userName, String password);
	
	/**
	 * 文件上传
	 * @param directory 上传到sftp目录 (eg:/home/test/)
	 * @param uploadFile 要上传的文件,包括路径(eg:/home/123.txt)
	 * @return
	 */
	boolean uploadFile(String directory, String uploadFile);
	
	/**
	 * 文件下载
	 * @param src linux服务器文件地址
	 * @param dst 本地存放地址
	 * @return
	 */
	boolean downloadFile(String src, String dst);
}
