package com.txdb.gpmanage.core.entity.impl;

import com.txdb.gpmanage.core.entity.BaseGPEntity;
import com.txdb.gpmanage.core.entity.annotation.FieldTag;
import com.txdb.gpmanage.core.entity.annotation.TableTag;

/**
 * GP 预警配置
 * 
 * @author ws
 */
@TableTag(tableName = "gpmail")
public class MailEntity extends BaseGPEntity {

	@FieldTag(constraint = "PRIMARY KEY")
	private int id;
	private String senderAddress;
	private String senderPassword;
	private String sendName;
	private String stmpHost;
	private int ssl = 0;
	private int port = 25;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getSenderPassword() {
		return senderPassword;
	}

	public void setSenderPassword(String senderPassword) {
		this.senderPassword = senderPassword;
	}

	public String getSendName() {
		return sendName;
	}

	public void setSendName(String sendName) {
		this.sendName = sendName;
	}

	public int getSsl() {
		return ssl;
	}

	public void setSsl(int ssl) {
		this.ssl = ssl;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getStmpHost() {
		return stmpHost;
	}

	public void setStmpHost(String stmpHost) {
		this.stmpHost = stmpHost;
	}

}
