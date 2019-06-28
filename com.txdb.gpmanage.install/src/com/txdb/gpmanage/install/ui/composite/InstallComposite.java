package com.txdb.gpmanage.install.ui.composite;

import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.txdb.gpmanage.application.composite.AbstractComposite;
import com.txdb.gpmanage.core.common.CommonUtil;
import com.txdb.gpmanage.core.entity.InstallInfo;
import com.txdb.gpmanage.core.entity.impl.GPTreeNode;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.install.service.InstallUiService;

/**
 * 安装tab页面板
 * 
 * @author ws
 *
 */
public class InstallComposite extends AbstractComposite {
	/**
	 * 安装信息
	 */
	private InstallInfo installInfo;
	/**
	 * 依赖软件
	 */
	private DenpendencySoftComposite denpendencySoft;
	/**
	 * 主机管理面板
	 */
	private HostMangeComposite hostManage;
	/**
	 * 环境配置面板
	 */
	private ConfigureComposite configure;
	/**
	 * master节点安装面板
	 */
	private InstallMasterComposite instllMaster;
	/**
	 * 秘钥交换面板
	 */
	private SshComposite ssh;
	/**
	 * 系统检测面板
	 */
	private CheckSystemComposite checkSystem;
	/**
	 * 初始化文件配置面板
	 */
	private InitFileComposite initfile;
	/**
	 * 初始化集群面板
	 */
	private InstanceInstallComposite instanceInstall;
	/**
	 * 分节点安装面板
	 */
	private InstallSegmentComposite installSegment;

	public InstallComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	public void createmain() {
		installInfo = new InstallInfo();
		service = new InstallUiService(Display.getDefault());
		StackLayout layout = new StackLayout();
		right.setLayout(layout);
		hostManage = new HostMangeComposite(this, right, CompositeCode.HOSTMANAGE);
		denpendencySoft = new DenpendencySoftComposite(this, right, CompositeCode.DENPENDENCY_SOFT);
		configure = new ConfigureComposite(this, right, CompositeCode.CONFIGURE);
		instllMaster = new InstallMasterComposite(this, right, CompositeCode.INSTALL_MASTER);
		ssh = new SshComposite(this, right, CompositeCode.SSH);
		installSegment = new InstallSegmentComposite(this, right, CompositeCode.INSTALL_SEGMENT);
		checkSystem = new CheckSystemComposite(this, right, CompositeCode.SYSTEM_CHECK);
		initfile = new InitFileComposite(this, right, CompositeCode.INIT_FILE);
		instanceInstall = new InstanceInstallComposite(this, right, CompositeCode.RUN_INSTALL);
		layout.topControl = hostManage;
		right.layout();
	}

	@Override
	protected void setInput() {
		List<GPTreeNode> nodes = CommonUtil.createIntallNode();
		tv.setInput(nodes);
		tv.expandToLevel(nodes.get(0), 1);
		tv.setSelection(new StructuredSelection(new Object[] { nodes.get(0) }));
	}

	public HostMangeComposite getHostManage() {
		return hostManage;
	}

	public ConfigureComposite getConfigure() {
		return configure;
	}

	public InstallMasterComposite getInstllMaster() {
		return instllMaster;
	}

	public SshComposite getSsh() {
		return ssh;
	}

	public CheckSystemComposite getCheckSystem() {
		return checkSystem;
	}

	public InitFileComposite getInitfile() {
		return initfile;
	}

	public InstanceInstallComposite getInstanceInstall() {
		return instanceInstall;
	}

	public InstallInfo getInstallInfo() {
		return installInfo;
	}

	public void setInstallInfo(InstallInfo installInfo) {
		this.installInfo = installInfo;
	}

	public InstallSegmentComposite getInstallSegment() {
		return installSegment;
	}

	public DenpendencySoftComposite getDenpendencySoft() {
		return denpendencySoft;
	}
}
