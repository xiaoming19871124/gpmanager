package com.txdb.gpmanage.jetty.test;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class LocalJettyServerImpl extends LocalJettyServer {

	public LocalJettyServerImpl(int port) throws IOException {
		if (server == null) {
			// Add Web Server
			server = new Server(new InetSocketAddress(port));
			
			// Config WebContext
			WebAppContext webAppContext = new WebAppContext();
			webAppContext.setContextPath("/");
			
			// Config File Scan Path
			File resDir = new File("./WebContent");
			webAppContext.setResourceBase(resDir.getCanonicalPath());
			webAppContext.setConfigurationDiscovered(true);
			webAppContext.setParentLoaderPriority(true);
			server.setHandler(webAppContext);
		}
	}
	
	@Override
	void start() throws Exception {
		server.start();
	}

	@Override
	void stop() throws Exception {
		server.stop();
		server.join();
	}

	@Override
	boolean isStarted() {
		return server.isStarted();
	}

	@Override
	boolean isStopped() {
		return server.isStopped();
	}

}
