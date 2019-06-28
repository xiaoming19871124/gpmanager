package com.txdb.gpmanage.monitor.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.entity.impl.SystemWarningEntity;
import com.txdb.gpmanage.monitor.i18n.MessageConstants;
import com.txdb.gpmanage.monitor.i18n.ResourceHandler;

public class SystemWarningDialog extends Dialog {
	
	private SystemWarningEntity warning;
	private Label errorLb;
	
	private WarningItem cpu;
	private WarningItem io;
	private WarningItem network;
	private WarningItem disk;
	private WarningItem healthy;
	private WarningItem memory;
	private WarningItem query;
	private WarningItem skew;
	
	private Text warnNameText;
	private Text mailText;
	
	private static String NUM_TYPE = "(%)";
	private static String TIME_TYPE = "(s)";
	private static String MBS_TYPE = "(MB/S)";
	private static String OTHER_TYPE = "";

	public SystemWarningEntity getWarn() {
		return warning;
	}

	public SystemWarningDialog(Shell parentShell, SystemWarningEntity warning) {
		super(parentShell);
		this.warning = warning;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(ResourceHandler.getValue(warning == null ? 
				MessageConstants.WARNING_DIALOG_TITLE_ADD : 
				MessageConstants.WARNING_DIALOG_TITLE_MODIFY));
		
		Point size = new Point(650, 380);
		newShell.setSize(size);
		newShell.setMinimumSize(size);
		
		Rectangle rect = Display.getCurrent().getBounds();
		newShell.setLocation((rect.width - size.x) / 2, (rect.height - size.y) / 2 - 50);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		createWarnName(parent);
		createWarnItem(parent);
		createEmail(parent);
		setValue();
		return parent;
	}

	private void createWarnName(Composite parent) {
		Composite comp_warningName = new Composite(parent, SWT.NONE);
		comp_warningName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp_warningName.setLayout(new GridLayout(2, false));
		
		new Label(comp_warningName, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.WARNING_DIALOG_NAME));
		warnNameText = new Text(comp_warningName, SWT.BORDER);
		warnNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createWarnItem(Composite parent) {
		Composite group_main = new Composite(parent, SWT.NONE);
		group_main.setLayoutData(new GridData(GridData.FILL_BOTH));
		group_main.setLayout(new GridLayout(2, true));
		
		// Part I (System)
		Group group_system = new Group(group_main, SWT.NONE);
		group_system.setText(ResourceHandler.getValue(MessageConstants.WARNING_DIALOG_GROUP_SYSTEM));
		group_system.setLayout(new GridLayout(1, true));
		group_system.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		cpu = new WarningItem(group_system, ResourceHandler.getValue(MessageConstants.WARNING_DIALOG_GROUP_SYSTEM_CPU), true, SystemWarningDialog.NUM_TYPE);
		io = new WarningItem(group_system, ResourceHandler.getValue(MessageConstants.WARNING_DIALOG_GROUP_SYSTEM_IO), true, SystemWarningDialog.MBS_TYPE);
		network = new WarningItem(group_system, ResourceHandler.getValue(MessageConstants.WARNING_DIALOG_GROUP_SYSTEM_NETWORK), true, SystemWarningDialog.MBS_TYPE);
		memory = new WarningItem(group_system, ResourceHandler.getValue(MessageConstants.WARNING_DIALOG_GROUP_SYSTEM_MEMORY), true, SystemWarningDialog.NUM_TYPE);
		
		// Part II (Greenplum)
		Group group_gp = new Group(group_main, SWT.NONE);
		group_gp.setText(ResourceHandler.getValue(MessageConstants.WARNING_DIALOG_GROUP_GREENPLUM));
		group_gp.setLayout(new GridLayout(1, true));
		group_gp.setLayoutData(new GridData(GridData.FILL_BOTH));

		disk = new WarningItem(group_gp, ResourceHandler.getValue(MessageConstants.WARNING_DIALOG_GROUP_GREENPLUM_DISK), true, SystemWarningDialog.NUM_TYPE);
		healthy = new WarningItem(group_gp, ResourceHandler.getValue(MessageConstants.WARNING_DIALOG_GROUP_GREENPLUM_HEALTH), false, SystemWarningDialog.OTHER_TYPE);
		query = new WarningItem(group_gp, ResourceHandler.getValue(MessageConstants.WARNING_DIALOG_GROUP_GREENPLUM_QUERY), true, SystemWarningDialog.TIME_TYPE);
		skew = new WarningItem(group_gp, ResourceHandler.getValue(MessageConstants.WARNING_DIALOG_GROUP_GREENPLUM_SKEW));
		
		// Part III (Error Message)
		errorLb = new Label(group_main, SWT.RIGHT);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		errorLb.setLayoutData(gd);
		errorLb.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		errorLb.setText("");
	}

	private void createEmail(Composite parent) {
		Composite comp_mail = new Composite(parent, SWT.NONE);
		comp_mail.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp_mail.setLayout(new GridLayout(2, false));
		
		new Label(comp_mail, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.WARNING_DIALOG_MAIL));
		mailText = new Text(comp_mail, SWT.BORDER);
		mailText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void setValue() {
		if (warning != null) {
			warnNameText.setText(warning.getWarningName());
			cpu.setCheck(null != warning.getCpuWarning() && !warning.getCpuWarning().isEmpty());
			cpu.setValue(warning.getCpuWarning() != null ? warning.getCpuWarning() : "");

			io.setCheck(warning.getIoWarning() != null && !warning.getIoWarning().isEmpty());
			io.setValue(warning.getIoWarning() != null ? warning.getIoWarning() : "");

			network.setCheck(warning.getNetWorkWarning() != null && !warning.getNetWorkWarning().isEmpty());
			network.setValue(warning.getNetWorkWarning() != null ? warning.getNetWorkWarning() : "");

			disk.setCheck(warning.getDiskWarning() != null && !warning.getDiskWarning().isEmpty());
			disk.setValue(warning.getDiskWarning() != null ? warning.getDiskWarning() : "");

			healthy.setCheck(warning.getFaultWarning() == 1);
			healthy.setValue("");

			memory.setCheck(warning.getMemoryWarning() != null && !warning.getMemoryWarning().isEmpty());
			memory.setValue(warning.getMemoryWarning() != null ? warning.getMemoryWarning() : "");

			query.setCheck(warning.getQueryWarning() != null && !warning.getQueryWarning().isEmpty());
			query.setValue(warning.getQueryWarning() != null ? warning.getQueryWarning() : "");

			skew.setCheck(warning.getSkewWarning() != null && !warning.getSkewWarning().isEmpty());
			skew.setValue(warning.getSkewWarning() != null ? warning.getSkewWarning() : "");
			mailText.setText(warning.getMail());
		}
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		return null;
	}

	@Override
	protected void initializeBounds() {
		Composite comp = (Composite) getButtonBar();
		super.createButton(comp, IDialogConstants.OK_ID, ResourceHandler.getValue("ok"), false);
		super.createButton(comp, IDialogConstants.CANCEL_ID, ResourceHandler.getValue("cancel"), false);
		super.initializeBounds();
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			if (warning == null)
				warning = new SystemWarningEntity();
			String warnName = warnNameText.getText();
			if (warnName == null || warnName.isEmpty()) {
				errorLb.setText("warn name can not empty");
				return;
			}
			warning.setWarningName(warnName);
			String mailStr = mailText.getText();
			if (mailStr == null || mailStr.isEmpty()) {
				errorLb.setText("email can not empty");
				return;
			}
			warning.setMail(mailStr);
			boolean isWarncpu = cpu.isCheck();
			if (isWarncpu && cpu.getValue() != null)
				warning.setCpuWarning(cpu.getValue());
			boolean isWwarnIo = io.isCheck();
			if (isWwarnIo && io.getValue() != null)
				warning.setIoWarning(io.getValue());
			boolean isWarnNetWwork = network.isCheck();
			if (isWarnNetWwork && network.getValue() != null)
				warning.setNetWorkWarning(network.getValue());
			boolean isWarnDisk = disk.isCheck();
			if (isWarnDisk && disk.getValue() != null)
				warning.setDiskWarning(disk.getValue());
			boolean isWarnHealthy = healthy.isCheck();
			warning.setFaultWarning(isWarnHealthy ? 1 : 0);
			boolean isWarnMemory = memory.isCheck();
			if (isWarnMemory && memory.getValue() != null)
				warning.setMemoryWarning(memory.getValue());
			boolean isWarnQuery = query.isCheck();
			if (isWarnQuery && query.getValue() != null)
				warning.setQueryWarning(query.getValue());
			boolean isWarnSkew = skew.isCheck();
			if (isWarnSkew && skew.getValue() != null)
				warning.setSkewWarning(skew.getValue());
			setReturnCode(IDialogConstants.OK_ID);
		} else
			setReturnCode(IDialogConstants.CANCEL_ID);
		
		close();
	}

	@Override
	protected Point getInitialSize() {
		Point point = super.getInitialSize();
		return new Point(point.x + 100, point.y);
	}

	private class WarningItem extends Composite {
		private Button check;
		private Text valueText;

		public WarningItem(Composite parent, int style) {
			super(parent, style);
		}

		public WarningItem(Composite parent, String title, boolean isSelect, boolean isEnabled, String type, String defualValue) {
			super(parent, SWT.NONE);
			createUI(title, isSelect, isEnabled, type, defualValue);
		}

		public WarningItem(Composite parent, String title, boolean isEnabled, String type) {
			super(parent, SWT.NONE);
			createUI(title, false, isEnabled, type, "");
		}

		public WarningItem(Composite parent, String title) {
			super(parent, SWT.NONE);
			createUI(title, false, true, SystemWarningDialog.NUM_TYPE, "");
		}

		private void createUI(String title, boolean isSelect, boolean isEnabled, String type, String defualValue) {
			setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
			setLayout(new GridLayout(3, false));
			
			Label titleLabel = new Label(this, SWT.NONE);
			titleLabel.setText(title + type + ": ");
			
			valueText = new Text(this, SWT.BORDER);
			valueText.setEnabled(isEnabled);
			valueText.setText(defualValue == null ? "" : defualValue);
			valueText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			check = new Button(this, SWT.CHECK);
			check.setSelection(isSelect);
		}

		public String getValue() {
			return valueText.getText();
		}

		public void setValue(String value) {
			valueText.setText(value);
		}

		public void setCheck(boolean isCheck) {
			check.setSelection(isCheck);
		}

		public boolean isCheck() {
			return check.getSelection();
		}
	}
}
