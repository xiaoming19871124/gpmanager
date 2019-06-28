package com.txdb.gpmanage.jetty.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;

public class BaseServlet extends HttpServlet {
//	private String label = "&label=10:10:10&value={0}|{1}|{2}|";
	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

		// 测试ajax使用
		httpServletResponse.setContentType("text/plain");
		PrintWriter printerWriter = httpServletResponse.getWriter();
		SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		
		String uname = httpServletRequest.getParameter("uname");
		System.out.println("uname: " + uname);
		
		printerWriter.print("["
				+ "{\"key\":\"First\",\"value\":\"One\"},"
				+ "{\"key\":\"Second\",\"value\":\"Two\"},"
				+ "{\"key\":\"Third\",\"value\":\"Three\"},"
				+ "{\"key\":\"Jetty Version\",\"value\":\"" + Server.getVersion() + "\"},"
				+ "{\"key\":\"Date\",\"value\":\"" + format.format(new Date()) + "\"},"
				+ "{\"key\":\"uname\",\"value\":\"" + uname + "\"}"
				+ "]");
	}
}
