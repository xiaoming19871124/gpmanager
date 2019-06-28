<%@ page language="java" import="com.txdb.gpmanage.charts.dataproviders.*" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<html>

<head>
    <title>Segment Health</title>
    <script type="text/javascript" src="../../js/fusioncharts.js"></script>
    <script type="text/javascript" src="../../js/themes/fusioncharts.theme.zune.js"></script>
    <script type="text/javascript">
    	<%
    		String monitorName = request.getParameter("monitorName");
    		GpSegmentDataProvider dataProvider = GpSegmentDataProvider.getInstance(monitorName);
    	%>
        <%=dataProvider.getChartBody_segment_health_status()%>
        <%=dataProvider.getChartBody_segment_health_replication_mode()%>
        <%=dataProvider.getChartBody_segment_health_referred_role()%>
    </script>
</head>

<body>

	<table width="100%" height="100%">
		<tr>
			<td align="center">
				<div id="chart-container1">Segment Health For Status</div>
			</td>
			<td align="center">
				<div id="chart-container2">Segment Health For Replication Mode</div>
			</td>
			<td align="center">
				<div id="chart-container3">Segment Health For Preferred Role</div>
			</td>
		</tr>
	</table>
</body>

</html>