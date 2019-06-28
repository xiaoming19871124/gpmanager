package com.txdb.gpmanage.jetty.test;

import java.io.IOException;

import org.eclipse.jetty.server.Server;

public abstract class LocalJettyServer {
	
	public static final int DEFAULT_PORT = 9681;
	protected Server server;
	
	public static LocalJettyServer getInstance() throws IOException {
		return getInstance(DEFAULT_PORT);
	}
	
	public static LocalJettyServer getInstance(int port) throws IOException {
		return new LocalJettyServerImpl(port);
	}
	
	abstract void start() throws Exception;
	abstract void stop() throws Exception;
	abstract boolean isStarted();
	abstract boolean isStopped();
	
	public static void main(String[] args) {
		try {
			// http://localhost:9681/services/listServices
			LocalJettyServer jettyServer = LocalJettyServer.getInstance();
			jettyServer.start();
			
			// http://localhost:9681/fusioncharts-suite-xt/fusionWidgets/realtimeline3.html
			// http://127.0.0.1:9681/fusioncharts-suite-xt/fusionWidgets/realtimeline.html
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
