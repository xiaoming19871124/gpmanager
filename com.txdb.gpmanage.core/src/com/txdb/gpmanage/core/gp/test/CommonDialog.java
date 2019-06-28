package com.txdb.gpmanage.core.gp.test;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CommonDialog extends TitleAreaDialog {

	private String dialogTitle = "";
	private String dialogMessage = "";
	private String initTextContent = "";
	private Object resultObject = null;

	private Text txt_modify;
	private boolean readonly;

	private Point applicationSize = new Point(500, 600);

	protected CommonDialog(Shell parentShell, String initTextContent) {
		super(parentShell);
		this.initTextContent = initTextContent;
	}

	public CommonDialog(Shell parentShell) {
		super(parentShell);
	}
	
	public CommonDialog(Shell parentShell, boolean readonly) {
		super(parentShell);
		this.readonly = readonly;
	}
	
	public void setInitTextContent(String initTextContent) {
		this.initTextContent = initTextContent;
	}
	
	@Override
	public void setTitle(String newTitle) {
		this.dialogTitle = newTitle;
	}
	
	@Override
	public void setMessage(String newMessage) {
		this.dialogMessage = newMessage;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		super.setTitle(dialogTitle);
		super.setMessage(dialogMessage, IMessageProvider.INFORMATION);

		Composite topComp = new Composite(parent, SWT.BORDER);
		topComp.setLayoutData(new GridData(GridData.FILL_BOTH));

		// TODO
		topComp.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(GridData.FILL_BOTH);
		txt_modify = new Text(topComp, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		txt_modify.setLayoutData(gridData);
		txt_modify.setText(initTextContent);
		txt_modify.setEditable(!readonly);

		return topComp;
	}

	public void setSize(int width, int height) {
		applicationSize = new Point(width, height);
	}

	@Override
	protected Point getInitialSize() {
		return applicationSize;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true).setEnabled(!readonly);;
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		setReturnCode(1);
		if (buttonId == 0) {
			String[] lines = txt_modify.getText().replaceAll("\r", "").split("\n");
//			Properties props = new Properties();
//			for (String host : hosts) {
//				String[] ipName = host.split(" ");
//				System.out.println(ipName[0] +"<->"+ ipName[1]);
//				props.setProperty(ipName[0], ipName[1]);
//			}
			resultObject = lines;
			setReturnCode(0);
		}
		close();
	}

	public Object getResultObject() {
		return resultObject;
	}

	public void setResultObject(Object resultObject) {
		this.resultObject = resultObject;
	}
}
