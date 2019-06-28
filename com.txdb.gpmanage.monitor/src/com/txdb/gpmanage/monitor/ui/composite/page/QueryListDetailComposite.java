package com.txdb.gpmanage.monitor.ui.composite.page;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.entry.gpmon.Database;
import com.txdb.gpmanage.core.gp.entry.gpmon.Queries;
import com.txdb.gpmanage.core.gp.service.proxy.GpManageServiceProxy;
import com.txdb.gpmanage.monitor.i18n.MessageConstants;
import com.txdb.gpmanage.monitor.i18n.ResourceHandler;
import com.txdb.gpmanage.monitor.ui.composite.MonitorComposite;
import com.txdb.gpmanage.monitor.ui.composite.MonitorContainer;

public class QueryListDetailComposite extends BaseCompositePage {

	private Calendar calendar = Calendar.getInstance();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdf_ms = new SimpleDateFormat("m'm's's'");
	
	private Label lbl_queries_running;
	private Label lbl_queries_queued;
	private Label lbl_queries_blocked;
	
	private Table table_queryList;
	private Table table_queryDetail;
	
	public QueryListDetailComposite(MonitorComposite mainComposite, MonitorContainer mainContainer, CompositeCode code) {
		super(mainComposite, mainContainer, code, 
				ResourceHandler.getValue(MessageConstants.MONITOR_QUERYMONITOR_LISTDETAILS_TITLE),
				ResourceHandler.getValue(MessageConstants.MONITOR_QUERYMONITOR_LISTDETAILS_SUBTITLE));
	}
	
	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout(1, true));
		
		// 1.0 Operation Area
		Composite operationComp = new Composite(composte, SWT.NONE);
		operationComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		operationComp.setLayout(new GridLayout(10, false));
		
		lbl_queries_running = new Label(operationComp, SWT.NONE);
		lbl_queries_running.setText("● 0 " + ResourceHandler.getValue(MessageConstants.MONITOR_QUERYMONITOR_LISTDETAILS_STATUS_RUNNING) + "\t");
		lbl_queries_running.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
		lbl_queries_running.setAlignment(SWT.RIGHT);
		
		lbl_queries_queued = new Label(operationComp, SWT.NONE);
		lbl_queries_queued.setText("● 0 " + ResourceHandler.getValue(MessageConstants.MONITOR_QUERYMONITOR_LISTDETAILS_STATUS_QUEUED) + "\t");
		lbl_queries_queued.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW));
		lbl_queries_queued.setAlignment(SWT.RIGHT);
		
		lbl_queries_blocked = new Label(operationComp, SWT.NONE);
		lbl_queries_blocked.setText("● 0 " + ResourceHandler.getValue(MessageConstants.MONITOR_QUERYMONITOR_LISTDETAILS_STATUS_BLOCKED));
		lbl_queries_blocked.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		lbl_queries_blocked.setAlignment(SWT.RIGHT);
		
		new Label(operationComp, SWT.NONE).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button btn_cancel = new Button(operationComp, SWT.NONE);
		btn_cancel.setText(ResourceHandler.getValue(MessageConstants.MONITOR_QUERYMONITOR_LISTDETAILS_CANCEL));
		btn_cancel.setEnabled(false);
		GridData gd = new GridData();
		gd.widthHint = 135;
		btn_cancel.setLayoutData(gd);
		
		Button btn_export = new Button(operationComp, SWT.NONE);
		btn_export.setText(ResourceHandler.getValue(MessageConstants.MONITOR_QUERYMONITOR_LISTDETAILS_EXPORT));
		btn_export.setEnabled(false);
		gd = new GridData();
		gd.widthHint = 135;
		btn_export.setLayoutData(gd);
		
		// 2.0 Data Area
		Composite dataComp = new Composite(composte, SWT.NONE);
		dataComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		dataComp.setLayout(new GridLayout(1, true));
		
		Group groupList = new Group(dataComp, SWT.NONE);
		groupList.setText(ResourceHandler.getValue(MessageConstants.MONITOR_QUERYMONITOR_LISTDETAILS_GROUP_LIST));
		groupList.setLayoutData(new GridData(GridData.FILL_BOTH));
		groupList.setLayout(new FillLayout());
		
		table_queryList = new Table(groupList, SWT.FULL_SELECTION);
		table_queryList.setHeaderVisible(true);
		table_queryList.setLinesVisible(true);
		String[] tableHeader = { 
				"Query ID", "Status", "User", "Database", "Res Queue", "Submitted", 
				"Queued Time", "Run Time", "Spill Files", "Blocked by" };
		for (int i = 0; i < tableHeader.length; i++) {
			TableColumn column = new TableColumn(table_queryList, SWT.NONE);
			column.setText(tableHeader[i]);
			column.pack();
		}
		
		Group groupDetails = new Group(dataComp, SWT.NONE);
		groupDetails.setText(ResourceHandler.getValue(MessageConstants.MONITOR_QUERYMONITOR_LISTDETAILS_GROUP_DETAIL));
		groupDetails.setLayoutData(new GridData(GridData.FILL_BOTH));
		groupDetails.setLayout(new FillLayout(SWT.VERTICAL));
		
		table_queryDetail = new Table(groupDetails, SWT.FULL_SELECTION);
		table_queryDetail.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		table_queryDetail.setHeaderVisible(true);
		table_queryDetail.setLinesVisible(true);
		String[] tableDetailHeader = { 
				"Query ID", "Status", "User", "Database", "Submit Time", "Wait Time", "Run Time", 
				"End Time", "CPU Skew", "Row Skew", "Queue", "Priority" };
		for (int i = 0; i < tableDetailHeader.length; i++) {
			TableColumn column = new TableColumn(table_queryDetail, SWT.NONE);
			column.setText(tableDetailHeader[i]);
			column.pack();
		}
		final Text text_queryTxt = new Text(groupDetails, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		text_queryTxt.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		table_queryList.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseDown(MouseEvent e) {
				TableItem[] items = table_queryList.getSelection();
				if (items.length <= 0)
					return;
				
				Queries queries = (Queries) items[0].getData();
				
				long milliseconds_qtime = queries.getTstart().getTime() - queries.getTsubmit().getTime();
				long milliseconds_rtime = queries.getTfinish().getTime() - queries.getTstart().getTime();
				
				calendar.setTimeInMillis(milliseconds_qtime);
				String qtimeStr = sdf_ms.format(calendar.getTime());
				
				calendar.setTimeInMillis(milliseconds_rtime);
				String rtimeStr = sdf_ms.format(calendar.getTime());
				
				table_queryDetail.removeAll();
				TableItem tableItem = new TableItem(table_queryDetail, SWT.NONE);
				tableItem.setText(new String[] { 
						queries.getTmid() + "-" + queries.getSsid() + "-" + queries.getCcnt(),
						queries.getStatus(),
						queries.getUsername(),
						queries.getDb(),
						sdf.format(queries.getTsubmit()),
						qtimeStr.startsWith("0m") ? qtimeStr.substring(2) : qtimeStr,
						rtimeStr.startsWith("0m") ? rtimeStr.substring(2) : rtimeStr,
						sdf.format(queries.getTfinish()),
						String.valueOf(queries.getSkew_cpu()),
						String.valueOf(queries.getSkew_rows()),
						queries.getRsqname(),
						queries.getRqppriority() });
				
				int columnCount = table_queryDetail.getColumnCount();
				for (int i = 0; i < columnCount; i++)
					table_queryDetail.getColumn(i).pack();
				
				text_queryTxt.setText(queries.getQuery_text());
			}
		});
	}
	
	private void fillTableData() {
		GpManageServiceProxy proxy = getContainer().getGpController().getManageServiceProxy();
		
		// Queries Count for running, queued, blocked
		List<Database> databaseList = proxy.queryDatabase();
		if (databaseList != null && databaseList.size() > 0) {
			Database database = databaseList.get(0);
			final String running = "● " + database.getQueries_running() + " " + ResourceHandler.getValue(MessageConstants.MONITOR_QUERYMONITOR_LISTDETAILS_STATUS_RUNNING) + "\t";
			final String queued = "● " + database.getQueries_queued() + " " + ResourceHandler.getValue(MessageConstants.MONITOR_QUERYMONITOR_LISTDETAILS_STATUS_QUEUED) + "\t";
			final String blocked = "● " + (database.getQueries_total() - database.getQueries_running() - database.getQueries_queued()) + " " + ResourceHandler.getValue(MessageConstants.MONITOR_QUERYMONITOR_LISTDETAILS_STATUS_BLOCKED);
			
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					lbl_queries_running.setText(running);
					lbl_queries_queued.setText(queued);
					lbl_queries_blocked.setText(blocked);
				}
			});
		}
		
		// Queries List
		List<Queries> queriesList = proxy.queryQueries();
		System.err.println("################### queriesList: " + queriesList);
		
		// Warning Service
		mainContainer.sendMonitorWarning(queriesList);
		
		if (queriesList == null)
			queriesList = new ArrayList<Queries>();
		
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				table_queryList.removeAll();
			}
		});
		for (final Queries queries : queriesList) {
			long milliseconds_qtime = queries.getTstart().getTime() - queries.getTsubmit().getTime();
			long milliseconds_rtime = queries.getTfinish().getTime() - queries.getTstart().getTime();
			
			calendar.setTimeInMillis(milliseconds_qtime);
			final String qtimeStr = sdf_ms.format(calendar.getTime());
			
			calendar.setTimeInMillis(milliseconds_rtime);
			final String rtimeStr = sdf_ms.format(calendar.getTime());
			
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					TableItem tableItem = new TableItem(table_queryList, SWT.NONE);
					tableItem.setData(queries);
					tableItem.setText(new String[] { 
							queries.getTmid() + "-" + queries.getSsid() + "-" + queries.getCcnt(),
							queries.getStatus(),
							queries.getUsername(),
							queries.getDb(),
							"",
							sdf.format(queries.getTsubmit()),
							qtimeStr.startsWith("0m") ? 
									qtimeStr.substring(2) : qtimeStr, rtimeStr.startsWith("0m") ? rtimeStr.substring(2) : rtimeStr, "", "" });
				}
			});
		}
	}

	@Override
	public void loadData() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void loadStaticData() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("QueryListDetailComposite loadData");
		fillTableData();
	}
}
