//package com.txdb.gpmanage.core.gp.dao;
//
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//
//import com.jcraft.jsch.JSchException;
//
//public class RuntimeExecuteDaoImpl extends BaseExecuteDao {
//
//	private Runtime runTime;
//	
//	public RuntimeExecuteDaoImpl() {
//		runTime = Runtime.getRuntime();
//		if (runTime == null) {
//			System.err.println("<Create runtime environment failed>");
//		}
//	}
//	
//	@Override
//	public String getHost() {
//		return "localhost";
//	}
//	
//	@Override
//	public String getUserName() {
//		return null;
//	}
//	
//	@Override
//	public boolean login() {
//		// TODO Auto-generated method stub
//		return true;
//	}
//	
//	@Override
//	public void logout() {
//		// TODO Auto-generated method stub
//	}
//	
//	@Override
//	public boolean isConnected() {
//		return false;
//	}
//	
//	@Override
//	public String executeCommand(String command) {
//		String resultString = "";
//		try {
//			Process process = runTime.exec(command);
//			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
//			System.out.println("<Local command is: " + command + ">");
//			
//			String line;
//			while ((line = input.readLine()) != null)
//				resultString += line + "\n";
//			
//			input.close();
//			process.destroy();
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return resultString;
//	}
//	
//	@Override
//	public boolean executeCommandExists(String pathType, String filePath, String message) throws JSchException, IOException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//	
//	@Override
//	public boolean upload(String directory, String sftpFileName, InputStream input) throws JSchException {
//		// TODO Auto-generated method stub
//		// cp或者mv
//		return false;
//	}
//
//	@Override
//	public boolean upload(String directory, String uploadFile) throws FileNotFoundException, JSchException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//	
//	public static void main(String[] args) {
//		IExecuteDao dao = new RuntimeExecuteDaoImpl();
//		
//		String resultString = null;
//		try {
//			resultString = dao.executeCommand("lsb_release -a");
//			
//		} catch (JSchException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(resultString);
//	}
//}
