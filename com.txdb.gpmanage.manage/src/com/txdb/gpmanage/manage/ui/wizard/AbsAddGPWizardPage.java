package com.txdb.gpmanage.manage.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * 添加gpwizard page 超类
 * 
 * @author ws
 *
 */
public abstract class AbsAddGPWizardPage extends WizardPage implements ModifyListener {
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 描述信息
	 */
	private String message;

	protected AbsAddGPWizardPage(String pageName) {
		super(pageName);
	}

	protected AbsAddGPWizardPage(String pageName, String title, String message) {
		super(pageName);
		this.title = title;
		this.message = message;
	}

	@Override
	public void createControl(Composite parent) {
		setTitle(title);
		setMessage(message);
		this.setPageComplete(false);
		Composite topComp = new Composite(parent, SWT.NULL);
//		topComp.setLayout(new GridLayout(2, false));
		createUI(topComp);
		setControl(topComp);
	}


	/**
	 * 创建主界面
	 * 
	 * @param composite
	 */
	protected abstract void createUI(Composite composite);

	/**
	 * 验证输入信息
	 * 
	 * @return
	 */
	protected abstract boolean validateValue();

}
