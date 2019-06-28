package com.txdb.gpmanage.manage.ui.wizard;

import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.wizard.Wizard;

import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.ui.composite.ManageComposite;

/**
 * 添加集群Wizard
 * 
 * @author ws
 *
 */
public class AddGPWizard extends Wizard {
	// /**
	// * master节点设置
	// */
	// private HostSetPage hostSetPage;
	/**
	 * gp集群设置
	 */
	private GPSetPage gpSetPage;
	/**
	 * 节点信息
	 */
	private GPManagerEntity node;
	/**
	 * 管理界面实例
	 */
	private ManageComposite manageComposite;

	@Override
	public boolean performFinish() {
		return true;
	}

	public AddGPWizard() {
		this.setDialogSettings(new DialogSettings("set master host"));
	}

	// public AddGPWizard(ManageComposite manageComposite) {
	// this.setDialogSettings(new DialogSettings("set master host"));
	// this.manageComposite = manageComposite;
	// }

	public AddGPWizard(ManageComposite manageComposite, GPManagerEntity node) {
		this.setDialogSettings(new DialogSettings(ResourceHandler.getValue("add_gp_title")));
		this.manageComposite = manageComposite;
		this.node = node;
	}

	// public AddGPWizard(GPManagerEntity node) {
	// this.node = node;
	// this.setDialogSettings(new DialogSettings("set master host"));
	// }

	@Override
	public void addPages() {
		// hostSetPage = new HostSetPage("set master host",
		// "master host configure", "input master host message");
		gpSetPage = new GPSetPage(ResourceHandler.getValue("gp_add_dlg_name"), ResourceHandler.getValue("gp_add_dlg_title"), ResourceHandler.getValue("gp_add_dlg_desc"));
		this.addPage(gpSetPage);
		// this.addPage(hostSetPage);
	}

	public GPManagerEntity getNode() {
		return node;
	}

	public ManageComposite getManageComposite() {
		return manageComposite;
	}

	public void setManageComposite(ManageComposite manageComposite) {
		this.manageComposite = manageComposite;
	}

}
