package com.txdb.gpmanage.manage.ui.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
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

import com.txdb.gpmanage.manage.entity.Compartment;
import com.txdb.gpmanage.manage.entity.DbLabel;
import com.txdb.gpmanage.manage.entity.Level;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.ui.provider.CompartmentLabelProvider;

public class AddDbLabelDialog extends Dialog {
	// private Text labelNameText;
	private Text labelIdText;
	private DbLabel dblabel;
	private ListViewer levelList;
	private CheckboxTableViewer compartmentTv;
	private List<Level> levels;
	private List<Compartment> compartments;
	private Level selectLevel;
	private List<Compartment> selectCompartment;
	private Label errorLb;

	public AddDbLabelDialog(Shell parentShell, DbLabel dbLabel) {
		super(parentShell);
		this.dblabel = dbLabel;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (dblabel != null)
			newShell.setText(ResourceHandler.getValue("add"));
		else
			newShell.setText(ResourceHandler.getValue("modify"));
	}

	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		top.setLayout(new GridLayout(4, false));

		Label idLabel = new Label(top, SWT.NONE);
		idLabel.setText("id");
		idLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

		labelIdText = new Text(top, SWT.BORDER);
		labelIdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		Composite landc = new Composite(top, SWT.NONE);
		landc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		landc.setLayout(new FillLayout(SWT.HORIZONTAL));

		Group levelGroup = new Group(landc, SWT.NONE);
		levelGroup.setText("level");
		levelGroup.setLayout(new FillLayout());
		Group compartmentGroup = new Group(landc, SWT.NONE);
		compartmentGroup.setText("compartment");
		compartmentGroup.setLayout(new FillLayout());
		// Level list
		levelList = new ListViewer(levelGroup, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		levelList.setUseHashlookup(true);
		// levelList.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL,
		// true, true, 3, 1));
		levelList.setLabelProvider(new LabelProvider());
		levelList.setContentProvider(new ArrayContentProvider());
		// compartment table
		compartmentTv = CheckboxTableViewer.newCheckList(compartmentGroup, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		compartmentTv.setContentProvider(new ArrayContentProvider());
		compartmentTv.setLabelProvider(new CompartmentLabelProvider());
		Table compartmentTable = compartmentTv.getTable();
		TableLayout policyTLayout = new TableLayout();// 专用于表格的布局
		compartmentTable.setLayout(policyTLayout);
		compartmentTable.setHeaderVisible(true);// 设置表格头部是否可见
		compartmentTable.setLinesVisible(true);// 设置线条是否可见
		policyTLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(compartmentTable, SWT.NONE).setText("name");
		policyTLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(compartmentTable, SWT.NONE).setText("ID");
		levelList.setInput(levels);
		compartmentTv.setInput(compartments);
		levelList.refresh();
		compartmentTv.refresh();
		levelList.setSelection(selectLevel == null ? StructuredSelection.EMPTY : new StructuredSelection(selectLevel));
		compartmentTv.setCheckedElements(selectCompartment.toArray(new Compartment[selectCompartment.size()]));
		if (dblabel != null) {
			labelIdText.setText(String.valueOf(dblabel.getId()));
			labelIdText.setEditable(false);
		} else {
			labelIdText.setEditable(true);
		}
		errorLb = new Label(top, SWT.RIGHT);
		errorLb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		errorLb.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
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

	public void setValue(List<Level> levels, List<Compartment> compartments, Level selectLevel, List<Compartment> selectCompartment) {
		this.levels = levels;
		this.compartments = compartments;
		this.selectLevel = selectLevel;
		this.selectCompartment = selectCompartment;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			errorLb.setText("add.....");
			String id = labelIdText.getText();
			if (id == null || id.isEmpty()) {
				errorLb.setText("id is null");
				return;
			}
			try {
				Integer.valueOf(id);
			} catch (NumberFormatException e) {
				errorLb.setText("id is error");
				return;
			}
			StructuredSelection levelSelection = (StructuredSelection) levelList.getSelection();
			if (levelSelection.isEmpty()) {
				errorLb.setText("please select level");
				return;
			}
			Level l = (Level) levelSelection.getFirstElement();
			String l_name = l.getName();

			Object[] coms =  compartmentTv.getCheckedElements();
			String c_name = "";
			if (coms != null && coms.length > 0) {
				for (Object c : coms) {
					c_name += (((Compartment)c).getName() + ",");
				}
			}
			if (c_name.length() > 0)
				c_name = c_name.substring(0, c_name.lastIndexOf(","));
			String name = l_name + ":" + c_name;
			if (dblabel == null) {
				dblabel = new DbLabel(name, Integer.valueOf(id));
			} else {
				dblabel.setName(name);
			}
			setReturnCode(IDialogConstants.OK_ID);
		} else {
			setReturnCode(IDialogConstants.CANCEL_ID);
		}

		close();
	}

	@Override
	protected Point getInitialSize() {
		Point initialSize = super.getInitialSize();

		return new Point(initialSize.x + 200, initialSize.y);// super.getInitialSize()可以得到原来对话框的大小
	}

	public DbLabel getDblabel() {
		return dblabel;
	}
}
