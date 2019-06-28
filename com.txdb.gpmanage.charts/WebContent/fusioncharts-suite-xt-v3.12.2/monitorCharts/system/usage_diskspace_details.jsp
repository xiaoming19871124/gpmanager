<%@ page language="java" import="com.txdb.gpmanage.charts.dataproviders.*" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<html>

<head>
    <title>Diskspace Usage Details</title>
    <script type="text/javascript" src="../../js/fusioncharts.js"></script>
    <script type="text/javascript" src="../../js/themes/fusioncharts.theme.ocean.js"></script>
    <script type="text/javascript">
    	<%
    		String monitorName = request.getParameter("monitorName");
    		DiskspaceDataProvider dataProvider = DiskspaceDataProvider.getInstance(monitorName);
    	%>
        <%=dataProvider.getChartBody_diskspace_details()%>
    </script>
</head>

<body>
	<div id="chart-container">Diskspace Usage Details</div>
</body>

</html>