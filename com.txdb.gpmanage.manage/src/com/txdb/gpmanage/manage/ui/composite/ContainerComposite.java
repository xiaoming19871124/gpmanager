package com.txdb.gpmanage.manage.ui.composite;

import java.util.List;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;

import com.txdb.gpmanage.core.entity.DbUser;
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.entity.impl.GPTreeNode;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.manage.service.ManageUiService;

public class ContainerComposite extends Composite {
	/**
	 * 父容器 ManageComposite
	 */
	private ManageComposite parent;
	/**
	 * 对应的节点
	 */
	private GPTreeNode node;
	/**
	 * 扩容页面
	 */
	private ExtendMangeComposite extendMangeComposite;
	/**
	 * 表重分布页面
	 */
	private RedistributionComposite redistributionComposite;
	/**
	 * mirror管理页面
	 */
	private AddMirrorComposite addMirrorComposite;
	/**
	 * 集群管理首页
	 */
	private GPManageComposite gpMangeComposite;
	/**
	 * 故障管理首页
	 */
	private BreakDownComposite breakDownComposite;
	/**
	 * standby管理页面
	 */
	private AddStandbyComposite addStandbyComposite;
	/**
	 * 权限管理页面
	 */
	private AuthorityMangeComposite authorityMangeComposite;
	/**
	 * 连接管理页面
	 */
	private ConnectionMangeComposite connectionMangeComposite;
	/**
	 * 集群参数设置页面
	 */
	private ParamMangeComposite paramMangeComposite;
	/**
	 * 用户管理页面
	 */
	private UserMangeComposite userMangeComposite;
	/**
	 * 强制访问控制页面
	 */
	private PolicyMangeComposite policyMangeComposite;

	public ContainerComposite(Composite parent, Composite right, GPTreeNode node, int style) {
		super(right, style);
		this.node = node;
		this.parent = (ManageComposite) parent;
		StackLayout layout = new StackLayout();
		this.setLayout(layout);
		createUI();
	}

	/**
	 * 右侧功能区
	 */
	private void createUI() {
		GPManagerEntity gpNode = (GPManagerEntity) node.getGPEntity();
		String role = gpNode.getRole();
		if (role.equals("D")) {
			gpMangeComposite = new GPManageComposite(parent, this, null);
			extendMangeComposite = new ExtendMangeComposite(parent, this, CompositeCode.EXTEND);
			redistributionComposite = new RedistributionComposite(parent, this, CompositeCode.REDISTRIBUTION);
			addStandbyComposite = new AddStandbyComposite(parent, this, CompositeCode.ADDSTANDBY);
			addMirrorComposite = new AddMirrorComposite(parent, this, CompositeCode.ADDMIRROR);
			breakDownComposite = new BreakDownComposite(parent, this, CompositeCode.BREAKEDIWN);
			connectionMangeComposite = new ConnectionMangeComposite(parent, this, CompositeCode.CONNICTMANGE);
			paramMangeComposite = new ParamMangeComposite(parent, this, CompositeCode.PARAM_MANAGE);
			authorityMangeComposite = new AuthorityMangeComposite(parent, this, CompositeCode.AUTHORITYMANAGE);
			userMangeComposite = new UserMangeComposite(parent, this, CompositeCode.USERMANAGE);
			// policyMangeComposite = new PolicyMangeComposite(parent, this,
			// CompositeCode.POLICY);
			StackLayout layout = (StackLayout) this.getLayout();
			layout.topControl = gpMangeComposite;
			this.layout();
		} else {
			policyMangeComposite = new PolicyMangeComposite(parent, this, CompositeCode.POLICY);
			StackLayout layout = (StackLayout) this.getLayout();
			layout.topControl = policyMangeComposite;
		}

	}

	public GPTreeNode getNode() {
		return node;
	}

	public void setNode(GPTreeNode node) {
		this.node = node;
	}

	public void setTopComposite(GPTreeNode node) {
		CompositeCode code = node.getCompositeCode();
		StackLayout layout = (StackLayout) this.getLayout();
		if (code == null) {
			layout.topControl = gpMangeComposite;
			this.layout();
			return;
		}
		switch (code) {
		case EXTEND:
			layout.topControl = extendMangeComposite;
			break;
		case REDISTRIBUTION:
			layout.topControl = redistributionComposite;
			break;
		case ADDSTANDBY:
			layout.topControl = addStandbyComposite;
			break;
		case ADDMIRROR:
			layout.topControl = addMirrorComposite;
			break;
		case BREAKEDIWN:
			layout.topControl = breakDownComposite;
			break;
		case CONNICTMANGE:
			layout.topControl = connectionMangeComposite;
			break;
		case PARAM_MANAGE:
			layout.topControl = paramMangeComposite;
			break;
		case AUTHORITYMANAGE:
			layout.topControl = authorityMangeComposite;
			break;
		case USERMANAGE:
			layout.topControl = userMangeComposite;
			break;
		case POLICY:
			layout.topControl = policyMangeComposite;
			break;
		default:
			layout.topControl = gpMangeComposite;
			break;
		}
		this.layout();
	}

	public void updateAllValue() {
		GPManagerEntity entity = (GPManagerEntity) node.getGPEntity();
		List<DbUser> dbUsers = ((ManageUiService) parent.getService()).loadUser(entity);
		if (entity.getRole().equals("D")) {
			if (entity.getHasMirror() != 0)
				addMirrorComposite.loadMirrorState();
			if (entity.getHasStandby() != 0)
				addStandbyComposite.loadStandbyState();
			connectionMangeComposite.updateAuthority();
			extendMangeComposite.cleanValues();
			redistributionComposite.clearAll();
			userMangeComposite.updateUser(dbUsers);
			authorityMangeComposite.loadData(dbUsers);
		} else {
			policyMangeComposite.loadValue(dbUsers);
		}
	}

	public AuthorityMangeComposite getAuthorityMangeComposite() {
		return authorityMangeComposite;
	}

	/**
	 * 刷新界面值
	 */
	// public void refreshValues() {
	// gpMangeComposite.refreshMaster();
	// addStandbyComposite.refreshStandby();
	// addMirrorComposite.refreshMirror();
	// connectionMangeComposite.refreshAuthority();
	// redistributionComposite.updateAll();
	// }
}
