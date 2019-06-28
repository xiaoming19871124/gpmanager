package com.txdb.gpmanage.core.gp.monitor.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.txdb.gpmanage.core.gp.constant.IConstantsCmds;
import com.txdb.gpmanage.core.gp.monitor.IMonitorServer;
import com.txdb.gpmanage.core.gp.monitor.IMonitorService;

public class MonitorController implements IMonitorService {

	private static MonitorController controller;
	private List<IMonitorServer> serverList;
	
	public static MonitorController getInstance() {
		if (controller == null)
			controller = new MonitorController();
		return controller;
	}
	
	public MonitorController() {
		serverList = new ArrayList<IMonitorServer>();

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(IConstantsCmds.MONITOR_SERVER_EXTENSION_ID);
		IExtension[] extensions = extensionPoint.getExtensions();
		System.out.println(">> Found All(" + extensions.length + ") WebServer Extension(s).");

		for (IExtension extension : extensions) {
			IConfigurationElement[] serverElements = extension.getConfigurationElements();
			for (IConfigurationElement serverElement : serverElements) {
				// String id = serverElement.getAttribute("id").toLowerCase();
				try {
					IMonitorServer webserver = (IMonitorServer) serverElement.createExecutableExtension("class");
					webserver.init();
					serverList.add(webserver);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void listServer() {
		for (IMonitorServer server : serverList)
			System.out.println("webserver: " + server + ", isStarted: " + server.isStarted());
	}
	
	public List<IMonitorServer> getServerList() {
		return serverList;
	}

	public void serverStartup() throws Exception {
		for (IMonitorServer server : serverList) {
			if (!server.isStarted())
				server.start();
		}
	}

	public void serverShutdown() throws Exception {
		for (IMonitorServer server : serverList)
			server.stop();
	}
	
	public IMonitorService[] getMonitorServiceList() {
		IMonitorService[] serviceArray = new IMonitorService[serverList.size()];
		for (int i = 0; i < serverList.size(); i++)
			serviceArray[i] = serverList.get(i).getMonitorService();
		return serviceArray;
	}

	
	@Override
	public String updateGpSegmentConf(String monitorName, String gpSegmentConfListJson) throws IOException {
		return serverList.get(0).getMonitorService().updateGpSegmentConf(monitorName, gpSegmentConfListJson);
	}

	@Override
	public String updateSystemNow(String monitorName, String systemListJson) throws IOException {
		return serverList.get(0).getMonitorService().updateSystemNow(monitorName, systemListJson);
	}

	@Override
	public String updateSystemHistory(String monitorName, String systemListJson) throws IOException {
		return serverList.get(0).getMonitorService().updateSystemHistory(monitorName, systemListJson);
	}
	
	@Override
	public String requireSystemHistory(String requireConnectionJson) throws IOException {
		return serverList.get(0).getMonitorService().requireSystemHistory(requireConnectionJson);
	}

	@Override
	public String updateDatabaseNow(String monitorName, String databaseListJson) throws IOException {
		return serverList.get(0).getMonitorService().updateDatabaseNow(monitorName, databaseListJson);
	}

	@Override
	public String updateDatabaseHistory(String monitorName, String databaseListJson) throws IOException {
		return serverList.get(0).getMonitorService().updateDatabaseHistory(monitorName, databaseListJson);
	}
	
	@Override
	public String requireDatabaseHistory(String requireConnectionJson) throws IOException {
		return serverList.get(0).getMonitorService().requireDatabaseHistory(requireConnectionJson);
	}

	@Override
	public String updateDiskspaceNow(String monitorName, String diskspaceListJson) throws IOException {
		return serverList.get(0).getMonitorService().updateDiskspaceNow(monitorName, diskspaceListJson);
	}

	@Override
	public String updateDiskspaceHistory(String monitorName, String diskspaceListJson) throws IOException {
		return serverList.get(0).getMonitorService().updateDiskspaceHistory(monitorName, diskspaceListJson);
	}
	
	@Override
	public String requireDiskspaceHistory(String requireConnectionJson) throws IOException {
		return serverList.get(0).getMonitorService().requireDiskspaceHistory(requireConnectionJson);
	}
}
