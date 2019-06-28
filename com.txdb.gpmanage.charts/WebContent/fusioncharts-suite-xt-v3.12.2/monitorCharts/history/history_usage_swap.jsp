<%@ page language="java" import="com.txdb.gpmanage.charts.dataproviders.*" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<html>

<head>
    <title>History Usage Swap</title>
    <script type="text/javascript" src="../../js/fusioncharts.js"></script>
    <script type="text/javascript">
    	<%
    		String monitorName = request.getParameter("monitorName");
    		SystemDataProvider dataProvider = SystemDataProvider.getInstance(monitorName);
    	%>
        <%=dataProvider.getChartBody_historyUsageSwap()%>
    </script>
</head>

<body>
    <div id="chart-container6">History Usage Swap</div>
</body>

</html>