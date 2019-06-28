package com.txdb.gpmanage.core.gp.connector;

import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.service.proxy.GpEnvServiceProxy;
import com.txdb.gpmanage.core.gp.service.proxy.GpExpandServiceProxy;
import com.txdb.gpmanage.core.gp.service.proxy.GpInstallServiceProxy;
import com.txdb.gpmanage.core.gp.service.proxy.GpManageServiceProxy;

public interface IGPConnector {
	
	void setCallback(UICallBack callback);
	
	GPResultSet connect();
	
	void disconnect();
	
	IExecuteDao getDao();
	
	String getDaoInfo();
	
	String getOriginHostname();
	
	String getHostname();
	
	void setHostname(String hostname);
	
	GpEnvServiceProxy getEnvServiceProxy();
	
	GpInstallServiceProxy getInstallServiceProxy();
	
	GpExpandServiceProxy getExpandServiceProxy();
	
	GpManageServiceProxy getManageServiceProxy();
}
