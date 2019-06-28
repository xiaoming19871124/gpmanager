package com.txdb.gpmanage.manage.ui.dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.core.common.CommonUtil;
import com.txdb.gpmanage.core.entity.Host;
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.exception.HostRole;
import com.txdb.gpmanage.core.gp.entry.GPRequiredSw;
import com.txdb.gpmanage.core.log.LogUtil;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.ui.composite.ExtendMangeComposite;
import com.txdb.gpmanage.manage.ui.composite.ManageComposite;

/**
 * 添加扩展机器
 * 
 * @author ws
 *
 */
public class AddExtendHostDialog extends Dialog {
	private Host host;
	private Text ipText;
	private Text userNameText;
	private Text pwdText;
	private Label errorLb;
	private boolean isAddStandby;
	private IupperComposite extendMangeComposite;
	Button okBtn;
	Button cancelBtn;
	Group group;
	private Shell shell;
	private Point point;
private	ScrolledComposite scrolledComposite ;
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
	 * 默认的Nproc
	 */
	private String defualtNprocValue;
	/**
	 * blockNumb
	 */
	private int blockNumb;
	private String defualtBlockNumb;

	public Host getHost() {
		return host;
	}

	public AddExtendHostDialog(Shell parentShell, Host host, boolean isAddStandby) {
		super(parentShell);
		this.host = host;
		this.isAddStandby = isAddStandby;
	}

	public AddExtendHostDialog(IupperComposite extendMangeComposite, Host host, boolean isAddStandby) {
		super(extendMangeComposite.getShell());
		this.extendMangeComposite = extendMangeComposite;
		this.host = host;
		this.isAddStandby = isAddStandby;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		this.shell = newShell;
		if (host == null)
			newShell.setText(ResourceHandler.getValue("add"));
		else
			newShell.setText(ResourceHandler.getValue("modify"));

	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite top = new Composite(parent, SWT.NONE);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		top.setLayout(new GridLayout(4, false));
		// ip
		Label ipLb = new Label(top, SWT.NONE);
		ipLb.setText(ResourceHandler.getValue("hostMange.table.hostIp"));
		ipLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		ipText = new Text(top, SWT.BORDER);
		ipText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		// 主机用户名
		Label userNameLb = new Label(top, SWT.NONE);
		userNameLb.setText(ResourceHandler.getValue("hostMange.table.userName"));
		userNameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		userNameText = new Text(top, SWT.BORDER);
		userNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		// 密码
		Label pwdLb = new Label(top, SWT.NONE);
		pwdLb.setText(ResourceHandler.getValue("password"));
		pwdLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		pwdText = new Text(top, SWT.BORDER | SWT.PASSWORD);
		pwdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		if(host!=null){
			ipText.setText(host.getIp());
			userNameText.setText(host.getUserName());
			pwdText.setText(host.getPassword());
		}
		errorLb = new Label(top, SWT.RIGHT);
		errorLb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		errorLb.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		final Button validateBtn = new Button(top, SWT.NONE);
		validateBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		validateBtn.setText(ResourceHandler.getValue("validate"));
		final Button advancedBtn = new Button(top, SWT.NONE);
		advancedBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		advancedBtn.setText(ResourceHandler.getValue("advanced"));
		 advancedBtn.setEnabled(false);
		validateBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				errorLb.setText(ResourceHandler.getValue("validateing"));
				validateBtn.setEnabled(false);
				okBtn.setEnabled(false);
				cancelBtn.setEnabled(false);
				String ip = ipText.getText();
				String userName = userNameText.getText();
				String pwd = pwdText.getText();
				HostRole role = HostRole.STANDBY;
				if (!isAddStandby)
					role = HostRole.SEGEMENT;
				final Host valifyHost = new Host();
				valifyHost.setIp(ip);
				valifyHost.setUserName(userName);
				valifyHost.setPassword(pwd);
				valifyHost.setRole(role);
				final Display display = AddExtendHostDialog.this.getShell().getDisplay();
				final GPManagerEntity gp = ((ManageComposite) extendMangeComposite.getMianComposite()).getGp();
				// 是否能连接上
				new Thread(new Runnable() {

					@Override
					public void run() {
						boolean isCurrect = extendMangeComposite.getService().verificationHost(valifyHost);
						if (!isCurrect) {
							display.syncExec(new Runnable() {
								@Override
								public void run() {
									errorLb.setText(ResourceHandler.getValue("err_host"));
									validateBtn.setEnabled(true);
									okBtn.setEnabled(true);
									cancelBtn.setEnabled(true);
								}
							});
							return;
						}
						if (!isAddStandby) {
							// 是否已经添加到列表
							boolean isExist = extendMangeComposite.getService().isExistHost(valifyHost, (List<Host>) ((ExtendMangeComposite) extendMangeComposite).getTv().getInput());
							if (isExist) {
								display.syncExec(new Runnable() {
									@Override
									public void run() {
										errorLb.setText(ResourceHandler.getValue("err_host_exist"));
										okBtn.setEnabled(true);
										cancelBtn.setEnabled(true);
										validateBtn.setEnabled(true);
									}
								});
								return;
							}

						}
						// 是否已经在集群中存在
						boolean isInGp = extendMangeComposite.getService().isHostInGP(valifyHost, gp);
						if (isInGp && !isAddStandby) {
							display.syncExec(new Runnable() {
								@Override
								public void run() {
									errorLb.setText(ResourceHandler.getValue("err_host_exist"));
									validateBtn.setEnabled(true);
									okBtn.setEnabled(true);
									cancelBtn.setEnabled(true);
								}
							});
							return;
						}

						if (isInGp && isAddStandby) {
							boolean isMaster = extendMangeComposite.getService().isMaster(valifyHost, gp);
							if (isMaster) {
								display.asyncExec(new Runnable() {
									@Override
									public void run() {
										validateBtn.setEnabled(true);
										okBtn.setEnabled(true);
										cancelBtn.setEnabled(true);
										errorLb.setText(ResourceHandler.getValue("standby.master.error"));
									}
								});
								return;
							}
						}

						display.asyncExec(new Runnable() {
							@Override
							public void run() {
								errorLb.setText(ResourceHandler.getValue("configure_correct"));
								validateBtn.setEnabled(true);
								okBtn.setEnabled(true);
								cancelBtn.setEnabled(true);
								advancedBtn.setEnabled(true);
							}
						});

					}
				}).start();
			}
		});
		advancedBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (group == null || group.isDisposed()) {
					 scrolledComposite = new ScrolledComposite(top, SWT.H_SCROLL | SWT.V_SCROLL);
					scrolledComposite.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
					group = new Group(scrolledComposite, SWT.NONE);
					group.setLayout(new GridLayout(2, false));
					scrolledComposite.setContent(group);
					// 设置滚动条面板
					createTableViewer();
					createParamComposite();
					
					Composite functionCom = new Composite(group, SWT.NONE);
					functionCom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,2,1));
					functionCom.setLayout(new FillLayout());
					// 创建表单对象
					FormToolkit ft = new FormToolkit(functionCom.getDisplay());
					// 通过表单工具对象创建可滚动的表单对象
					final ScrolledForm form = ft.createScrolledForm(functionCom);
					form.getBody().setLayout(new GridLayout());
					createSysctlSelection(form, ft);
					createLimitSelection(form, ft);
					scrolledComposite.setMinSize(group.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					scrolledComposite.setExpandHorizontal(true);
					scrolledComposite.setExpandVertical(true);
					shell.setSize(new Point(point.x+200, point.y + 400));
					top.layout();
					parent.layout();
					shell.layout();

				} else {
					group.dispose();
					scrolledComposite.dispose();
					shell.setSize(point);
					top.layout();
					parent.layout();
					shell.layout();
				}
			}
		});
		return top;
	}

	private void createTableViewer() {
		final Button soft = new Button(group, SWT.NONE);
		soft.setText(ResourceHandler.getValue("soft_check"));
		soft.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		final Label label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		final TableViewer tv = new TableViewer(group, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		Table table = tv.getTable();
		GridData gd_table =	new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_table.heightHint=100;
		table.setLayoutData(gd_table);
		TableLayout tLayout = new TableLayout();// 专用于表格的布局
		table.setLayout(tLayout);
		table.setHeaderVisible(true);// 设置表格头部是否可见
		table.setLinesVisible(true);// 设置线条是否可见
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("soft.name"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("soft.need.version"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("soft.install.version"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("soft.check.result"));
		tv.setLabelProvider(new SoftTableLabelProvider());
		tv.setContentProvider(new ArrayContentProvider());
		soft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String ip = ipText.getText();
				String userName = userNameText.getText();
				String pwd = pwdText.getText();
				final Host valifyHost = new Host();
				valifyHost.setIp(ip);
				valifyHost.setUserName(userName);
				valifyHost.setPassword(pwd);
				final Display display = AddExtendHostDialog.this.getShell().getDisplay();
				soft.setEnabled(false);
				label.setText(ResourceHandler.getValue("verificating"));
				new Thread(new Runnable() {

					@Override
					public void run() {
						final List<GPRequiredSw> sw = extendMangeComposite.getService().checkSoft(valifyHost);
						display.syncExec(new Runnable() {

							@Override
							public void run() {
								tv.setInput(sw);
								tv.refresh();
								soft.setEnabled(true);
								label.setText("");
							}
						});
					}
				}).start();
			}
		});
	}

	private void createParamComposite() {

		// 服务器参数配置面板
		Composite functionCom = new Composite(group, SWT.NONE);
		functionCom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
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
		closedSelinux.setLayoutData(gd_closedSelinux);
		final Button closedFirewall = new Button(configureComp, SWT.NONE);
		closedFirewall.setText(ResourceHandler.getValue("hostConfigure.close.firewall"));
		GridData gd_closedFirewall = new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1);
		closedFirewall.setLayoutData(gd_closedFirewall);

		// 修改磁盘预读扇区
		Label blockdevLb = new Label(configureComp, SWT.NONE);
		blockdevLb.setText(ResourceHandler.getValue("hostConfigure.modify.blockdev"));
		blockdevLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));

		final Text blockdevText = new Text(configureComp, SWT.BORDER);
		GridData gd_blockdevText = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
		gd_blockdevText.widthHint = 90;
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
		blockdevBtn.setLayoutData(gd_blockdevBtn);
		final Button resetBlockdevBtn = new Button(configureComp, SWT.NONE);
		resetBlockdevBtn.setText(ResourceHandler.getValue("reset_defualt_value"));
		GridData gd_resetBlockdevBtn = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
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
		gd_nprocText.widthHint = 90;
		nprocText.setLayoutData(gd_nprocText);
		if (defualtNprocValue != null) {
			nprocText.setText(defualtNprocValue);
		}
		final Button nprocBtn = new Button(configureComp, SWT.NONE);
		nprocBtn.setText(ResourceHandler.getValue("modify"));
		GridData gd_nprocBtn = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		nprocBtn.setLayoutData(gd_nprocBtn);
		final Button resetNprocBtn = new Button(configureComp, SWT.NONE);
		resetNprocBtn.setText(ResourceHandler.getValue("reset_defualt_value"));
		GridData gd_resetNprocBtn = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		resetNprocBtn.setLayoutData(gd_resetNprocBtn);
		resetNprocBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
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

				String ip = ipText.getText();
				String userName = userNameText.getText();
				String pwd = pwdText.getText();
				final Host valifyHost = new Host();
				valifyHost.setIp(ip);
				valifyHost.setUserName(userName);
				valifyHost.setPassword(pwd);
				final Display display = AddExtendHostDialog.this.getShell().getDisplay();
				closedSelinux.setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						extendMangeComposite.getService().closedSelinux(valifyHost);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								closedSelinux.setEnabled(true);
							}
						});
					}
				}).start();

			}
		});
		closedFirewall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				closedFirewall.setEnabled(false);
				String ip = ipText.getText();
				String userName = userNameText.getText();
				String pwd = pwdText.getText();
				final Host valifyHost = new Host();
				valifyHost.setIp(ip);
				valifyHost.setUserName(userName);
				valifyHost.setPassword(pwd);
				final Display display = AddExtendHostDialog.this.getShell().getDisplay();
				new Thread(new Runnable() {
					@Override
					public void run() {
						String msg = extendMangeComposite.getService().closeIptables(valifyHost);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								closedFirewall.setEnabled(true);
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

				String ip = ipText.getText();
				String userName = userNameText.getText();
				String pwd = pwdText.getText();
				final Host valifyHost = new Host();
				valifyHost.setIp(ip);
				valifyHost.setUserName(userName);
				valifyHost.setPassword(pwd);
				final Display display = AddExtendHostDialog.this.getShell().getDisplay();
				blockdevBtn.setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						String msg = extendMangeComposite.getService().modifyBlockdev(valifyHost, blockNumb);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								blockdevBtn.setEnabled(true);
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
				String ip = ipText.getText();
				String userName = userNameText.getText();
				String pwd = pwdText.getText();
				final Host valifyHost = new Host();
				valifyHost.setIp(ip);
				valifyHost.setUserName(userName);
				valifyHost.setPassword(pwd);
				final Display display = AddExtendHostDialog.this.getShell().getDisplay();
				nprocBtn.setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						String msg = extendMangeComposite.getService().modifynproc(valifyHost, nproc);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								nprocBtn.setEnabled(true);
							}
						});
					}
				}).start();
			}
		});

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
				modifySysBtn.setEnabled(false);
				String ip = ipText.getText();
				String userName = userNameText.getText();
				String pwd = pwdText.getText();
				final Host valifyHost = new Host();
				valifyHost.setIp(ip);
				valifyHost.setUserName(userName);
				valifyHost.setPassword(pwd);
				final Display display = AddExtendHostDialog.this.getShell().getDisplay();
				final java.util.List<String> param = new ArrayList<String>();
				for (AttributeComposite ac : sysctlCompostie) {
					param.add(ac.getValue());
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						String msg = extendMangeComposite.getService().modifySysctl(valifyHost, param);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								modifySysBtn.setEnabled(true);
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
				
				modifyLimitBtn.setEnabled(false);
				String ip = ipText.getText();
				String userName = userNameText.getText();
				String pwd = pwdText.getText();
				final Host valifyHost = new Host();
				valifyHost.setIp(ip);
				valifyHost.setUserName(userName);
				valifyHost.setPassword(pwd);
				final Display display = AddExtendHostDialog.this.getShell().getDisplay();
				final java.util.List<String> param = new ArrayList<String>();
				for (AttributeComposite ac : limitCompostie) {
					param.add(ac.getValue());
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						String msg = extendMangeComposite.getService().modifyLimit(valifyHost, param);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								modifyLimitBtn.setEnabled(true);
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
	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		return null;
	}

	@Override
	protected void initializeBounds() {
		Composite comp = (Composite) getButtonBar();// 取得按钮面板
		okBtn = super.createButton(comp, IDialogConstants.OK_ID, ResourceHandler.getValue("ok"), false);
		cancelBtn = super.createButton(comp, IDialogConstants.CANCEL_ID, ResourceHandler.getValue("cancel"), false);
		super.initializeBounds();
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			errorLb.setText(ResourceHandler.getValue("validateing"));
			String ip = ipText.getText();
			String userName = userNameText.getText();
			String pwd = pwdText.getText();
			HostRole role = HostRole.STANDBY;
			if (!isAddStandby)
				role = HostRole.SEGEMENT;
			Host valifyHost = new Host();
			valifyHost.setIp(ip);
			valifyHost.setUserName(userName);
			valifyHost.setPassword(pwd);
			valifyHost.setRole(role);

			// 是否能连接上
			boolean isCurrect = extendMangeComposite.getService().verificationHost(valifyHost);
			if (!isCurrect) {
				errorLb.setText(ResourceHandler.getValue("err_host"));
				return;
			}
			if (!isAddStandby) {
				// 是否已经添加到列表
				boolean isExist = extendMangeComposite.getService().isExistHost(valifyHost, (List<Host>) ((ExtendMangeComposite) extendMangeComposite).getTv().getInput());
				if (isExist) {
					errorLb.setText(ResourceHandler.getValue("err_host_exist"));
					return;
				}
			}
			// 是否已经在集群中存在
			GPManagerEntity gp = ((ManageComposite) extendMangeComposite.getMianComposite()).getGp();
			boolean isInGp = extendMangeComposite.getService().isHostInGP(valifyHost, gp);
			if (isInGp && !isAddStandby) {
				errorLb.setText(ResourceHandler.getValue("err_host_exist"));
				return;
			}
			if (isInGp && isAddStandby) {
				boolean isMaster = extendMangeComposite.getService().isMaster(valifyHost, gp);
				if (isMaster) {
					errorLb.setText(ResourceHandler.getValue("standby.master.error"));
					return;
				}
			}
			if (host != null) {
				host.setName(valifyHost.getName());
				host.setIp(ip);
				host.setUserName(userName);
				host.setPassword(pwd);
				host.setRole(role);
				host.setInGP(isInGp);
			} else {
				host = valifyHost;
				host.setInGP(isInGp);
			}
			setReturnCode(IDialogConstants.OK_ID);
		} else {
			setReturnCode(IDialogConstants.CANCEL_ID);
		}
		close();
	}

	@Override
	protected Point getInitialSize() {
		// TODO Auto-generated method stub
		Point initPoint = super.getInitialSize();
		point = new Point(initPoint.x + 100, initPoint.y);
		return point;
	}

	protected boolean isResizable() {
		return true;
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
}
