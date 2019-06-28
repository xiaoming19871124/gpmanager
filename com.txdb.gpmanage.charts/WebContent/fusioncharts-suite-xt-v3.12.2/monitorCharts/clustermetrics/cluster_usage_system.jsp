<%@ page language="java" import="com.txdb.gpmanage.charts.dataproviders.*" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<html>

<head>
    <title>Cluster Usage System</title>
    <script type="text/javascript" src="../../js/fusioncharts.js"></script>
    <script type="text/javascript" src="../../js/themes/fusioncharts.theme.ocean.js"></script>
    <script type="text/javascript">
    	<%
    		String monitorName = request.getParameter("monitorName");
    		SystemDataProvider dataProvider = SystemDataProvider.getInstance(monitorName);
    	%>
        <%=dataProvider.getChartBody_clusterUsageCpu()%>
        <%=dataProvider.getChartBody_clusterUsageMemory()%>
        <%=dataProvider.getChartBody_clusterUsageDiskIO()%>
        <%=dataProvider.getChartBody_clusterUsageNetwork()%>
        <%=dataProvider.getChartBody_clusterUsageLoad()%>
        <%=dataProvider.getChartBody_clusterUsageSwap()%>
    </script>
</head>

<body>
	<hr style="height:3px;border:none;border-top:3px double skyblue;" />
	<div id="chart-container1">CPU</div>
	<hr style="height:3px;border:none;border-top:3px double skyblue;" />
	<div id="chart-container2">Memory</div>
	<hr style="height:3px;border:none;border-top:3px double skyblue;" />
	<div id="chart-container3">Diso I/O</div>
	<hr style="height:3px;border:none;border-top:3px double skyblue;" />
	<div id="chart-container4">Network I/O</div>
	<hr style="height:3px;border:none;border-top:3px double skyblue;" />
	<div id="chart-container5">Load</div>
	<hr style="height:3px;border:none;border-top:3px double skyblue;" />
	<div id="chart-container6">Swap</div>
	<hr style="height:3px;border:none;border-top:3px double skyblue;" />
</body>

</html>