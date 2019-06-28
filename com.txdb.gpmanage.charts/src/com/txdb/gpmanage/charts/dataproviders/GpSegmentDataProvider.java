package com.txdb.gpmanage.charts.dataproviders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.txdb.gpmanage.charts.ChartUtil;
import com.txdb.gpmanage.charts.entries.Color;
import com.txdb.gpmanage.charts.entries.FusionCharts;
import com.txdb.gpmanage.core.utils.JsonUtils;
import com.txdb.gpmanage.gpmon.entries.GpSegmentConf;

public class GpSegmentDataProvider {

	private static Map<String, GpSegmentDataProvider> providerMap = new HashMap<String, GpSegmentDataProvider>();
	
	private String monitorName;
	
	private List<GpSegmentConf> gpSegmentConfList;
	
	// Chart Json Body
	private String chartBody_segment_health_status;
	private String chartBody_segment_health_replication_mode;
	private String chartBody_segment_health_referred_role;
	
	// Chart Colorrange
	private Color[] colorArray_status = new Color[] {
			new Color(-1, -1, "Unknow", "#ECEFED"),
			new Color( 0,  0, "Up"    , "#64E23D"),
			new Color( 1,  1, "Down"  , "#E60002")
	};
	private Color[] colorArray_rep = new Color[] {
			new Color(-1, -1, "Unknow"         , "#ECEFED"),
			new Color( 0,  0, "Synced"         , "#64E23D"),
			new Color( 1,  1, "Not Syncing"    , "#E60002"),
			new Color( 2,  2, "Resyncing"      , "#F2B622"),
			new Color( 3,  3, "Change Tracking", "#577378")
	};
	private Color[] colorArray_pre = new Color[] {
			new Color(-1, -1, "Unknow"       , "#ECEFED"),
			new Color( 0,  0, "Preferred"    , "#64E23D"),
			new Color( 1,  1, "Not Preferred", "#E60002")
	};
	
	// Data
	private int status_down = 0, status_up = 0;
	private int mode_notsync = 0, mode_resync = 0, mode_tracking = 0, mode_synced = 0;
	private int prerole_notpre = 0, prerole_pre = 0;
	
	public static GpSegmentDataProvider getInstance(String monitorName) {
		if (!providerMap.containsKey(monitorName))
			providerMap.put(monitorName, new GpSegmentDataProvider(monitorName));
		return providerMap.get(monitorName);
	}
	
	public GpSegmentDataProvider(String monitorName) {
		this.monitorName = monitorName;
	}
	
	public String getMonitorName() {
		return monitorName;
	}
	
	public void updateGpSegmentConfJson(String gpSegmentConfListJson) {
		gpSegmentConfList = JsonUtils.toCollection(gpSegmentConfListJson, GpSegmentConf.class);
		
		// Build Chart Body
		FusionCharts chart_status = ChartUtil.buildBulb("Status", colorArray_status, "dp/segment_health_status_dp.jsp?monitorName=" + getMonitorName());
		chart_status.setRenderAt("chart-container1");
		chartBody_segment_health_status = ChartUtil.toJsonChart(chart_status);
		
		FusionCharts chart_rep = ChartUtil.buildBulb("Replication Mode", colorArray_rep, "dp/segment_health_replication_mode_dp.jsp?monitorName=" + getMonitorName());
		chart_rep.setRenderAt("chart-container2");
		chartBody_segment_health_replication_mode = ChartUtil.toJsonChart(chart_rep);
		
		FusionCharts chart_pre = ChartUtil.buildBulb("Preferred Role", colorArray_pre, "dp/segment_health_preferred_role_dp.jsp?monitorName=" + getMonitorName());
		chart_pre.setRenderAt("chart-container3");
		chartBody_segment_health_referred_role = ChartUtil.toJsonChart(chart_pre);
		
		// Count up for each state
		status_down = 0; status_up = 0;
		mode_notsync = 0; mode_resync = 0; mode_tracking = 0; mode_synced = 0;
		prerole_notpre = 0; prerole_pre = 0;
		
		for (GpSegmentConf segmentConf : gpSegmentConfList) {
			if (segmentConf == null || segmentConf.getContent() <= -1)
				continue;
			
			// 1. Status: Down, Up
			if (GpSegmentConf.STATUS_UP.equals(segmentConf.getStatus()))
				status_up ++;
			else
				status_down ++;
			
			// 2. Replication Mode: Not Syncing, Resyncing, Change Tracking, Synced
			switch (segmentConf.getMode()) {
			case GpSegmentConf.MODE_NOTSYNC:
				mode_notsync ++;
				break;
			case GpSegmentConf.MODE_RESYNC:
				mode_resync ++;
				break;
			case GpSegmentConf.MODE_TRACKING:
				mode_tracking ++;
				break;
			case GpSegmentConf.MODE_SYNCED:
				mode_synced ++;
				break;
			}
			
			// 3. Preferred Role: Not Preferred, Preferred
			if (segmentConf.getRole().equals(segmentConf.getPreferred_role()))
				prerole_pre ++;
			else
				prerole_notpre ++;
		}
	}

	public List<GpSegmentConf> getGpSegmentConfList() {
		return gpSegmentConfList;
	}

	public void setGpSegmentConfList(List<GpSegmentConf> gpSegmentConfList) {
		this.gpSegmentConfList = gpSegmentConfList;
	}

	public String getChartBody_segment_health_status() {
		return chartBody_segment_health_status;
	}
	public String getDataLabel_segment_health_status() {
		return status_down + " Down<br>" + 
			   status_up + " Up";
	}
	public String getDataValue_segment_health_status() {
		return status_down <= 0 ? "0" : "1";
	}

	public String getChartBody_segment_health_replication_mode() {
		return chartBody_segment_health_replication_mode;
	}
	public String getDataLabel_segment_health_replication_mode() {
		return mode_notsync + " Not Syncing<br>" +
			   mode_resync + " Resyncing<br>" +
			   mode_tracking + " Change Tracking<br>" +
			   mode_synced + " Synced";
	}
	public String getDataValue_segment_health_replication_mode() {
		String flag = "-1";
		if (mode_notsync > 0)
			flag = "1";
		else if (mode_resync > 0)
			flag = "2";
		else if (mode_tracking > 0)
			flag = "3";
		else
			flag = "0";
		return flag;
	}

	public String getChartBody_segment_health_referred_role() {
		return chartBody_segment_health_referred_role;
	}
	public String getDataLabel_segment_health_referred_role() {
		return prerole_notpre + " Not Preferred<br>" +
			   prerole_pre + " Preferred";
	}
	public String getDataValue_segment_health_referred_role() {
		return prerole_notpre <= 0 ? "0" : "1";
	}
}
