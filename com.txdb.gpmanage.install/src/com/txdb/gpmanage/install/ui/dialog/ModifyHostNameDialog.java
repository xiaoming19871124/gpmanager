package com.txdb.gpmanage.install.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.common.CommonUtil;
import com.txdb.gpmanage.core.entity.Host;
import com.txdb.gpmanage.install.i18n.ResourceHandler;

public class ModifyHostNameDialog extends Dialog {
	private List<Host> host;
	private List<ModifyComp> modifyComps;
	private Map<String, Host> map;
	private Label errLb;

	public Map<String, Host> getMap() {
		return map;
	}

	public ModifyHostNameDialog(Shell parentShell, List<Host> host) {
		super(parentShell);
		this.host = host;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(ResourceHandler.getValue("modify"));

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		top.setLayout(new GridLayout(3, false));
		modifyComps = new ArrayList<ModifyComp>();
		for (Host h : host) {
			ModifyComp com = new ModifyComp(h, top);
			modifyComps.add(com);
		}
		errLb = new Label(top, SWT.RIGHT);
		errLb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		errLb.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		return top;
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		return null;
	}

	@Override
	protected void initializeBounds() {
		Composite comp = (Composite) getButtonBar();// 取得按钮面板
		super.createButton(comp, IDialogConstants.OK_ID, ResourceHandler.getValue("ok"), false);
		super.createButton(comp, IDialogConstants.CANCEL_ID, ResourceHandler.getValue("cancel"), false);
		super.initializeBounds();
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			map = new HashMap<String, Host>();
			for (int i = 0; i < modifyComps.size(); i++) {
				ModifyComp com = modifyComps.get(i);
				if (!com.isChange())
					continue;
				String newName = com.getModifyName();
				if (!CommonUtil.validateHostName(newName)) {
					errLb.setText(ResourceHandler.getValue("hostConfigure.fail.modifyHostName", new String[] { newName }));
					return;
				}
				for (int j = i + 1; j < modifyComps.size(); j++) {
					ModifyComp inCom = modifyComps.get(j);
					if (!inCom.isChange()) {
						if (newName.equals(inCom.getHost().getName())) {
							errLb.setText(ResourceHandler.getValue("hostConfigure.fail.HostName",new String[]{newName}));
							return;
						}
					}
					if (newName.equals(inCom.getModifyName())) {
						errLb.setText(ResourceHandler.getValue("hostConfigure.fail.HostName",new String[]{newName}));
						return;
					}
				}

			}
			for (ModifyComp com : modifyComps) {
				boolean isChange = com.isChange();
				if (!isChange)
					continue;
				Host host = com.getHost();
				String newName = com.getModifyName();
				map.put(newName, host);
			}
			setReturnCode(IDialogConstants.OK_ID);
		} else {
			setReturnCode(IDialogConstants.CANCEL_ID);
		}
		close();
	}

	// @Override
	// protected Point getInitialSize() {
	// return new Point(300, 400);// super.getInitialSize()可以得到原来对话框的大小
	// }
	private class ModifyComp {
		private Host host;
		private Text text;
		private boolean isChange = false;

		public ModifyComp(Host host, Composite parent) {
			this.host = host;
			Label nameLb = new Label(parent, SWT.NONE);
			nameLb.setText(host.getName());
			Label lb = new Label(parent, SWT.NONE);
			lb.setText("-->");
			text = new Text(parent, SWT.BORDER);
			nameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
			lb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		}

		public String getModifyName() {
			String changName = "";
			if (text.getText() != null && !text.getText().isEmpty()) {
				changName = text.getText().trim();
			}
			return changName;

		}

		public boolean isChange() {
			if (text.getText() != null && !text.getText().isEmpty())
				isChange = true;
			return isChange;
		}

		public Host getHost() {
			return host;
		}
	}
}
