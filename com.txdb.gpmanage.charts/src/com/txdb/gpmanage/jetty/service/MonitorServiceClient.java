package com.txdb.gpmanage.jetty.service;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;

import com.txdb.gpmanage.core.gp.monitor.IMonitorService;

public class MonitorServiceClient implements IMonitorService {

	private static IMonitorService monitorService;
	
	public static IMonitorService getInstance() {
		if (monitorService == null)
			monitorService = new MonitorServiceClient();
		return monitorService;
	}
	
	@Override
	public String updateGpSegmentConf(String monitorName, String gpSegmentConfListJson) throws IOException {
		Object[] result = invoke("updateGpSegmentConf",
				new String[] { monitorName, gpSegmentConfListJson },
				new Class[] { String.class, String.class });
		return result[0].toString();
	}

	@Override
	public String updateSystemNow(String monitorName, String systemListJson) throws IOException {
		Object[] result = invoke("updateSystemNow",
				new String[] { monitorName, systemListJson },
				new Class[] { String.class, String.class });
		return result[0].toString();
	}

	@Override
	public String updateSystemHistory(String monitorName, String systemListJson) throws IOException {
		Object[] result = invoke("updateSystemHistory",
				new String[] { monitorName, systemListJson },
				new Class[] { String.class, String.class });
		return result[0].toString();
	}
	
	@Override
	public String requireSystemHistory(String requireConnectionJson) throws IOException {
		// TODO 
		Object[] result = invoke("requireSystemHistory",
				new String[] { requireConnectionJson },
				new Class[] { String.class });
		return result[0].toString();
	}
	
	@Override
	public String updateDatabaseNow(String monitorName, String databaseListJson) throws IOException {
		Object[] result = invoke("updateDatabaseNow",
				new String[] { monitorName, databaseListJson },
				new Class[] { String.class, String.class });
		return result[0].toString();
	}

	@Override
	public String updateDatabaseHistory(String monitorName, String databaseListJson) throws IOException {
		Object[] result = invoke("updateDatabaseHistory",
				new String[] { monitorName, databaseListJson },
				new Class[] { String.class, String.class });
		return result[0].toString();
	}
	
	@Override
	public String requireDatabaseHistory(String requireConnectionJson) throws IOException {
		// TODO 
		Object[] result = invoke("requireDatabaseHistory",
				new String[] { requireConnectionJson },
				new Class[] { String.class });
		return result[0].toString();
	}

	@Override
	public String updateDiskspaceNow(String monitorName, String diskspaceListJson) throws IOException {
		Object[] result = invoke("updateDiskspaceNow",
				new String[] { monitorName, diskspaceListJson },
				new Class[] { String.class, String.class });
		return result[0].toString();
	}

	@Override
	public String updateDiskspaceHistory(String monitorName, String diskspaceListJson) throws IOException {
		Object[] result = invoke("updateDiskspaceHistory",
				new String[] { monitorName, diskspaceListJson },
				new Class[] { String.class, String.class });
		return result[0].toString();
	}
	
	@Override
	public String requireDiskspaceHistory(String requireConnectionJson) throws IOException {
		// TODO 
		Object[] result = invoke("requireDiskspaceHistory",
				new String[] { requireConnectionJson },
				new Class[] { String.class });
		return result[0].toString();
	}
	
	@SuppressWarnings("rawtypes")
	public static Object[] invoke(String method, String[] params, Class[] classes) throws IOException {
		RPCServiceClient client = new RPCServiceClient();
		Options option = client.getOptions();
		EndpointReference reference = new EndpointReference(address);
		option.setTimeOutInMilliSeconds(6000000);
		option.setTo(reference);
		QName qname = new QName(namespaceURI, method);
		Object[] result = client.invokeBlocking(qname, params, classes);

		return result;
	}
}
