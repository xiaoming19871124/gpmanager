package com.txdb.gpmanage.core.gp.dao;

public class JSchExecuteDaoImpl extends BaseExecuteDao {

	public JSchExecuteDaoImpl(String host) { 
		super(host);
	}
	
	public JSchExecuteDaoImpl(String host, String username, String password) { 
		super(host, username, password);
	}
	
	public JSchExecuteDaoImpl(String host, String username, String password, int sshPort) { 
		super(host, username, password, sshPort);
	}
}
