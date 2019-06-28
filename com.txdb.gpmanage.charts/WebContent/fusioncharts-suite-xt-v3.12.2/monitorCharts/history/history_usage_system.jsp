<%@ page language="java" import="com.txdb.gpmanage.charts.dataproviders.*" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<html>

<head>
    <title>History Usage System</title>
    <script type="text/javascript" src="../../js/fusioncharts.js"></script>
    <script type="text/javascript">
    	<%
    		String monitorName = request.getParameter("monitorName");
    		SystemDataProvider dataProvider = SystemDataProvider.getInstance(monitorName);
    	%>
        <%=dataProvider.getChartBody_historyUsageCpu()%>
        <%=dataProvider.getChartBody_historyUsageMemory()%>
        <%=dataProvider.getChartBody_historyUsageDiskIO()%>
        <%=dataProvider.getChartBody_historyUsageNetwork()%>
        <%=dataProvider.getChartBody_historyUsageLoad()%>
        <%=dataProvider.getChartBody_historyUsageSwap()%>
    </script>
</head>

<body>
	<HR style="border:3 double #987cb9" color=#987cb9 SIZE=3>
    <div id="chart-container1">History Usage CPU</div>
    <HR style="border:3 double #987cb9" color=#987cb9 SIZE=3>
    <div id="chart-container2">History Usage Memory</div>
    <HR style="border:3 double #987cb9" color=#987cb9 SIZE=3>
    <div id="chart-container3">History Usage Disk I/O</div>
    <HR style="border:3 double #987cb9" color=#987cb9 SIZE=3>
    <div id="chart-container4">History Usage Network</div>
    <HR style="border:3 double #987cb9" color=#987cb9 SIZE=3>
    <div id="chart-container5">History Usage Load</div>
    <HR style="border:3 double #987cb9" color=#987cb9 SIZE=3>
    <div id="chart-container6">History Usage Swap</div>
    <HR style="border:3 double #987cb9" color=#987cb9 SIZE=3>
</body>

</html>