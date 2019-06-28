<%@ page language="java" import="com.txdb.gpmanage.charts.dataproviders.*" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%
	String monitorName = request.getParameter("monitorName");
	GpSegmentDataProvider dataProvider = GpSegmentDataProvider.getInstance(monitorName);
%>
&label=<%=dataProvider.getDataLabel_segment_health_status()%>&value=<%=dataProvider.getDataValue_segment_health_status()%>
</body>
</html>