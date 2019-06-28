package com.txdb.gpmanage.audit.ui.composite.page;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.audit.constant.AuditConstant;
import com.txdb.gpmanage.audit.i18n.MessageConstants;
import com.txdb.gpmanage.audit.i18n.ResourceHandler;
import com.txdb.gpmanage.audit.ui.composite.AuditComposite;
import com.txdb.gpmanage.audit.ui.composite.AuditContainer;
import com.txdb.gpmanage.core.entity.AuditEntity;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.connector.UICallBack;
import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.service.proxy.GpEnvServiceProxy;
import com.txdb.gpmanage.core.gp.service.proxy.GpManageServiceProxy;

public class AuditLogComposite extends BaseCompositePage implements UICallBack {

	private Text txt_command;
	private String[] commandFragments;
	
	private ProgressBar progressBar;
	private Button btn_search;
	private Button btn_export;
	
	private TabFolder tabFolder;
	private Text txt_content;
	private Table table_content;
	
	// Find Area
	private Button btn_find;
	
	// Controls in Page Area
	private Link lnk_total;
	private Button btn_first;
	private Button btn_previous;
	private Button btn_next;
	private Button btn_last;
	
	private Link lnk_turnTo;
	private Text txt_pageNo;
	private Label lbl_turnTo;
	
//	private static String pageTextInfo = "Total of <A>%s</A> pieces of data are found, current page <A>%s/%s</A>.";
	private final int rowCountPerPage = 50;
	private int currPageNum;
	private int totalPageNum;
	private boolean tableFormatted;
	
	private List<AuditEntity> auditInfoList;
	private AuditEntity currAuditCondition;
	
	// Save for query from a target table name.
	private String currentSerialNo;
	
	private String uploadFile;
	
	public AuditLogComposite(AuditComposite mainComposite, AuditContainer mainContainer, CompositeCode code) {
		super(mainComposite, mainContainer, code, "Audit Log", "from pg_log");
	}
	
	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout(1, true));
		currAuditCondition = new AuditEntity();
		
		// 1.0 Operation Area
		Composite operationComp = new Composite(composte, SWT.NONE);
		operationComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		operationComp.setLayout(gridLayout);
		
		txt_command = new Text(operationComp, SWT.NONE);
		txt_command.setEditable(false);
		txt_command.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		txt_command.setLayoutData(gd);
		txt_command.setText("gplogfilter [<timestamp_options>] [<pattern_options>] [<output_options>] [<input_options>] [<input_file>]");
		
		progressBar = new ProgressBar(operationComp, SWT.HORIZONTAL | SWT.INDETERMINATE);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		progressBar.setVisible(false);
		
		btn_search = new Button(operationComp, SWT.NONE);
		
		btn_search.setText(ResourceHandler.getValue(MessageConstants.AUDIT_LOG_SEARCH));
		gd = new GridData();
		gd.widthHint = 135;
		btn_search.setLayoutData(gd);
		
		btn_export = new Button(operationComp, SWT.NONE);
		btn_export.setText(ResourceHandler.getValue(MessageConstants.AUDIT_LOG_EXPORT));
		btn_export.setEnabled(false);
		gd = new GridData();
		gd.widthHint = 135;
		btn_export.setLayoutData(gd);
		
		btn_search.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currAuditCondition = new AuditEntity();
				commandFragments = txt_command.getText().split(AT_FLAG);
				Thread taskThread = new Thread() {
					@Override
					public void run() {
						boolean keepRunning = true;
						
						// 1.0 Forbid operations during process running.
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								table_content.removeAll();
								btn_find.setEnabled(false);
								
								btn_search.setEnabled(false);
								btn_export.setEnabled(false);
								txt_content.setText("");
								progressBar.setVisible(true);
								tabFolder.setSelection(0);
							}
						});
						
						// 2.0 Prepare Search
						IExecuteDao dao = getContainer().getGpController().getDao();
						dao.setCallback(AuditLogComposite.this);
						GpManageServiceProxy manage_proxy = getContainer().getGpController().getManageServiceProxy();
						GpEnvServiceProxy env_proxy = getContainer().getGpController().getEnvServiceProxy();
						String filterCommand = commandFragments.length >= 2 ? commandFragments[1] : commandFragments[0];
						
						currentSerialNo = generateSerialNo();
						String logFileName = generateLogFileName(currentSerialNo, commandFragments.length < 2);
						filterCommand = filterCommand.replace(KEYWORD_TEMPNAME, logFileName);
						String extraCommand = commandFragments.length >= 2 ? commandFragments[0] : null;
						
						// 2.1 Upload Local File
						if (filterCommand.contains(KEYWORD_UPLOAD)) {
							String uploadPath = manage_proxy.localPath(true) + PGLOG_UPLOAD_DIR;
							uploadFile = getContainer().getLocalFileWithDelimiter();
							String[] uploadFileFragments = uploadFile.split(AT_FLAG);
							keepRunning = env_proxy.uploadFile(uploadPath, uploadFileFragments[0]);
							
							if (!keepRunning)
								MessageDialog.openWarning(getShell(),
											ResourceHandler.getValue(MessageConstants.AUDIT_DIALOG_TITLE_INFO),
											ResourceHandler.getValue(MessageConstants.AUDIT_DIALOG_MESSAGE_UPLOAD_FAILED, new String[] { uploadFileFragments[0] }));
							filterCommand = filterCommand.replaceAll(KEYWORD_UPLOAD + "\\\\", uploadPath);
						}
						
						// 2.2 Do Search
						filterCommand = filterCommand.contains(" -n ") ? filterCommand : filterCommand + "-n 50";
						if (keepRunning)
							manage_proxy.gpLogFilter(filterCommand, extraCommand);
						
						// 3.0 Restore
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								btn_search.setEnabled(true);
								progressBar.setVisible(false);
								
								if (txt_command.getText().contains(" -o ")) {
									tabFolder.setSelection(1);
									btn_export.setEnabled(true);
									btn_find.setEnabled(true);
									btn_find.notifyListeners(SWT.Selection, null);
								} else
									updatePageDescription();
							}
						});
					}
				};
				taskThread.start();
			}
		});

		btn_export.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				DirectoryDialog directoryDialog = new DirectoryDialog(getShell());
				String localFileDir = directoryDialog.open();
				if (localFileDir == null)
					return;
				
				GpManageServiceProxy manageProxy = getContainer().getGpController().getManageServiceProxy();
				String remoteFileDir = manageProxy.localPath() + PGLOG_GATHER_DIR;
				commandFragments = txt_command.getText().split(AT_FLAG);
				String sshCommand = commandFragments.length >= 2 ? commandFragments[0] : null;
				try {
					
					
					boolean result = manageProxy.downloadLogData(localFileDir, remoteFileDir, currentSerialNo, sshCommand);
					if (result)
						MessageDialog.openInformation(getShell(), 
								ResourceHandler.getValue(MessageConstants.AUDIT_DIALOG_TITLE_INFO), 
								ResourceHandler.getValue(MessageConstants.AUDIT_DIALOG_MESSAGE_DOWNLOAD_SUCCESSED));
					else
						MessageDialog.openError(getShell(), 
								ResourceHandler.getValue(MessageConstants.AUDIT_DIALOG_TITLE_ERROR), 
								ResourceHandler.getValue(MessageConstants.AUDIT_DIALOG_MESSAGE_DOWNLOAD_ERROR));
					
				} catch (Exception ex) {
					MessageDialog.openError(getShell(), 
							ResourceHandler.getValue(MessageConstants.AUDIT_DIALOG_TITLE_EXCEPTION), 
							ResourceHandler.getValue(MessageConstants.AUDIT_DIALOG_MESSAGE_DOWNLOAD_EXCEPTION, new String[] { ex.getMessage() }));
				}
			}
		});
		
		// 2.0 Logs && Content
		tabFolder = new TabFolder(composte, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// 2.1 Gplogfilter Logs
		TabItem tabItem_logs = new TabItem(tabFolder, SWT.NONE);
		tabItem_logs.setText(ResourceHandler.getValue(MessageConstants.AUDIT_LOG_TAB_LOGS));
		txt_content = new Text(tabFolder, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		txt_content.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		txt_content.setLayoutData(new GridData(GridData.FILL_BOTH));
		txt_content.setEditable(false);
		tabItem_logs.setControl(txt_content);
		
		// 2.2 Log Content
		TabItem tabItem_data = new TabItem(tabFolder, SWT.NONE);
		tabItem_data.setText(ResourceHandler.getValue(MessageConstants.AUDIT_LOG_TAB_CONTENT));
		Composite comp_content = new Composite(tabFolder, SWT.NONE);
		comp_content.setLayout(new GridLayout(1, false));
		tabItem_data.setControl(comp_content);
		
		Composite findComp = new Composite(comp_content, SWT.NO_TRIM);
		findComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		findComp.setLayout(new GridLayout(2, false));
		
		new Link(findComp, SWT.NONE).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btn_find = new Button(findComp, SWT.NONE);
		btn_find.setText(ResourceHandler.getValue(MessageConstants.AUDIT_LOG_FIND));
		gd = new GridData();
		gd.widthHint = 135;
		btn_find.setLayoutData(gd);
		
		table_content = new Table(comp_content, SWT.FULL_SELECTION | SWT.BORDER);
		table_content.setHeaderVisible(true);
		table_content.setLinesVisible(true);
		table_content.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		for (String fieldName : AuditConstant.logEntryFields) {
			String[] fieldFragments = fieldName.split(AuditConstant.fieldFlag);
			TableColumn column = new TableColumn(table_content, SWT.NONE);
			column.setText(fieldFragments[0]);
//			column.setWidth(200);
		}
		
		// 2.3 Page Area
		Composite pageDescComp = new Composite(comp_content, SWT.NO_TRIM);
		pageDescComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pageDescComp.setLayout(new GridLayout(8, false));
		
		lnk_total = new Link(pageDescComp, SWT.NONE);
		lnk_total.setText(ResourceHandler.getValue(MessageConstants.AUDIT_LOG_PAGE_DESC, new String[] { "0", "0", "0" }));
		lnk_total.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		btn_first = createButton(pageDescComp, ResourceHandler.getValue(MessageConstants.AUDIT_LOG_PAGE_FIRST));
		btn_previous = createButton(pageDescComp, ResourceHandler.getValue(MessageConstants.AUDIT_LOG_PAGE_PREVIOUS));
		btn_next = createButton(pageDescComp, ResourceHandler.getValue(MessageConstants.AUDIT_LOG_PAGE_NEXT));
		btn_last = createButton(pageDescComp, ResourceHandler.getValue(MessageConstants.AUDIT_LOG_PAGE_LAST));
		
		lnk_turnTo = new Link(pageDescComp, SWT.NONE);
		lnk_turnTo.setText(ResourceHandler.getValue(MessageConstants.AUDIT_LOG_PAGE_TURN_PREFIX));
		txt_pageNo = new Text(pageDescComp, SWT.BORDER);
		gd = new GridData();
		gd.widthHint = 50;
		txt_pageNo.setLayoutData(gd);
		txt_pageNo.setText("0");
		lbl_turnTo = new Label(pageDescComp, SWT.NONE);
		lbl_turnTo.setText(ResourceHandler.getValue(MessageConstants.AUDIT_LOG_PAGE_TURN_SUFFIX));
		
		txt_pageNo.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				Pattern pattern = Pattern.compile("[0-9]\\d*");
				Matcher matcher = pattern.matcher(e.text);
				if (matcher.matches())
					e.doit = true;
				else if (e.text.length() > 0)
					e.doit = false;
				else
					e.doit = true;
			}
		});
		
		updatePageDescription();
		btn_find.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (currentSerialNo == null || "".equals(currentSerialNo)) {
					MessageDialog.openWarning(getShell(), 
							ResourceHandler.getValue(MessageConstants.AUDIT_DIALOG_TITLE_WARNING), 
							ResourceHandler.getValue(MessageConstants.AUDIT_DIALOG_MESSAGE_FIND_TIP));
					return;
				}
				
//				currAuditCondition.setLoguser(txt_user.getText().trim());
//				currAuditCondition.setLogdatabase(txt_database.getText().trim());
//				currAuditCondition.setLoghost(txt_host.getText().trim());
//				
//				currAuditCondition.set_logseverity_panic(btn_panic.getSelection());
//				currAuditCondition.set_logseverity_fatal(btn_fatal.getSelection());
//				currAuditCondition.set_logseverity_error(btn_error.getSelection());
//				currAuditCondition.set_logseverity_warning(btn_warning.getSelection());
//				
//				Calendar calendarFrom = Calendar.getInstance();
//				calendarFrom.set(dateF.getYear(), dateF.getMonth(), dateF.getDay(), timeF.getHours(), timeF.getMinutes(), timeF.getSeconds());
//				Calendar calendarTo = Calendar.getInstance();
//				calendarTo.set(dateT.getYear(), dateT.getMonth(), dateT.getDay(), timeT.getHours(), timeT.getMinutes(), timeT.getSeconds());
//				currAuditCondition.set_logtimeFrom(calendarFrom.getTime());
//				currAuditCondition.set_logtimeTo(calendarTo.getTime());
				
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
						MessageDialog.openWarning(getShell(), 
								ResourceHandler.getValue(MessageConstants.AUDIT_DIALOG_TITLE_WARNING), 
								ResourceHandler.getValue(MessageConstants.AUDIT_DIALOG_MESSAGE_PAGE_TURNON_TIP));
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
	
	private Button createButton(Composite comp, String name) {
		Button btn = new Button(comp, SWT.NONE);
		btn.setText(name);
		GridData gd = new GridData();
		gd.widthHint = 80;
		btn.setLayoutData(gd);
		return btn;
	}
	
	private void searchTableData(final int pageNum) {
		btn_find.setEnabled(false);
		
		btn_first.setEnabled(false);
		btn_previous.setEnabled(false);
		btn_next.setEnabled(false);
		btn_last.setEnabled(false);
		
		lnk_turnTo.setEnabled(false);
		txt_pageNo.setEnabled(false);
		lbl_turnTo.setEnabled(false);
		
		currAuditCondition.setLimit(rowCountPerPage);
		currAuditCondition.setOffset(pageNum * rowCountPerPage - rowCountPerPage);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				GpManageServiceProxy proxy = getContainer().getGpController().getManageServiceProxy();
				auditInfoList = proxy.queryAuditData(currAuditCondition, currAuditCondition.tableName(currentSerialNo));
				fillTableData();
				
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						int total = currAuditCondition.getTotalCount();
						currPageNum = 0;
						totalPageNum = 0;
						if (total > 0) {
							currPageNum = pageNum;
							totalPageNum = total / rowCountPerPage + (total % rowCountPerPage > 0 ? 1 : 0);
						}
						updatePageDescription();
						btn_find.setEnabled(true);
					}
				});
			}
		}).start();
	}
	
	private void fillTableData() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				table_content.removeAll();
			}
		});
		if (auditInfoList == null || auditInfoList.size() <= 0)
			return;
		
		final SimpleDateFormat sdf_short = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		for (final AuditEntity auditInfo : auditInfoList) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					Date session_start_time = auditInfo.getSession_start_time();
					new TableItem(table_content, SWT.NONE).setText(new String[] { 
									sdf_short.format(auditInfo.getEvent_time()),
									auditInfo.getUser_name(),
									auditInfo.getDatabase_name(),
									auditInfo.getProcess_id(),
									auditInfo.getThread_id(),
									auditInfo.getRemote_host(),
									auditInfo.getRemote_port(),
									session_start_time == null ? null : sdf_short.format(session_start_time),
									String.valueOf(auditInfo.getTransaction_id()),
									auditInfo.getGp_session_id(),
									auditInfo.getGp_command_count(),
									auditInfo.getGp_segment(),
									auditInfo.getSlice_id(),
									auditInfo.getDistr_tranx_id(),
									auditInfo.getLocal_tranx_id(),
									auditInfo.getSub_tranx_id(),
									auditInfo.getEvent_severity(),
									auditInfo.getSql_state_code(),
									auditInfo.getEvent_message(),
									auditInfo.getEvent_detail(),
									auditInfo.getEvent_hint(),
									auditInfo.getInternal_query(),
									String.valueOf(auditInfo.getInternal_query_pos()),
									auditInfo.getEvent_context(),
									auditInfo.getDebug_query_string(),
									String.valueOf(auditInfo.getError_cursor_pos()),
									auditInfo.getFunc_name(),
									auditInfo.getFile_name(),
									String.valueOf(auditInfo.getFile_line()),
									auditInfo.getStack_trace() });
				}
			});
		}
		if (!tableFormatted) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					TableColumn[] tcs = table_content.getColumns();
					for (TableColumn column : tcs)
						column.pack();
				}
			});
			tableFormatted = true;
		}
	}
	
	private void updatePageDescription() {
		int total = currAuditCondition.getTotalCount();
		if (total <= 0) {
			currPageNum = 0;
			totalPageNum = 0;
		}
		lnk_total.setText(ResourceHandler.getValue(MessageConstants.AUDIT_LOG_PAGE_DESC, new Integer[] { total, currPageNum, totalPageNum }));
		
		btn_first.setEnabled(currPageNum > 1);
		btn_previous.setEnabled(currPageNum > 1);
		btn_next.setEnabled(currPageNum < totalPageNum);
		btn_last.setEnabled(currPageNum < totalPageNum);
		
		lnk_turnTo.setEnabled(totalPageNum > 1);
		txt_pageNo.setEnabled(totalPageNum > 1);
		lbl_turnTo.setEnabled(totalPageNum > 1);
	}
	
	private String generateSerialNo() {
		Calendar calenter = Calendar.getInstance();
		int month = calenter.get(Calendar.MONTH) + 1;
		String date = "" + calenter.get(Calendar.YEAR) + (month > 10 ? month : "0" + month) + calenter.get(Calendar.DAY_OF_MONTH);
		
		int hour = calenter.get(Calendar.HOUR_OF_DAY);
		int minute = calenter.get(Calendar.MINUTE);
		int second = calenter.get(Calendar.SECOND);
		String time = "_" + (hour > 9 ? hour : "0" + hour) + (minute > 9 ? minute : "0" + minute) + (second > 9 ? second : "0" + second);
		String uuid = "_" + java.util.UUID.randomUUID().toString().replaceAll("-", "");
		
		return date + time + uuid;
	}
	
	private String generateLogFileName(String serialNo, boolean isMaster) {
		String logFileName = isMaster ? "master" : "segment";
		return logFileName + "_" + KEYWORD_HOSTNAME + "_" + serialNo + ".output";
	}
	
	public void updateCommand(final String command) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				txt_command.setText(command);
			}
		});
	}
	
	@Override
	public void refreshUI(final String msg) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				txt_content.append(msg);
			}
		});
	}
	
	@Override
	public void loadData() {}
	
	@Override
	public void loadStaticData() throws Exception {}
}
