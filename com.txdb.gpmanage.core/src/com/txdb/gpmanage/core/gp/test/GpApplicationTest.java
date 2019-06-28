package com.txdb.gpmanage.core.gp.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.gp.connector.GPConnectorImpl;
import com.txdb.gpmanage.core.gp.connector.IGPConnector;
import com.txdb.gpmanage.core.gp.connector.UICallBack;
import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPConfig;
import com.txdb.gpmanage.core.gp.entry.GPConfigValue;
import com.txdb.gpmanage.core.gp.entry.GPExpandStatusDetail;
import com.txdb.gpmanage.core.gp.entry.GPRequiredSw;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.entry.GPSegmentInfo;
import com.txdb.gpmanage.core.gp.entry.PGDatabaseInfo;
import com.txdb.gpmanage.core.gp.entry.PGHbaInfo;
import com.txdb.gpmanage.core.gp.entry.gpmon.Database;
import com.txdb.gpmanage.core.gp.entry.gpmon.Diskspace;
import com.txdb.gpmanage.core.gp.entry.gpmon.LogAlert;
import com.txdb.gpmanage.core.gp.entry.gpmon.Queries;
import com.txdb.gpmanage.core.gp.monitor.controller.MonitorController;
import com.txdb.gpmanage.core.gp.service.IGpExpandService;
import com.txdb.gpmanage.core.gp.service.IGpManageService;
import com.txdb.gpmanage.core.utils.JsonUtils;

public class GpApplicationTest implements UICallBack {

	// private Display display;
	private Shell shell;
	private Point applicationSize = new Point(1200, 650);

	private List<IGPConnector> gpControllerList = new ArrayList<IGPConnector>();

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdf_short = new SimpleDateFormat("MM-dd HH:mm:ss");
	private SimpleDateFormat sdf_ms = new SimpleDateFormat("m'm's's'");
	
	private Composite mainContent;
	private Composite logContent;
	private Table hostTable;
	private Text logText;

	private final String MONITOR_SAMPLE = "monitor_sample";
	
	public static FontData fontData = new FontData("Courier New", 9, SWT.NONE);
	public static Font logFont;
	
	
	public GpApplicationTest() {
		// display = new Display();
		shell = new Shell(Display.getCurrent(), SWT.SHELL_TRIM);
		init();
	}

	private void init() {
		shell.setText("GP安装演示");
		Rectangle screenBounds = Display.getCurrent().getBounds();
		shell.setBounds((screenBounds.width - applicationSize.x) / 2, (screenBounds.height - applicationSize.y) / 2, applicationSize.x, applicationSize.y);

		shell.setLayout(new FillLayout());
		Composite parent = new Composite(shell, SWT.NORMAL);
		createClientArea(parent);
	}

	public void open() {
		shell.open();
		// while (!shell.isDisposed()) {
		// if (!Display.getCurrent().readAndDispatch())
		// Display.getCurrent().sleep();
		// }
		// Display.getCurrent().dispose();
	}

	public void createClientArea(Composite parent) {
		GridData gridData;
		GridLayout gridLayout = new GridLayout(2, false);
		parent.setLayout(gridLayout);

		// Left Content Area
		mainContent = new Composite(parent, SWT.NO_TRIM);
		gridData = new GridData(GridData.FILL_BOTH);
		mainContent.setLayoutData(gridData);
		mainContent.setLayout(new GridLayout(1, true));

		// Left 1.0 Group for host list
		Group group_hostList = new Group(mainContent, SWT.NONE);
		group_hostList.setText("终端列表");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 200;
		group_hostList.setLayoutData(gridData);
		group_hostList.setLayout(new GridLayout(2, false));

		// Left 1.1 Group Content
		hostTable = new Table(group_hostList, SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK | SWT.BORDER);
		hostTable.setHeaderVisible(true);
		hostTable.setLinesVisible(true);

		createTableColumn(hostTable, "Hostname", 90);
		createTableColumn(hostTable, "IpAddress", 120);
		createTableColumn(hostTable, "Username", 80);
		createTableColumn(hostTable, "Password", 80);
		createTableColumn(hostTable, "OSName", 235);

		createTableItem(hostTable, "mdw", "192.168.0.120", "root", "123456", "unknow");
		createTableItem(hostTable, "sdw1", "192.168.0.121", "root", "123456", "unknow");
		createTableItem(hostTable, "mdw", "192.168.0.120", "gpadmin", "123456", "unknow");
		createTableItem(hostTable, "sdw1", "192.168.0.121", "gpadmin", "123456", "unknow");

		gridData = new GridData(GridData.FILL_BOTH);
		hostTable.setLayoutData(gridData);

		Composite hostOptArea = new Composite(group_hostList, SWT.NO_TRIM);
		gridData = new GridData(GridData.FILL_VERTICAL);
		gridData.widthHint = 80;
		hostOptArea.setLayoutData(gridData);
		hostOptArea.setLayout(new GridLayout(1, true));

		Button btn_add = new Button(hostOptArea, SWT.PUSH);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		btn_add.setLayoutData(gridData);
		btn_add.setText("Add");
		btn_add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommonDialog dialog = new CommonDialog(shell);
				StringBuffer buffer = new StringBuffer();
				buffer.append("mdw 192.168.0.120 root 123456\n");
				buffer.append("sdw1 192.168.0.121 root 123456\n");

				dialog.setInitTextContent(buffer.toString());
				if (dialog.open() != 0)
					return;

				String[] resultArray = (String[]) dialog.getResultObject();
				for (String row : resultArray) {
					String[] rowFragments = row.split(" ");
					if (rowFragments.length < 4)
						continue;
					createTableItem(hostTable, rowFragments[0], rowFragments[1], rowFragments[2], rowFragments[3], "unknow");
				}
			}
		});

		Button btn_remove = new Button(hostOptArea, SWT.PUSH);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		btn_remove.setLayoutData(gridData);
		btn_remove.setText("Remove");
		btn_remove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean sure = MessageDialog.openQuestion(shell, "Confirm", "Are you sure to remove current selection data?");
				if (!sure)
					return;
				
				TableItem[] items = hostTable.getItems();
				int i = items.length - 1;
				for (; i >= 0; i--) {
					if (items[i].getChecked())
						hostTable.remove(i);
				}
			}
		});

		Button btn_webserver = new Button(hostOptArea, SWT.PUSH);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		btn_webserver.setLayoutData(gridData);
		btn_webserver.setText("WebServer");
		btn_webserver.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MonitorController controller = MonitorController.getInstance();
				try {
					controller.serverStartup();
					controller.listServer();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		// ----------------------------------------------------------------------------------------------------------------
		Button btn_connect = new Button(hostOptArea, SWT.PUSH);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		btn_connect.setLayoutData(gridData);
		btn_connect.setText("connect");
		btn_connect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (IGPConnector ctrl : gpControllerList)
					ctrl.disconnect();
				gpControllerList.clear();

				TableItem[] tableItems = hostTable.getItems();
				for (TableItem tableItem : tableItems) {
					if (tableItem.getChecked()) {
						IGPConnector gpController = new GPConnectorImpl(tableItem.getText(1), tableItem.getText(2), tableItem.getText(3));
						gpController.setHostname(tableItem.getText(0));
						if (gpController.connect().isSuccessed()) {
							gpController.setCallback(GpApplicationTest.this);
							gpControllerList.add(gpController);
							logInfo("Host(" + gpController.getDao().getHost() + ") by User(" + gpController.getDao().getSshUserName() + ") is connected");
						} else
							logError("Host(" + gpController.getDao().getHost() + ") by User(" + gpController.getDao().getSshUserName() + ") connect failed");
					}
				}
				updateServiceStatus();
			}
		});
		Button btn_psql = new Button(hostOptArea, SWT.PUSH);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		btn_psql.setLayoutData(gridData);
		btn_psql.setText("psql");
		btn_psql.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Host(" + gpControllerList.get(0).getDao().getHost() + ") Connecting JDBC...");
//				boolean connected = gpControllerList.get(0).getManageServiceProxy().connectJdbc("gpadmin", "", IGpManageService.DEFAULT_SYSTEM_DB_POSTGRES);
				boolean connected = gpControllerList.get(0).getManageServiceProxy().connectJdbc("gpadmin", "", IGpManageService.DEFAULT_SYSTEM_DB_GPPERFMON);
				if (connected)
					logInfo("Host(" + gpControllerList.get(0).getDao().getHost() + ") JDBC Connected.");
				else
					logError("Host(" + gpControllerList.get(0).getDao().getHost() + ") Connect JDBC failed!");
			}
		});
		Button btn_disconnect = new Button(hostOptArea, SWT.PUSH);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		btn_disconnect.setLayoutData(gridData);
		btn_disconnect.setText("disconnect");
		btn_disconnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (IGPConnector gpCtrl : gpControllerList) {
					gpCtrl.disconnect();
					logInfo("Host(" + gpCtrl.getDao().getHost() + ") by User( " + gpCtrl.getDao().getSshUserName() + " ) is disconnected");
				}
				updateServiceStatus();
			}
		});

		// Left 2 TabFolder
		// Main TabFolder -- Install(Setup), Management(Expand, Query)
		TabFolder mainTabFolder = new TabFolder(mainContent, SWT.NONE);
		mainTabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Install(Setup), Management(Expand, Query)
		TabItem tabItem_install = new TabItem(mainTabFolder, SWT.NONE);
		tabItem_install.setText("GreenPlum安装与管理 Install(Setup), Management(Expand, Query)");
		Composite clientArea_install = new Composite(mainTabFolder, SWT.NONE);
		tabItem_install.setControl(clientArea_install);
		clientArea_install.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		clientArea_install.setLayout(new GridLayout(1, false));

		// Monitor, Analyze
		TabItem tabItem_monitor = new TabItem(mainTabFolder, SWT.NONE);
		tabItem_monitor.setText("GreenPlum图表与监控 Monitor, Analyze");
		Composite clientArea_monitor = new Composite(mainTabFolder, SWT.NONE);
		tabItem_monitor.setControl(clientArea_monitor);
		clientArea_monitor.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
		clientArea_monitor.setLayout(new GridLayout(1, false));

		// Content in Part I
		TabFolder tabFolder = new TabFolder(clientArea_install, SWT.NONE);
		createTabArea_environment(tabFolder);
		createTabArea_install_bin(tabFolder);
		createTabArea_expand(tabFolder);
		createTabArea_manage(tabFolder);
		createTabArea_other(tabFolder);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		tabFolder.setLayoutData(gridData);

		// Content in Part II
		TabFolder tabFolder_Monitor = new TabFolder(clientArea_monitor, SWT.NONE);
		createTabArea_alert(tabFolder_Monitor);
		createTabArea_queryMonitor(tabFolder_Monitor);
		createTabArea_hostMetrics(tabFolder_Monitor);
		createTabArea_hostMetrics_charts(tabFolder_Monitor);
		createTabArea_clusterMetrics(tabFolder_Monitor);
		createTabArea_clusterMetrics_charts(tabFolder_Monitor);
		createTabArea_history(tabFolder_Monitor);
		createTabArea_history_chart(tabFolder_Monitor);
		createTabArea_system(tabFolder_Monitor);
		createTabArea_system_chart(tabFolder_Monitor);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		tabFolder_Monitor.setLayoutData(gridData);

		// ------------------------------------------------------------------------------------------------------------------------
		// Right Content Area
		logContent = new Composite(parent, SWT.NO_TRIM);
		gridData = new GridData(SWT.RIGHT, SWT.FILL, true, true);
		gridData.widthHint = 500;
		logContent.setLayoutData(gridData);
		logContent.setLayout(new GridLayout(1, true));

		// Right 1 Text
		logText = new Text(logContent, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		if (logFont == null)
			logFont = new Font(shell.getDisplay(), fontData);
		logText.setFont(logFont);
		gridData = new GridData(GridData.FILL_BOTH);
		logText.setLayoutData(gridData);

		// Right 2 Composite for 2 buttons
		Composite logOperation = new Composite(logContent, SWT.NO_TRIM);
		logOperation.setLayout(new FillLayout());
		gridData = new GridData(SWT.RIGHT, SWT.BOTTOM, false, false);
		gridData.heightHint = 25;
		logOperation.setLayoutData(gridData);

		Button btnClear = new Button(logOperation, SWT.NONE);
		btnClear.setText("Clear Log");
		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (MessageDialog.openConfirm(shell, "Clear Log", "Are you sure for clean the log area?"))
					logText.setText("");
			}
		});
		Button btn_sleak = new Button(logOperation, SWT.NONE);
		btn_sleak.setText("S-Leak");
		btn_sleak.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Sleak sleak = new Sleak();
				sleak.open();
			}
		});
		final Button btn_Max = new Button(logOperation, SWT.NONE);
		btn_Max.setText("Max");
		btn_Max.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isNormal = mainContent.getVisible();
				mainContent.setVisible(!isNormal);
				if (isNormal) {
					btn_Max.setText("→");
					// 设置最大化
					GridData gridData = new GridData(SWT.RIGHT, SWT.FILL, true, true);
					gridData.widthHint = 2500;
					logContent.setLayoutData(gridData);
					logContent.getParent().layout();

				} else {
					btn_Max.setText("Max");
					// 设置还原
					GridData gridData = new GridData(SWT.RIGHT, SWT.FILL, true, true);
					gridData.widthHint = 500;
					logContent.setLayoutData(gridData);
					logContent.getParent().layout();
				}
			}
		});
	}

	private void createTabArea_environment(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("环境配置 All by root");
		Composite clientArea = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(clientArea);
		clientArea.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));

		GridData gridData;
		GridLayout gridLayout = new GridLayout(1, false);
		clientArea.setLayout(gridLayout);

		gridData = new GridData(GridData.FILL_BOTH);
		Group group_operation = new Group(clientArea, SWT.NONE);
		group_operation.setText("环境配置明细");
		group_operation.setLayoutData(gridData);
		group_operation.setLayout(new GridLayout(1, true));

		Button btn_hostname = new Button(group_operation, SWT.PUSH);
		btn_hostname.setText("✔ /etc/sysconfig/network (修改hostname)");
		btn_hostname.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin to update /etc/sysconfig/network");
				for (IGPConnector gpCtrl : gpControllerList) {
					GPResultSet resultSet = gpCtrl.getEnvServiceProxy().updateHostname(gpCtrl.getHostname());
					logInfo("Update /etc/sysconfig/network Successed: " + resultSet.isSuccessed());
					printResultSet(resultSet);
				}
			}
		});

		Button btn_resolv = new Button(group_operation, SWT.PUSH);
		btn_resolv.setText("✔ /etc/resolv.conf (修改nameserver)");
		btn_resolv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin to update resolv.conf");
				for (IGPConnector gpCtrl : gpControllerList) {
					GPResultSet resultSet = gpCtrl.getEnvServiceProxy().updateResolvConf("192.168.0.2");
					logInfo("Update resolv.conf Successed: " + resultSet.isSuccessed());
					printResultSet(resultSet);
				}
			}
		});

		Button btn_hosts = new Button(group_operation, SWT.PUSH);
		btn_hosts.setText("✔ /etc/hosts");
		btn_hosts.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				update_etc_hosts();
			}
		});

		Button btn_iptables = new Button(group_operation, SWT.PUSH);
		btn_iptables.setText("✔ service iptables stop (Tips: Ubuntu系统命令为\"ufw disable\")");
		btn_iptables.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin to close iptables");
				for (IGPConnector gpCtrl : gpControllerList) {
					GPResultSet resultSet = gpCtrl.getEnvServiceProxy().closeIptables();
					logInfo("Close iptables Successed: " + resultSet.isSuccessed());
					printResultSet(resultSet);
				}
			}
		});

		Button btn_selinux = new Button(group_operation, SWT.PUSH);
		btn_selinux.setText("✔ /etc/selinux/config");
		btn_selinux.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin to close SeLinux");
				for (IGPConnector gpCtrl : gpControllerList) {
					GPResultSet resultSet = gpCtrl.getEnvServiceProxy().closeSeLinux();
					logInfo("Close SeLinux Successed: " + resultSet.isSuccessed());
					printResultSet(resultSet);
				}
			}
		});

		Composite comp_os_params = new Composite(group_operation, SWT.BORDER);
		comp_os_params.setLayout(new GridLayout(4, false));
		Label lbl_gpexpand_inputfile = new Label(comp_os_params, SWT.NONE);
		lbl_gpexpand_inputfile.setText("(软&&硬限制)");
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		lbl_gpexpand_inputfile.setLayoutData(gridData);

		final Button btn_sysctl = new Button(comp_os_params, SWT.PUSH);
		btn_sysctl.setText("● /etc/sysctl.conf");
		final Button btn_sysctl_a = new Button(comp_os_params, SWT.PUSH);
		btn_sysctl_a.setText("★ sysctl -A");
		final Button btn_limits = new Button(comp_os_params, SWT.PUSH);
		btn_limits.setText("● /etc/security/limits.conf");
		final Button btn_nproc = new Button(comp_os_params, SWT.PUSH);
		btn_nproc.setText("● /etc/security/limits.d/90-nproc.conf");
		SelectionAdapter sa = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				String logBeginMsg = "";
				Properties sysctlProps = new Properties();
				Properties limitsProps = new Properties();
				Properties nprocProps = new Properties();
				Properties tempProps = null;

				CommonDialog dialog = new CommonDialog(shell);
				if (e.widget == btn_sysctl) {
					// /usr/lib/sysctl.d/90-override.conf
					logBeginMsg = "Update btn_sysctl";
					dialog.setTitle("修改 /etc/sysctl.conf");
					dialog.setMessage("共享内存(段)最大值");

					// # Disable netfilter on bridges
					// #net.bridge.bridge-nf-call-ip6tables = 0
					// #net.bridge.bridge-nf-call-iptables = 0
					// #net.bridge.bridge-nf-call-arptables = 0

					StringBuffer buffer = new StringBuffer();
					// buffer.append("kernel.shmmax = 5000000000\n");
					buffer.append("kernel.shmmax = 500000000\n");
					buffer.append("kernel.shmmni = 4096\n");
					// buffer.append("kernel.shmall = 40000000000\n");
					buffer.append("kernel.shmall = 4000000000\n");
					// buffer.append("kernel.sem = 250 5120000 100 20480\n");
					buffer.append("kernel.sem = 250 5120000 100 2048\n");
					buffer.append("kernel.sysrq = 1\n");
					buffer.append("kernel.core_uses_pid = 1\n");
					buffer.append("kernel.msgmnb = 65536\n");
					buffer.append("kernel.msgmax = 65536\n");
					buffer.append("kernel.msgmni = 2048\n");
					buffer.append("net.ipv4.tcp_syncookies = 1\n");
					buffer.append("net.ipv4.ip_forward = 0\n");
					buffer.append("net.ipv4.conf.default.accept_source_route = 0\n");
					buffer.append("net.ipv4.tcp_tw_recycle = 1\n");
					buffer.append("net.ipv4.tcp_max_syn_backlog = 4096\n");
					buffer.append("net.ipv4.conf.default.rp_filter = 1\n");
					buffer.append("net.ipv4.conf.default.arp_filter = 1\n");
					buffer.append("net.ipv4.conf.all.arp_filter = 1\n");
					buffer.append("net.ipv4.ip_local_port_range = 1025 65535\n");
					buffer.append("net.core.netdev_max_backlog = 10000\n");
					buffer.append("net.core.rmem_max = 2097152\n");
					buffer.append("net.core.wmem_max = 2097152\n");
					buffer.append("vm.overcommit_memory = 2\n");
					dialog.setInitTextContent(buffer.toString());
					tempProps = sysctlProps;

				} else if (e.widget == btn_limits) {
					logBeginMsg = "Update btn_limits";
					dialog.setTitle("修改 /etc/security/limits.conf");
					dialog.setMessage("文件描述符最大数、单用户最大进程数");

					StringBuffer buffer = new StringBuffer();
					buffer.append("* soft nofile 65536\n");
					buffer.append("* hard nofile 65536\n");
					buffer.append("* soft nproc 131072\n");
					buffer.append("* hard nproc 131072\n");
					buffer.append("* soft core unlimited\n");
					dialog.setInitTextContent(buffer.toString());
					tempProps = limitsProps;

				} else if (e.widget == btn_nproc) {
					// /etc/security/limits.d/20-nproc.conf
					logBeginMsg = "Update btn_nproc";
					dialog.setTitle("修改 /etc/security/limits.d/90-nproc.conf");
					dialog.setMessage("文件描述符最大数、单用户最大进程数(其他)");
					dialog.setInitTextContent("* soft nproc 131072\n");
					tempProps = nprocProps;

				} else {
					logWarn("Unknow Selection Listener");
					Properties allProps = gpControllerList.get(0).getEnvServiceProxy().getSysctlParams();
					if (allProps != null) {
						dialog = new CommonDialog(shell, true);
						dialog.setTitle("查看系统 sysctl 参数");
						dialog.setMessage("查看系统 sysctl 参数");

						StringBuffer tempBuffer = new StringBuffer("");
						Iterator<Object> iterator = allProps.keySet().iterator();
						while (iterator.hasNext()) {
							String key = (String) iterator.next();
							tempBuffer.append(key + " = " + allProps.getProperty(key) + "\n");
						}

						dialog.setInitTextContent(tempBuffer.toString());
						dialog.open();
					}
					return;
				}

				if (dialog.open() != 0)
					return;

				String[] resultArray = (String[]) dialog.getResultObject();
				for (String resultStr : resultArray) {
					System.err.println("resultStr: " + resultStr);
					String[] kv = null;
					if (resultStr.contains("*")) {
						kv = resultStr.trim().split(" ");
						if (kv.length != 4)
							continue;
						tempProps.put(kv[0].trim() + " " + kv[1].trim() + " " + kv[2].trim(), kv[3].trim());

					} else {
						kv = resultStr.trim().split("=");
						if (kv.length != 2)
							continue;
						tempProps.put(kv[0].trim(), kv[1].trim());
					}
				}
				logInfo("Begin " + logBeginMsg);
				for (IGPConnector gpCtrl : gpControllerList) {
					GPResultSet rs = gpCtrl.getEnvServiceProxy().updateSysctl_limits_90_nproc(sysctlProps, limitsProps, nprocProps);
					logInfo(logBeginMsg + " Successed: " + rs.isSuccessed());
					printResultSet(rs);
				}
			}
		};
		btn_sysctl.addSelectionListener(sa);
		btn_sysctl_a.addSelectionListener(sa);
		btn_limits.addSelectionListener(sa);
		btn_nproc.addSelectionListener(sa);

		Button btn_blockdev = new Button(group_operation, SWT.PUSH);
		btn_blockdev.setText("✔ blockdev (修改磁盘预读扇区)");
		btn_blockdev.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin update blockDev");
				for (IGPConnector gpCtrl : gpControllerList) {
					GPResultSet rs = gpCtrl.getEnvServiceProxy().blockDev(65536);
					logInfo("Update blockDev Successed: " + rs.isSuccessed());
					printResultSet(rs);
				}
			}
		});

		Button btn_useradd = new Button(group_operation, SWT.PUSH);
		btn_useradd.setText("✔ useradd -m -d /home/gpadmin -s /bin/bash gpadmin (创建GP管理用户)");
		btn_useradd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin add user \"gpadmin\"");
				for (IGPConnector gpCtrl : gpControllerList) {
					GPResultSet rs = gpCtrl.getEnvServiceProxy().createOrReplaceGpUser("gpadmin", "123456");
					logInfo("Add user \"gpadmin\" Successed: " + rs.isSuccessed());
					printResultSet(rs);
				}
			}
		});
	}

	private void createTableColumn(Table hostTable, String text, int width) {
		TableColumn column = new TableColumn(hostTable, SWT.LEFT);
		column.setText(text);
		column.setWidth(width);
		column.setResizable(false);
	}

	private void createTableItem(Table hostTable, String... strings) {
		TableItem item = new TableItem(hostTable, SWT.CENTER);
		item.setText(strings);
	}

	private void updateServiceStatus() {
		for (IGPConnector gpCtrl : gpControllerList) {
			IExecuteDao dao = gpCtrl.getDao();
			System.err.println("Hostname: " + dao.getHostname());
			TableItem[] tableItems = hostTable.getItems();
			for (TableItem tableItem : tableItems) {
				if (tableItem.getText(1).equals(dao.getHost()) && tableItem.getText(2).equals(dao.getSshUserName()))
					tableItem.setText(4, dao.getExecutorName());
			}
		}
	}

	private void createTabArea_install_bin(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("✔ 程序安装 && ✔ 源码安装");
		Composite clientArea = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(clientArea);
		clientArea.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));

		GridData gridData;
		GridLayout gridLayout = new GridLayout(2, true);
		clientArea.setLayout(gridLayout);

		// ------------
		Group group_bin = new Group(clientArea, SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL);
		group_bin.setText("bin file <Master by root>");
		group_bin.setLayoutData(new GridData(GridData.FILL_BOTH));
		group_bin.setLayout(new GridLayout(1, false));

		Button btn_upload = new Button(group_bin, SWT.PUSH);
		btn_upload.setText("✔ 上传 greenplum-db-5.3.0-rhel6-x86_64.bin");
		btn_upload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin upload greenplum-db-5.3.0-rhel6-x86_64.bin");
				if (gpControllerList.size() <= 0) {
					logWarn("No Host was connected");
					return;
				}
				GPResultSet rs = gpControllerList.get(0).getInstallServiceProxy().sendGpFile("/root/", "E:\\greenplum database\\softwares\\greenplum-db-5.3.0-rhel6-x86_64.bin");
				logInfo("upload greenplum-db-5.3.0-rhel6-x86_64.bin Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_rpm = new Button(group_bin, SWT.PUSH);
		btn_rpm.setText("✔ 解压 greenplum-db-5.3.0-rhel6-x86_64.bin");
		btn_rpm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin rpm greenplum-db-5.3.0-rhel6-x86_64.bin");
				if (gpControllerList.size() <= 0) {
					logWarn("No Host was connected");
					return;
				}
				GPResultSet rs = gpControllerList.get(0).getInstallServiceProxy().rpmGpFile("/root/", "greenplum-db-5.3.0-rhel6-x86_64.bin", "/usr/local/greenplum-db-5.3.0");
				logInfo("rpm greenplum-db-5.3.0-rhel6-x86_64.bin Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Group group_source = new Group(clientArea, SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL);
		group_source.setText("source file <Master by root>");
		group_source.setLayoutData(new GridData(GridData.FILL_BOTH));
		group_source.setLayout(new GridLayout(1, false));

		Button btn_checkEnv = new Button(group_source, SWT.PUSH);
		btn_checkEnv.setText("✔ 第一步 检查软件环境 <All by root>");
		btn_checkEnv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin to Check Environment: " + gpControllerList.size());
				for (IGPConnector gpCtrl : gpControllerList) {
					GPResultSet resultSet = gpCtrl.getEnvServiceProxy().checkRequiredSoftware();
					logInfo("Check Env Successed: " + resultSet.isSuccessed());
					printResultSet(resultSet);

					List<GPRequiredSw> requiredList = resultSet.getRequiredSwList();
					for (GPRequiredSw sw : requiredList) {
						boolean end = sw == requiredList.get(requiredList.size() - 1);
						logInfo(sw.toString() + (end ? "\n" : ""));
					}
				}
			}
		});

		Button btn_uploadGpdb = new Button(group_source, SWT.PUSH);
		btn_uploadGpdb.setText("✔ 第二步 上传 D:/.../product_v19****/gpdb.tar.gz");
		btn_uploadGpdb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (gpControllerList.size() <= 0) {
					logWarn("No Host was connected");
					return;
				}
//				String tarFile = "D:/gp_xuji/gp6.x_xuji/product_v190329/gpdb.tar.gz";
//				String tarFile = "D:/gp_xuji/gp6.x_xuji/product_v190425/gpdb.tar.gz";
//				String tarFile = "D:/gp_xuji/gp6.x_xuji/product_v190426/gpdb.tar.gz";
//				String tarFile = "D:/gp_xuji/gp6.x_xuji/product_v190506/gpdb.tar.gz";
//				String tarFile = "D:/gp_xuji/gp6.x_xuji/product_v190509/gpdb.tar.gz";
//				String tarFile = "D:/gp_xuji/gp6.x_xuji/product_v190510/gpdb.tar.gz";
//				String tarFile = "D:/gp_xuji/gp6.x_xuji/product_v190513/gpdb.tar.gz";
				String tarFile = "D:/gp_xuji/gp6.x_xuji/product_v190513/gpdb_rel6/gpdb.tar.gz";
//				String tarFile = "D:/gp_xuji/gp6.x_xuji/product_v190513/gpdb_rel7/gpdb.tar.gz";
				
				logInfo("Begin upload " + tarFile);
				GPResultSet rs = gpControllerList.get(0).getInstallServiceProxy().sendGpFile("/usr/local/gptest", tarFile);
				logInfo("upload " + tarFile + " Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_rpmGpdb = new Button(group_source, SWT.PUSH);
		btn_rpmGpdb.setText("✔ 第三步 解压 /usr/local/greenplum-db-5.10.0-rhel6.tar.gz");
		btn_rpmGpdb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin rpm greenplum-db-5.10.0-rhel6.tar.gz");
				if (gpControllerList.size() <= 0) {
					logWarn("No Host was connected");
					return;
				}
//				String tarFile = "/usr/local/gptest/greenplum-db-5.10.0-rhel6.tar.gz";
				String tarFile = "/usr/local/gptest/gpdb.tar.gz";
				GPResultSet rs = gpControllerList.get(0).getInstallServiceProxy().gpdbTargz(tarFile, "/usr/local/gptest");
				logInfo("rpm greenplum-db-5.10.0-rhel6.tar.gz Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});
		// -------------

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		Group group = new Group(clientArea, SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL);
		group.setText("程序安装步骤");
		group.setLayoutData(gridData);
		group.setLayout(new GridLayout(2, false));

		Button btn_profile = new Button(group, SWT.PUSH);
		btn_profile.setText("✔ .bashrc && .bash_profile（Unbuntu为\".profile\"） && /etc/profile <Master by root>");
		btn_profile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin update .bashrc && .bash_profile(or \".profile\") && /etc/profile");

				String gpHome = "/usr/local/gptest/greenplum-db";
				String masterDataDirectory = "/gpdata/master/gpseg-1";
				String gpuserName = "gpadmin";
				GPResultSet rs = gpControllerList.get(0).getInstallServiceProxy().configGpEnv(5432, gpHome, masterDataDirectory, gpuserName, null);
				logInfo("Update environment profile Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_hostfile = new Button(group, SWT.PUSH);
		btn_hostfile.setText("✔ 创建并覆盖 all_host, all_segment 文件 <Master by root>");
		btn_hostfile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin create host file...");
				GPResultSet rs = gpControllerList.get(0).getInstallServiceProxy().createNodeListFile("/opt/gpinst", new String[] { "mdw", "sdw1" }, new String[] { "sdw1" });
				logInfo("Create host file Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_exkeys = new Button(group, SWT.PUSH);
		btn_exkeys.setText("✔ 交换秘钥 <Master by root>");
		btn_exkeys.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin exchange keys...");
				Map<String, String> passwordMap = new HashMap<String, String>();
				passwordMap.put("mdw", "123456");
				passwordMap.put("sdw1", "123456");
				passwordMap.put("sdw2", "123456");

				GPResultSet rs = gpControllerList.get(0).getInstallServiceProxy().gpSSHExKeys("/opt/gpinst/all_host", passwordMap);
				logInfo("Exchange keys Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_gpseginstall = new Button(group, SWT.PUSH);
		btn_gpseginstall.setText("✔ 批量安装Segment节点 <Master by root>");
		btn_gpseginstall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin gpseginstall...");
				GPResultSet rs = gpControllerList.get(0).getInstallServiceProxy().gpSegInstall("/opt/gpinst/all_host", "gpadmin", "123456");
				logInfo("GPseginstall Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_chkinstall = new Button(group, SWT.PUSH);
		btn_chkinstall.setText("✔ 检查安装情况（可选） <Master by root>");
		btn_chkinstall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin check gpseginstall...");
				GPResultSet rs = gpControllerList.get(0).getInstallServiceProxy().gpChkInstallDir("/opt/gpinst/all_host");
				logInfo("Check gpseginstall Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_gpdata = new Button(group, SWT.PUSH);
		btn_gpdata.setText("✔ 创建gpdata工作目录及赋权(/gpdata/...) <Master by root>");
		btn_gpdata.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin prepare gpdata dir...");
				GPResultSet rs;
				rs = gpControllerList.get(0).getInstallServiceProxy().gpMakeDataDir("/gpdata/master/", "gpadmin");
				printResultSet(rs);
				logInfo("Make master dir Successed: " + rs.isSuccessed());
				rs = gpControllerList.get(0).getInstallServiceProxy().gpMakeDataDir("/gpdata/primary/", "/opt/gpinst/all_host", "gpadmin");
				printResultSet(rs);
				logInfo("Make segment dir Successed: " + rs.isSuccessed());
				rs = gpControllerList.get(0).getInstallServiceProxy().gpMakeDataDir("/gpdata/mirror/", "/opt/gpinst/all_host", "gpadmin");
				printResultSet(rs);
				logInfo("Make mirror dir Successed: " + rs.isSuccessed());
			}
		});

		Button btn_templete = new Button(group, SWT.PUSH);
		btn_templete.setText("✔ 编辑安装配置文件模板 <Master by gpadmin>");
		btn_templete.setToolTipText("mkdir -p /home/gpadmin/gpconfigs\n" + "cp /usr/local/gptest/greenplum-db/docs/cli_help/gpconfigs/gpinitsystem_config /home/gpadmin/gpconfigs/\n" + "chmod 775 /home/gpadmin/gpconfigs/gpinitsystem_config");
		btn_templete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommonDialog dialog = new CommonDialog(shell);
				dialog.setTitle("配置初始化配置文件gpinitsystem_config");
				dialog.setMessage("准备 /home/gpadmin/gpconfigs/gpinitsystem_config");

				StringBuffer buffer = new StringBuffer();
				buffer.append("MASTER_HOSTNAME=mdw\n");
				buffer.append("MASTER_DIRECTORY=/gpdata/master\n");
				buffer.append("MASTER_PORT=5432\n");
				buffer.append("DATABASE_NAME=test\n");
				buffer.append("MACHINE_LIST_FILE=/opt/gpinst/all_host\n");
				buffer.append("declare -a DATA_DIRECTORY=(/gpdata/primary /gpdata/primary)\n");
				buffer.append("declare -a MIRROR_DATA_DIRECTORY=(/gpdata/mirror /gpdata/mirror)\n");

				// 如果带Mirror需要加入以下三个端口
				buffer.append("MIRROR_PORT_BASE=21000\n");
				buffer.append("REPLICATION_PORT_BASE=22000\n");
				buffer.append("MIRROR_REPLICATION_PORT_BASE=23000\n");

				dialog.setInitTextContent(buffer.toString());

				if (dialog.open() != 0)
					return;

				String[] resultArray = (String[]) dialog.getResultObject();
				Properties initSystemProps = new Properties();
				for (String resultStr : resultArray) {
					System.err.println("resultStr: " + resultStr);
					String[] kv = resultStr.split("=");
					if (kv.length != 2)
						continue;
					initSystemProps.put(kv[0].trim(), kv[1].trim());
				}
				logInfo("Begin prepare gpdata dir...");
				GPResultSet rs = gpControllerList.get(0).getInstallServiceProxy().gpInitSystemCfg("/usr/local/gptest", "/home/gpadmin/gpconfigs/", initSystemProps);
				logInfo("Prepare gpdata dir Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_gpinitsystem = new Button(group, SWT.PUSH);
		btn_gpinitsystem.setText("✔ 初始化GP <Master by gpadmin>");
		btn_gpinitsystem.setToolTipText("gpinitsystem -c /home/gpadmin/gpconfigs/gpinitsystem_config -h /opt/gpinst/all_host");
		btn_gpinitsystem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin System Deploy...");
				GPResultSet rs = gpControllerList.get(0).getInstallServiceProxy().gpSystemDeploy("/home/gpadmin/gpconfigs/", "/opt/gpinst/all_host");
				logInfo("System Deploy Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});
	}

	private void createTabArea_expand(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("程序扩展");
		Composite clientArea = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(clientArea);

		clientArea.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		GridData gridData;
		clientArea.setLayout(new GridLayout(3, false));

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		gridData = new GridData(GridData.FILL_BOTH);
		Group group_standby = new Group(clientArea, SWT.NONE);
		group_standby.setText("增加/删除Standby");
		group_standby.setLayoutData(gridData);
		group_standby.setLayout(new GridLayout(1, false));

		// s1.将新主机加入hosts文件
		new Label(group_standby, SWT.NONE).setText("准备工作 5 步 <Master by root>");
		Button btn_hostsForStandby = new Button(group_standby, SWT.PUSH);
		btn_hostsForStandby.setText("✔ /etc/hosts <All by root>");
		btn_hostsForStandby.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				update_etc_hosts();
			}
		});

		// s2.建立扩展配置文件
		Composite comp_hostexpand_exkeys_standby = new Composite(group_standby, SWT.NO_TRIM);
		comp_hostexpand_exkeys_standby.setLayout(new GridLayout(2, false));
		Button btn_sbHostFile = new Button(comp_hostexpand_exkeys_standby, SWT.PUSH);
		btn_sbHostFile.setText("✔ host_expand");
		btn_sbHostFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Prepare host file...");
				String[] new_host = { "msdw" };
				GPResultSet rs = gpControllerList.get(0).getExpandServiceProxy().createExpandNodeListFile("/opt/gpinst", new_host);
				logInfo("Prepare host file Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});
		// s3.交换秘钥
		Button btn_exkeyNewHost = new Button(comp_hostexpand_exkeys_standby, SWT.PUSH);
		btn_exkeyNewHost.setText("✔ gpssh-exkeys");
		btn_exkeyNewHost.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Exchange keys for new host...");
				Map<String, String> passwordMap = new HashMap<String, String>();
				passwordMap.put("mdw", "123456");
				passwordMap.put("sdw1", "123456");
				passwordMap.put("sdw2", "123456");
				passwordMap.put("msdw", "123456");
				GPResultSet rs = gpControllerList.get(0).getExpandServiceProxy().expandSSHExKeys("/opt/gpinst", passwordMap);
				logInfo("Exchange keys for new host Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		// s4.准备工作目录/gpdata/... 并执行Standby添加
		Composite comp_datadir_seginstall_standby = new Composite(group_standby, SWT.NO_TRIM);
		comp_datadir_seginstall_standby.setLayout(new GridLayout(2, false));
		Button btn_gpdata = new Button(comp_datadir_seginstall_standby, SWT.PUSH);
		btn_gpdata.setText("✔ /gpdata/...");
		btn_gpdata.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin prepare gpdata dir...");
				GPResultSet rs;
				rs = gpControllerList.get(0).getInstallServiceProxy().gpMakeDataDir("/gpdata/master/", "/opt/gpinst/" + IGpExpandService.DEFAULT_HOST_EXPAND, "gpadmin");
				printResultSet(rs);
				logInfo("Make (standby)master dir Successed: " + rs.isSuccessed());
				logInfo("Make standby dir Successed: " + rs.isSuccessed());

			}
		});

		// s5.安装Standby
		Button btn_gpseginstall = new Button(comp_datadir_seginstall_standby, SWT.PUSH);
		btn_gpseginstall.setText("✔ gpseginstall");
		btn_gpseginstall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Run gpseginstall...");
				GPResultSet rs = gpControllerList.get(0).getInstallServiceProxy().gpSegInstall("/opt/gpinst/" + IGpExpandService.DEFAULT_HOST_EXPAND, "gpadmin", "123456");
				logInfo("Run gpseginstall Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		// s6.部署Standby
		new Label(group_standby, SWT.NONE).setText("<Master by gpadmin>");
		Button btn_addStandby = new Button(group_standby, SWT.PUSH);
		btn_addStandby.setText("✔ gpinitstandby -s msdw(部署)");
		btn_addStandby.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Add Standby...");
				GPResultSet rs = gpControllerList.get(0).getExpandServiceProxy().addStandby("msdw", "123456");
				logInfo("Add Standby Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		// s7.删除Standby
		Button btn_removeStandby = new Button(group_standby, SWT.PUSH);
		btn_removeStandby.setText("✔ gpinitstandby -r -a(移除)");
		btn_removeStandby.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Remove Standby...");
				GPResultSet rs = gpControllerList.get(0).getExpandServiceProxy().removeStandby();
				logInfo("Remove Standby Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		gridData = new GridData(GridData.FILL_BOTH);
		Group group_segment = new Group(clientArea, SWT.NONE);
		group_segment.setText("扩展Segment");
		group_segment.setLayoutData(gridData);
		group_segment.setLayout(new GridLayout(1, false));

		new Label(group_segment, SWT.NONE).setText("准备工作 5 步 <Master by root>");
		// p(1/5).将新主机加入hosts文件
		Button btn_hostsForSeg = new Button(group_segment, SWT.PUSH);
		btn_hostsForSeg.setText("✔ /etc/hosts <All by root>");
		btn_hostsForSeg.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				update_etc_hosts();
			}
		});

		Composite comp_hostexpand_exkeys = new Composite(group_segment, SWT.NO_TRIM);
		comp_hostexpand_exkeys.setLayout(new GridLayout(2, false));

		// p(2/5).建立扩展配置文件host_expand
		Button btn_hostFile = new Button(comp_hostexpand_exkeys, SWT.PUSH);
		btn_hostFile.setText("✔ host_expand");
		btn_hostFile.setToolTipText("需要先启动GP，连接JDBC");
		btn_hostFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Prepare host file...");
				String[] new_host = { "sdw2" };
				GPResultSet rs = gpControllerList.get(0).getExpandServiceProxy().createExpandNodeListFile("/opt/gpinst", new_host);
				logInfo("Prepare host file Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		// p(3/5).建立新主机节点之间信任
		Button btn_exkeyNewSeg = new Button(comp_hostexpand_exkeys, SWT.PUSH);
		btn_exkeyNewSeg.setText("✔ gpssh-exkeys");
		btn_exkeyNewSeg.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Exchange keys for new host...");
				Map<String, String> passwordMap = new HashMap<String, String>();
				passwordMap.put("sdw1", "123456");
				passwordMap.put("sdw2", "123456");
				GPResultSet rs = gpControllerList.get(0).getExpandServiceProxy().expandSSHExKeys("/opt/gpinst", passwordMap);
				logInfo("Exchange keys for new host Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Composite comp_datadir_seginstall = new Composite(group_segment, SWT.NO_TRIM);
		comp_datadir_seginstall.setLayout(new GridLayout(2, false));

		// p(4/5).准备工作目录/gpdata/...
		Button btn_gpdataDir = new Button(comp_datadir_seginstall, SWT.PUSH);
		btn_gpdataDir.setText("✔ /gpdata/...");
		btn_gpdataDir.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin prepare gpdata dir...");
				GPResultSet rs;
				rs = gpControllerList.get(0).getInstallServiceProxy().gpMakeDataDir("/gpdata/primary/", "/opt/gpinst/" + IGpExpandService.DEFAULT_HOST_EXPAND, "gpadmin");
				printResultSet(rs);
				logInfo("Make segment dir Successed: " + rs.isSuccessed());
				rs = gpControllerList.get(0).getInstallServiceProxy().gpMakeDataDir("/gpdata/mirror/", "/opt/gpinst/" + IGpExpandService.DEFAULT_HOST_EXPAND, "gpadmin");
				printResultSet(rs);
				logInfo("Make mirror dir Successed: " + rs.isSuccessed());
			}
		});

		// p(5/5).新节点安装GP
		Button btn_gpSegInstall = new Button(comp_datadir_seginstall, SWT.PUSH);
		btn_gpSegInstall.setText("✔ gpseginstall");
		btn_gpSegInstall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Run gpseginstall...");
				GPResultSet rs = gpControllerList.get(0).getInstallServiceProxy().gpSegInstall("/opt/gpinst/" + IGpExpandService.DEFAULT_HOST_EXPAND, "gpadmin", "123456");
				logInfo("Run gpseginstall Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		new Label(group_segment, SWT.NONE).setText("扩展工作 7 步 <Master by gpadmin>");
		Composite comp_gpexpand_inputfile = new Composite(group_segment, SWT.BORDER);
		comp_gpexpand_inputfile.setLayout(new GridLayout(3, false));
		Label lbl_gpexpand_inputfile = new Label(comp_gpexpand_inputfile, SWT.NONE);
		lbl_gpexpand_inputfile.setText("gpexpand_inputfile_20180620_185324:");
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		lbl_gpexpand_inputfile.setLayoutData(gridData);

		// s(1/7).生成扩展的配置文件
		Button btn_generate = new Button(comp_gpexpand_inputfile, SWT.PUSH);
		btn_generate.setText("✔ 生成");
		btn_generate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Generate configuration file...");
				GPResultSet rs = gpControllerList.get(0).getExpandServiceProxy().genGpexpandInputfile("/opt/gpinst/" + IGpExpandService.DEFAULT_HOST_EXPAND);
				logInfo("Generate configuration file Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});
		// s(2/7).修改inputfile
		Button btn_modifySegInputFile = new Button(comp_gpexpand_inputfile, SWT.PUSH);
		btn_modifySegInputFile.setText("✘ 修改");
		btn_modifySegInputFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageDialog.openInformation(shell, "Tips", "暂无，敬请期待...");
			}
		});
		// s(3/7).执行扩展操作
		Button btn_expand = new Button(comp_gpexpand_inputfile, SWT.PUSH);
		btn_expand.setText("✔ 执行");
		btn_expand.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		btn_expand.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin GP Expand...");
				GPResultSet rs = gpControllerList.get(0).getExpandServiceProxy().expandSegment();
				logInfo("GP Expand Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Composite comp_queryTablesLv_cfg = new Composite(group_segment, SWT.NO_TRIM);
		comp_queryTablesLv_cfg.setLayout(new GridLayout(2, false));
		// s(4/7)查询分布级别
		Button btn_queryTableLv = new Button(comp_queryTablesLv_cfg, SWT.PUSH);
		btn_queryTableLv.setText("✔ 查询分布级别");
		btn_queryTableLv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Query gpexpand.status_detail...");
				List<GPExpandStatusDetail> statusDetailList = gpControllerList.get(0).getExpandServiceProxy().queryExpandStatusDetail();
				logInfo("Query gpexpand.status_detail Successed: " + (statusDetailList != null));

				for (GPExpandStatusDetail statusDetail : statusDetailList)
					System.out.println(statusDetail.toString());
			}
		});
		// s(5/7)设置分布级别
		Button btn_cfgTableLv = new Button(comp_queryTablesLv_cfg, SWT.PUSH);
		btn_cfgTableLv.setText("✔ 设置分布级别");
		btn_cfgTableLv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Config gpexpand.status_detail...");
				boolean isSuccessed = gpControllerList.get(0).getExpandServiceProxy().updateExpandRank("postgres", "public.distributors", 12);
				logInfo("Config gpexpand.status_detail Successed: " + isSuccessed);
			}
		});

		Composite comp_redistribute_clean = new Composite(group_segment, SWT.NO_TRIM);
		comp_redistribute_clean.setLayout(new GridLayout(2, false));
		// s(6/7).数据重分布
		Button btn_redistribute = new Button(comp_redistribute_clean, SWT.PUSH);
		btn_redistribute.setText("✔ 数据重分布");
		btn_redistribute.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Redistribute...");
				GPResultSet rs = gpControllerList.get(0).getExpandServiceProxy().redistribute();
				logInfo("Redistribute Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});
		// s(7/7).清除Schema
		Button btn_cleanSchema = new Button(comp_redistribute_clean, SWT.PUSH);
		btn_cleanSchema.setText("✔ 清除Schema");
		btn_cleanSchema.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Clean Schema...");
				GPResultSet rs = gpControllerList.get(0).getExpandServiceProxy().expandCheanSchema();
				logInfo("Clean Schema Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		gridData = new GridData(GridData.FILL_BOTH);
		Group group_mirror = new Group(clientArea, SWT.NONE);
		group_mirror.setText("安装Mirror");
		group_mirror.setLayoutData(gridData);
		group_mirror.setLayout(new GridLayout(1, false));

		Label lbl_mirrorPrepare = new Label(group_mirror, SWT.NONE);
		lbl_mirrorPrepare.setText("准备工作 5 步 <Master by root>");
		lbl_mirrorPrepare.setEnabled(false);
		// p(1/5).将新主机加入hosts文件
		Button btn_hostsForMirr = new Button(group_mirror, SWT.PUSH);
		btn_hostsForMirr.setText("✔ /etc/hosts <All by root>");
		btn_hostsForMirr.setEnabled(false);
		btn_hostsForMirr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				update_etc_hosts();
			}
		});

		Composite comp_hostexpand_exkeys_mirror = new Composite(group_mirror, SWT.NO_TRIM);
		comp_hostexpand_exkeys_mirror.setLayout(new GridLayout(2, false));
		// p(2/5).建立扩展配置文件host_expand
		Button btn_hostFileMirr = new Button(comp_hostexpand_exkeys_mirror, SWT.PUSH);
		btn_hostFileMirr.setText("✔ host_expand");
		btn_hostFileMirr.setEnabled(false);
		btn_hostFileMirr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Prepare host file...");
				String[] new_host = { "msdw" };
				GPResultSet rs = gpControllerList.get(0).getExpandServiceProxy().createExpandNodeListFile("/opt/gpinst", new_host);
				logInfo("Prepare host file Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});
		// p(3/5).建立新主机节点之间信任
		Button btn_exkeyNewMirr = new Button(comp_hostexpand_exkeys_mirror, SWT.PUSH);
		btn_exkeyNewMirr.setText("✔ gpssh-exkeys");
		btn_exkeyNewMirr.setEnabled(false);
		btn_exkeyNewMirr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Exchange keys for new host...");
				Map<String, String> passwordMap = new HashMap<String, String>();
				passwordMap.put("msdw", "123456");
				GPResultSet rs = gpControllerList.get(0).getExpandServiceProxy().expandSSHExKeys("/opt/gpinst", passwordMap);
				logInfo("Exchange keys for new host Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Composite comp_datadir_mirrinstall = new Composite(group_mirror, SWT.NO_TRIM);
		comp_datadir_mirrinstall.setLayout(new GridLayout(2, false));
		// p(4/5).准备工作目录/gpdata/mirror
		Button btn_gpdataMirr = new Button(comp_datadir_mirrinstall, SWT.PUSH);
		btn_gpdataMirr.setText("✔ /gpdata/...");
		btn_gpdataMirr.setEnabled(false);
		btn_gpdataMirr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin prepare gpdata dir...");
				GPResultSet rs;
				rs = gpControllerList.get(0).getInstallServiceProxy().gpMakeDataDir("/gpdata/mirror/", "/opt/gpinst/" + IGpExpandService.DEFAULT_HOST_EXPAND, "gpadmin");
				printResultSet(rs);
				logInfo("Make mirror dir Successed: " + rs.isSuccessed());
			}
		});
		// p(5/5).新节点安装GP
		Button btn_gpseginstallMirr = new Button(comp_datadir_mirrinstall, SWT.PUSH);
		btn_gpseginstallMirr.setText("✔ gpseginstall");
		btn_gpseginstallMirr.setEnabled(false);
		btn_gpseginstallMirr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Run gpseginstall...");
				GPResultSet rs = gpControllerList.get(0).getInstallServiceProxy().gpSegInstall("/opt/gpinst/" + IGpExpandService.DEFAULT_HOST_EXPAND, "gpadmin", "123456");
				logInfo("Run gpseginstall Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		new Label(group_mirror, SWT.NONE).setText("安装Mirror工作 3 步 <Master by gpadmin>");
		Composite comp_addmirror_inputfile = new Composite(group_mirror, SWT.BORDER);
		comp_addmirror_inputfile.setLayout(new GridLayout(3, false));
		Label lbl_addmirror_inputfile = new Label(comp_addmirror_inputfile, SWT.NONE);
		lbl_addmirror_inputfile.setText("/home/gpadmin/addmirror:");
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		lbl_addmirror_inputfile.setLayoutData(gridData);

		// s(1/3).生成addmirror配置文件
		Button btn_generateMirr = new Button(comp_addmirror_inputfile, SWT.PUSH);
		btn_generateMirr.setText("✔ 生成");
		btn_generateMirr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Generate Mirror cfg...");
				String[] paths = { "/gpdata/mirror", "/gpdata/mirror", "/gpdata/mirror" };
				GPResultSet rs = gpControllerList.get(0).getExpandServiceProxy().generateMirrorsCfg(paths, true);
				logInfo("Generate Mirror cfg Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});
		// s(2/3).修改addmirror
		Button btn_expandMirr = new Button(comp_addmirror_inputfile, SWT.PUSH);
		btn_expandMirr.setText("✘ 修改");
		btn_expandMirr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageDialog.openInformation(shell, "Tips", "暂无，敬请期待...");
			}
		});
		// s(3/3).执行Mirror扩展
		Button btn_gpaddmirrors = new Button(comp_addmirror_inputfile, SWT.PUSH);
		btn_gpaddmirrors.setText("✔ 执行gpaddmirrors");
		btn_gpaddmirrors.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		btn_gpaddmirrors.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Add Mirror By cfg...");
				GPResultSet rs = gpControllerList.get(0).getExpandServiceProxy().addMirrorByCfg();
				logInfo("Add Mirror By cfg Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});
	}

	private void createTabArea_manage(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("GP管理 Master by gpadmin");
		Composite clientArea = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(clientArea);
		clientArea.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));

		GridData gridData;
		GridLayout gridLayout = new GridLayout(1, false);
		clientArea.setLayout(gridLayout);

		gridData = new GridData(GridData.FILL_BOTH);
		Group group = new Group(clientArea, SWT.NONE);
		group.setText("GP管理工具");
		group.setLayoutData(gridData);
		group.setLayout(new GridLayout(3, false));

		Button btn_gpStart = new Button(group, SWT.PUSH);
		btn_gpStart.setText("✔ 启动集群");
		btn_gpStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin GP Startup...");
				GPResultSet rs = gpControllerList.get(0).getManageServiceProxy().gpStart("-a");
				logInfo("GP Startup Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_gpStartMaster = new Button(group, SWT.PUSH);
		btn_gpStartMaster.setText("✔ 启动Master");
		btn_gpStartMaster.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin GP Startup...");
				GPResultSet rs = gpControllerList.get(0).getManageServiceProxy().gpStart("-m");
				logInfo("GP Startup Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_gpStop = new Button(group, SWT.PUSH);
		btn_gpStop.setText("✔ 停止集群");
		btn_gpStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin GP Shutdown...");
				GPResultSet rs = gpControllerList.get(0).getManageServiceProxy().gpStop();
				logInfo("GP Shutdown Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_gpStop_M = new Button(group, SWT.PUSH);
		btn_gpStop_M.setText("✔ 快速停止集群");
		btn_gpStop_M.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin GP Quick Shutdown...");
				GPResultSet rs = gpControllerList.get(0).getManageServiceProxy().gpStop("-M fast");
				logInfo("GP Quick Shutdown Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_gpRestart = new Button(group, SWT.PUSH);
		btn_gpRestart.setText("✔ 重启集群");
		btn_gpRestart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin GP Restart...");
				GPResultSet rs = gpControllerList.get(0).getManageServiceProxy().gpStop("-r");
				logInfo("GP Restart Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_gpReload = new Button(group, SWT.PUSH);
		btn_gpReload.setText("✔ 重载配置文件");
		btn_gpReload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin GP Reload...");
				GPResultSet rs = gpControllerList.get(0).getManageServiceProxy().gpStop("-u");
				logInfo("GP Reload Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_gpState = new Button(group, SWT.PUSH);
		btn_gpState.setText("✔ gpstate 数据库运行状态");
		btn_gpState.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin GP State...");
				GPResultSet rs = gpControllerList.get(0).getManageServiceProxy().gpState();
				logInfo("GP State Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_gpCheck = new Button(group, SWT.PUSH);
		btn_gpCheck.setText("✔ gpcheck 系统环境参数检测");
		btn_gpCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin GP Check...");
				GPResultSet rs = gpControllerList.get(0).getManageServiceProxy().gpCheck("-f /opt/gpinst/all_host");
				logInfo("GP Check Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_gpCheckPerf = new Button(group, SWT.PUSH);
		btn_gpCheckPerf.setText("✔ gpcheckperf 硬件环境检测");
		btn_gpCheckPerf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin GP Check Perf...");
				GPResultSet rs = gpControllerList.get(0).getManageServiceProxy().gpCheckPerf("-f /opt/gpinst/all_host -d gpcheckperf");
				logInfo("GP Check Perf Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_gpCheckCat = new Button(group, SWT.PUSH);
		btn_gpCheckCat.setText("✔ gpcheckcat 校验数据库系统目录完整性");
		btn_gpCheckCat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin GP Check Cat...");
				GPResultSet rs = gpControllerList.get(0).getManageServiceProxy().gpCheckCat(5432, "-A");
				logInfo("GP Check Cat Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_pghbaconfCheck = new Button(group, SWT.PUSH);
		btn_pghbaconfCheck.setText("✔ pg_hba.conf 登录权限查询");
		btn_pghbaconfCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Update GP Authority...");
				List<PGHbaInfo> pgHbaInfoList = gpControllerList.get(0).getManageServiceProxy().checkAuthority("/gpdata/master/gpseg-1");
				logInfo("Update GP Authority Successed: " + (pgHbaInfoList != null));

				for (PGHbaInfo pgHbaInfo : pgHbaInfoList)
					logInfo(pgHbaInfo.toString());
			}
		});

		Button btn_pghbaconf = new Button(group, SWT.PUSH);
		btn_pghbaconf.setText("✔ pg_hba.conf 登录权限配置");
		btn_pghbaconf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String authorityString = "local   all     gpadmin ident\n" + "host    all     all     0.0.0.0/0    trust\n" + "host    all     all     192.168.0.1/28    trust\n" + "host    postgres,test     all     192.168.0.1/32    trust\n";

				CommonDialog dialog = new CommonDialog(shell);
				dialog.setTitle("修改 pg_hba.conf");
				dialog.setMessage("修改数据库访问权限");
				dialog.setInitTextContent(authorityString);
				if (dialog.open() != 0)
					return;

				List<PGHbaInfo> pgHbaInfoList = new ArrayList<PGHbaInfo>();
				String[] authorityArray = (String[]) dialog.getResultObject();
				for (String authorityStr : authorityArray) {
					authorityStr = authorityStr.replaceAll("\\ +", " ");
					authorityStr = authorityStr.replaceAll(",\\ +", ",");
					authorityStr = authorityStr.replaceAll("\\ +,", ",");
					pgHbaInfoList.add(new PGHbaInfo(authorityStr));
				}

				logInfo("Begin Update GP Authority...");
				GPResultSet rs = gpControllerList.get(0).getManageServiceProxy().updateAuthority("/gpdata/master/gpseg-1", pgHbaInfoList);
				logInfo("Update GP Authority Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});

		Button btn_listDB = new Button(group, SWT.PUSH);
		btn_listDB.setText("✔ List Databases");
		btn_listDB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// logInfo("Begin List Databases...");
				// GPResultSet rs =
				// gpControllerList.get(0).getManageServiceProxy().psql("-l");
				// logInfo("List Databases Successed: " + rs.isSuccessed());
				// printResultSet(rs);

				logInfo("Begin List Databases...");
				List<PGDatabaseInfo> databaseInfoList = gpControllerList.get(0).getManageServiceProxy().queryPGDatabaseInfo();
				logInfo("List Databases Successed: " + (databaseInfoList != null));

				if (databaseInfoList == null)
					return;

				for (PGDatabaseInfo databaseInfo : databaseInfoList)
					logInfo(databaseInfo.toString());
			}
		});

		Button btn_querySegInfo = new Button(group, SWT.PUSH);
		btn_querySegInfo.setText("✔ gp_segment_configuration");
		btn_querySegInfo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Query gp_segment_configuration...");
				List<GPSegmentInfo> segmentInfoList = gpControllerList.get(0).getManageServiceProxy().queryGPSegmentInfo();
				logInfo("Query gp_segment_configuration Successed: " + (segmentInfoList != null));

				for (GPSegmentInfo segmentInfo : segmentInfoList)
					System.out.println(segmentInfo.toString());

				for (GPSegmentInfo segmentInfo : segmentInfoList)
					logInfo(segmentInfo.toString());
			}
		});
		
		new Label(group, SWT.NONE);

		Button btn_gpconfig_get = new Button(group, SWT.PUSH);
		btn_gpconfig_get.setText("✔ gpconfig(get)");
		btn_gpconfig_get.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Get gpconfig...");
				GPConfigValue gpConfigValue = gpControllerList.get(0).getManageServiceProxy().gpconfig("max_connections");
				logInfo("Get gpconfig Successed: " + (gpConfigValue != null));
				
				if (gpConfigValue != null)
					logInfo(gpConfigValue.toString());
				
				gpConfigValue = gpControllerList.get(0).getManageServiceProxy().gpconfig("optimizer_parallel_union");
				logInfo("Get gpconfig Successed: " + (gpConfigValue != null));
				
				if (gpConfigValue != null)
					logInfo(gpConfigValue.toString());
			}
		});

		Button btn_gpconfig_set = new Button(group, SWT.PUSH);
		btn_gpconfig_set.setText("✔ gpconfig(set)");
		btn_gpconfig_set.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Set gpconfig...");
				GPResultSet rs = gpControllerList.get(0).getManageServiceProxy().gpconfig("max_connections", "750", "150");
				logInfo("Set gpconfig Successed: " + rs.isSuccessed());
				printResultSet(rs);
			}
		});
		
		Button btn_gpconfig_list = new Button(group, SWT.PUSH);
		btn_gpconfig_list.setText("✔ gpconfig(list)");
		btn_gpconfig_list.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin get gpconfig list...");
				List<GPConfig> gpconfigList = gpControllerList.get(0).getManageServiceProxy().gpconfigList();
				logInfo("Get gpconfig list Successed(" + (gpconfigList == null ? 0 : gpconfigList.size()) + "): " + (gpconfigList != null));
				
				if (gpconfigList != null)
					for (GPConfig gpconfig : gpconfigList)
						logInfo(gpconfig.toString());
				
				// 可以通过 GPConfig.getGPConfig(<attributeName>)快速获取指定的属性对象
				System.err.println("HAHA: " + GPConfig.getGPConfig("max_connections").toString());
			}
		});
		
//		Button btn_gpperfmon = new Button(group, SWT.PUSH);
//		btn_gpperfmon.setText("✔ gpperfmon");
//		btn_gpperfmon.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				logInfo("Begin install gpperfmon...");
//				List<GPConfig> gpconfigList = gpControllerList.get(0).getManageServiceProxy().gpconfigList();
//				logInfo("Get gpconfig list Successed(" + (gpconfigList == null ? 0 : gpconfigList.size()) + "): " + (gpconfigList != null));
//			}
//		});
	}

	private void createTabArea_other(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("其他");
		Composite clientArea = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(clientArea);
		clientArea.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));

		GridData gridData;
		GridLayout gridLayout = new GridLayout(1, false);
		clientArea.setLayout(gridLayout);

		gridData = new GridData(GridData.FILL_BOTH);
		Group group = new Group(clientArea, SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL);
		group.setText("其他步骤");
		group.setLayoutData(gridData);
		group.setLayout(new GridLayout(1, false));

		Button btn_gpstate_e = new Button(group, SWT.PUSH);
		btn_gpstate_e.setText("✔ 检查失败的segment镜像 <Master by gpadmin>");
		btn_gpstate_e.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin check abnormal segment...");
				GPResultSet rs = gpControllerList.get(0).getManageServiceProxy().gpState("-e");
				logInfo("Check abnormal segment Successed: " + rs.isSuccessed());
			}
		});

		Button btn_gprecoverseg = new Button(group, SWT.PUSH);
		btn_gprecoverseg.setText("✔ 恢复失败的segment镜像 <Master by gpadmin>");
		btn_gprecoverseg.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Recover Segment...");
				GPResultSet rs = gpControllerList.get(0).getManageServiceProxy().gpRecoverSeg();
				logInfo("Recover Segment Successed: " + rs.isSuccessed());
			}
		});

		Button btn_gprecoverseg_r = new Button(group, SWT.PUSH);
		btn_gprecoverseg_r.setText("✔ 恢复segment的原有角色 <Master by gpadmin>");
		btn_gprecoverseg_r.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin rebalances primary and mirror segments...");
				GPResultSet rs = gpControllerList.get(0).getManageServiceProxy().gpRecoverSeg("-r");
				logInfo("Rebalances primary and mirror segments Successed: " + rs.isSuccessed());
			}
		});

		new Label(group, SWT.NONE);
		Button btn_gpactivatestandby = new Button(group, SWT.PUSH);
		btn_gpactivatestandby.setText("✔ master/standby切换 <Standby by gpadmin>");
		btn_gpactivatestandby.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Activate Standby...");
				GPResultSet rs = gpControllerList.get(0).getManageServiceProxy().gpActivateStandby(5432, "/gpdata/master/gpseg-1");
				logInfo("Activate Standby Successed: " + rs.isSuccessed());
			}
		});

		Button btn_gpdeletesystem = new Button(group, SWT.PUSH);
		btn_gpdeletesystem.setText("✔ 删除GP数据库 <Master by gpadmin>");
		btn_gpdeletesystem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Delete System...");
				// GPResultSet rs =
				// gpControllerList.get(0).getManageServiceProxy().gpDeleteSystem("/gpdata/master/gpseg-1");
				GPResultSet rs = gpControllerList.get(0).getManageServiceProxy().gpDeleteSystem();
				logInfo("Delete System Successed: " + rs.isSuccessed());
			}
		});

		Button btn_syncHosts = new Button(group, SWT.PUSH);
		btn_syncHosts.setText("✔ 同步/etc/hosts文件 <Master by root>");
		btn_syncHosts.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Sync Hosts...");
				GPResultSet rs = gpControllerList.get(0).getInstallServiceProxy().gpScp("/opt/gpinst/all_host", "/etc/hosts");
				logInfo("Sync Hosts Successed: " + rs.isSuccessed());
			}
		});

		Button btn_syncClock = new Button(group, SWT.PUSH);
		btn_syncClock.setText("✔ 时钟同步 <Master by root>");
		btn_syncClock.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin Sync Clock...");
				GPResultSet rs = gpControllerList.get(0).getEnvServiceProxy().clockSynchronize("/opt/gpinst/all_host");
				logInfo("Sync Clock Successed: " + rs.isSuccessed());
			}
		});

		Button btn_test = new Button(group, SWT.PUSH);
		btn_test.setText("✔ test");
		btn_test.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logInfo("Begin test System...");
				for (IGPConnector controller : gpControllerList)
					System.err.println(controller.getOriginHostname());
			}
		});
	}
	
	private void createTabArea_alert(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Alerts from pg_log");
		Composite clientArea = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(clientArea);
		clientArea.setLayout(new GridLayout(1, false));

		Composite operationComp = new Composite(clientArea, SWT.NO_TRIM);
		operationComp.setLayout(new GridLayout(11, false));
		operationComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button refreshBtn = new Button(operationComp, SWT.NONE);
		refreshBtn.setText("Refresh");

		createChkButton(operationComp, "Panic", true);
		createChkButton(operationComp, "Fatal", true);
		createChkButton(operationComp, "Error", true);
		createChkButton(operationComp, "Warning", true);

		Label fillLbl = new Label(operationComp, SWT.NONE);
		fillLbl.setText("❤");
		fillLbl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final DateTime dateF = new DateTime(operationComp, SWT.DATE | SWT.LONG);
		final DateTime timeF = new DateTime(operationComp, SWT.TIME | SWT.LONG);
		new Label(operationComp, SWT.NONE).setText("~");
		final DateTime dateT = new DateTime(operationComp, SWT.DATE | SWT.LONG);
		final DateTime timeT = new DateTime(operationComp, SWT.TIME | SWT.LONG);

		Composite showComp = new Composite(clientArea, SWT.NO_TRIM);
		showComp.setLayout(new FillLayout());
		showComp.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Table table = new Table(showComp, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final String[] tableHeader = { "Time", "Severity", "Message", "User", "Database", "Host" };
		for (int i = 0; i < tableHeader.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(tableHeader[i]);
			column.pack();
		}
		refreshBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				table.removeAll();

				Calendar calendarF = Calendar.getInstance();
				calendarF.set(dateF.getYear(), dateF.getMonth(), dateF.getDay(), timeF.getHours(), timeF.getMinutes(), timeF.getSeconds());
				Calendar calendarT = Calendar.getInstance();
				calendarT.set(dateT.getYear(), dateT.getMonth(), dateT.getDay(), timeT.getHours(), timeT.getMinutes(), timeT.getSeconds());

//				List<LogAlert> logAlertList = gpControllerList.get(0).getManageServiceProxy().queryLogAlert(calendarF.getTime(), calendarT.getTime());
				List<LogAlert> logAlertList = gpControllerList.get(0).getManageServiceProxy().queryLogAlert();

				for (LogAlert logAlert : logAlertList)
					new TableItem(table, SWT.NONE).setText(new String[] { sdf_short.format(logAlert.getLogtime()), logAlert.getLogseverity(), logAlert.getLogmessage(), logAlert.getLoguser(), logAlert.getLogdatabase(), logAlert.getLoghost() });
				for (int i = 0; i < tableHeader.length; i++)
					table.getColumn(i).pack();
			}
		});
	}
	
	private void createChkButton(Composite comp, String name, boolean checked) {
		Button chkBtn = new Button(comp, SWT.CHECK);
		chkBtn.setText(name);
		chkBtn.setSelection(checked);
	}
	
	private void createTabArea_queryMonitor(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Query Monitor");
		Composite clientArea = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(clientArea);
		clientArea.setLayout(new GridLayout(1, true));
		
		final Composite operationComp = new Composite(clientArea, SWT.NONE);
		operationComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		operationComp.setLayout(new GridLayout(10, false));
		
		Button btn_refresh = new Button(operationComp, SWT.NONE);
		btn_refresh.setText("Refresh");
		Label lbl_desc = new Label(operationComp, SWT.NONE);
		lbl_desc.setText("Current queries by all users");
		lbl_desc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Label lbl_queries_running = new Label(operationComp, SWT.NONE);
		lbl_queries_running.setText("● 0 Running\t");
		lbl_queries_running.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
		final Label lbl_queries_queued = new Label(operationComp, SWT.NONE);
		lbl_queries_queued.setText("● 0 Queued\t");
		lbl_queries_queued.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
		final Label lbl_queries_blocked = new Label(operationComp, SWT.NONE);
		lbl_queries_blocked.setText("● 0 Blocked");
		lbl_queries_blocked.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		
		Composite showComp = new Composite(clientArea, SWT.BORDER);
		showComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		showComp.setLayout(new FillLayout());
		
		final TabFolder tabFolder_queries = new TabFolder(showComp, SWT.NONE);
		TabItem tabItem_queryList = new TabItem(tabFolder_queries, SWT.NONE);
		
		// Tab: Query List
		tabItem_queryList.setText("Query List (✔ Double Click to Detail ┉┉▶)");
		Composite queryListArea = new Composite(tabFolder_queries, SWT.NONE);
		tabItem_queryList.setControl(queryListArea);
		queryListArea.setLayout(new FillLayout());
		
		final Table table_queryList = new Table(queryListArea, SWT.BORDER | SWT.FULL_SELECTION);
		table_queryList.setHeaderVisible(true);
		table_queryList.setLinesVisible(true);
		
		final String[] tableHeader = { "Query ID", "Status", "User",
				"Database", "Res Queue", "Submitted", "Queued Time",
				"Run Time", "Spill Files", "Blocked by" };
		for (int i = 0; i < tableHeader.length; i++) {
			TableColumn column = new TableColumn(table_queryList, SWT.NONE);
			column.setText(tableHeader[i]);
			column.pack();
		}
		
		// Tab: Query Details
		TabItem tabItem_queryDetail = new TabItem(tabFolder_queries, SWT.NONE);
		tabItem_queryDetail.setText("Query Details");
		final Composite queryDetailArea = new Composite(tabFolder_queries, SWT.NONE);
		tabItem_queryDetail.setControl(queryDetailArea);
		queryDetailArea.setLayout(new GridLayout(1, true));
		
		final Table table_queryDetail = new Table(queryDetailArea, SWT.BORDER | SWT.FULL_SELECTION);
		table_queryDetail.setHeaderVisible(true);
		table_queryDetail.setLinesVisible(true);
		table_queryDetail.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final String[] tableDetailHeader = { "Query ID", "Status", "User",
				"Database", "Submit Time", "Wait Time", "Run Time", "End Time",
				"CPU Skew", "Row Skew", "Queue", "Priority" };
		for (int i = 0; i < tableDetailHeader.length; i++) {
			TableColumn column = new TableColumn(table_queryDetail, SWT.NONE);
			column.setText(tableDetailHeader[i]);
			column.pack();
		}
		final Text text_queryTxt = new Text(queryDetailArea, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 10;
		text_queryTxt.setLayoutData(gd);
		
		// Events
		final Calendar calendar = Calendar.getInstance();
		btn_refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IGpManageService proxy = gpControllerList.get(0).getManageServiceProxy();
				
				// Queries Count for running, queued, blocked
				List<Database> databaseList = proxy.queryDatabase();
				if (databaseList != null && databaseList.size() > 0) {
					Database database = databaseList.get(0);
					lbl_queries_running.setText("● " + database.getQueries_running() + " Running\t");
					lbl_queries_queued.setText("● " + database.getQueries_queued() + " Queued\t");
					lbl_queries_blocked.setText("● " + (
							database.getQueries_total() - 
							database.getQueries_running() - 
							database.getQueries_queued()) + " Blocked");
					operationComp.layout();
				}
				
				// Queries List
				List<Queries> queriesList = proxy.queryQueries();
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
				for (int i = 0; i < tableHeader.length; i++)
					table_queryList.getColumn(i).pack();
				
				tabFolder_queries.setSelection(0);
			}
		});
		
		table_queryList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
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
				for (int i = 0; i < tableDetailHeader.length; i++)
					table_queryDetail.getColumn(i).pack();
				
				text_queryTxt.setText(queries.getQuery_text());
				tabFolder_queries.setSelection(1);
				queryDetailArea.layout(true);
			}
		});
	}
	
	private void createTabArea_hostMetrics(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Host Metrics");
		Composite clientArea = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(clientArea);
		clientArea.setLayout(new GridLayout(1, false));
		
		Composite operationComp = new Composite(clientArea, SWT.NO_TRIM);
		operationComp.setLayout(new GridLayout(10, false));
		operationComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button btn_refresh = new Button(operationComp, SWT.NONE);
		btn_refresh.setText("Refresh");
		new Label(operationComp, SWT.NO_TRIM).setText("Realtime statistics by server");
		Label desc = new Label(operationComp, SWT.BORDER);
		desc.setText("Contribution to Skew visualization (点我！点我！点我！)");
		desc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				StringBuffer descTxtBuff = new StringBuffer();
				descTxtBuff.append("This visualization aims to give you an indication of skew contribution by calcuating each server's standard deviation from the mean:\n\n");
				descTxtBuff.append("☎ Low Skew :\n");
				descTxtBuff.append("\t Value is within 1 standard deviation from mean *\n\n");
				descTxtBuff.append("☎ Moderate Skew :\n");
				descTxtBuff.append("\t Value is between 1 and 2 standard deviations from mean\n\n");
				descTxtBuff.append("☎ High Skew :\n");
				descTxtBuff.append("\t Value is between 2 and 3 standard deviations from mean\n\n");
				descTxtBuff.append("☎ Very High Skew :\n");
				descTxtBuff.append("\t Value is above 3 standard deviations from mean\n\n");
				descTxtBuff.append("* If variance of set is < 3 skew is considered very low regardless of deviations from mean");
				
				MessageDialog.openInformation(shell, "Contribution to Skew visualization", descTxtBuff.toString());
			}
		});
		
		Composite showComp = new Composite(clientArea, SWT.BORDER);
		showComp.setLayout(new FillLayout());
		showComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		final Table table = new Table(showComp, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final String[] tableHeader = { "Hostname", 
				"CPU Total/Sys/User (%)", 
				"Memory in Use(%)", 
				"Disk R(MB/s) Skew", 
				"Disk W(MB/s) Skew", 
				"Net R(MB/s) Skew", 
				"Net W(MB/s) Skew" };
		
		for (int i = 0; i < tableHeader.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(tableHeader[i]);
			column.pack();
		}
		
		btn_refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				table.removeAll();

				List<com.txdb.gpmanage.core.gp.entry.gpmon.System> systemList = gpControllerList.get(0).getManageServiceProxy().querySystem();
				for (com.txdb.gpmanage.core.gp.entry.gpmon.System system : systemList)
					new TableItem(table, SWT.NONE).setText(new String[] { 
							String.valueOf(system.getHostname()),
							String.valueOf(system.getCpu_used()),
							String.valueOf(system.getMem_used_percent()),
							String.valueOf(formatPoint2((double) system.getDisk_rb_rate() / 1024 / 1024)),
							String.valueOf(formatPoint2((double) system.getDisk_wb_rate() / 1024 / 1024)),
							String.valueOf(formatPoint2((double) system.getNet_rb_rate() / 1024 / 1024)),
							String.valueOf(formatPoint2((double) system.getNet_wb_rate() / 1024 / 1024)) });
				for (int i = 0; i < tableHeader.length; i++)
					table.getColumn(i).pack();
			}
		});
	}
	
	private void createTabArea_hostMetrics_charts(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("● Host Metrics");
		Composite clientArea = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(clientArea);
		clientArea.setLayout(new GridLayout(1, false));
		
		Composite operationComp = new Composite(clientArea, SWT.NO_TRIM);
		operationComp.setLayout(new GridLayout(10, false));
		operationComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button btn_monitorStart = new Button(operationComp, SWT.NONE);
		btn_monitorStart.setText("开启监控(Update per 15s)");
		Button btn_monitorStop = new Button(operationComp, SWT.NONE);
		btn_monitorStop.setText("停止监控");
		new Label(operationComp, SWT.NO_TRIM).setText("Realtime statistics by server");
		Label desc = new Label(operationComp, SWT.BORDER);
		desc.setText("Contribution to Skew visualization (点我！点我！点我！)");
		desc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				StringBuffer descTxtBuff = new StringBuffer();
				descTxtBuff.append("This visualization aims to give you an indication of skew contribution by calcuating each server's standard deviation from the mean:\n\n");
				descTxtBuff.append("☎ Low Skew :\n");
				descTxtBuff.append("\t Value is within 1 standard deviation from mean *\n\n");
				descTxtBuff.append("☎ Moderate Skew :\n");
				descTxtBuff.append("\t Value is between 1 and 2 standard deviations from mean\n\n");
				descTxtBuff.append("☎ High Skew :\n");
				descTxtBuff.append("\t Value is between 2 and 3 standard deviations from mean\n\n");
				descTxtBuff.append("☎ Very High Skew :\n");
				descTxtBuff.append("\t Value is above 3 standard deviations from mean\n\n");
				descTxtBuff.append("* If variance of set is < 3 skew is considered very low regardless of deviations from mean");
				
				MessageDialog.openInformation(shell, "Contribution to Skew visualization", descTxtBuff.toString());
			}
		});
		
		final TabFolder tabFolder_hostMetrics = new TabFolder(clientArea, SWT.NONE);
		tabFolder_hostMetrics.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// CPU Chart
		TabItem tabItem_cpu = new TabItem(tabFolder_hostMetrics, SWT.NONE);
		tabItem_cpu.setText("CPU Usage (%) ");
		Composite clientArea_cpu = new Composite(tabFolder_hostMetrics, SWT.NONE);
		tabItem_cpu.setControl(clientArea_cpu);
		clientArea_cpu.setLayout(new GridLayout(1, true));
		final Browser browser_cpu = new Browser(clientArea_cpu, SWT.BORDER);
		browser_cpu.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// Memory Chart
		TabItem tabItem_mem = new TabItem(tabFolder_hostMetrics, SWT.NONE);
		tabItem_mem.setText("Memory Usage (%) ");
		Composite clientArea_mem = new Composite(tabFolder_hostMetrics, SWT.NONE);
		tabItem_mem.setControl(clientArea_mem);
		clientArea_mem.setLayout(new GridLayout(1, true));
		final Browser browser_memory = new Browser(clientArea_mem, SWT.BORDER);
		browser_memory.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// Skew Chart
		TabItem tabItem_disk_net = new TabItem(tabFolder_hostMetrics, SWT.NONE);
		tabItem_disk_net.setText("Disk && Net Skew (MB/s) ");
		Composite clientArea_disk_net = new Composite(tabFolder_hostMetrics, SWT.NONE);
		tabItem_disk_net.setControl(clientArea_disk_net);
		clientArea_disk_net.setLayout(new GridLayout(1, true));
		final Browser browser_skew = new Browser(clientArea_disk_net, SWT.BORDER);
		browser_skew.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// Refresh for each 15s
		final long timeInterval = 15000;
		final List<MonitorTaskThread> taskList = new ArrayList<MonitorTaskThread>();
		btn_monitorStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MonitorTaskThread taskThread = new MonitorTaskThread() {
					@Override
					public void taskBody() {
						// TODO Auto-generated method stub
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								List<com.txdb.gpmanage.core.gp.entry.gpmon.System> systemList = gpControllerList.get(0).getManageServiceProxy().querySystem();
								
								// >>>>>>>>>>>>>>>> Notify Charts
								try {
									String systemListJson = JsonUtils.toJsonArray(systemList, true);
									String result = MonitorController.getInstance().updateSystemNow(MONITOR_SAMPLE, systemListJson);
									logInfo(result);
								} catch (IOException e) {
									logError(e.getMessage());
								}
								// Show Charts (仅需加载一次即可)
								if (browser_cpu.getData("loaded") == null) {
									// 依次切换到每个标签，用来解决图表指定宽高百分比时，不能显示的问题（原因未知）
									int ic = tabFolder_hostMetrics.getItemCount();
									for (int i = 0; i < ic; i++)
										tabFolder_hostMetrics.setSelection(i);
									tabFolder_hostMetrics.setSelection(0);
									
									browser_cpu.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/hostmetrics/host_usage_cpu.jsp");
									browser_memory.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/hostmetrics/host_usage_memory.jsp");
									browser_skew.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/hostmetrics/host_usage_skew.jsp");
									browser_cpu.setData("loaded", true);
								}
							}
						});
						try {
							Thread.sleep(timeInterval);
						} catch (InterruptedException e) {}
					}
				};
				taskList.add(taskThread);
				taskThread.start();
			}
		});
		btn_monitorStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (MonitorTaskThread task : taskList)
					task.stopMe();
				taskList.clear();
			}
		});
	}
	
	private void createTabArea_clusterMetrics(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Cluster Metrics");
		Composite clientArea = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(clientArea);
		clientArea.setLayout(new GridLayout(1, false));
		
		Composite operationComp = new Composite(clientArea, SWT.NO_TRIM);
		operationComp.setLayout(new GridLayout(10, false));
		operationComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button btn_refresh = new Button(operationComp, SWT.NONE);
		btn_refresh.setText("Refresh");
		new Label(operationComp, SWT.NO_TRIM).setText("All hosts excluding Master and Standby Master (计算显示集群各项指标的平均值)");
		
		Composite showComp = new Composite(clientArea, SWT.NO_TRIM);
		showComp.setLayout(new FillLayout());
		showComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		TabFolder clusterTabFolder = new TabFolder(showComp, SWT.NONE);
		
		// TODO
		Composite area_queries = createTabItem(clusterTabFolder, "Queries");
		final String[] header_queries = new String[] { "ctime", "queries_total", "queries_running", "queries_queued" };
		final Table table_queries = createTable(area_queries, header_queries);
		
		// TODO
		Composite area_cpu = createTabItem(clusterTabFolder, "CPU");
		final String[] header_cpu = new String[] { "ctime", "average_system", "average_user", "average_idle" };
		final Table table_cpu = createTable(area_cpu, header_cpu);
		
		// TODO
		Composite area_mem = createTabItem(clusterTabFolder, "Memory");
		final String[] header_mem = new String[] { "ctime", "In Use(%)" };
		final Table table_mem = createTable(area_mem, header_mem);
		
		// TODO
		Composite area_disk = createTabItem(clusterTabFolder, "Disk I/O");
		final String[] header_disk = new String[] { "ctime", "Read MB/s", "Write MB/s" };
		final Table table_disk = createTable(area_disk, header_disk);
		
		// TODO
		Composite area_net = createTabItem(clusterTabFolder, "Network");
		final String[] header_net = new String[] { "ctime", "Read MB/s", "Write MB/s" };
		final Table table_net = createTable(area_net, header_net);

		// TODO
		Composite area_load = createTabItem(clusterTabFolder, "Load");
		final String[] header_load = new String[] { "ctime", "1 min", "5 min", "15 min" };
		final Table table_load = createTable(area_load, header_load);
		
		// TODO
		Composite area_swap = createTabItem(clusterTabFolder, "Swap");
		final String[] header_swap = new String[] { "ctime", "In Use(%)" };
		final Table table_swap = createTable(area_swap, header_swap);
		
		
		btn_refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
				// 1.0 Queries
				table_queries.removeAll();
				List<Database> databaseList = gpControllerList.get(0).getManageServiceProxy().queryDatabase();
				for (Database database : databaseList)
					new TableItem(table_queries, SWT.NONE).setText(new String[] { 
							sdf.format(database.getCtime()),
							String.valueOf(database.getQueries_total()),
							String.valueOf(database.getQueries_running()),
							String.valueOf(database.getQueries_queued()) });
				for (int i = 0; i < header_queries.length; i++)
					table_queries.getColumn(i).pack();
				
				// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
				List<com.txdb.gpmanage.core.gp.entry.gpmon.System> systemList = gpControllerList.get(0).getManageServiceProxy().querySystem();
				String ctime = sdf.format(systemList.get(0).getCtime());
				
				double sum_cpu_user = 0, sum_cpu_sys = 0, sum_cpu_idle = 0;
				double ave_cpu_user = 0, ave_cpu_sys = 0, ave_cpu_idle = 0;
				
				long sum_mem_actual_used = 0, sum_mem_total = 0;
				double mem_actual_used_percent = 0;

				long /*sum_disk_ro = 0, */sum_disk_rb = 0, /*sum_disk_wo = 0, */sum_disk_wb = 0;
				long /*ave_disk_ro = 0, */ave_disk_rb = 0, /*ave_disk_wo = 0, */ave_disk_wb = 0;
				
				long /*sum_net_rp = 0, */sum_net_rb = 0, /*sum_net_wp = 0, */sum_net_wb = 0;
				long /*ave_net_rp = 0, */ave_net_rb = 0, /*ave_net_wp = 0, */ave_net_wb = 0;

				double sum_load0 = 0, sum_load1 = 0, sum_load2 = 0;
				double ave_load0 = 0, ave_load1 = 0, ave_load2 = 0;

				long sum_swap_used = 0, sum_swap_total = 0;
				double swap_used_percent = 0;
				
				for (com.txdb.gpmanage.core.gp.entry.gpmon.System system : systemList) {
					sum_cpu_user += system.getCpu_user();
					sum_cpu_sys += system.getCpu_sys();
					sum_cpu_idle += system.getCpu_idle();
					
					sum_mem_actual_used += system.getMem_actual_used();
					sum_mem_total += system.getMem_total();
					
//					sum_disk_ro += system.getDisk_ro_rate();
					sum_disk_rb += system.getDisk_rb_rate();
//					sum_disk_wo += system.getDisk_wo_rate();
					sum_disk_wb += system.getDisk_wb_rate();
					
//					sum_net_rp += system.getNet_rp_rate();
					sum_net_rb += system.getNet_rb_rate();
//					sum_net_wp += system.getNet_wp_rate();
					sum_net_wb += system.getNet_wb_rate();
					
					sum_load0 += system.getLoad0();
					sum_load1 += system.getLoad1();
					sum_load2 += system.getLoad2();
					
					sum_swap_used += system.getSwap_used();
					sum_swap_total += system.getSwap_total();
				}
				// Figure Average
				ave_cpu_user = formatPoint2(sum_cpu_user / systemList.size());
				ave_cpu_sys = formatPoint2(sum_cpu_sys / systemList.size());
				ave_cpu_idle = formatPoint2(sum_cpu_idle / systemList.size());
				
				mem_actual_used_percent = formatPoint2(sum_mem_actual_used / (double)sum_mem_total * 100);
				
//				ave_disk_ro = sum_disk_ro / systemList.size();
				ave_disk_rb = sum_disk_rb / systemList.size();
//				ave_disk_wo = sum_disk_wo / systemList.size();
				ave_disk_wb = sum_disk_wb / systemList.size();
				
//				ave_net_rp = sum_net_rp / systemList.size();
				ave_net_rb = sum_net_rb / systemList.size();
//				ave_net_wp = sum_net_wp / systemList.size();
				ave_net_wb = sum_net_wb / systemList.size();
				
				ave_load0 = formatPoint2(sum_load0 / systemList.size());
				ave_load1 = formatPoint2(sum_load1 / systemList.size());
				ave_load2 = formatPoint2(sum_load2 / systemList.size());
				
				swap_used_percent = formatPoint2(sum_swap_used / (double)sum_swap_total * 100);
				
				// 2.0 CPU
				table_cpu.removeAll();
				new TableItem(table_cpu, SWT.NONE).setText(new String[] {ctime, 
						String.valueOf(ave_cpu_sys), String.valueOf(ave_cpu_user), String.valueOf(ave_cpu_idle)});
				for (int i = 0; i < header_cpu.length; i++)
					table_cpu.getColumn(i).pack();
				
				// 2.1 Memory
				table_mem.removeAll();
				new TableItem(table_mem, SWT.NONE).setText(new String[] { ctime, String.valueOf(mem_actual_used_percent) });
				for (int i = 0; i < header_mem.length; i++)
					table_mem.getColumn(i).pack();
				
				// 2.2 Disk I/O
				table_disk.removeAll();
				new TableItem(table_disk, SWT.NONE).setText(new String[] { ctime, 
						String.valueOf(formatPoint2((double) ave_disk_rb / 1024 / 1024)),
						String.valueOf(formatPoint2((double) ave_disk_wb / 1024 / 1024)) });
				for (int i = 0; i < header_disk.length; i++)
					table_disk.getColumn(i).pack();
				
				// 2.3 Network
				table_net.removeAll();
				new TableItem(table_net, SWT.NONE).setText(new String[] { ctime, 
						String.valueOf(formatPoint2((double) ave_net_rb / 1024 / 1024)), 
						String.valueOf(formatPoint2((double) ave_net_wb / 1024 / 1024)) });
				for (int i = 0; i < header_net.length; i++)
					table_net.getColumn(i).pack();
				
				// 2.4 Load
				table_load.removeAll();
				new TableItem(table_load, SWT.NONE).setText(new String[] { ctime, 
						String.valueOf(ave_load0), String.valueOf(ave_load1), String.valueOf(ave_load2)});
				for (int i = 0; i < header_load.length; i++)
					table_load.getColumn(i).pack();
				
				// 2.5 Swap
				table_swap.removeAll();
				new TableItem(table_swap, SWT.NONE).setText(new String[] { ctime, String.valueOf(swap_used_percent) });
				for (int i = 0; i < header_swap.length; i++)
					table_swap.getColumn(i).pack();
			}
		});
	}
	
	private void createTabArea_clusterMetrics_charts(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("● Cluster Metrics");
		Composite clientArea = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(clientArea);
		clientArea.setLayout(new GridLayout(1, false));
		
		Composite operationComp = new Composite(clientArea, SWT.NO_TRIM);
		operationComp.setLayout(new GridLayout(10, false));
		operationComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button btn_monitorStart = new Button(operationComp, SWT.NONE);
		btn_monitorStart.setText("开启监控(Update per 15s)");
		Button btn_monitorStop = new Button(operationComp, SWT.NONE);
		btn_monitorStop.setText("停止监控");
		new Label(operationComp, SWT.NO_TRIM).setText("All hosts excluding Master and Standby Master (计算显示集群各项指标的平均值)");
		
		Composite showComp = new Composite(clientArea, SWT.NO_TRIM);
		showComp.setLayout(new FillLayout());
		showComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		final TabFolder clusterTabFolder = new TabFolder(showComp, SWT.NONE);
		
		// TODO
		Composite area_queries = createTabItem(clusterTabFolder, "Queries");
		final Browser browser_queries = new Browser(area_queries, SWT.BORDER);
		
		// TODO
		Composite area_cpu = createTabItem(clusterTabFolder, "CPU");
		final Browser browser_cpu = new Browser(area_cpu, SWT.BORDER);
		
		// TODO
		Composite area_mem = createTabItem(clusterTabFolder, "Memory");
		final Browser browser_mem = new Browser(area_mem, SWT.BORDER);
		
		// TODO
		Composite area_disk = createTabItem(clusterTabFolder, "Disk I/O");
		final Browser browser_disk = new Browser(area_disk, SWT.BORDER);
		
		// TODO
		Composite area_net = createTabItem(clusterTabFolder, "Network");
		final Browser browser_net = new Browser(area_net, SWT.BORDER);
		
		// TODO
		Composite area_load = createTabItem(clusterTabFolder, "Load");
		final Browser browser_load = new Browser(area_load, SWT.BORDER);
		
		// TODO
		Composite area_swap = createTabItem(clusterTabFolder, "Swap");
		final Browser browser_swap = new Browser(area_swap, SWT.BORDER);
		
		// Refresh for each 15s
		final long timeInterval = 15000;
		final List<MonitorTaskThread> taskList = new ArrayList<MonitorTaskThread>();
		btn_monitorStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MonitorTaskThread taskThread = new MonitorTaskThread() {
					@Override
					public void taskBody() {
						// TODO Auto-generated method stub
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								List<Database> databaseList = gpControllerList.get(0).getManageServiceProxy().queryDatabase();
								List<com.txdb.gpmanage.core.gp.entry.gpmon.System> systemList = gpControllerList.get(0).getManageServiceProxy().querySystem();
								
								// >>>>>>>>>>>>>>>> Notify Charts
								try {
									String databaseListJson = JsonUtils.toJsonArray(databaseList, true);
									String result = MonitorController.getInstance().updateDatabaseNow(MONITOR_SAMPLE, databaseListJson);
									logInfo(result);
								} catch (IOException e) {
									logError(e.getMessage());
								}
								try {
									String systemListJson = JsonUtils.toJsonArray(systemList, true);
									String result = MonitorController.getInstance().updateSystemNow(MONITOR_SAMPLE, systemListJson);
									logInfo(result);
								} catch (IOException e) {
									logError(e.getMessage());
								}
								// Show Charts (仅需加载一次即可)
								if (browser_queries.getData("loaded") == null) {
									// 依次切换到每个标签，用来解决图表指定宽高百分比时，不能显示的问题（原因未知）
									int ic = clusterTabFolder.getItemCount();
									for (int i = 0; i < ic; i++)
										clusterTabFolder.setSelection(i);
									clusterTabFolder.setSelection(0);
									
									browser_queries.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_queries.jsp");
									browser_cpu.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_cpu.jsp");
									browser_mem.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_memory.jsp");
									browser_disk.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_diskio.jsp");
									browser_net.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_network.jsp");
									browser_load.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_load.jsp");
									browser_swap.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_swap.jsp");
									browser_queries.setData("loaded", true);
								}
							}
						});
						try {
							Thread.sleep(timeInterval);
						} catch (InterruptedException e) {}
					}
				};
				taskList.add(taskThread);
				taskThread.start();
			}
		});
		btn_monitorStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (MonitorTaskThread task : taskList)
					task.stopMe();
				taskList.clear();
			}
		});
	}
	
	private void createTabArea_history(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Histoy");
		Composite clientArea = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(clientArea);
		clientArea.setLayout(new GridLayout(1, false));
		
		Composite operationComp = new Composite(clientArea, SWT.NO_TRIM);
		operationComp.setLayout(new GridLayout(10, false));
		operationComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button btn_refresh = new Button(operationComp, SWT.NONE);
		btn_refresh.setText("Refresh");
		new Label(operationComp, SWT.NO_TRIM).setText("Historical Queries & Metrics");
		
		final DateTime dateF = new DateTime(operationComp, SWT.DATE | SWT.LONG);
		final DateTime timeF = new DateTime(operationComp, SWT.TIME | SWT.LONG);
		new Label(operationComp, SWT.NONE).setText("~");
		final DateTime dateT = new DateTime(operationComp, SWT.DATE | SWT.LONG);
		final DateTime timeT = new DateTime(operationComp, SWT.TIME | SWT.LONG);
		
		final Calendar calendar = Calendar.getInstance();
		
		Composite showComp = new Composite(clientArea, SWT.NO_TRIM);
		showComp.setLayout(new FillLayout());
		showComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		final TabFolder clusterTabFolder = new TabFolder(showComp, SWT.NONE);
		
		// TODO
		Composite area_queries = createTabItem(clusterTabFolder, "Queries");
		final String[] header_queries = new String[] { "ctime", "queries_total", "queries_running", "queries_queued" };
		final Table table_queries = createTable(area_queries, header_queries);
		
		// TODO
		Composite area_cpu = createTabItem(clusterTabFolder, "CPU");
		final String[] header_cpu = new String[] { "ctime", "average_system", "average_user", "average_idle" };
		final Table table_cpu = createTable(area_cpu, header_cpu);
		
		// TODO
		Composite area_mem = createTabItem(clusterTabFolder, "Memory");
		final String[] header_mem = new String[] { "ctime", "In Use(%)" };
		final Table table_mem = createTable(area_mem, header_mem);
		
		// TODO
		Composite area_disk = createTabItem(clusterTabFolder, "Disk I/O");
		final String[] header_disk = new String[] { "ctime", "Read MB/s", "Write MB/s" };
		final Table table_disk = createTable(area_disk, header_disk);
		
		// TODO
		Composite area_net = createTabItem(clusterTabFolder, "Network");
		final String[] header_net = new String[] { "ctime", "Read MB/s", "Write MB/s" };
		final Table table_net = createTable(area_net, header_net);

		// TODO
		Composite area_load = createTabItem(clusterTabFolder, "Load");
		final String[] header_load = new String[] { "ctime", "1 min", "5 min", "15 min" };
		final Table table_load = createTable(area_load, header_load);
		
		// TODO
		Composite area_swap = createTabItem(clusterTabFolder, "Swap");
		final String[] header_swap = new String[] { "ctime", "In Use(%)" };
		final Table table_swap = createTable(area_swap, header_swap);
		
		// TODO
		Composite area_queriesList = createTabItem(clusterTabFolder, "QueriesList (✔ Double Click to Detail ┉┉▶)");
		final String[] header_queriesList = new String[] { "Query ID",
				"Status", "User", "Database", "Submitted", "Queued Time",
				"Run Time", "Ended", "CPU Skew", "Row Skew", "Queue",
				"Priority" };
		final Table table_queriesList = createTable(area_queriesList, header_queriesList);
		
		// TODO
		final Composite area_queriesDetails = createTabItem(clusterTabFolder, "QueriesDetails");
		area_queriesDetails.setLayout(new GridLayout(1, true));
		final String[] header_queriesDetails = { "Query ID", "Status", "User",
				"Database", "Submit Time", "Wait Time", "Run Time", "End Time",
				"CPU Skew", "Row Skew", "Queue", "Priority" };
		final Table table_queriesDetails = createTable(area_queriesDetails, header_queriesDetails);
		table_queriesDetails.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Text text_queryTxt = new Text(area_queriesDetails, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 10;
		text_queryTxt.setLayoutData(gd);
		
		btn_refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
				Calendar calendarF = Calendar.getInstance();
				calendarF.set(dateF.getYear(), dateF.getMonth(), dateF.getDay(), timeF.getHours(), timeF.getMinutes(), timeF.getSeconds());
				Calendar calendarT = Calendar.getInstance();
				calendarT.set(dateT.getYear(), dateT.getMonth(), dateT.getDay(), timeT.getHours(), timeT.getMinutes(), timeT.getSeconds());
				IGpManageService proxy = gpControllerList.get(0).getManageServiceProxy();
				
				// 1.0 Queries
				table_queries.removeAll();
				List<Database> databaseList = proxy.queryDatabase(calendarF.getTime(), calendarT.getTime(), true);
				for (Database database : databaseList)
					new TableItem(table_queries, SWT.NONE).setText(new String[] { 
							sdf.format(database.getCtime()),
							String.valueOf(database.getQueries_total()),
							String.valueOf(database.getQueries_running()),
							String.valueOf(database.getQueries_queued()) });
				for (int i = 0; i < header_queries.length; i++)
					table_queries.getColumn(i).pack();
				
				// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
				table_cpu.removeAll();
				table_mem.removeAll();
				table_disk.removeAll();
				table_net.removeAll();
				table_load.removeAll();
				table_swap.removeAll();
				List<com.txdb.gpmanage.core.gp.entry.gpmon.System> systemList = proxy.querySystemAvg(calendarF.getTime(), calendarT.getTime(), true);
				for (com.txdb.gpmanage.core.gp.entry.gpmon.System system : systemList) {
					// 2.0 CPU
					new TableItem(table_cpu, SWT.NONE).setText(new String[] { sdf.format(system.getCtime()), 
							String.valueOf(system.getCpu_sys()), String.valueOf(system.getCpu_user()), String.valueOf(system.getCpu_idle())});

					// 2.1 Memory
					new TableItem(table_mem, SWT.NONE).setText(new String[] { sdf.format(system.getCtime()), 
							String.valueOf(system.getMem_used_percent()) });

					// 2.2 Disk I/O
					new TableItem(table_disk, SWT.NONE).setText(new String[] { sdf.format(system.getCtime()), 
							String.valueOf(formatPoint2((double) system.getDisk_rb_rate() / 1024 / 1024)),
							String.valueOf(formatPoint2((double) system.getDisk_wb_rate() / 1024 / 1024)) });

					// 2.3 Network
					new TableItem(table_net, SWT.NONE).setText(new String[] { sdf.format(system.getCtime()), 
							String.valueOf(formatPoint2((double) system.getNet_rb_rate() / 1024 / 1024)), 
							String.valueOf(formatPoint2((double) system.getNet_wb_rate() / 1024 / 1024)) });

					// 2.4 Load
					new TableItem(table_load, SWT.NONE).setText(new String[] { sdf.format(system.getCtime()), 
							String.valueOf(system.getLoad0()), String.valueOf(system.getLoad1()), String.valueOf(system.getLoad2())});

					// 2.5 Swap
					new TableItem(table_swap, SWT.NONE).setText(new String[] { sdf.format(system.getCtime()), 
							String.valueOf(formatPoint2(system.getSwap_used() / (double) system.getSwap_total() * 100)) });
				}
				for (int i = 0; i < header_cpu.length; i++)
					table_cpu.getColumn(i).pack();
				for (int i = 0; i < header_mem.length; i++)
					table_mem.getColumn(i).pack();
				for (int i = 0; i < header_disk.length; i++)
					table_disk.getColumn(i).pack();
				for (int i = 0; i < header_net.length; i++)
					table_net.getColumn(i).pack();
				for (int i = 0; i < header_load.length; i++)
					table_load.getColumn(i).pack();
				for (int i = 0; i < header_swap.length; i++)
					table_swap.getColumn(i).pack();
				
				// 3.0 Queries List
				table_queriesList.removeAll();
				List<Queries> queriesList = proxy.queryQueries(calendarF.getTime(), calendarT.getTime(), true);
				for (Queries queries : queriesList) {
					long milliseconds_qtime = queries.getTstart().getTime() - queries.getTsubmit().getTime();
					long milliseconds_rtime = queries.getTfinish().getTime() - queries.getTstart().getTime();
					
					calendar.setTimeInMillis(milliseconds_qtime);
					String qtimeStr = sdf_ms.format(calendar.getTime());
					
					calendar.setTimeInMillis(milliseconds_rtime);
					String rtimeStr = sdf_ms.format(calendar.getTime());
					
					TableItem item = new TableItem(table_queriesList, SWT.NONE);
					item.setData(queries);
					item.setText(new String[] { 
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
							queries.getRqppriority()
					});
				}
				for (int i = 0; i < header_queriesList.length; i++)
					table_queriesList.getColumn(i).pack();
			}
		});
		
		table_queriesList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TableItem[] items = table_queriesList.getSelection();
				if (items.length <= 0)
					return;
				
				Queries queries = (Queries) items[0].getData();
				
				long milliseconds_qtime = queries.getTstart().getTime() - queries.getTsubmit().getTime();
				long milliseconds_rtime = queries.getTfinish().getTime() - queries.getTstart().getTime();
				
				calendar.setTimeInMillis(milliseconds_qtime);
				String qtimeStr = sdf_ms.format(calendar.getTime());
				
				calendar.setTimeInMillis(milliseconds_rtime);
				String rtimeStr = sdf_ms.format(calendar.getTime());
				
				table_queriesDetails.removeAll();
				TableItem tableItem = new TableItem(table_queriesDetails, SWT.NONE);
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
				for (int i = 0; i < header_queriesDetails.length; i++)
					table_queriesDetails.getColumn(i).pack();
				
				text_queryTxt.setText(queries.getQuery_text());
				clusterTabFolder.setSelection(8);
				area_queriesDetails.layout(true);
			}
		});
	}
	
	private void createTabArea_history_chart(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("● Histoy");
		Composite clientArea = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(clientArea);
		clientArea.setLayout(new GridLayout(1, false));
		
		Composite operationComp = new Composite(clientArea, SWT.NO_TRIM);
		operationComp.setLayout(new GridLayout(10, false));
		operationComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button btn_refresh = new Button(operationComp, SWT.NONE);
		btn_refresh.setText("Refresh");
		new Label(operationComp, SWT.NO_TRIM).setText("Historical Queries & Metrics");
		
		final DateTime dateF = new DateTime(operationComp, SWT.DATE | SWT.LONG);
		final DateTime timeF = new DateTime(operationComp, SWT.TIME | SWT.LONG);
		new Label(operationComp, SWT.NONE).setText("~");
		final DateTime dateT = new DateTime(operationComp, SWT.DATE | SWT.LONG);
		final DateTime timeT = new DateTime(operationComp, SWT.TIME | SWT.LONG);
		
		Composite showComp = new Composite(clientArea, SWT.NO_TRIM);
		showComp.setLayout(new FillLayout());
		showComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		final TabFolder historyTabFolder = new TabFolder(showComp, SWT.NONE);
		
		// TODO
		Composite area_queries = createTabItem(historyTabFolder, "Queries");
		final Browser browser_queries = new Browser(area_queries, SWT.BORDER);
		
		// TODO
		Composite area_cpu = createTabItem(historyTabFolder, "CPU");
		final Browser browser_cpu = new Browser(area_cpu, SWT.BORDER);
		
		// TODO
		Composite area_mem = createTabItem(historyTabFolder, "Memory");
		final Browser browser_mem = new Browser(area_mem, SWT.BORDER);
		
		// TODO
		Composite area_disk = createTabItem(historyTabFolder, "Disk I/O");
		final Browser browser_disk = new Browser(area_disk, SWT.BORDER);
		
		// TODO
		Composite area_net = createTabItem(historyTabFolder, "Network");
		final Browser browser_net = new Browser(area_net, SWT.BORDER);
		
		// TODO
		Composite area_load = createTabItem(historyTabFolder, "Load");
		final Browser browser_load = new Browser(area_load, SWT.BORDER);
		
		// TODO
		Composite area_swap = createTabItem(historyTabFolder, "Swap");
		final Browser browser_swap = new Browser(area_swap, SWT.BORDER);
		
		btn_refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Thread taskThread = new Thread() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
								Calendar calendarF = Calendar.getInstance();
								calendarF.set(dateF.getYear(), dateF.getMonth(), dateF.getDay(), timeF.getHours(), timeF.getMinutes(), timeF.getSeconds());
								Calendar calendarT = Calendar.getInstance();
								calendarT.set(dateT.getYear(), dateT.getMonth(), dateT.getDay(), timeT.getHours(), timeT.getMinutes(), timeT.getSeconds());
								IGpManageService proxy = gpControllerList.get(0).getManageServiceProxy();
								
								// 1.0 Queries
								List<Database> databaseHistoryList = proxy.queryDatabase(calendarF.getTime(), calendarT.getTime(), true);
								// 2.0 Others (cpu, memory, diskio, netio, load, swap)
								List<com.txdb.gpmanage.core.gp.entry.gpmon.System> systemHistoryList = proxy.querySystemAvg(calendarF.getTime(), calendarT.getTime(), true);
								
								// >>>>>>>>>>>>>>>> Notify Charts
								try {
									String databaseListJson = JsonUtils.toJsonArray(databaseHistoryList, true);
									String result = MonitorController.getInstance().updateDatabaseHistory(MONITOR_SAMPLE, databaseListJson);
									logInfo(result);
								} catch (IOException e) {
									logError(e.getMessage());
								}
								try {
									String systemListJson = JsonUtils.toJsonArray(systemHistoryList, true);
									String result = MonitorController.getInstance().updateSystemHistory(MONITOR_SAMPLE, systemListJson);
									logInfo(result);
								} catch (IOException e) {
									logError(e.getMessage());
								}
								
								int ic = historyTabFolder.getItemCount();
								for (int i = 0; i < ic; i++)
									historyTabFolder.setSelection(i);
								historyTabFolder.setSelection(0);
								
								browser_queries.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_queries.jsp");
								browser_cpu.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_cpu.jsp");
								browser_mem.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_memory.jsp");
								browser_disk.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_diskio.jsp");
								browser_net.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_network.jsp");
								browser_load.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_load.jsp");
								browser_swap.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_swap.jsp");
							}
						});
					}
				};
				taskThread.start();
			}
		});
	}
	
	private void createTabArea_system(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("System");
		Composite clientArea = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(clientArea);
		clientArea.setLayout(new FillLayout());
		TabFolder systemTabFolder = new TabFolder(clientArea, SWT.NONE);
		GridData gd;
		
		// TODO
		Composite comp_Segment = createTabItem(systemTabFolder, "Segment Status(Role and health related information)");
		comp_Segment.setLayout(new GridLayout(2, true));
		
		Button segRefresh = new Button(comp_Segment, SWT.NONE);
		segRefresh.setText("Refresh Segment Status");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		segRefresh.setLayoutData(gd);
		
		// Segment Summary
		Composite compSegSummary = new Composite(comp_Segment, SWT.NO_TRIM);
		compSegSummary.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// >>> Content & Template Values
		compSegSummary.setLayout(new GridLayout(2, true));
		Label lbl_DatabaseState = new Label(compSegSummary, SWT.BORDER);
		lbl_DatabaseState.setText("☎ Database State :");
		lbl_DatabaseState.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label lbl_TotalSegments = new Label(compSegSummary, SWT.BORDER);
		lbl_TotalSegments.setText("☎ Total Segments :");
		lbl_TotalSegments.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Label lbl_v1 = new Label(compSegSummary, SWT.NONE);
		lbl_v1.setLayoutData(new GridData(GridData.FILL_BOTH));
		lbl_v1.setAlignment(SWT.CENTER);
		final Label lbl_v2 = new Label(compSegSummary, SWT.NONE);
		lbl_v2.setLayoutData(new GridData(GridData.FILL_BOTH));
		lbl_v2.setAlignment(SWT.CENTER);
		Label lbl_MirrorsPrimary = new Label(compSegSummary, SWT.BORDER);
		lbl_MirrorsPrimary.setText("☎ Mirrors Acting as Primary :");
		lbl_MirrorsPrimary.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label lbl_SegmentHosts = new Label(compSegSummary, SWT.BORDER);
		lbl_SegmentHosts.setText("☎ Segment Hosts :");
		lbl_SegmentHosts.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Label lbl_v3 = new Label(compSegSummary, SWT.NONE);
		lbl_v3.setLayoutData(new GridData(GridData.FILL_BOTH));
		lbl_v3.setAlignment(SWT.CENTER);
		final Label lbl_v4 = new Label(compSegSummary, SWT.NONE);
		lbl_v4.setLayoutData(new GridData(GridData.FILL_BOTH));
		lbl_v4.setAlignment(SWT.CENTER);
		
		lbl_v1.setText("[ Unknow ]");
		lbl_v1.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		lbl_v2.setText("[ 0 ]");
		lbl_v3.setText("[ 0 ]");
		lbl_v4.setText("[ 0 ]");
		
		// Segment Health
		Composite compSegHealth = new Composite(comp_Segment, SWT.BORDER);
		compSegHealth.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// >>> Content & Template Values
		compSegHealth.setLayout(new FillLayout());
		Composite comp_s = new Composite(compSegHealth, SWT.NO_TRIM);
		comp_s.setLayout(new GridLayout(1, true));
		new Label(comp_s, SWT.NONE).setText("Status");
		new Label(comp_s, SWT.NONE).setText("----------------------");
		final Label lbl_status_down = new Label(comp_s, SWT.NONE);
		final Label lbl_status_up = new Label(comp_s, SWT.NONE);
		
		Composite comp_r = new Composite(compSegHealth, SWT.NO_TRIM);
		comp_r.setLayout(new GridLayout(1, true));
		new Label(comp_r, SWT.NONE).setText("Replication Mode");
		new Label(comp_r, SWT.NONE).setText("----------------------");
		final Label lbl_mode_notsync = new Label(comp_r, SWT.NONE);
		final Label lbl_mode_resync = new Label(comp_r, SWT.NONE);
		final Label lbl_mode_tracking = new Label(comp_r, SWT.NONE);
		final Label lbl_mode_synced = new Label(comp_r, SWT.NONE);
		
		Composite comp_p = new Composite(compSegHealth, SWT.NO_TRIM);
		comp_p.setLayout(new GridLayout(1, true));
		new Label(comp_p, SWT.NONE).setText("Preferred Role");
		new Label(comp_p, SWT.NONE).setText("----------------------");
		final Label lbl_role_notpre = new Label(comp_p, SWT.NONE);
		final Label lbl_role_pre = new Label(comp_p, SWT.NONE);
		
		lbl_status_down.setText("0 Down");
		lbl_status_down.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		lbl_status_up.setText("0 Up");
		lbl_status_up.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
		
		lbl_mode_notsync.setText("0 Not Syncing");
		lbl_mode_notsync.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		lbl_mode_resync.setText("0 Resyncing");
		lbl_mode_resync.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
		lbl_mode_tracking.setText("0 Change Tracking");
		lbl_mode_tracking.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		lbl_mode_synced.setText("0 Synced");
		lbl_mode_synced.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
		
		lbl_role_notpre.setText("0 Not Preferred");
		lbl_role_notpre.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		lbl_role_pre.setText("0 Preferred");
		lbl_role_pre.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
		
		// Segment Detail
		Composite compSegDetail = new Composite(comp_Segment, SWT.NO_TRIM);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		compSegDetail.setLayoutData(gd);
		// >>> Content
		compSegDetail.setLayout(new FillLayout());
		final String[] header_SegDetail = new String[] { "Hostname", "Address", "Port", "DBID", "Content ID", "Status", "Role", "Preferred Role", "Replication Mode", "Data Directory" };
		final Table table_SegDetail = createTable(compSegDetail, header_SegDetail);
		
		segRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				int status_down = 0, status_up = 0;
				int mode_notsync = 0, mode_resync = 0, mode_tracking = 0, mode_synced = 0;
				int prerole_notpre = 0, prerole_pre = 0;
				
				Map<String, Boolean> host_mapMark = new HashMap<String, Boolean>();
				int host_count = 0;
				
				// Segment Detail
				IGpManageService proxy = gpControllerList.get(0).getManageServiceProxy();
				List<GPSegmentInfo> segmentList = proxy.queryGPSegmentInfo();
				table_SegDetail.removeAll();
				for (GPSegmentInfo segment : segmentList) {
					if (segment.getContent() <= -1)
						continue;
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
				for (int i = 0; i < header_SegDetail.length; i++)
					table_SegDetail.getColumn(i).pack();
				
				// Segment Summary
				lbl_v2.setText("[ " + (status_up + status_down) + " ]");
				lbl_v3.setText("[ " + (prerole_notpre / 2) + " ]");
				lbl_v4.setText("[ " + host_count + " ]");
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
				lbl_v1.setText("[ " + statusKeyWord + " ]");
				lbl_v1.setForeground(Display.getCurrent().getSystemColor(colorId));
				
				// Segment Health
				lbl_status_down.setText(status_down + " Down");
				lbl_status_up.setText(status_up + " Up");
				
				lbl_mode_notsync.setText(mode_notsync + " Not Syncing");
				lbl_mode_resync.setText(mode_resync + " Resyncing");
				lbl_mode_tracking.setText(mode_tracking + " Change Tracking");
				lbl_mode_synced.setText(mode_synced + " Synced");
				
				lbl_role_notpre.setText(prerole_notpre + " Not Preferred");
				lbl_role_pre.setText(prerole_pre + " Preferred");
			}
		});
		
		// TODO
		Composite comp_Storage = createTabItem(systemTabFolder, "Storage Status(Disk capacity information and history)");
		comp_Storage.setLayout(new GridLayout(2, true));
		
		Button stgRefresh = new Button(comp_Storage, SWT.NONE);
		stgRefresh.setText("Refresh Storage Status");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		stgRefresh.setLayoutData(gd);
		
		// Disk Usage Summary
		Composite compStgSummary = new Composite(comp_Storage, SWT.NO_TRIM);
		compStgSummary.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// >>> Content & Template Values
		compStgSummary.setLayout(new FillLayout());
		Composite sum_comp_seg = new Composite(compStgSummary, SWT.NONE);
		sum_comp_seg.setLayout(new GridLayout(1, true));
		Label sum_title_seg = new Label(sum_comp_seg, SWT.BORDER);
		sum_title_seg.setText("GP Segments");
		sum_title_seg.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(sum_comp_seg, SWT.NONE).setText("--------------------------------");
		final Label sum_value_seg_used = new Label(sum_comp_seg, SWT.NONE);
		sum_value_seg_used.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Label sum_value_seg_free = new Label(sum_comp_seg, SWT.NONE);
		sum_value_seg_free.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Label sum_value_seg_total = new Label(sum_comp_seg, SWT.NONE);
		sum_value_seg_total.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Composite sum_comp_mas = new Composite(compStgSummary, SWT.NONE);
		sum_comp_mas.setLayout(new GridLayout(1, true));
		Label sum_title_mas = new Label(sum_comp_mas, SWT.BORDER);
		sum_title_mas.setText("GP Master");
		sum_title_mas.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(sum_comp_mas, SWT.NONE).setText("--------------------------------");
		final Label sum_value_mas_used = new Label(sum_comp_mas, SWT.NONE);
		sum_value_mas_used.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Label sum_value_mas_free = new Label(sum_comp_mas, SWT.NONE);
		sum_value_mas_free.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Label sum_value_mas_total = new Label(sum_comp_mas, SWT.NONE);
		sum_value_mas_total.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		sum_value_seg_used.setText("Used: 0GB (0%)");
		sum_value_seg_used.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
		sum_value_seg_free.setText("Free: 0GB (0%)");
		sum_value_seg_free.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
		sum_value_seg_total.setText("Total: 0GB (0%)");
		sum_value_seg_total.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		
		sum_value_mas_used.setText("Used: 0GB (0%)");
		sum_value_mas_used.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
		sum_value_mas_free.setText("Free: 0GB (0%)");
		sum_value_mas_free.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
		sum_value_mas_total.setText("Total: 0GB (0%)");
		sum_value_mas_total.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));

		// GP Segments Usage History
		Composite compStgHistory = new Composite(comp_Storage, SWT.NO_TRIM);
		compStgHistory.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// >>> Content
		compStgHistory.setLayout(new GridLayout(1, true));
		final Combo combo_since = new Combo(compStgHistory, SWT.READ_ONLY);
		combo_since.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo_since.setItems(new String[] { "1 Hour", "6 Hours", "24 Hours", "7 Days", "2 Weeks", "12 Weeks" });
		combo_since.select(0);
		
		final String[] header_StgRecent = new String[] { "ctime", "Disk Space Used(%)", "Disk Space Free(GB)", "Total Space(GB)" };
		final Table table_StgRecent = createTable(compStgHistory, header_StgRecent);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 100;
		table_StgRecent.setLayoutData(gd);
		
		combo_since.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				// GP Segments Usage History
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				
				String[] periodFragments = combo_since.getText().split(" ");
				if (periodFragments[1].contains("Hour"))
					calendar.add(Calendar.HOUR, 0 - Integer.parseInt(periodFragments[0]));
				else if (periodFragments[1].contains("Weeks"))
					calendar.add(Calendar.DATE, 0 - Integer.parseInt(periodFragments[0]) * 7);
				else
					calendar.add(Calendar.DATE, -7);
				Date dateFrom = calendar.getTime();
				
				IGpManageService proxy = gpControllerList.get(0).getManageServiceProxy();
				List<Diskspace> diskspaceList = proxy.queryDiskspaceAve(dateFrom, new Date(), true);
				
				table_StgRecent.removeAll();
				for (Diskspace diskspace : diskspaceList) {
					if (!"/".equals(diskspace.getFilesystem()))
						continue;
					new TableItem(table_StgRecent, SWT.NONE).setText(new String[] { 
							sdf.format(diskspace.getCtime()),
							String.valueOf(formatPoint2((double) diskspace.getBytes_used() / 1024 / 1024 / 1024)) + "GB / " +
							String.valueOf(formatPoint2((double) diskspace.getBytes_used() / diskspace.getTotal_bytes() * 100)) + "%",
							String.valueOf(formatPoint2((double) diskspace.getBytes_available() / 1024 / 1024 / 1024)),
							String.valueOf(formatPoint2((double) diskspace.getTotal_bytes() / 1024 / 1024 / 1024)) });
				}
				for (int i = 0; i < header_StgRecent.length; i++)
					table_StgRecent.getColumn(i).pack();
			}
		});
		
		// Storage Detail
		Composite compStgDetail = new Composite(comp_Storage, SWT.NO_TRIM);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		compStgDetail.setLayoutData(gd);
		// >>> Content
		compStgDetail.setLayout(new FillLayout());
		final String[] header_StgDetail = new String[] { "Hostname", "Data Directory", "Disk Space Used(%)", "Disk Space Free(GB)", "Total Space(GB)" };
		final Table table_StgDetail = createTable(compStgDetail, header_StgDetail);
		
		stgRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				long master_sum_used = 0, master_sum_free = 0, master_sum_total = 0;
				long segment_sum_used = 0, segment_sum_free = 0, segment_sum_total = 0;
				Map<String, Boolean> masterKeyMap = new HashMap<String, Boolean>();
				Map<String, Boolean> segmentKeyMap = new HashMap<String, Boolean>();
				IGpManageService proxy = gpControllerList.get(0).getManageServiceProxy();
				
				List<GPSegmentInfo> segmentList = proxy.queryGPSegmentInfo();
				for (GPSegmentInfo segment : segmentList) {
					String hostname = segment.getHostname();
					if (segment.getContent() == -1) {
						if (!masterKeyMap.containsKey(hostname))
							masterKeyMap.put(hostname, true);
					} else {
						if (!segmentKeyMap.containsKey(hostname))
							segmentKeyMap.put(hostname, true);
					}
				}
				
				// Storage Detail
				List<Diskspace> diskspaceList = proxy.queryDiskspace();
				table_StgDetail.removeAll();
				for (Diskspace diskspace : diskspaceList) {
					String hostname = diskspace.getHostname();
					if (!"/".equals(diskspace.getFilesystem()))
						continue;
					new TableItem(table_StgDetail, SWT.NONE).setText(new String[] { 
							diskspace.getHostname(),
							diskspace.getFilesystem(),
							String.valueOf(formatPoint2((double) diskspace.getBytes_used() / 1024 / 1024 / 1024)) + "GB / " +
							String.valueOf(formatPoint2((double) diskspace.getBytes_used() / diskspace.getTotal_bytes() * 100)) + "%",
							String.valueOf(formatPoint2((double) diskspace.getBytes_available() / 1024 / 1024 / 1024)),
							String.valueOf(formatPoint2((double) diskspace.getTotal_bytes() / 1024 / 1024 / 1024)) });
					
					// Gathering data for diskspace
					if (masterKeyMap.containsKey(hostname)) {
						master_sum_used += diskspace.getBytes_used();
						master_sum_free += diskspace.getBytes_available();
						master_sum_total += diskspace.getTotal_bytes();
					}
					if (segmentKeyMap.containsKey(hostname)) {
						segment_sum_used += diskspace.getBytes_used();
						segment_sum_free += diskspace.getBytes_available();
						segment_sum_total += diskspace.getTotal_bytes();
					}
				}
				for (int i = 0; i < header_StgDetail.length; i++)
					table_StgDetail.getColumn(i).pack();
				
				// Disk Usage Summary
				sum_value_seg_used.setText("Used: " + formatPoint2((double) segment_sum_used / 1024 / 1024 / 1024) + "GB (" + formatPoint2((double) segment_sum_used / segment_sum_total * 100) + "%)");
				sum_value_seg_free.setText("Free: " + formatPoint2((double) segment_sum_free / 1024 / 1024 / 1024) + "GB (" + formatPoint2((double) segment_sum_free / segment_sum_total * 100) + "%)");
				sum_value_seg_total.setText("Total: " + formatPoint2((double) segment_sum_total / 1024 / 1024 / 1024) + "GB (100%)");
				
				sum_value_mas_used.setText("Used: " + formatPoint2((double) master_sum_used / 1024 / 1024 / 1024) + "GB (" + formatPoint2((double) master_sum_used / master_sum_total * 100) + "%)");
				sum_value_mas_free.setText("Free: " + formatPoint2((double) master_sum_free / 1024 / 1024 / 1024) + "GB (" + formatPoint2((double) master_sum_free / master_sum_total * 100) + "%)");
				sum_value_mas_total.setText("Total: " + formatPoint2((double) master_sum_total / 1024 / 1024 / 1024) + "GB (100%)");
			}
		});
	}
	
	private void createTabArea_system_chart(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("● System");
		Composite clientArea = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(clientArea);
		clientArea.setLayout(new FillLayout());
		TabFolder systemTabFolder = new TabFolder(clientArea, SWT.NONE);
		GridData gd;
		
		// TODO
		Composite comp_Segment = createTabItem(systemTabFolder, "Segment Status(Role and health related information)");
		comp_Segment.setLayout(new GridLayout(2, true));
		comp_Segment.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
		
		Button segStart = new Button(comp_Segment, SWT.NONE);
		segStart.setText("开启监控(Segment)");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		segStart.setLayoutData(gd);
		Button segStop = new Button(comp_Segment, SWT.NONE);
		segStop.setText("停止监控");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		segStop.setLayoutData(gd);
		
		final Browser browser_segment_status = new Browser(comp_Segment, SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		browser_segment_status.setLayoutData(gd);
		
		// Refresh for each 15s
		final long timeInterval = 15000;
		final List<MonitorTaskThread> taskList_seg = new ArrayList<MonitorTaskThread>();
		segStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MonitorTaskThread taskThread = new MonitorTaskThread() {
					@Override
					public void taskBody() {
						// TODO Auto-generated method stub
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								List<GPSegmentInfo> segmentInfoList = gpControllerList.get(0).getManageServiceProxy().queryGPSegmentInfo();
								
								// >>>>>>>>>>>>>>>> Notify Charts
								try {
									String gpSegmentConfListJson = JsonUtils.toJsonArray(segmentInfoList, true);
									String result = MonitorController.getInstance().updateGpSegmentConf(MONITOR_SAMPLE, gpSegmentConfListJson);
									logInfo(result);
								} catch (IOException e) {
									logError(e.getMessage());
								}
								// Show Charts (仅需加载一次即可)
								if (browser_segment_status.getData("loaded") == null) {
									browser_segment_status.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/system/segment_health.jsp");
									browser_segment_status.setData("loaded", true);
								}
							}
						});
						try {
							Thread.sleep(timeInterval);
						} catch (InterruptedException e) {}
					}
				};
				taskList_seg.add(taskThread);
				taskThread.start();
			}
		});
		segStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (MonitorTaskThread task : taskList_seg)
					task.stopMe();
				taskList_seg.clear();
			}
		});
		
		// TODO
//		Composite comp_Storage = createTabItem(systemTabFolder, "Storage Status(Disk capacity information and history)");
		TabItem tabItem_segment = new TabItem(systemTabFolder, SWT.V_SCROLL);
		tabItem_segment.setText("Storage Status(Disk capacity information and history)");
		ScrolledComposite comp_Storage_scrolled = new ScrolledComposite(systemTabFolder, SWT.H_SCROLL | SWT.V_SCROLL);
		tabItem_segment.setControl(comp_Storage_scrolled);
		
		comp_Storage_scrolled.setLayout(new GridLayout(1, true));
		Composite comp_Storage = new Composite(comp_Storage_scrolled, SWT.NONE);
		comp_Storage.setSize(-1, 600);
		comp_Storage_scrolled.setContent(comp_Storage);
		comp_Storage_scrolled.setExpandHorizontal(true);
		comp_Storage.setLayout(new GridLayout(2, true));
		comp_Storage.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
		
		Button stgStart = new Button(comp_Storage, SWT.NONE);
		stgStart.setText("开启监控(Storage)");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		stgStart.setLayoutData(gd);
		Button stgStop = new Button(comp_Storage, SWT.NONE);
		stgStop.setText("停止监控");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		stgStop.setLayoutData(gd);
		
		// > Part 1
		final Browser browser_diskspace_desc = new Browser(comp_Storage, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 255; // 225
		gd.verticalSpan = 2;
		browser_diskspace_desc.setLayoutData(gd);
		
		// > Part 2
		final Combo combo_since = new Combo(comp_Storage, SWT.READ_ONLY);
		combo_since.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo_since.setItems(new String[] { "1 Hour", "6 Hours", "24 Hours", "7 Days", "2 Weeks", "12 Weeks" });
		combo_since.select(0);
		
		final Browser browser_diskspace_history = new Browser(comp_Storage, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 225;
		browser_diskspace_history.setLayoutData(gd);
		
		combo_since.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Thread taskThread = new Thread() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								// GP Segments Usage History
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(new Date());
								
								String[] periodFragments = combo_since.getText().split(" ");
								if (periodFragments[1].contains("Hour"))
									calendar.add(Calendar.HOUR, 0 - Integer.parseInt(periodFragments[0]));
								else if (periodFragments[1].contains("Weeks"))
									calendar.add(Calendar.DATE, 0 - Integer.parseInt(periodFragments[0]) * 7);
								else
									calendar.add(Calendar.DATE, -7);
								Date dateFrom = calendar.getTime();
								
								IGpManageService proxy = gpControllerList.get(0).getManageServiceProxy();
								List<Diskspace> diskspaceList = proxy.queryDiskspaceAve(dateFrom, new Date(), true);
								
								// >>>>>>>>>>>>>>>> Notify Charts
								try {
									String diskspaceListJson = JsonUtils.toJsonArray(diskspaceList, true);
									String result = MonitorController.getInstance().updateDiskspaceHistory(MONITOR_SAMPLE, diskspaceListJson);
									logInfo(result);
								} catch (IOException e) {
									logError(e.getMessage());
								}
								browser_diskspace_history.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_diskspace.jsp");
							}
						});
					}
				};
				taskThread.start();
			}
		});
		
		// Part 3
		final Browser browser_diskspace_details = new Browser(comp_Storage, SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		browser_diskspace_details.setLayoutData(gd);
		
		// Refresh for each 15s
		final List<MonitorTaskThread> taskList_stg = new ArrayList<MonitorTaskThread>();
		stgStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MonitorTaskThread taskThread = new MonitorTaskThread() {
					@Override
					public void taskBody() {
						// TODO Auto-generated method stub
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								List<GPSegmentInfo> segmentInfoList = gpControllerList.get(0).getManageServiceProxy().queryGPSegmentInfo();
								List<Diskspace> diskspaceList = gpControllerList.get(0).getManageServiceProxy().queryDiskspace();
								
								// >>>>>>>>>>>>>>>> Notify Charts
								try {
									String gpSegmentConfListJson = JsonUtils.toJsonArray(segmentInfoList, true);
									String result = MonitorController.getInstance().updateGpSegmentConf(MONITOR_SAMPLE, gpSegmentConfListJson);
									logInfo(result);
								} catch (IOException e) {
									logError(e.getMessage());
								}
								try {
									String diskspaceListJson = JsonUtils.toJsonArray(diskspaceList, true);
									String result = MonitorController.getInstance().updateDiskspaceNow(MONITOR_SAMPLE, diskspaceListJson);
									logInfo(result);
								} catch (IOException e) {
									logError(e.getMessage());
								}
								// Show Charts (仅需加载一次即可)
								if (browser_diskspace_desc.getData("loaded") == null) {
									browser_diskspace_desc.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/system/usage_diskspace.jsp");
									browser_diskspace_details.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/system/usage_diskspace_details.jsp");
									browser_diskspace_desc.setData("loaded", true);
								}
							}
						});
						try {
							Thread.sleep(timeInterval);
						} catch (InterruptedException e) {}
					}
				};
				taskList_stg.add(taskThread);
				taskThread.start();
			}
		});
		stgStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (MonitorTaskThread task : taskList_stg)
					task.stopMe();
				taskList_stg.clear();
			}
		});
	}
	
	private double formatPoint2(double var) {
		return new BigDecimal(var).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	private Composite createTabItem(TabFolder tabFolder, String tabText) {
		TabItem tabItem_segment = new TabItem(tabFolder, SWT.NONE);
		tabItem_segment.setText(tabText);
		Composite area_segment = new Composite(tabFolder, SWT.NONE);
		tabItem_segment.setControl(area_segment);
		area_segment.setLayout(new FillLayout());
		return area_segment;
	}
	
	private Table createTable(Composite composite, String[] header) {
		final Table table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		for (int i = 0; i < header.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(header[i]);
			column.pack();
		}
		return table;
	}

	private void update_etc_hosts() {
		CommonDialog dialog = new CommonDialog(shell);
		dialog.setTitle("修改/etc/hosts");
		dialog.setMessage("格式：<ip> <hostname>，例如”192.168.0.120 mdw“");
		dialog.setInitTextContent("192.168.0.120 mdw\n" + "192.168.0.121 sdw1");
		if (dialog.open() != 0)
			return;

		String[] resultArray = (String[]) dialog.getResultObject();
		Map<String, String[]> hostMap = new LinkedHashMap<String, String[]>();
		for (String line : resultArray) {
			if ("".equals(line.trim()))
				continue;
			String[] lineFragments = line.split(" ");
			if (lineFragments.length != 2)
				continue;

			String[] hostnames = hostMap.get(lineFragments[0]);
			if (hostnames == null)
				hostnames = new String[] { lineFragments[1] };
			else {
				String[] tempHostnames = new String[hostnames.length + 1];
				System.arraycopy(hostnames, 0, tempHostnames, 0, hostnames.length);
				tempHostnames[hostnames.length] = lineFragments[1];
				hostnames = tempHostnames;
			}
			hostMap.put(lineFragments[0], hostnames);
		}
		if (hostMap.isEmpty())
			return;

		Iterator<Entry<String, String[]>> iterator = hostMap.entrySet().iterator();
		logInfo("Begin to update /etc/hosts: ");
		while (iterator.hasNext()) {
			Map.Entry<String, String[]> entry = iterator.next();
			String[] values = entry.getValue();
			for (String value : values)
				logInfo(entry.getKey() + " --> " + value);
		}
		for (IGPConnector gpCtrl : gpControllerList) {
			GPResultSet resultSet = gpCtrl.getEnvServiceProxy().updateHosts(hostMap);
			logInfo("Update Successed: " + resultSet.isSuccessed());
			printResultSet(resultSet);
		}
	}

	private void printResultSet(GPResultSet parentRs) {
		printResultSet(parentRs, "%Root%");
		List<GPResultSet> osRsList = parentRs.getChildResultSetList();
		for (GPResultSet osRs : osRsList)
			logInfo(osRs.getExecutor().getExecutorName() + " -> " + osRs.isSuccessed());
		logInfo("总耗时: " + parentRs.getTimeCost() + " ms");
		log("", "");
	}

	private void printResultSet(GPResultSet resultSet, String resultPath) {
		if (resultSet == null) {
			System.out.println("@ > resultSet is null.");
			return;
		}
		IExecuteDao executor = resultSet.getExecutor();
		List<GPResultSet> childResultSetList = resultSet.getChildResultSetList();

		String logPrefix = "● > ";
		System.out.println(logPrefix + "path: " + resultPath);
		System.out.println(logPrefix + "dao: " + (executor == null ? "Main" : executor.getExecutorName()) + "; childCount:" + (childResultSetList == null ? 0 : childResultSetList.size()));
		System.out.println(logPrefix + "cmd: " + (resultSet.getExecutedCmd() == null ? null : resultSet.getExecutedCmd().replaceAll("\n", " ")));
		System.out.println(logPrefix + "succ: " + resultSet.isSuccessed());
		System.out.println("★ <");

		if (childResultSetList != null && childResultSetList.size() > 0) {
			for (GPResultSet crs : childResultSetList)
				printResultSet(crs, resultPath + " -> " + crs.getExecutor().getHost());
		}
	}

	private void logInfo(String logContent) {
		log("INFO", logContent);
	}

	private void logWarn(String logContent) {
		log("WARN", logContent);
	}

	private void logError(String logContent) {
		log("ERROR", logContent);
	}

	private void log(String logPrefix, String logContent) {
		String prefix = "[" + logPrefix + "] ";
		if ("".equals(logPrefix.trim()))
			prefix = logPrefix;

		logText.append(prefix + logContent + "\n");
	}

	@Override
	public void refreshUI(String msg) {
		logText.append(msg);
	}
}
