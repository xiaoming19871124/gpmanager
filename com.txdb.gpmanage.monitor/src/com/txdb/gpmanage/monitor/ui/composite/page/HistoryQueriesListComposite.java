package com.txdb.gpmanage.monitor.ui.composite.page;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.connector.IGPConnector;
import com.txdb.gpmanage.core.gp.entry.gpmon.Queries;
import com.txdb.gpmanage.core.gp.service.proxy.GpManageServiceProxy;
import com.txdb.gpmanage.monitor.i18n.MessageConstants;
import com.txdb.gpmanage.monitor.i18n.ResourceHandler;
import com.txdb.gpmanage.monitor.ui.composite.MonitorComposite;
import com.txdb.gpmanage.monitor.ui.composite.MonitorContainer;

public class HistoryQueriesListComposite extends BaseCompositePage {

	private Calendar calendar = Calendar.getInstance();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdf_ms = new SimpleDateFormat("m'm's's'");
	
	private Table table_queryList;
	private Table table_queryDetail;
	
	public HistoryQueriesListComposite(MonitorComposite mainComposite, MonitorContainer mainContainer, CompositeCode code) {
		super(mainComposite, mainContainer, code, 
				ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_QUERIESLIST_TITLE), 
				ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_QUERIESLIST_SUBTITLE));
	}
	
	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout(1, true));
		
		// 1.0 Operation Area
		Composite operComp = new Composite(composte, SWT.NONE);
		operComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		operComp.setLayout(gridLayout);
		
		Button btn_search = new Button(operComp, SWT.NONE);
		btn_search.setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_QUERIESLIST_FILTER_SEARCH));
		GridData gd = new GridData();
		gd.widthHint = 135;
		btn_search.setLayoutData(gd);
		
		Composite comp_coditions = new Composite(operComp, SWT.NONE);
		gridLayout = new GridLayout(11, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		comp_coditions.setLayout(gridLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 11;
		comp_coditions.setLayoutData(gd);
		
		new Label(comp_coditions, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_QUERIESLIST_FILTER_DATETIMEFROM));
		final DateTime dateF = new DateTime(comp_coditions, SWT.DATE | SWT.LONG);
		final DateTime timeF = new DateTime(comp_coditions, SWT.TIME | SWT.LONG);
		new Label(comp_coditions, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_QUERIESLIST_FILTER_DATETIMETO));
		final DateTime dateT = new DateTime(comp_coditions, SWT.DATE | SWT.LONG);
		final DateTime timeT = new DateTime(comp_coditions, SWT.TIME | SWT.LONG);
		
		Label fillLabel = new Label(comp_coditions, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fillLabel.setLayoutData(gd);
		
		new Label(comp_coditions, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_QUERIESLIST_FILTER_DATABASE));
		final Text txt_db = new Text(comp_coditions, SWT.BORDER);
		txt_db.setText("gpperfmon");
		gd = new GridData();
		gd.widthHint = 120;
		txt_db.setLayoutData(gd);
		
		new Label(comp_coditions, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_QUERIESLIST_FILTER_USER));
		final Text txt_user = new Text(comp_coditions, SWT.BORDER);
		txt_user.setText("gpmon");
		gd = new GridData();
		gd.widthHint = 120;
		txt_user.setLayoutData(gd);
		
		// 2.0 Data Area
		Composite dataComp = new Composite(composte, SWT.NONE);
		dataComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		dataComp.setLayout(new GridLayout(1, true));
		
		Group groupList = new Group(dataComp, SWT.NONE);
		groupList.setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_QUERIESLIST_GROUP_LIST));
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
		groupDetails.setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_QUERIESLIST_GROUP_DETAILS));
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
		
		btn_search.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				Calendar calendarF = Calendar.getInstance();
				calendarF.set(dateF.getYear(), dateF.getMonth(), dateF.getDay(), timeF.getHours(), timeF.getMinutes(), timeF.getSeconds());
				Calendar calendarT = Calendar.getInstance();
				calendarT.set(dateT.getYear(), dateT.getMonth(), dateT.getDay(), timeT.getHours(), timeT.getMinutes(), timeT.getSeconds());
				
				fillTableData(calendarF.getTime(), calendarT.getTime(), txt_user.getText(), txt_db.getText());
			}
		});
		
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
	
	private void fillTableData(Date from, Date to, String username, String db) {
		IGPConnector controller = getContainer().getGpController();
		if (controller == null) {
			MessageDialog.openWarning(getShell(), "Tip", "Need connect monitor server first!");
			return;
		}
		GpManageServiceProxy proxy = controller.getManageServiceProxy();
		
		// Queries List
		List<Queries> queriesList = proxy.queryQueries(from, to, true, 
				username.trim().equals("") ? null : username, 
				db.trim().equals("") ? null : db);
		table_queryList.removeAll();
		for (Queries queries : queriesList) {
			long milliseconds_qtime = queries.getTstart().getTime() - queries.getTsubmit().getTime();
			long milliseconds_rtime = queries.getTfinish().getTime() - queries.getTstart().getTime();
			
			calendar.setTimeInMillis(milliseconds_qtime);
			String qtimeStr = sdf_ms.format(calendar.getTime());
			
			calendar.setTimeInMillis(milliseconds_rtime);
			String rtimeStr = sdf_ms.format(calendar.getTime());
			
			TableItem tableItem = new TableItem(table_queryList, SWT.NONE);
			tableItem.setData(queries);
			tableItem.setText(new String[] {
					queries.getTmid() + "-" + queries.getSsid() + "-" + queries.getCcnt(),
					queries.getStatus(),
					queries.getUsername(),
					queries.getDb(),
					"",
					sdf.format(queries.getTsubmit()),
					qtimeStr.startsWith("0m") ? qtimeStr.substring(2) : qtimeStr,
					rtimeStr.startsWith("0m") ? rtimeStr.substring(2) : rtimeStr,
					"",
					"" });
		}
		int columnCount = table_queryList.getColumnCount();
		for (int i = 0; i < columnCount; i++)
			table_queryList.getColumn(i).pack();
	}
	
	@Override
	public void loadData() {
		// TODO Auto-generated method stub
		System.out.println("HistoryQueriesListComposite loadData");
	}
}
