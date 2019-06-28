<%@ page language="java" import="com.txdb.gpmanage.charts.dataproviders.*" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<html>

<head>
    <title>Queries History</title>
    <script type="text/javascript" src="../../js/fusioncharts.js"></script>
    <script type="text/javascript">
    	<%
    		String monitorName = request.getParameter("monitorName");
    		DatabaseDataProvider dataProvider = DatabaseDataProvider.getInstance(monitorName);
    	%>
        <%=dataProvider.getChartBody_historyUsageQueries()%>
    </script>
</head>

<body>
    <div id="chart-container">Queries History</div>
</body>

</html>