package com.txdb.gpmanage.core.gp.test;

public abstract class MonitorTaskThread extends Thread {

	private boolean stopMe;

	@Override
	public void run() {
		while (!stopMe)
			taskBody();
	}

	public void stopMe() {
		stopMe = true;
	}
	
	public abstract void taskBody();
}
