package com.txdb.gpmanage.monitor.ui.composite.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.entry.GPSegmentInfo;
import com.txdb.gpmanage.core.gp.service.proxy.GpManageServiceProxy;
import com.txdb.gpmanage.monitor.i18n.MessageConstants;
import com.txdb.gpmanage.monitor.i18n.ResourceHandler;
import com.txdb.gpmanage.monitor.ui.composite.MonitorComposite;
import com.txdb.gpmanage.monitor.ui.composite.MonitorContainer;

public class SystemSegmentComposite extends BaseCompositePage {

	private Label lbl_v1;
	private Label lbl_v2;
	private Label lbl_v3;
	private Label lbl_v4;
	
	private Browser browser_segment_status;
	private Table table_SegDetail;
	private boolean tablePacked = false;
	
	public SystemSegmentComposite(MonitorComposite mainComposite, MonitorContainer mainContainer, CompositeCode code) {
		super(mainComposite, mainContainer, code, 
				ResourceHandler.getValue(MessageConstants.MONITOR_SYSTEM_SEGMENT_TITLE), 
				ResourceHandler.getValue(MessageConstants.MONITOR_SYSTEM_SEGMENT_SUBTITLE));
	}
	
	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout(2, true));
		Display display = Display.getDefault();
		GridData gd;
		
		// 1.0 Segment Summary
		Composite summaryComp = new Composite(composte, SWT.NONE);
		summaryComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		summaryComp.setLayout(new GridLayout(2, true));
		
		Label summaryTitle = new Label(summaryComp, SWT.NONE);
		summaryTitle.setText(ResourceHandler.getValue(MessageConstants.MONITOR_SYSTEM_SEGMENT_SUMMARY));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		summaryTitle.setLayoutData(gd);
		summaryTitle.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		summaryTitle.setBackground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
		
		Label lbl_DatabaseState = new Label(summaryComp, SWT.BORDER);
		lbl_DatabaseState.setText(" ☀ " + ResourceHandler.getValue(MessageConstants.MONITOR_SYSTEM_SEGMENT_SUMMARY_STATE));
		lbl_DatabaseState.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lbl_DatabaseState.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		Label lbl_TotalSegments = new Label(summaryComp, SWT.BORDER);
		lbl_TotalSegments.setText(" ☀ " + ResourceHandler.getValue(MessageConstants.MONITOR_SYSTEM_SEGMENT_SUMMARY_TOTALSEGMENT));
		lbl_TotalSegments.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lbl_TotalSegments.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		lbl_v1 = new Label(summaryComp, SWT.NONE);
		lbl_v1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lbl_v1.setAlignment(SWT.CENTER);
		
		lbl_v2 = new Label(summaryComp, SWT.NONE);
		lbl_v2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lbl_v2.setAlignment(SWT.CENTER);
		
		Label lbl_MirrorsPrimary = new Label(summaryComp, SWT.BORDER);
		lbl_MirrorsPrimary.setText(" ☀ " + ResourceHandler.getValue(MessageConstants.MONITOR_SYSTEM_SEGMENT_SUMMARY_MIRRORASPRIMARY));
		lbl_MirrorsPrimary.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lbl_MirrorsPrimary.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		Label lbl_SegmentHosts = new Label(summaryComp, SWT.BORDER);
		lbl_SegmentHosts.setText(" ☀ " + ResourceHandler.getValue(MessageConstants.MONITOR_SYSTEM_SEGMENT_SUMMARY_SEGMENTHOSTS));
		lbl_SegmentHosts.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lbl_SegmentHosts.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		lbl_v3 = new Label(summaryComp, SWT.NONE);
		lbl_v3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lbl_v3.setAlignment(SWT.CENTER);
		
		lbl_v4 = new Label(summaryComp, SWT.NONE);
		lbl_v4.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lbl_v4.setAlignment(SWT.CENTER);
		
		Label lbl_recommended = new Label(summaryComp, SWT.BORDER);
		lbl_recommended.setText(" ☀ " + ResourceHandler.getValue(MessageConstants.MONITOR_SYSTEM_SEGMENT_SUMMARY_RECOMMENDED));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		lbl_recommended.setLayoutData(gd);
		lbl_recommended.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		final Text txt_v5 = new Text(summaryComp, SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		txt_v5.setLayoutData(gd);
		txt_v5.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		lbl_v1.setText("[ Unknow ]");
		lbl_v1.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
		lbl_v2.setText("[ 0 ]");
		lbl_v3.setText("[ 0 ]");
		lbl_v4.setText("[ 0 ]");
		txt_v5.setText("None");
		
		
		// 2.0 Segment Health
		Composite healthComp = new Composite(composte, SWT.NONE);
		healthComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		healthComp.setLayout(new GridLayout(1, true));
		
		Label healthTitle = new Label(healthComp, SWT.NONE);
		healthTitle.setText(ResourceHandler.getValue(MessageConstants.MONITOR_SYSTEM_SEGMENT_HEALTH));
		healthTitle.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		healthTitle.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		healthTitle.setBackground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
		
		browser_segment_status = new Browser(healthComp, SWT.BORDER);
		browser_segment_status.setLayoutData(new GridData(GridData.FILL_BOTH));
		browser_segment_status.setMenu(new Menu(browser_segment_status));
		
		
		// 3.0 Segment Details
		Composite detailComp = new Composite(composte, SWT.NONE);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.heightHint = 250;
		detailComp.setLayoutData(gd);
		detailComp.setLayout(new GridLayout(1, true));
		
		Label detailsTitle = new Label(detailComp, SWT.NONE);
		detailsTitle.setText(ResourceHandler.getValue(MessageConstants.MONITOR_SYSTEM_SEGMENT_DETAILS));
		detailsTitle.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		detailsTitle.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		detailsTitle.setBackground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
		
		table_SegDetail = new Table(detailComp, SWT.BORDER | SWT.FULL_SELECTION);
		table_SegDetail.setLayoutData(new GridData(GridData.FILL_BOTH));
		table_SegDetail.setHeaderVisible(true);
		table_SegDetail.setLinesVisible(true);

		String[] header_SegDetail = new String[] { "Hostname", "Address",
				"Port", "DBID", "Content ID", "Status", "Role",
				"Preferred Role", "Replication Mode", "Data Directory" };
		for (int i = 0; i < header_SegDetail.length; i++) {
			TableColumn column = new TableColumn(table_SegDetail, SWT.NONE);
			column.setText(header_SegDetail[i]);
			column.pack();
		}
	}
	
	private void fillData() {
		int status_down = 0, status_up = 0;
		int mode_notsync = 0, mode_resync = 0, mode_tracking = 0, mode_synced = 0;
		int prerole_notpre = 0, prerole_pre = 0;
		
		Map<String, Boolean> host_mapMark = new HashMap<String, Boolean>();
		int host_count = 0;
		
		// Segment Detail
		GpManageServiceProxy proxy = getContainer().getGpController().getManageServiceProxy();
		List<GPSegmentInfo> segmentList = proxy.queryGPSegmentInfo();
		if (segmentList == null)
			segmentList = new ArrayList<GPSegmentInfo>();
		
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				table_SegDetail.removeAll();
			}
		});
		for (final GPSegmentInfo segment : segmentList) {
			if (segment.getContent() <= -1)
				continue;
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					new TableItem(table_SegDetail, SWT.NONE).setText(new String[] { 
							segment.getHostname(),
							segment.getAddress(),
							String.valueOf(segment.getPort()),
							String.valueOf(segment.getDbid()),
							String.valueOf(segment.getContent()),
							segment.getStatus(), 
							segment.getRole(),
							segment.getPreferred_role(),
							segment.getMode(), segment.getDatadir() });
				}
			});
			
			// Count up for each state
			// 1. Status: Down, Up
			if (GPSegmentInfo.STATUS_UP.equals(segment.getStatus()))
				status_up ++;
			else
				status_down ++;
			
			// 2. Replication Mode: Not Syncing, Resyncing, Change Tracking, Synced
			switch (segment.getMode()) {
			case GPSegmentInfo.MODE_NOTSYNC:
				mode_notsync ++;
				break;
			case GPSegmentInfo.MODE_RESYNC:
				mode_resync ++;
				break;
			case GPSegmentInfo.MODE_TRACKING:
				mode_tracking ++;
				break;
			case GPSegmentInfo.MODE_SYNCED:
				mode_synced ++;
				break;
			}
			
			// 3. Preferred Role: Not Preferred, Preferred
			if (segment.getRole().equals(segment.getPreferred_role()))
				prerole_pre ++;
			else
				prerole_notpre ++;
			
			if (!host_mapMark.containsKey(segment.getHostname())) {
				host_mapMark.put(segment.getHostname(), true);
				host_count ++;
			}
		}
		if (!tablePacked) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					int columnCount = table_SegDetail.getColumnCount();
					for (int i = 0; i < columnCount; i++)
						table_SegDetail.getColumn(i).pack();
				}
			});
			tablePacked = true;
		}
		
		// Segment Summary
		String statusKeyWord = "Normal";
		int colorId = SWT.COLOR_GREEN;
		if (status_down > 0) {
			statusKeyWord = "Down";
			colorId = SWT.COLOR_RED;
		} else if (mode_synced < status_up) {
			colorId = SWT.COLOR_YELLOW;
			if (mode_notsync > 0)
				statusKeyWord = "Not Syncing";
			else if (mode_resync > 0)
				statusKeyWord = "Resyncing";
			else if (mode_tracking > 0)
				statusKeyWord = "Change Tracking";
		}
		
		final int temp_status_all = status_up + status_down;
		final int temp_prerole_notpre = prerole_notpre;
		final int temp_host_count = host_count;
		final String temp_statusKeyWord = statusKeyWord;
		final int temp_colorId = colorId;
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				lbl_v2.setText("[ " + temp_status_all + " ]");
				lbl_v3.setText("[ " + (temp_prerole_notpre / 2) + " ]");
				lbl_v4.setText("[ " + temp_host_count + " ]");
				
				lbl_v1.setText("[ " + temp_statusKeyWord + " ]");
				lbl_v1.setForeground(Display.getCurrent().getSystemColor(temp_colorId));
			}
		});
		
//		// Segment Health
//		lbl_status_down.setText(status_down + " Down");
//		lbl_status_up.setText(status_up + " Up");
//		
//		lbl_mode_notsync.setText(mode_notsync + " Not Syncing");
//		lbl_mode_resync.setText(mode_resync + " Resyncing");
//		lbl_mode_tracking.setText(mode_tracking + " Change Tracking");
//		lbl_mode_synced.setText(mode_synced + " Synced");
//		
//		lbl_role_notpre.setText(prerole_notpre + " Not Preferred");
//		lbl_role_pre.setText(prerole_pre + " Preferred");
	}
	
	@Override
	public void loadData() {
		// TODO Auto-generated method stub
	}

	@Override
	public void loadStaticData() {
		// TODO Auto-generated method stub
		System.out.println("SystemSegmentComposite loadData");
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				String inited = (String) browser_segment_status.getData("inited");
				if (!"true".equals(inited)) {
					browser_segment_status.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/system/segment_health.jsp?monitorName=" + getContainer().getMonitorName());
					browser_segment_status.setData("inited", "true");
				}
			}
		});
		fillData();
	}
}
