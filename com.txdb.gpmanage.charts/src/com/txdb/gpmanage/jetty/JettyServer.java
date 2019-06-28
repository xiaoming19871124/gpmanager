package com.txdb.gpmanage.jetty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import com.txdb.gpmanage.charts.Activator;
import com.txdb.gpmanage.core.gp.monitor.IMonitorServer;
import com.txdb.gpmanage.core.gp.monitor.IMonitorService;
import com.txdb.gpmanage.jetty.service.MonitorServiceClient;

public class JettyServer implements IMonitorServer {

	// http://localhost:9681/services/listServices
	public static final String PLUGIN_ID = "com.txdb.gpmanage.charts";

	protected Server server;

	private final String contextPath = "/";
	private final String resourceBase = "WebContent";

	public JettyServer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() throws IOException {
		init(DEFAULT_PORT);
	}

	@Override
	public void init(int port) throws IOException {
		if (server == null) {
			// Add Web Server
			server = new Server(new InetSocketAddress(port));

			// Config WebContext
			WebAppContext webAppContext = new WebAppContext();
			webAppContext.setContextPath(contextPath);
			
			// Config File Scan Path
			URL url = Activator.getDefault().getBundle().getEntry(resourceBase);
			String absPath = FileLocator.toFileURL(url).getPath();
			System.out.println(absPath);
			
			webAppContext.setResourceBase(absPath);
			webAppContext.setConfigurationDiscovered(true);
			webAppContext.setParentLoaderPriority(true);
			server.setHandler(webAppContext);
		}
	}

	@Override
	public void start() throws Exception {
		if (!isStarted())
			server.start();
	}

	@Override
	public void stop() throws Exception {
		server.stop();
		server.join();
	}

	@Override
	public boolean isStarted() {
		return server.isStarted();
	}

	@Override
	public boolean isStopped() {
		return server.isStopped();
	}

	@Override
	public IMonitorService getMonitorService() {
		return MonitorServiceClient.getInstance();
	}
}
