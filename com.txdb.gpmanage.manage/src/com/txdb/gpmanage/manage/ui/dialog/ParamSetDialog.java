package com.txdb.gpmanage.manage.ui.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.gp.entry.GPConfig;
import com.txdb.gpmanage.core.gp.entry.GPConfigValue;
import com.txdb.gpmanage.manage.entity.GPConfigParam;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.service.ManageUiService;

/**
 * 添加扩展机器
 * 
 * @author ws
 *
 */
public class ParamSetDialog extends Dialog {
	private GPConfigParam host;
	private ComboViewer paramCombo;
	private Text typeText;
	private Text minValueText;
	private Text maxValueText;
	private Text masterValueText;
	private Text segValueText;
	private Text masterText;
	private Text segText;
	private Combo masterCombo;
	private Combo segCombo;
	private Label masterLb;
	private Label segLb;
	private Label errorLb;
	private List<GPConfig> gpconfigs;
	private GPManagerEntity gp;
	private ManageUiService service;
	private GPConfigValue value;

	// private GPConfigParam param;

	public ParamSetDialog(Shell parentShell, List<GPConfig> gpconfigs, GPManagerEntity gp, ManageUiService service) {
		super(parentShell);
		this.gpconfigs = gpconfigs;
		this.gp = gp;
		this.service = service;
	}

	public ParamSetDialog(Shell parentShell, List<GPConfig> gpconfigs, GPManagerEntity gp, ManageUiService service, GPConfigParam param) {
		super(parentShell);
		this.gpconfigs = gpconfigs;
		this.gp = gp;
		this.service = service;
		this.host = param;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (host == null)
			newShell.setText(ResourceHandler.getValue("add"));
		else
			newShell.setText(ResourceHandler.getValue("modify"));

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite top = new Composite(parent, SWT.NONE);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		top.setLayout(new GridLayout(4, false));
		// 参数名
		Label nameLb = new Label(top, SWT.NONE);
		nameLb.setText(ResourceHandler.getValue("param_name"));
		nameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		paramCombo = new ComboViewer(top, SWT.BORDER|SWT.READ_ONLY);
		paramCombo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		paramCombo.setContentProvider(new ArrayContentProvider());
		paramCombo.setLabelProvider(new GpconfignewLabelProvider());
		paramCombo.setInput(gpconfigs);
		// 参数类型
		Label typeLb = new Label(top, SWT.NONE);
		typeLb.setText(ResourceHandler.getValue("type"));
		typeLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		typeText = new Text(top, SWT.NONE);
		typeText.setEditable(false);
		typeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		// 最小值
		Label minValueLb = new Label(top, SWT.NONE);
		minValueLb.setText(ResourceHandler.getValue("min_value"));
		minValueLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		minValueText = new Text(top, SWT.BORDER);
		minValueText.setEditable(false);
		minValueText.setEnabled(false);
		minValueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// 最大值
		Label maxValueLb = new Label(top, SWT.NONE);
		maxValueLb.setText(ResourceHandler.getValue("max_value"));
		maxValueLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		maxValueText = new Text(top, SWT.BORDER);
		maxValueText.setEditable(false);
		maxValueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// master当前值
		Label masterValueLb = new Label(top, SWT.NONE);
		masterValueLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		masterValueLb.setText(ResourceHandler.getValue("master_current_value"));
		masterValueText = new Text(top, SWT.BORDER);
		masterValueText.setEditable(false);
		masterValueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// segment当前值
		Label segValueLb = new Label(top, SWT.NONE);
		segValueLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		segValueLb.setText(ResourceHandler.getValue("segment_current_value"));
		segValueText = new Text(top, SWT.BORDER);
		segValueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		segValueText.setEditable(false);
		if (host == null || !host.getGpconfig().getVartype().equals("bool")) {
			masterLb = new Label(top, SWT.NONE);
			masterLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
			masterLb.setText(ResourceHandler.getValue("master_set_value"));
			masterText = new Text(top, SWT.BORDER);
			masterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			// segment设置值
			segLb = new Label(top, SWT.NONE);
			segLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
			segLb.setText(ResourceHandler.getValue("segment_set_value"));
			segText = new Text(top, SWT.BORDER);
			segText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		} else {
			masterLb = new Label(top, SWT.NONE);
			masterLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
			masterLb.setText(ResourceHandler.getValue("master_set_value"));
			masterCombo = new Combo(top, SWT.BORDER | SWT.READ_ONLY);
			masterCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			masterCombo.setItems(new String[] { "true", "false" });
			segLb = new Label(top, SWT.NONE);
			segLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
			segLb.setText(ResourceHandler.getValue("segment_set_value"));
			segCombo = new Combo(top, SWT.BORDER | SWT.READ_ONLY);
			segCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			segCombo.setItems(new String[] { "true", "false" });
		}

		if (host != null) {
			paramCombo.setSelection(new StructuredSelection(host.getGpconfig()));
			typeText.setText(host.getGpconfig().getVartype());
			maxValueText.setText(host.getGpconfig().getMax_val());
			minValueText.setText(host.getGpconfig().getMin_val());
			masterValueText.setText(host.getValue().getMasterValue());
			segValueText.setText(host.getValue().getSegmentValue());
			if (host.getGpconfig().getVartype().equals("bool")) {
				masterCombo.setText(host.getMasterValue());
				segCombo.setText(host.getSegValue());
			} else {
				segText.setText(host.getSegValue());
				masterText.setText(host.getMasterValue());
			}
			value = host.getValue();

		}
		paramCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				masterValueText.setText("");
				segValueText.setText("");
				errorLb.setText("");
				paramCombo.getCombo().setEnabled(false);
				StructuredSelection selection = (StructuredSelection) paramCombo.getSelection();
				final GPConfig gpconfig = (GPConfig) selection.getFirstElement();
				typeText.setText(gpconfig.getVartype());
				minValueText.setText(gpconfig.getMin_val());
				maxValueText.setText(gpconfig.getMax_val());
				refreshUI(gpconfig, top);
				final Display display = Display.getCurrent();
				errorLb.setText(ResourceHandler.getValue("get_current_value"));
				new Thread(new Runnable() {
					@Override
					public void run() {
						value = service.queryParamValueByName(gp, gpconfig.getName());
						if (value != null) {
							display.syncExec(new Runnable() {
								@Override
								public void run() {
									masterValueText.setText(value.getMasterValue() == null ? "" : value.getMasterValue());
									segValueText.setText(value.getSegmentValue() == null ? "" : value.getSegmentValue());
									errorLb.setText("");
									paramCombo.getCombo().setEnabled(true);
								}
							});
						} else {
							display.syncExec(new Runnable() {
								@Override
								public void run() {
									errorLb.setText(ResourceHandler.getValue("get_current_value_error"));
									paramCombo.getCombo().setEnabled(true);
								}
							});
						}
					}
				}).start();

			}
		});
		return top;
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		return null;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		// composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		// create a layout with spacing and margins appropriate for the font
		// size.
		GridLayout layout = new GridLayout();
		layout.numColumns = 0; // this is incremented by createButton
		layout.makeColumnsEqualWidth = false;
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		composite.setLayout(layout);
		// GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END
		// | GridData.VERTICAL_ALIGN_CENTER);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		composite.setLayoutData(data);
		composite.setFont(parent.getFont());

		// Add the buttons to the button bar.
		createButtonsForButtonBar(composite);
		return composite;
	}

	@Override
	protected void initializeBounds() {
		Composite comp = (Composite) getButtonBar();// 取得按钮面板
		// 错误提示
		((GridLayout) comp.getLayout()).numColumns++;
		errorLb = new Label(comp, SWT.RIGHT);
		errorLb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		errorLb.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		super.createButton(comp, IDialogConstants.OK_ID, ResourceHandler.getValue("ok"), false);
		super.createButton(comp, IDialogConstants.CANCEL_ID, ResourceHandler.getValue("cancel"), false);
		super.initializeBounds();
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			errorLb.setText(ResourceHandler.getValue("validateing"));
			StructuredSelection selection = (StructuredSelection) paramCombo.getSelection();
			if (selection.isEmpty()) {
				errorLb.setText(ResourceHandler.getValue("select_param_error"));
				return;
			}
			final GPConfig gpconfig = (GPConfig) selection.getFirstElement();
			GPConfigParam param = new GPConfigParam();
			if (gpconfig.getVartype().equals("bool")) {
				param.setMasterValue(masterCombo.getText());
				param.setSegValue(segCombo.getText());
			} else {
				boolean isMasterReady = verify("master", gpconfig.getVartype(), masterText.getText(), gpconfig.getMax_val(), gpconfig.getMin_val());
				boolean isSegReady = verify("segment", gpconfig.getVartype(), segText.getText(), gpconfig.getMax_val(), gpconfig.getMin_val());
				if (!isMasterReady || !isSegReady) {
					return;
				}
				param = new GPConfigParam();
				param.setMasterValue(masterText.getText());
				param.setSegValue(segText.getText());
			}
			param.setGpconfig(gpconfig);
			param.setValue(value);
			if (host == null) {
				host = param;
			} else {
				host.setGpconfig(gpconfig);
				host.setValue(value);
				host.setMasterValue(param.getMasterValue());
				host.setSegValue(param.getSegValue());

			}
			setReturnCode(IDialogConstants.OK_ID);
		} else {
			setReturnCode(IDialogConstants.CANCEL_ID);
		}
		close();
	}

	@Override
	protected Point getInitialSize() {
		Point point = super.getInitialSize();
		return new Point(point.x + 100, point.y);// super.getInitialSize()可以得到原来对话框的大小
	}

	public static void main(String[] args) {
		String a = "a,";
		String[] as = a.split(",");
		for (String s : as) {
			System.out.println(s);
		}
	}

	private void refreshUI(GPConfig gpconfig, Composite top) {
		String type = gpconfig.getVartype();
		if (type.equals("bool")) {
			if (masterText != null && !masterText.isDisposed()) {
				masterText.dispose();
				segText.dispose();
				masterLb.dispose();
				segLb.dispose();
				masterText = null;
				segText = null;
				masterLb = null;
				segLb = null;
			}
			if (masterCombo == null || masterCombo.isDisposed()) {
				masterLb = new Label(top, SWT.NONE);
				masterLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
				masterLb.setText(ResourceHandler.getValue("master_set_value"));
				masterCombo = new Combo(top, SWT.BORDER | SWT.READ_ONLY);
				masterCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				masterCombo.setItems(new String[] { "true", "false" });
				masterCombo.setText("true");
				segLb = new Label(top, SWT.NONE);
				segLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
				segLb.setText(ResourceHandler.getValue("segment_set_value"));
				segCombo = new Combo(top, SWT.BORDER | SWT.READ_ONLY);
				segCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				segCombo.setItems(new String[] { "true", "false" });
				segCombo.setText("true");
			}

		} else {
			if (masterCombo != null && !masterCombo.isDisposed()) {
				masterCombo.dispose();
				segCombo.dispose();
				masterLb.dispose();
				segLb.dispose();
				masterCombo = null;
				segCombo = null;
				masterLb = null;
				segLb = null;
			}
			if (masterText == null || masterText.isDisposed()) {
				masterLb = new Label(top, SWT.NONE);
				masterLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
				masterLb.setText(ResourceHandler.getValue("master_set_value"));
				masterText = new Text(top, SWT.BORDER);
				masterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				// segment设置值
				segLb = new Label(top, SWT.NONE);
				segLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
				segLb.setText(ResourceHandler.getValue("segment_set_value"));
				segText = new Text(top, SWT.BORDER);
				segText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			}

		}
		if (gpconfig.getContext().equals("internal")) {
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		} else {
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		}
		top.layout();
		// shell.layout();
	}

	private boolean verify(String role, String type, String value, String max_val, String min_val) {
		if (value == null || value.isEmpty()) {
			errorLb.setText(ResourceHandler.getValue("role_value_null", new String[] { role }));
			return false;
		}
		if (type.equals("integer")) {
			try {
				int intValue = Integer.valueOf(value);
				int intMax_val = Integer.valueOf(max_val);
				int intMin_val = Integer.valueOf(min_val);
				if (intValue <= intMax_val && intValue >= intMin_val) {
					return true;
				}
			} catch (NumberFormatException e) {
				errorLb.setText(ResourceHandler.getValue("role_value_int", new String[] { role }));
				return false;
			}
		} else if (type.equals("real")) {
			try {
				Float intValue = Float.valueOf(value);
				Float intMax_val = Float.valueOf(max_val);
				Float intMin_val = Float.valueOf(min_val);
				if (intValue <= intMax_val && intValue >= intMin_val) {
					return true;
				}
			} catch (NumberFormatException e) {
				errorLb.setText(ResourceHandler.getValue("role_value_real", new String[] { role }));
				return false;
			}
		}
		return true;
	}

	private class GpconfignewLabelProvider implements ILabelProvider {

		@Override
		public void removeListener(ILabelProviderListener listener) {

		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void dispose() {
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public String getText(Object element) {
			return ((GPConfig) element).getName();
		}

		@Override
		public Image getImage(Object element) {
			return null;
		}
	}

	public GPConfigParam getParam() {
		return host;
	}
}
