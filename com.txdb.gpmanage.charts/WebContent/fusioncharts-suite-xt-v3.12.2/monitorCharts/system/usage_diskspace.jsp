<%@ page language="java" import="com.txdb.gpmanage.charts.dataproviders.*" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<html>

<head>
    <title>GP Master/Segments Usage Summary</title>
    <script type="text/javascript" src="../../js/fusioncharts.js"></script>
    <script type="text/javascript" src="../../js/themes/fusioncharts.theme.zune.js"></script>
    <script type="text/javascript">
    	<%
    		String monitorName = request.getParameter("monitorName");
    		DiskspaceDataProvider dataProvider = DiskspaceDataProvider.getInstance(monitorName);
    	%>
        <%=dataProvider.getChartBody_diskspace_master()%>
        <%=dataProvider.getChartBody_diskspace_segments()%>
    </script>
</head>

<body>
	<table width="100%" height="100%">
		<tr>
			<td align="center">
				<div id="chart-container1">GP Master</div>
			</td>
			<td align="center">
				<div id="chart-container2">GP Segments</div>
			</td>
		</tr>
	</table>
</body>

</html>