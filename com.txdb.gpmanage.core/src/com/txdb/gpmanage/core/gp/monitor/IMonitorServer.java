package com.txdb.gpmanage.core.gp.monitor;

import java.io.IOException;

public interface IMonitorServer {

	public static final int DEFAULT_PORT = 9681;

	void init() throws IOException;
	void init(int port) throws IOException;

	void start() throws Exception;
	void stop() throws Exception;

	boolean isStarted();
	boolean isStopped();

	IMonitorService getMonitorService();
}
