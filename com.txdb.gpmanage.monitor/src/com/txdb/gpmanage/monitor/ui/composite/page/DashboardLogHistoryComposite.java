package com.txdb.gpmanage.monitor.ui.composite.page;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.entry.gpmon.LogAlert;
import com.txdb.gpmanage.core.gp.service.proxy.GpManageServiceProxy;
import com.txdb.gpmanage.monitor.i18n.MessageConstants;
import com.txdb.gpmanage.monitor.i18n.ResourceHandler;
import com.txdb.gpmanage.monitor.ui.composite.MonitorComposite;
import com.txdb.gpmanage.monitor.ui.composite.MonitorContainer;

public class DashboardLogHistoryComposite extends BaseCompositePage {

	// Controls in Search Area
	private Button btn_panic;
	private Button btn_fatal;
	private Button btn_error;
	private Button btn_warning;
	
	private Text txt_user;
	private Text txt_database;
	private Text txt_host;
	
	private DateTime dateF;
	private DateTime timeF;
	private DateTime dateT;
	private DateTime timeT;
	
	private Button btn_search;
	private Button btn_export;
	
	// Controls in Page Area
	private Link lnk_total;
	private Button btn_first;
	private Button btn_previous;
	private Button btn_next;
	private Button btn_last;
	
	private Link lnk_turnTo;
	private Text txt_pageNo;
	private Label lbl_turnTo;
	
	private List<LogAlert> logAlertList;
	private LogAlert currAlertCondition;
	
	private final int rowCountPerPage = 50;
	private int currPageNum;
	private int totalPageNum;
	
	private Table table;
	
	public DashboardLogHistoryComposite(MonitorComposite mainComposite, MonitorContainer mainContainer, CompositeCode code) {
		super(mainComposite, mainContainer, code, 
				ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_ALERTS_TITLE), 
				ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_ALERTS_SUBTITLE));
	}
	
	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout(1, true));
		currAlertCondition = new LogAlert();
		
		// 1.0 Operation Area
		// Line 1
		Composite operComp = new Composite(composte, SWT.NONE);
		operComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		operComp.setLayout(new GridLayout(11, false));
		
		new Label(operComp, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_ALERTS_FILTER_USER));
		txt_user = new Text(operComp, SWT.BORDER);
		txt_user.setText("gpmon");
		GridData gd = new GridData();
		gd.widthHint = 120;
		txt_user.setLayoutData(gd);
		
		new Label(operComp, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_ALERTS_FILTER_DATABASE));
		txt_database = new Text(operComp, SWT.BORDER);
		txt_database.setText("gpperfmon");
		gd = new GridData();
		gd.widthHint = 120;
		txt_database.setLayoutData(gd);
		
		new Label(operComp, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_ALERTS_FILTER_HOST));
		txt_host = new Text(operComp, SWT.BORDER);
		txt_host.setText("[local]");
		gd = new GridData();
		gd.widthHint = 120;
		txt_host.setLayoutData(gd);
		
		new Label(operComp, SWT.NONE).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btn_panic = createCheckButton(operComp, LogAlert.LOG_PANIC, true);
		btn_fatal = createCheckButton(operComp, LogAlert.LOG_FATAL, true);
		btn_error = createCheckButton(operComp, LogAlert.LOG_ERROR, true);
		btn_warning = createCheckButton(operComp, LogAlert.LOG_WARNING, true);
		
		// Line 2
		Composite line2Comp = new Composite(operComp, SWT.NO_TRIM);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 11;
		line2Comp.setLayoutData(gd);
		GridLayout gridLayout = new GridLayout(9, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		line2Comp.setLayout(gridLayout);
		
		new Label(line2Comp, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_ALERTS_FILTER_DATETIMEFROM));
		dateF = new DateTime(line2Comp, SWT.DATE | SWT.LONG);
		timeF = new DateTime(line2Comp, SWT.TIME | SWT.LONG);
		new Label(line2Comp, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_ALERTS_FILTER_DATETIMETO));
		dateT = new DateTime(line2Comp, SWT.DATE | SWT.LONG);
		timeT = new DateTime(line2Comp, SWT.TIME | SWT.LONG);
		
		new Label(line2Comp, SWT.NONE).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btn_search = new Button(line2Comp, SWT.NONE);
		btn_search.setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_ALERTS_FILTER_SEARCH));
		gd = new GridData();
		gd.widthHint = 135;
		btn_search.setLayoutData(gd);
		
		btn_export = new Button(line2Comp, SWT.NONE);
		btn_export.setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_ALERTS_FILTER_EXPORT));
		btn_export.setEnabled(false);
		gd = new GridData();
		gd.widthHint = 135;
		btn_export.setLayoutData(gd);
		
		// 2.0 Data Area
		Composite dataComp = new Composite(composte, SWT.NO_TRIM);
		dataComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		dataComp.setLayout(new FillLayout());
		
		table = new Table(dataComp, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		final String[] tableHeader = { "Time", "Severity", "Message", "User", "Database", "Host" };
		for (int i = 0; i < tableHeader.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(tableHeader[i]);
			column.setWidth(i == 2 ? 300 : 100);
		}
		
		// 3.0 Page Description
		Composite pageDescComp = new Composite(composte, SWT.NO_TRIM);
		pageDescComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pageDescComp.setLayout(new GridLayout(8, false));
		
		lnk_total = new Link(pageDescComp, SWT.NONE);
		lnk_total.setText(
				ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_ALERTS_PAGE_DESC, new String[] {"N/A", "N/A", "N/A"}));
		lnk_total.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		btn_first = createButton(pageDescComp, ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_ALERTS_PAGE_FIRST));
		btn_previous = createButton(pageDescComp, ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_ALERTS_PAGE_PREVIOUS));
		btn_next = createButton(pageDescComp, ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_ALERTS_PAGE_NEXT));
		btn_last = createButton(pageDescComp, ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_ALERTS_PAGE_LAST));
		
		lnk_turnTo = new Link(pageDescComp, SWT.NONE);
		lnk_turnTo.setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_ALERTS_PAGE_TURNTO_PREFIX));
		txt_pageNo = new Text(pageDescComp, SWT.BORDER);
		gd = new GridData();
		gd.widthHint = 50;
		txt_pageNo.setLayoutData(gd);
		txt_pageNo.setText("0");
		lbl_turnTo = new Label(pageDescComp, SWT.NONE);
		lbl_turnTo.setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_ALERTS_PAGE_TURNTO_SUFFIX));
		
		txt_pageNo.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				int code = (int) e.character;
				if (code == 0)
					return;
				
				// 1.0 排除非数字以及退格的情况
				if ((code < 48 || code > 57) && code != 8) {
					e.doit = false;
					return;
				}
				// 2.0 排除大于当前最大页数的情况
				String fullStr = txt_pageNo.getText() + "" + e.character;
				if (code != 8) {
					int pageNum = Integer.parseInt(fullStr);
					if (pageNum > totalPageNum)
						e.doit = false;
				}
				// 3.0 排除无效的数字0
				String origStr = txt_pageNo.getText();
				if (code == 48 && origStr.length() > 0) {
					if (Integer.parseInt(origStr) <= 0)
						e.doit = false;
				}
			}
		});
		
		updatePageDescription();
		btn_search.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currAlertCondition.setLoguser(txt_user.getText().trim());
				currAlertCondition.setLogdatabase(txt_database.getText().trim());
				currAlertCondition.setLoghost(txt_host.getText().trim());
				
				currAlertCondition.set_logseverity_panic(btn_panic.getSelection());
				currAlertCondition.set_logseverity_fatal(btn_fatal.getSelection());
				currAlertCondition.set_logseverity_error(btn_error.getSelection());
				currAlertCondition.set_logseverity_warning(btn_warning.getSelection());
				
				Calendar calendarFrom = Calendar.getInstance();
				calendarFrom.set(dateF.getYear(), dateF.getMonth(), dateF.getDay(), timeF.getHours(), timeF.getMinutes(), timeF.getSeconds());
				Calendar calendarTo = Calendar.getInstance();
				calendarTo.set(dateT.getYear(), dateT.getMonth(), dateT.getDay(), timeT.getHours(), timeT.getMinutes(), timeT.getSeconds());
				currAlertCondition.set_logtimeFrom(calendarFrom.getTime());
				currAlertCondition.set_logtimeTo(calendarTo.getTime());
				
				searchTableData(1);
			}
		});
		
		SelectionAdapter sa = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.widget == btn_first) {
					searchTableData(1);
					
				} else if (e.widget == btn_previous) {
					searchTableData(currPageNum - 1);
					
				} else if (e.widget == btn_next) {
					searchTableData(currPageNum + 1);
					
				} else if (e.widget == btn_last) {
					searchTableData(totalPageNum);
					
				} else if (e.widget == lnk_turnTo) {
					if (txt_pageNo.getText().length() <= 0)
						txt_pageNo.setText("0");
					int pageNum = Integer.parseInt(txt_pageNo.getText());
					if (pageNum <= 0) {
						MessageDialog.openWarning(getShell(), "Warning", "Page number cannot be 0.");
						return;
					}
					searchTableData(pageNum);
				}
			}
		};
		btn_first.addSelectionListener(sa);
		btn_previous.addSelectionListener(sa);
		btn_next.addSelectionListener(sa);
		btn_last.addSelectionListener(sa);
		lnk_turnTo.addSelectionListener(sa);
	}
	
	private Button createCheckButton(Composite comp, String name, boolean checked) {
		Button checkBtn = new Button(comp, SWT.CHECK);
		checkBtn.setText(name);
		checkBtn.setSelection(checked);
		return checkBtn;
	}
	
	private Button createButton(Composite comp, String name) {
		Button btn = new Button(comp, SWT.NONE);
		btn.setText(name);
		GridData gd = new GridData();
		gd.widthHint = 80;
		btn.setLayoutData(gd);
		return btn;
	}
	
	private void searchTableData(final int pageNum) {
		btn_search.setEnabled(false);
		
		btn_first.setEnabled(false);
		btn_previous.setEnabled(false);
		btn_next.setEnabled(false);
		btn_last.setEnabled(false);
		
		lnk_turnTo.setEnabled(false);
		txt_pageNo.setEnabled(false);
		lbl_turnTo.setEnabled(false);
		
		currAlertCondition.setLimit(rowCountPerPage);
		currAlertCondition.setOffset(pageNum * rowCountPerPage - rowCountPerPage);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				GpManageServiceProxy proxy = getContainer().getGpController().getManageServiceProxy();
				logAlertList = proxy.queryLogAlert(currAlertCondition, true);
				fillTableData();
				
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						int total = currAlertCondition.getTotalCount();
						currPageNum = 0;
						totalPageNum = 0;
						if (total > 0) {
							currPageNum = pageNum;
							totalPageNum = total / rowCountPerPage + (total % rowCountPerPage > 0 ? 1 : 0);
						}
						updatePageDescription();
						btn_search.setEnabled(true);
					}
				});
			}
		}).start();
	}
	
	private void fillTableData() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				table.removeAll();
			}
		});
		if (logAlertList == null || logAlertList.size() <= 0)
			return;
		
		final SimpleDateFormat sdf_short = new SimpleDateFormat("MM-dd HH:mm:ss");
		for (final LogAlert logAlert : logAlertList) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					new TableItem(table, SWT.NONE).setText(
							new String[] { sdf_short.format(
									logAlert.getLogtime()), 
									logAlert.getLogseverity(), 
									logAlert.getLogmessage(), 
									logAlert.getLoguser(), 
									logAlert.getLogdatabase(), 
									logAlert.getLoghost() });
				}
			});
		}
	}
	
	private void updatePageDescription() {
		int total = currAlertCondition.getTotalCount();
		lnk_total.setText(
				ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_ALERTS_PAGE_DESC, new Integer[] {total, currPageNum, totalPageNum}));
		
		btn_first.setEnabled(currPageNum > 1);
		btn_previous.setEnabled(currPageNum > 1);
		btn_next.setEnabled(currPageNum < totalPageNum);
		btn_last.setEnabled(currPageNum < totalPageNum);
		
		lnk_turnTo.setEnabled(totalPageNum > 1);
		txt_pageNo.setEnabled(totalPageNum > 1);
		lbl_turnTo.setEnabled(totalPageNum > 1);
	}
	
	@Override
	public void loadData() {}
	
	@Override
	public void loadStaticData() throws Exception {}
}
