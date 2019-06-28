package com.txdb.gpmanage.monitor.service;

import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.txdb.gpmanage.core.common.CommonUtil;
import com.txdb.gpmanage.core.common.SqliteDao;
import com.txdb.gpmanage.core.entity.IGPEntity;
import com.txdb.gpmanage.core.entity.impl.GPMonitorEntity;
import com.txdb.gpmanage.core.entity.impl.MailEntity;
import com.txdb.gpmanage.core.entity.impl.SystemWarningEntity;
import com.txdb.gpmanage.core.gp.entry.GPSegmentInfo;
import com.txdb.gpmanage.core.gp.entry.gpmon.Diskspace;
import com.txdb.gpmanage.core.gp.entry.gpmon.Queries;
import com.txdb.gpmanage.core.log.LogUtil;

public class WarnServer {
	private GPMonitorEntity monitorEntity;
	private static String WARN_SEGMENT = "warn_segment.vm";
	private static String CPU_WARN = "warn_cup.vm";
	private static String IO_WARN = "warn_io.vm";
	private static String MEMORY_WARN = "warn_memory.vm";
	private static String DISK_WARN = "warn_disk.vm";
	private static String QUERY_WARN = "warn_query.vm";
	private boolean isWarnedCPU_WARN = false;
	private boolean isWarnedDISK_WARN = false;
	private boolean isWarnedMAMERY_WARN = false;
	private boolean isWarnedIOREAD_WARN = false;
	private boolean isWarnedIOWRITE_WARN = false;
	private boolean isWarnedNETWRITE_WARN = false;
	private boolean isWarnedNETREAD_WARN = false;
	private boolean isWarnedHEALTH_WARN = false;
	private boolean isWarnedQUERY_WARN = false;

	public WarnServer(GPMonitorEntity monitorEntity) {
		this.monitorEntity = monitorEntity;
	}

	public void warn(final List<?> entities) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (entities == null || entities.size() < 1) {
					LogUtil.info("no warn get");
					return;
				}
				try {
					if (entities.get(0) instanceof GPSegmentInfo) {
						LogUtil.info("analysis warn segment status");
						warnHealth(entities);
					} else if (entities.get(0) instanceof com.txdb.gpmanage.core.gp.entry.gpmon.System) {
						LogUtil.info("analysis warn system");
						warnSystem(entities);
					} else if (entities.get(0) instanceof Diskspace) {
						LogUtil.info("analysis warn Diskspace");
						warnDisk(entities);
					} else if (entities.get(0) instanceof Queries) {
						LogUtil.info("analysis warn Diskspace");
						warnQuery(entities);
					} else {
						LogUtil.error("no configure warn type");
					}
				} catch (Exception e) {
					LogUtil.error("warn code has error", e);
				}
			}
		}).start();
	}

	/**
	 * segment info warn
	 * 
	 * @param entities
	 * @throws Exception
	 */
	synchronized private void warnHealth(List<?> entities) throws Exception {
		if (!isHaveWarn() || isWarnedHEALTH_WARN)
			return;
		List<SystemWarningEntity> warns = monitorEntity.getWarns();
		for (SystemWarningEntity warn : warns) {
			if (warn.getFaultWarning() == 0) {
				continue;
			} else {
				for (int i = 0; i < entities.size(); i++) {
					GPSegmentInfo segment = (GPSegmentInfo) entities.get(i);
					if (segment.getStatus().equals(GPSegmentInfo.STATUS_DOWN)) {
						isWarnedHEALTH_WARN = true;
						Map<String, String> values = new HashMap<String, String>();
						values.put("master", monitorEntity.getHostname());
						values.put("role", segment.getRole());
						values.put("server", segment.getHostname());
						values.put("error", "down");
						sendMail(warn, getMailContent(WARN_SEGMENT, values));
					}
					if (segment.getStatus().equals(GPSegmentInfo.MODE_NOTSYNC)) {
						isWarnedHEALTH_WARN = true;
						Map<String, String> values = new HashMap<String, String>();
						values.put("master", monitorEntity.getHostname());
						values.put("role", segment.getRole());
						values.put("server", segment.getHostname());
						values.put("error", "asynchronous");
						sendMail(warn, getMailContent(WARN_SEGMENT, values));
					}
				}
			}
		}
	}

	/**
	 * server health info warn
	 * 
	 * @param segmentInfoList
	 * @throws Exception
	 */
	synchronized private void warnSystem(List<?> entities) throws Exception {
		if (!isHaveWarn())
			return;
		List<SystemWarningEntity> warns = monitorEntity.getWarns();
		for (SystemWarningEntity warn : warns) {
			String cpu = warn.getCpuWarning();
			String io = warn.getIoWarning();
			String netWork = warn.getNetWorkWarning();
			String memory = warn.getMemoryWarning();
			for (int i = 0; i < entities.size(); i++) {
				com.txdb.gpmanage.core.gp.entry.gpmon.System sys = (com.txdb.gpmanage.core.gp.entry.gpmon.System) entities.get(i);
				// cup
				if (!isWarnedCPU_WARN) {
					double cpu_used = sys.getCpu_used();// xx.xx%
					if (cpu != null && !cpu.isEmpty()) {
						if (cpu_used >= Double.valueOf(cpu)) {
							isWarnedCPU_WARN = true;
							Map<String, String> values = new HashMap<String, String>();
							values.put("master", monitorEntity.getHostname());
							values.put("server", sys.getHostname());
							values.put("value", String.valueOf(cpu_used));
							sendMail(warn, getMailContent(CPU_WARN, values));
						}
					}
				}
				// Disk i/o
				if (io != null && !io.isEmpty()) {
					double wio = formatPoint2(sys.getDisk_wb_rate()/1024.0/1024);// Mb/S
					double rio = formatPoint2(sys.getDisk_rb_rate()/1024.0/1024);// Mb/s
					if (!isWarnedIOWRITE_WARN) {
						if (wio >= Double.valueOf(io)) {
							isWarnedIOWRITE_WARN = true;
							Map<String, String> values = new HashMap<String, String>();
							values.put("master", monitorEntity.getHostname());
							values.put("server", sys.getHostname());
							values.put("event", "disk");
							values.put("rorw", "write");
							values.put("value", String.valueOf(wio));
							sendMail(warn, getMailContent(IO_WARN, values));
						}
					}
					if (!isWarnedIOREAD_WARN) {
						if (rio >= Double.valueOf(io)) {
							isWarnedIOREAD_WARN = true;
							Map<String, String> values = new HashMap<String, String>();
							values.put("master", monitorEntity.getHostname());
							values.put("server", sys.getHostname());
							values.put("event", "disk");
							values.put("rorw", "read");
							values.put("value", String.valueOf(rio));
							sendMail(warn, getMailContent(IO_WARN, values));
						}
					}
				}
				// network i/o
				if (netWork != null && !netWork.isEmpty()) {
					double wNetwork = formatPoint2(sys.getNet_wb_rate()/1024.0/1024);// Mb/s
					double rNetWork = formatPoint2(sys.getNet_rb_rate()/1024.0/1024);// Mb/s
					if (!isWarnedNETWRITE_WARN) {
						if (wNetwork >= Double.valueOf(netWork)) {
							isWarnedNETWRITE_WARN = true;
							Map<String, String> values = new HashMap<String, String>();
							values.put("master", monitorEntity.getHostname());
							values.put("server", sys.getHostname());
							values.put("event", "network");
							values.put("rorw", "write");
							values.put("value", String.valueOf(wNetwork));
							sendMail(warn, getMailContent(IO_WARN, values));
						}

					}
					if (!isWarnedNETREAD_WARN) {
						if (rNetWork >= Double.valueOf(netWork)) {
							isWarnedNETREAD_WARN = true;
							Map<String, String> values = new HashMap<String, String>();
							values.put("master", monitorEntity.getHostname());
							values.put("server", sys.getHostname());
							values.put("event", "network");
							values.put("rorw", "read");
							values.put("value", String.valueOf(rNetWork));
							sendMail(warn, getMailContent(IO_WARN, values));
						}
					}
				}
				// memory
				if (memory != null && !memory.isEmpty() && !isWarnedMAMERY_WARN) {
					long memoryUsed = sys.getMem_actual_used();
					long memoryTotal = sys.getMem_total();
					double memoryUsedPoint = formatPoint2(memoryUsed / (double) memoryTotal * 100);
					if (memoryUsedPoint > Double.valueOf(memory)) {
						isWarnedMAMERY_WARN = true;
						Map<String, String> values = new HashMap<String, String>();
						values.put("master", monitorEntity.getHostname());
						values.put("server", sys.getHostname());
						values.put("value", String.valueOf(memoryUsedPoint));
						sendMail(warn, getMailContent(MEMORY_WARN, values));
					}
				}
			}
		}
	}

	synchronized private void warnDisk(List<?> entities) throws Exception {
		if (!isHaveWarn() || isWarnedDISK_WARN)
			return;
		List<SystemWarningEntity> warns = monitorEntity.getWarns();
		for (SystemWarningEntity warn : warns) {
			if (warn.getDiskWarning() == null || warn.getDiskWarning().isEmpty())
				continue;
			String warnValue = warn.getDiskWarning();
			for (int i = 0; i < entities.size(); i++) {
				Diskspace space = (Diskspace) entities.get(i);
				long used = space.getBytes_used();
				long total = space.getTotal_bytes();
				double usedPoint = formatPoint2(used / (double) total * 100);
				if (usedPoint > Double.valueOf(warnValue)) {
					isWarnedDISK_WARN = true;
					Map<String, String> values = new HashMap<String, String>();
					values.put("master", monitorEntity.getHostname());
					values.put("server", space.getHostname());
					values.put("disk", space.getFilesystem());
					values.put("value", String.valueOf(usedPoint));
					sendMail(warn, getMailContent(DISK_WARN, values));
				}
			}
		}
	}

	synchronized private void warnQuery(List<?> entities) throws Exception {
		if (!isHaveWarn() || isWarnedQUERY_WARN)
			return;
		List<SystemWarningEntity> warns = monitorEntity.getWarns();
		for (SystemWarningEntity warn : warns) {
			if (warn.getQueryWarning() == null || warn.getQueryWarning().isEmpty())
				continue;
			String warnValue = warn.getQueryWarning();
			for (int i = 0; i < entities.size(); i++) {
				Queries query = (Queries) entities.get(i);
				long milliseconds_rtime = query.getTfinish().getTime() - query.getTstart().getTime();
				double s_rtime = milliseconds_rtime / 1000;

				if (s_rtime > Double.valueOf(warnValue)) {
					isWarnedQUERY_WARN = true;
					Calendar calendar = Calendar.getInstance();
					SimpleDateFormat sdf_ms = new SimpleDateFormat("m'm's's'");
					calendar.setTimeInMillis(milliseconds_rtime);
					String rtimeStr = sdf_ms.format(calendar.getTime());
					Map<String, String> values = new HashMap<String, String>();
					values.put("master", monitorEntity.getHostname());
					values.put("id", String.valueOf(query.getSsid()));
					values.put("statu", query.getStatus());
					values.put("user",query.getUsername());
					values.put("application", query.getApplication_name());
					values.put("sql", query.getQuery_text());
					values.put("submitTime", getDate(query.getTsubmit()));
					values.put("time", rtimeStr);
					values.put("database", query.getDb());
					sendMail(warn, getMailContent(QUERY_WARN, values));
					;
				}
			}
		}
	}

	private void sendMail(SystemWarningEntity warn, String msg) throws Exception {
		MailEntity mail = getMailEntity();
		if (mail == null)
			return;
		// 1、连接邮件服务器的参数配置
		Properties props = new Properties();
		// 设置用户的认证方式
		props.setProperty("mail.smtp.auth", "true");
		// 设置传输协议
		props.setProperty("mail.transport.protocol", "smtp");
		// 设置是否使用ssl安全连接 ---一般都使用
		props.put("mail.smtp.ssl.enable", mail.getSsl() == 1);
		// 端口号
		props.put("mail.smtp.port", mail.getPort());
		// 设置发件人的SMTP服务器地址
		props.setProperty("mail.smtp.host", mail.getStmpHost());
		// 2、创建定义整个应用程序所需的环境信息的 Session 对象
		Session session = Session.getInstance(props);
		// 设置调试信息在控制台打印出来
		session.setDebug(true);
		// 3、创建邮件的实例对象
		Message message = getMimeMessage(mail, warn, msg, session);
		// 4、根据session对象获取邮件传输对象Transport
		Transport transport = session.getTransport();
		// 设置发件人的账户名和密码
		transport.connect(mail.getSenderAddress(), mail.getSenderPassword());
		// 发送邮件，并发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人,
		// 抄送人, 密送人
		transport.sendMessage(message, message.getAllRecipients());
		// 5、关闭邮件连接
		transport.close();
	}

	/**
	 * 获得创建一封邮件的实例对象
	 * 
	 * @param session
	 * @return
	 * @throws MessagingException
	 * @throws AddressException
	 */
	public static MimeMessage getMimeMessage(MailEntity mail, SystemWarningEntity warn, String msg, Session session) throws Exception {
		// 创建一封邮件的实例对象
		MimeMessage mmsg = new MimeMessage(session);
		// 设置发件人地址
		String sendName = (mail.getSendName() != null || !mail.getSendName().isEmpty()) ? mail.getSendName() : mail.getSenderAddress();
		mmsg.setFrom(new InternetAddress(mail.getSenderAddress(), sendName));
		/**
		 * 设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行 MimeMessage.RecipientType.TO:发送
		 * MimeMessage.RecipientType.CC：抄送 MimeMessage.RecipientType.BCC：密送
		 */
		Address[] internetAddressTo = new InternetAddress().parse(warn.getMail());
		mmsg.setRecipients(MimeMessage.RecipientType.TO, internetAddressTo);
		// 设置邮件主题
		mmsg.setSubject("GREENPLUM WARN", "UTF-8");
		// 设置邮件正文
		mmsg.setContent(msg, "text/html;charset=UTF-8");
		// 设置邮件的发送时间,默认立即发送
		mmsg.setSentDate(new Date());

		return mmsg;
	}

	private boolean isHaveWarn() {
		return monitorEntity.getWarns() != null && monitorEntity.getWarns().size() > 0;
	}

	private MailEntity getMailEntity() {
		List<IGPEntity> mails = SqliteDao.getInstance().queryGPEntity(new MailEntity(), null);
		if (mails == null || mails.isEmpty()) {
			LogUtil.error("no send email configure!");
		} else {
			return (MailEntity) mails.get(0);
		}
		return null;
	}

	private double formatPoint2(double var) {
		try {
			return new BigDecimal(var).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		} catch (Exception e) {
			return 0;
		}
	}

	// private String getQueryInfo(Queries query, String rtimeStr) {
	// StringBuffer info = new StringBuffer();
	// info.append("query ID:" + query.getSsid());
	// info.append("query statu:" + query.getStatus());
	// info.append("\r\n");
	// info.append("query submit data:" + getDate(query.getTsubmit()));
	//
	// info.append("query time:" + rtimeStr);
	// info.append("\r\n");
	// info.append("query user:" + query.getUsername());
	// info.append("query database:" + query.getDb());
	//
	// return info.toString();
	// }
	private String getDate(Date ctime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(ctime);
	}

	private String getMailContent(String mailContent, Map<String, String> values) {
		StringWriter stringWriter = new StringWriter(); // velocity引擎
		VelocityEngine velocityEngine = new VelocityEngine(); // 设置文件路径属性
		Properties properties = new Properties();
		try {
			File tempDir = CommonUtil.getFile("com.txdb.gpmanage.monitor", "/resource");
			properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, tempDir.getAbsolutePath()); // 引擎初始化属性配置

			velocityEngine.init(properties); // 加载指定模版
			Template template = velocityEngine.getTemplate(mailContent, "UTF-8"); // 填充模板内容
			VelocityContext velocityContext = new VelocityContext();
			Set<String> keys = values.keySet();
			for (String key : keys)
				velocityContext.put(key, values.get(key));
			template.merge(velocityContext, stringWriter);
			return stringWriter.toString();
		} catch (Exception e) {
			LogUtil.error("Get Mail Content failed " + mailContent, e);
			return "fail";
		}

	}

	 public static void main(String[] args) {
		 long a = 1234;
		 double b = a/1024.0;
		 System.out.println(b);
				 
	 }
}
