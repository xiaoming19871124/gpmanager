package com.txdb.gpmanage.install.ui.composite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.core.common.CommonUtil;
import com.txdb.gpmanage.core.entity.Host;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.log.LogUtil;
import com.txdb.gpmanage.install.i18n.ResourceHandler;
import com.txdb.gpmanage.install.service.InstallUiService;
import com.txdb.gpmanage.install.ui.dialog.ModifyHostNameDialog;

/**
 * 机器配置面板
 * 
 * @author ws
 *
 */
public class ConfigureComposite extends IupperComposite {
	/**
	 * 按钮宽度
	 */
	private static final int BTN_WIDTH = 100;// 按钮宽度
	/**
	 * 机器列表
	 */
	private ListViewer left;
	/**
	 * 
	 */
	private java.util.List<Host> allHost;
	/**
	 * 选择的机器列表
	 */
	private ListViewer right;
	/**
	 * blockNumb
	 */
	private int blockNumb;
	/**
	 * 默认blockNumb
	 */
	private String defualtBlockNumb;
	/**
	 * 默认的Nproc
	 */
	private String defualtNprocValue;
	/**
	 * plugin id
	 */
	private static final String PLUGIN_ID = "com.txdb.gpmanage.install";
	/**
	 * 90-nproc.properties路径
	 */
	private static final String NPROC_FILE_PATH = "resource/90-nproc.properties";
	/**
	 * rc.local.properties路径
	 */
	private static final String RC_LOCAL_FILE_PATH = "resource/rc.local.properties";
	/**
	 * limits.properties路径
	 */
	private static final String LIMIT_FILE_PATH = "resource/limits.properties";
	/**
	 * sysctl.properties路径
	 */
	private static final String SYSCTL_FILE_PATH = "resource/sysctl.properties";
	/**
	 * limits参数列表
	 */
	private java.util.List<AttributeComposite> limitCompostie;
	/**
	 * sysctl参数列表
	 */
	private java.util.List<AttributeComposite> sysctlCompostie;
	/**
	 * 是否配置完成
	 */
	private boolean isFinish = false;

	public ConfigureComposite(InstallComposite mianComposite, Composite parent, CompositeCode code) {
		super(mianComposite, parent, code, ResourceHandler.getValue("hostConfigure.title"), ResourceHandler.getValue("hostConfigure.desc"));
	}

	@Override
	public void createCenter(Composite composte) {
		// 整体布局
		GridLayout gd_mainComposite = new GridLayout();
		gd_mainComposite.marginHeight = 20;
		gd_mainComposite.marginWidth = 20;
		composte.setLayout(gd_mainComposite);
		creatSelectComposite(composte);
		createConfigureComposite(composte);
		creatSysctlFileComosite(composte);
	}

	/**
	 * sysctl及limits文件设置面板
	 * 
	 * @param mainComposite
	 */
	private void creatSysctlFileComosite(Composite mainComposite) {
		Composite functionCom = new Composite(mainComposite, SWT.NONE);
		functionCom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		functionCom.setLayout(new FillLayout());
		// 创建表单对象
		FormToolkit ft = new FormToolkit(functionCom.getDisplay());
		// 通过表单工具对象创建可滚动的表单对象
		final ScrolledForm form = ft.createScrolledForm(functionCom);
		form.getBody().setLayout(new GridLayout());
		createSysctlSelection(form, ft);
		createLimitSelection(form, ft);
	}

	/**
	 * sysctl设置面板
	 * 
	 * @param form
	 * @param ft
	 */
	private void createSysctlSelection(final ScrolledForm form, FormToolkit ft) {
		sysctlCompostie = new ArrayList<AttributeComposite>();
		Section sysctlSection = ft.createSection(form.getBody(), Section.TWISTIE | Section.TITLE_BAR);
		sysctlSection.setText(ResourceHandler.getValue("hostConfigure.modify.sysctl"));
		// sysctlSection.setLayout(new FillLayout());

		sysctlSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Properties sysctl = null;
		try {
			sysctl = CommonUtil.readProperties(PLUGIN_ID, SYSCTL_FILE_PATH);
		} catch (IOException e2) {
			LogUtil.error("read 90-nproc error", e2);
		}
		// 创建一个面板 作为内容折叠区域放置的控件
		Composite sysctlComposite = ft.createComposite(sysctlSection);
		GridLayout gl_sysctlComposite = new GridLayout(3, false);
		gl_sysctlComposite.marginWidth = 20;
		gl_sysctlComposite.horizontalSpacing = 20;
		sysctlComposite.setLayout(gl_sysctlComposite);

		if (sysctl != null) {
			Set<Object> keys = sysctl.keySet();
			for (Object o : keys) {
				String value = sysctl.getProperty(((String) o));
				String key = ((String) o).replaceAll("~", " ");
				AttributeComposite ac = new AttributeComposite(sysctlComposite, key, value);
				sysctlCompostie.add(ac);
			}
		}
		final Button modifySysBtn = new Button(sysctlComposite, SWT.NONE);
		modifySysBtn.setText(ResourceHandler.getValue("modify"));
		GridData gd_modifySysBtn = new GridData(SWT.RIGHT, SWT.TOP, true, false, 2, 1);
		gd_modifySysBtn.widthHint = 80;
		modifySysBtn.setLayoutData(gd_modifySysBtn);
		final Button resetSysctlBtn = new Button(sysctlComposite, SWT.NONE);
		resetSysctlBtn.setText(ResourceHandler.getValue("reset_defualt_value"));
		GridData gd_resetSysctlBtn = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
		gd_resetSysctlBtn.widthHint = 80;
		resetSysctlBtn.setLayoutData(gd_resetSysctlBtn);
		sysctlSection.setClient(sysctlComposite);
		// 为折叠面板添加展开 折叠的监听器
		sysctlSection.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				// 根据部件的新尺寸重新定位和更新滚动条
				form.reflow(true);
			}
		});

		modifySysBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final java.util.List<Host> hosts = ((InstallComposite) mianComposite).getInstallInfo().getConfHost();
				if (hosts.size() < 1)
					return;
				modifySysBtn.setEnabled(false);
				startBar();
				final java.util.List<String> param = new ArrayList<String>();
				for (AttributeComposite ac : sysctlCompostie) {
					param.add(ac.getValue());
				}
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((InstallUiService) getService()).modifySysctl(hosts, param, text, ConfigureComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								modifySysBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
		resetSysctlBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (AttributeComposite ac : sysctlCompostie) {
					ac.resetValue();
				}
			}
		});
	}

	/**
	 * limits文件设置面板
	 * 
	 * @param form
	 * @param ft
	 */
	private void createLimitSelection(final ScrolledForm form, FormToolkit ft) {
		limitCompostie = new ArrayList<AttributeComposite>();
		Properties limit = null;
		try {
			limit = CommonUtil.readProperties(PLUGIN_ID, LIMIT_FILE_PATH);
		} catch (IOException e2) {
			LogUtil.error("read 90-nproc error", e2);
		}
		// 创建内容区域 样式TWISTIE 显示背景标题TITLE_BAR
		Section st = ft.createSection(form.getBody(), Section.TWISTIE | Section.TITLE_BAR);
		st.setText(ResourceHandler.getValue("hostConfigure.modify.limits"));
		st.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// 创建一个面板 作为内容折叠区域放置的控件
		final Composite cs = ft.createComposite(st);
		cs.setLayout(new GridLayout(3, false));
		if (limit != null) {
			Set<Object> keys = limit.keySet();
			for (Object o : keys) {
				String value = limit.getProperty(((String) o));
				String key = ((String) o).replaceAll("~", " ");
				AttributeComposite ac = new AttributeComposite(cs, key, value);
				limitCompostie.add(ac);
				if (key.contains("soft core unlimited")) {
					ac.setEnable(false);
				}
			}
		}
		final Button modifyLimitBtn = new Button(cs, SWT.NONE);
		modifyLimitBtn.setText(ResourceHandler.getValue("modify"));
		GridData gd_modifyLimitBtn = new GridData(SWT.RIGHT, SWT.TOP, true, false, 2, 1);
		gd_modifyLimitBtn.widthHint = 80;
		modifyLimitBtn.setLayoutData(gd_modifyLimitBtn);
		final Button resetLimitBtn = new Button(cs, SWT.NONE);
		resetLimitBtn.setText(ResourceHandler.getValue("reset_defualt_value"));
		GridData gd_resetLimitBtn = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
		gd_modifyLimitBtn.widthHint = 80;
		resetLimitBtn.setLayoutData(gd_resetLimitBtn);
		st.setClient(cs);
		// 为折叠面板添加展开 折叠的监听器
		st.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				// 根据部件的新尺寸重新定位和更新滚动条
				form.reflow(true);
			}
		});
		modifyLimitBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final java.util.List<Host> hosts = ((InstallComposite) mianComposite).getInstallInfo().getConfHost();
				if (hosts.size() < 1)
					return;
				modifyLimitBtn.setEnabled(false);
				startBar();
				final java.util.List<String> param = new ArrayList<String>();
				for (AttributeComposite ac : limitCompostie) {
					param.add(ac.getValue());
				}
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((InstallUiService) getService()).modifyLimit(hosts, param, text, ConfigureComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								modifyLimitBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
		resetLimitBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (AttributeComposite ac : limitCompostie) {
					ac.resetValue();
				}
			}
		});
	}

	/**
	 * 创建服务器参数配置面板
	 * 
	 * @param mainComposite
	 */
	private void createConfigureComposite(Composite mainComposite) {
		// 服务器参数配置面板
		Composite functionCom = new Composite(mainComposite, SWT.NONE);
		functionCom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		functionCom.setLayout(new GridLayout(2, false));
		// "选择机器列表Label"
		Label configureLb = new Label(functionCom, SWT.NONE);
		configureLb.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		configureLb.setText(ResourceHandler.getValue("hostConfigure.parameter.configuration"));

		// 机器列表选择面板
		Composite configureComp = new Composite(functionCom, SWT.NONE);
		GridLayout gd_configureComp = new GridLayout(4, false);
		gd_configureComp.horizontalSpacing = 20;
		configureComp.setLayout(gd_configureComp);
		configureComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// 关闭相关任务
		Label closedServiceLb = new Label(configureComp, SWT.NONE);
		closedServiceLb.setText(ResourceHandler.getValue("hostConfigure.close.services"));
		closedServiceLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		final Button closedSelinux = new Button(configureComp, SWT.NONE);
		closedSelinux.setText(ResourceHandler.getValue("hostConfigure.close.selinux"));
		GridData gd_closedSelinux = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_closedSelinux.widthHint = BTN_WIDTH;
		closedSelinux.setLayoutData(gd_closedSelinux);
		final Button closedFirewall = new Button(configureComp, SWT.NONE);
		closedFirewall.setText(ResourceHandler.getValue("hostConfigure.close.firewall"));
		GridData gd_closedFirewall = new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1);
		gd_closedFirewall.widthHint = BTN_WIDTH;
		closedFirewall.setLayoutData(gd_closedFirewall);
		// 修改主机名称
		Label modifyHostNameLb = new Label(configureComp, SWT.NONE);
		modifyHostNameLb.setText(ResourceHandler.getValue("hostConfigure.modify.hostName"));
		modifyHostNameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		final Button modifyHostNameBtn = new Button(configureComp, SWT.NONE);
		modifyHostNameBtn.setText(ResourceHandler.getValue("modify"));
		GridData gd_modifyHostNameBtn = new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1);
		gd_modifyHostNameBtn.widthHint = BTN_WIDTH;
		modifyHostNameBtn.setLayoutData(gd_modifyHostNameBtn);

		// 修改hosts文件
		Label modifyHostsLb = new Label(configureComp, SWT.NONE);
		modifyHostsLb.setText(ResourceHandler.getValue("hostConfigure.modify.hostsFile"));
		modifyHostsLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		final Button modifyHostsBtn = new Button(configureComp, SWT.NONE);
		modifyHostsBtn.setText(ResourceHandler.getValue("modify"));
		GridData gd_modifyHostsBtn = new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1);
		gd_modifyHostsBtn.widthHint = BTN_WIDTH;
		modifyHostsBtn.setLayoutData(gd_modifyHostsBtn);
		// 修改磁盘预读扇区
		Label blockdevLb = new Label(configureComp, SWT.NONE);
		blockdevLb.setText(ResourceHandler.getValue("hostConfigure.modify.blockdev"));
		blockdevLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));

		final Text blockdevText = new Text(configureComp, SWT.BORDER);
		GridData gd_blockdevText = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
		gd_blockdevText.widthHint = BTN_WIDTH - 10;
		blockdevText.setLayoutData(gd_blockdevText);
		try {
			defualtBlockNumb = CommonUtil.readProperties(PLUGIN_ID, RC_LOCAL_FILE_PATH).getProperty("rc.local");
		} catch (IOException e2) {
			LogUtil.error("read rc.local error", e2);
		}
		blockdevText.setText(defualtBlockNumb);
		final Button blockdevBtn = new Button(configureComp, SWT.NONE);
		blockdevBtn.setText(ResourceHandler.getValue("modify"));
		GridData gd_blockdevBtn = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_blockdevBtn.widthHint = BTN_WIDTH;
		blockdevBtn.setLayoutData(gd_blockdevBtn);
		final Button resetBlockdevBtn = new Button(configureComp, SWT.NONE);
		resetBlockdevBtn.setText(ResourceHandler.getValue("reset_defualt_value"));
		GridData gd_resetBlockdevBtn = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_resetBlockdevBtn.widthHint = BTN_WIDTH;
		resetBlockdevBtn.setLayoutData(gd_resetBlockdevBtn);

		// 修改90-nproc.conf最大进程数量
		Label nprocLb = new Label(configureComp, SWT.NONE);
		nprocLb.setText(ResourceHandler.getValue("hostConfigure.modify.nproc"));
		nprocLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		try {
			defualtNprocValue = CommonUtil.readProperties(PLUGIN_ID, NPROC_FILE_PATH).getProperty("soft.nproc");
		} catch (IOException e2) {
			LogUtil.error("read 90-nproc error", e2);
		}
		final Text nprocText = new Text(configureComp, SWT.BORDER);
		GridData gd_nprocText = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
		gd_nprocText.widthHint = BTN_WIDTH - 10;
		nprocText.setLayoutData(gd_nprocText);
		if (defualtNprocValue != null) {
			nprocText.setText(defualtNprocValue);
		}
		final Button nprocBtn = new Button(configureComp, SWT.NONE);
		nprocBtn.setText(ResourceHandler.getValue("modify"));
		GridData gd_nprocBtn = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_nprocBtn.widthHint = BTN_WIDTH;
		nprocBtn.setLayoutData(gd_nprocBtn);
		final Button resetNprocBtn = new Button(configureComp, SWT.NONE);
		resetNprocBtn.setText(ResourceHandler.getValue("reset_defualt_value"));
		GridData gd_resetNprocBtn = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_resetNprocBtn.widthHint = BTN_WIDTH;
		resetNprocBtn.setLayoutData(gd_resetNprocBtn);
		resetNprocBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(defualtNprocValue);
				nprocText.setText(defualtNprocValue);
			}
		});
		resetBlockdevBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				blockdevText.setText(defualtBlockNumb);
			}
		});
		closedSelinux.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final java.util.List<Host> hosts = ((InstallComposite) mianComposite).getInstallInfo().getConfHost();
				if (hosts.size() < 1)
					return;
				closedSelinux.setEnabled(false);
				startBar();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((InstallUiService) getService()).closedSelinux(hosts, text, ConfigureComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								closedSelinux.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();

			}
		});
		closedFirewall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final java.util.List<Host> hosts = ((InstallComposite) mianComposite).getInstallInfo().getConfHost();
				if (hosts.size() < 1)
					return;
				closedFirewall.setEnabled(false);
				startBar();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((InstallUiService) getService()).closeIptables(hosts, text, ConfigureComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								closedFirewall.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();

			}
		});

		modifyHostNameBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				final java.util.List<Host> hosts = ((InstallComposite) mianComposite).getInstallInfo().getConfHost();
				if (hosts.size() < 1)
					return;
				ModifyHostNameDialog mhnDlg = new ModifyHostNameDialog(ConfigureComposite.this.getShell(), hosts);
				if (mhnDlg.open() == IDialogConstants.OK_ID) {
					modifyHostNameBtn.setEnabled(false);
					startBar();
					final Display display = Display.getCurrent();
					final Map<String, Host> map = mhnDlg.getMap();
					new Thread(new Runnable() {
						@Override
						public void run() {
							((InstallUiService) getService()).updataHostName(map, hosts, text, ConfigureComposite.this);
							display.syncExec(new Runnable() {
								@Override
								public void run() {
									right.refresh();
									((InstallComposite) mianComposite).getInstllMaster().updateHosts(hosts, "m");
									((InstallComposite) mianComposite).getHostManage().update();
									modifyHostNameBtn.setEnabled(true);
									stopBar();
								}
							});
						}
					}).start();
				}
			}
		});
		modifyHostsBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isFinish = false;
				final java.util.List<Host> hosts = ((InstallComposite) mianComposite).getInstallInfo().getConfHost();
				if (hosts.size() < 1)
					return;
				modifyHostsBtn.setEnabled(false);
				startBar();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						isFinish = ((InstallUiService) getService()).modifyHostFile(hosts, text, ConfigureComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								modifyHostsBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
		blockdevBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String blockdev = blockdevText.getText();
				if (blockdev == null || blockdev.isEmpty())
					return;

				try {
					blockNumb = Integer.valueOf(blockdev);
				} catch (NumberFormatException e1) {
					return;
				}
				final java.util.List<Host> hosts = ((InstallComposite) mianComposite).getInstallInfo().getConfHost();

				if (hosts.size() < 1)
					return;
				blockdevBtn.setEnabled(false);
				startBar();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((InstallUiService) getService()).modifyBlockdev(hosts, blockNumb, text, ConfigureComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								blockdevBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
		nprocBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final String nproc = nprocText.getText();
				if (nproc == null || nproc.isEmpty())
					return;

				try {
					Integer.valueOf(nproc);
				} catch (NumberFormatException e1) {
					return;
				}
				final java.util.List<Host> hosts = ((InstallComposite) mianComposite).getInstallInfo().getConfHost();

				if (hosts.size() < 1)
					return;
				nprocBtn.setEnabled(false);
				startBar();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((InstallUiService) getService()).modifynproc(hosts, nproc, text, ConfigureComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								nprocBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
	}

	/**
	 * 选择机器列表面板
	 * 
	 * @param mainComposite
	 */
	private void creatSelectComposite(Composite mainComposite) {
		allHost = new ArrayList<Host>();
		for (Host host : ((InstallComposite) mianComposite).getInstallInfo().getAllHost()) {
			allHost.add(host);
		}
		// 选择机器列表面板
		Composite selectHost = new Composite(mainComposite, SWT.NONE);
		GridData gd_selectHost = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd_selectHost.heightHint = 130;
		selectHost.setLayoutData(gd_selectHost);

		selectHost.setLayout(new GridLayout(2, false));
		// "选择机器列表Label"
		Label selectLb = new Label(selectHost, SWT.NONE);
		selectLb.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		selectLb.setText(ResourceHandler.getValue("hostConfigure.select.hosts"));
		// 机器列表选择面板
		Composite hostListComp = new Composite(selectHost, SWT.NONE);
		hostListComp.setLayout(new GridLayout(3, false));
		hostListComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		left = new ListViewer(hostListComp, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		// 机器列表list
		GridData gd_left = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gd_left.widthHint = 80;
		left.getList().setLayoutData(gd_left);
		left.setLabelProvider(new LabelProvider());
		left.setContentProvider(new ArrayContentProvider());
		left.setInput(allHost);
		// 操作面板
		Composite anctionCom = new Composite(hostListComp, SWT.NONE);
		anctionCom.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		anctionCom.setLayout(new GridLayout(1, false));
		Button moveToRight = new Button(anctionCom, SWT.NONE);
		GridData gd_allBtn = new GridData(40, 20);
		moveToRight.setText(">");
		Button moveAllToRight = new Button(anctionCom, SWT.NONE);
		moveAllToRight.setText(">>");
		Button moveAllToLeft = new Button(anctionCom, SWT.NONE);
		moveAllToLeft.setText("<<");
		Button moveToLeft = new Button(anctionCom, SWT.NONE);
		moveToLeft.setText("<");
		moveToRight.setLayoutData(gd_allBtn);
		moveAllToRight.setLayoutData(gd_allBtn);
		moveAllToLeft.setLayoutData(gd_allBtn);
		moveToLeft.setLayoutData(gd_allBtn);
		// 选中的机器列表
		right = new ListViewer(hostListComp, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		GridData gd_right = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gd_right.widthHint = 80;
		right.getList().setLayoutData(gd_right);
		right.setLabelProvider(new LabelProvider());
		right.setContentProvider(new ArrayContentProvider());
		right.setInput(((InstallComposite) mianComposite).getInstallInfo().getConfHost());
		SelectionAdapter selectionAdapter = creatBtnListener(left, right);
		// 为buttion添加监听
		moveToRight.addSelectionListener(selectionAdapter);
		moveAllToRight.addSelectionListener(selectionAdapter);
		moveAllToLeft.addSelectionListener(selectionAdapter);
		moveToLeft.addSelectionListener(selectionAdapter);
		// 为机器列表添加双击事件
		left.getList().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				verifyValue(left.getSelection(), left, right, "+");
			}

		});
		right.getList().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				verifyValue(right.getSelection(), right, left, "-");
			}

		});
	}

	/**
	 * 创建buttion选择监听
	 * 
	 * @param left
	 *            源列表
	 * @param right
	 *            目标列表
	 * @return 监听
	 */
	private SelectionAdapter creatBtnListener(final ListViewer left, final ListViewer right) {
		// 创建事件监听类，为内部类
		SelectionAdapter listener = new SelectionAdapter() {
			// 按钮单击事件处理的方法
			public void widgetSelected(SelectionEvent e) {
				// 取得触发事件的空间对象（按钮）
				Button bt = (Button) e.widget;
				if (bt.getText().equals(">")) // 如果是“>”按钮
				{
					verifyValue(left.getSelection(), left, right, "+");
				} else if (bt.getText().equals(">>")) { // 如果是“>>”按钮
					left.setSelection(new StructuredSelection((java.util.List<Host>) left.getInput()));
					verifyValue(left.getSelection(), left, right, "+");

				} else if (bt.getText().equals("<")) // 如果是“<”按钮
					verifyValue(right.getSelection(), right, left, "-");
				else if (bt.getText().equals("<<")) {
					// 如果是“<<”按钮
					right.setSelection(new StructuredSelection((java.util.List<Host>) right.getInput()));
					verifyValue(right.getSelection(), right, left, "-");
				}
			}
		};
		return listener;

	}

	public void modifyListData(Host host, String operate) {
		left.refresh();
		right.refresh();
		((InstallComposite) ConfigureComposite.this.mianComposite).getInstllMaster().updateHosts(host, operate);
	}

	/**
	 * 交换数据
	 * 
	 * @param selected
	 *            被选中的列
	 * @param from
	 *            源数据列表
	 * @param to
	 *            目标数据列表
	 */
	private void verifyValue(ISelection selected, ListViewer from, ListViewer to, String operate) {
		isFinish = false;
		StructuredSelection selection = (StructuredSelection) selected;
		if (selection == null || selection.isEmpty())
			return;
		java.util.List<Host> selectionHost = selection.toList();
		for (Host host : selectionHost) {
			((java.util.List<Host>) from.getInput()).remove(host);
			((java.util.List<Host>) to.getInput()).add(host);
		}
		from.refresh();
		to.refresh();
		((InstallComposite) ConfigureComposite.this.mianComposite).getInstllMaster().updateHosts(selectionHost, operate);
	}

	public void addHost(Host host) {
		allHost.add(host);
	}

	public void removeHost(Host host) {
		if (allHost.contains(host))
			allHost.remove(host);
	}

	private class AttributeComposite {
		private String key;
		private String value;
		private Text text;

		public AttributeComposite(Composite parent, String key, String value) {
			this.key = key;
			this.value = value;
			create(parent);

		}

		private void create(Composite parent) {
			Label lb = new Label(parent, SWT.NONE);
			lb.setText(key);
			lb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
			text = new Text(parent, SWT.BORDER);
			text.setText(value);
			text.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		}

		public String getValue() {
			return ((text.getText() == null || text.getText().isEmpty()) ? key + " = " + value : key + " = " + text.getText());
		}

		public void setEnable(boolean isEnable) {
			text.setEnabled(false);
		}

		public void resetValue() {
			text.setText(value);
		}
	}

	public boolean isFinish() {
		return isFinish;
	}

	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}

}
