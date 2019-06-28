package com.txdb.gpmanage.manage.ui.composite;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.core.entity.DbUser;
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.manage.entity.Policy;
import com.txdb.gpmanage.manage.service.ManageUiService;

/**
 * 扩容管理面板
 * 
 * @author ws
 *
 */
public class PolicyMangeComposite extends IupperComposite {
	private List<Policy> policy;
	private PolicySetComposite psc;
	private PolicyApplyComposite pac;

	PolicyMangeComposite(ManageComposite mianComposite, Composite parent, CompositeCode code) {
		super(mianComposite, parent, code, "安全管理--强制访问控制", "系统为保证更高程度的安全性，按照安全标准中安全策略的要求，所采取的强制访问检查手段");

	}

	@Override
	public void createCenter(Composite composte) {
		FillLayout gd_composite = new FillLayout();
		composte.setLayout(gd_composite);
		CTabFolder folder = new CTabFolder(composte, SWT.NONE);
		CTabItem grantTabItem = new CTabItem(folder, SWT.NONE);
		grantTabItem.setText("set policy");
		pac = new PolicyApplyComposite(folder, this,SWT.NONE);
		grantTabItem.setControl(pac);
		CTabItem policyTabItem = new CTabItem(folder, SWT.NONE);
		policyTabItem.setText("policy manage");
		psc = new PolicySetComposite(folder, this, SWT.NONE);
		policyTabItem.setControl(psc);
		folder.setSelection(grantTabItem);
	}

	public GPManagerEntity getGp() {
		return ((ManageComposite) mianComposite).getGp();
	}

	public StyledText getTextUI() {
		return text;
	}

	public void loadValue(final List<DbUser> users) {
		loadPolicy();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				pac.getUserList().setInput(users);
				pac.getPolicyCombo().setInput(policy);
				psc.getPolicyTv().setInput(policy);
				psc.getPolicyTv().refresh();
				pac.getUserList().refresh();
				pac.getPolicyCombo().refresh();
			}
		});
	}

	public void refreshPolicy() {
		pac.getPolicyCombo().refresh();
	}

	public void loadPolicy() {
		GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
		policy = ((ManageUiService) getService()).loadPollicy(gp, text, PolicyMangeComposite.this);
	}
}
