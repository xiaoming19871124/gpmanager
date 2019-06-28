<%@ page language="java" import="com.txdb.gpmanage.charts.dataproviders.*" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<html>

<head>
    <title>Cluster Usage Network</title>
    <script type="text/javascript" src="../../js/fusioncharts.js"></script>
    <script type="text/javascript" src="../../js/themes/fusioncharts.theme.ocean.js"></script>
    <script type="text/javascript">
        <%
    		String monitorName = request.getParameter("monitorName");
    		SystemDataProvider dataProvider = SystemDataProvider.getInstance(monitorName);
    	%>
        <%=dataProvider.getChartBody_clusterUsageNetwork()%>
    </script>
</head>

<body>
    <div id="chart-container4">Cluster Usage Network</div>
</body>

</html>