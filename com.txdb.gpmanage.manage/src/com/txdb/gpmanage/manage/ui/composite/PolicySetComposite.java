package com.txdb.gpmanage.manage.ui.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.txdb.gpmanage.manage.entity.Compartment;
import com.txdb.gpmanage.manage.entity.DbLabel;
import com.txdb.gpmanage.manage.entity.Level;
import com.txdb.gpmanage.manage.entity.Policy;
import com.txdb.gpmanage.manage.ui.dialog.AddDbLabelDialog;
import com.txdb.gpmanage.manage.ui.dialog.AddLevelOrCompartDialog;
import com.txdb.gpmanage.manage.ui.dialog.AddPolicyDialog;
import com.txdb.gpmanage.manage.ui.provider.CompartmentLabelProvider;
import com.txdb.gpmanage.manage.ui.provider.DbLabelLabelProvider;
import com.txdb.gpmanage.manage.ui.provider.LevelLabelProvider;
import com.txdb.gpmanage.manage.ui.provider.PolicyTableLabelProvider;

public class PolicySetComposite extends Composite {
	private TableViewer policyTv;
	private TableViewer levelTv;
	private TableViewer compartTv;
	private TableViewer dbLabelTv;
	private PolicyMangeComposite policyManageComposite;

	public PolicySetComposite(Composite parent, PolicyMangeComposite policyManageCompositeint, int style) {
		super(parent, style);
		this.policyManageComposite = policyManageCompositeint;
		init();
	}

	private void init() {
		FillLayout gd_policyComposite = new FillLayout();
		this.setLayout(gd_policyComposite);
		createPolicyUI(this);
		createLevelUI(this);
		createCompartUI(this);
		createLabelUI(this);
	}

	/**
	 * 创建POLICY UI
	 * 
	 * @param composte
	 */
	private void createPolicyUI(Composite composte) {
		// policy
		Group policyGroup = new Group(composte, SWT.NONE);
		policyGroup.setText("policy");
		policyGroup.setLayout(new GridLayout(4, true));
		policyTv = new TableViewer(policyGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		policyTv.setContentProvider(new ArrayContentProvider());
		policyTv.setLabelProvider(new PolicyTableLabelProvider());
		Table policyTable = policyTv.getTable();
		policyTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		TableLayout policyTLayout = new TableLayout();// 专用于表格的布局
		policyTable.setLayout(policyTLayout);
		policyTable.setHeaderVisible(true);// 设置表格头部是否可见
		policyTable.setLinesVisible(true);// 设置线条是否可见
		policyTLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(policyTable, SWT.NONE).setText("name");
		policyTLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(policyTable, SWT.NONE).setText("column");
		policyTLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(policyTable, SWT.NONE).setText("hide");
		policyTLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(policyTable, SWT.NONE).setText("enable");
		Button createBtn = new Button(policyGroup, SWT.NONE);
		createBtn.setText("create");
		Button deleteBtn = new Button(policyGroup, SWT.NONE);
		deleteBtn.setText("delete");
		Button enableBtn = new Button(policyGroup, SWT.NONE);
		enableBtn.setText("enable");
		Button disableBtn = new Button(policyGroup, SWT.NONE);
		disableBtn.setText("disable");
		policyTv.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selectin = (StructuredSelection) policyTv.getSelection();
				if (selectin.isEmpty())
					return;
				Policy p = (Policy) selectin.getFirstElement();

				// final GPManagerEntity gp = ((ManageComposite)
				// mianComposite).getGp();
				String queryLevel = "select short_name,level_id from mac_levels where policy_name='" + p.getName() + "'";
				String queryCompartment = "select comp_id,short_name from mac_compartments where policy_name='" + p.getName() + "'";
				String queryLabel = "select label_id,label from mac_labels where policy_name='" + p.getName() + "'";

				List<Map<String, Object>> levelRs = policyManageComposite.getMianComposite().getService()
						.executeQuery(policyManageComposite.getGp(), queryLevel, policyManageComposite.getTextUI(), policyManageComposite);
				List<Level> levels = new ArrayList<Level>();
				for (Map<String, Object> m : levelRs) {
					levels.add(new Level((String) m.get("short_name"), (int) m.get("level_id")));
				}

				List<Map<String, Object>> compartmentRs = policyManageComposite.getMianComposite().getService()
						.executeQuery(policyManageComposite.getGp(), queryCompartment, policyManageComposite.getTextUI(), policyManageComposite);
				List<Compartment> compartments = new ArrayList<Compartment>();
				for (Map<String, Object> m : compartmentRs) {
					compartments.add(new Compartment((String) m.get("short_name"), (int) m.get("comp_id")));
				}
				List<Map<String, Object>> labelRs = policyManageComposite.getMianComposite().getService()
						.executeQuery(policyManageComposite.getGp(), queryLabel, policyManageComposite.getTextUI(), policyManageComposite);
				List<DbLabel> labels = new ArrayList<DbLabel>();
				for (Map<String, Object> m : labelRs) {
					labels.add(new DbLabel((String) m.get("label"), (int) m.get("label_id")));
				}
				levelTv.setInput(levels);
				compartTv.setInput(compartments);
				dbLabelTv.setInput(labels);
			}
		});
		createBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				AddPolicyDialog apDlg = new AddPolicyDialog(policyManageComposite.getShell());
				if (apDlg.open() == IDialogConstants.CANCEL_ID)
					return;
				Policy p = apDlg.getPolicy();
				String sql = "CREATE  POLICY " + p.getName() + " WITH COLUMN " + p.getColumn() + (p.isHide() ? " HIDE" : "");
				int isSuccess = policyManageComposite.getMianComposite().getService().executeUpdate(policyManageComposite.getGp(), sql, policyManageComposite.getTextUI(), policyManageComposite);
				if (isSuccess != -1) {
					List<Policy> policys = (List<Policy>) policyTv.getInput();
					policys.add(p);
					policyTv.refresh();
					policyManageComposite.refreshPolicy();
				}

			}
		});
		deleteBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection selectin = (StructuredSelection) policyTv.getSelection();
				if (selectin.isEmpty())
					return;
				Policy p = (Policy) selectin.getFirstElement();

				String sql = "SELECT DROP_POLICY('" + p.getName() + "',false);";
				// final GPManagerEntity gp = ((ManageComposite)
				// mianComposite).getGp();
				List<Map<String, Object>> result = policyManageComposite.getMianComposite().getService()
						.executeQuery(policyManageComposite.getGp(), sql, policyManageComposite.getTextUI(), policyManageComposite);
				if (result != null) {
					List<Policy> policys = (List<Policy>) policyTv.getInput();
					policys.remove(p);
					policyTv.refresh();
					policyManageComposite.refreshPolicy();
				}
			}
		});
		disableBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection selectin = (StructuredSelection) policyTv.getSelection();
				if (selectin.isEmpty())
					return;
				Policy p = (Policy) selectin.getFirstElement();

				String sql = "SELECT DISABLE_POLICY('" + p.getName() + "')";
				// final GPManagerEntity gp = ((ManageComposite)
				// mianComposite).getGp();
				List<Map<String, Object>> result = policyManageComposite.getMianComposite().getService()
						.executeQuery(policyManageComposite.getGp(), sql, policyManageComposite.getTextUI(), policyManageComposite);
				if (result != null) {
					p.setEnable(false);
					policyTv.refresh();
					policyManageComposite.getMianComposite().getService().setMsg(policyManageComposite.getTextUI(), true, "[INFO]disable policy " + p.getName() + " success\n");
				} else {
					policyManageComposite.getMianComposite().getService().setMsg(policyManageComposite.getTextUI(), true, "[ERROR]disable policy " + p.getName() + "fail\n");
				}
			}
		});
		enableBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection selectin = (StructuredSelection) policyTv.getSelection();
				if (selectin.isEmpty())
					return;
				Policy p = (Policy) selectin.getFirstElement();
				String sql = "SELECT ENABLE_POLICY('" + p.getName() + "')";
				List<Map<String, Object>> result = policyManageComposite.getMianComposite().getService()
						.executeQuery(policyManageComposite.getGp(), sql, policyManageComposite.getTextUI(), policyManageComposite);
				if (result != null) {
					p.setEnable(true);
					policyTv.refresh();
					policyManageComposite.getMianComposite().getService().setMsg(policyManageComposite.getTextUI(), true, "[INFO]enable policy " + p.getName() + " success\n");
				} else {
					policyManageComposite.getMianComposite().getService().setMsg(policyManageComposite.getTextUI(), true, "[ERROR]enable policy " + p.getName() + "fail\n");
				}

			}
		});
	}

	/**
	 * 创建 LEVEL UI
	 * 
	 * @param composte
	 */
	private void createLevelUI(Composite composte) {
		// level
		Group levelGroup = new Group(composte, SWT.NONE);
		levelGroup.setText("level");
		levelGroup.setLayout(new GridLayout(2, true));
		levelTv = new TableViewer(levelGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		levelTv.setContentProvider(new ArrayContentProvider());
		levelTv.setLabelProvider(new LevelLabelProvider());
		levelTv.setInput(new ArrayList<Level>());
		Table levelTable = levelTv.getTable();
		levelTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		TableLayout tLayout = new TableLayout();// 专用于表格的布局
		levelTable.setLayout(tLayout);
		levelTable.setHeaderVisible(true);// 设置表格头部是否可见
		levelTable.setLinesVisible(true);// 设置线条是否可见
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(levelTable, SWT.NONE).setText("name");
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(levelTable, SWT.NONE).setText("id");
		Button createBtn = new Button(levelGroup, SWT.NONE);
		createBtn.setText("create");
		Button deleteBtn = new Button(levelGroup, SWT.NONE);
		deleteBtn.setText("delete");
		createBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection selectin = (StructuredSelection) policyTv.getSelection();
				if (selectin.isEmpty())
					return;
				Policy p = (Policy) selectin.getFirstElement();
				AddLevelOrCompartDialog apDlg = new AddLevelOrCompartDialog(policyManageComposite.getShell(), 0);
				if (apDlg.open() == IDialogConstants.CANCEL_ID)
					return;
				Level l = (Level) apDlg.getOb();
				String sql = "SELECT CREATE_LEVEL('" + p.getName() + "','" + l.getName() + "'," + l.getId() + ")";
				// final GPManagerEntity gp = ((ManageComposite)
				// mianComposite).getGp();
				List<Map<String, Object>> result = policyManageComposite.getMianComposite().getService()
						.executeQuery(policyManageComposite.getGp(), sql, policyManageComposite.getTextUI(), policyManageComposite);
				if (result != null) {
					List<Level> levels = (List<Level>) levelTv.getInput();
					levels.add(l);
					levelTv.refresh();
				}

			}
		});
		deleteBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection selectin = (StructuredSelection) policyTv.getSelection();
				if (selectin.isEmpty())
					return;
				StructuredSelection levelSelectin = (StructuredSelection) levelTv.getSelection();
				if (levelSelectin.isEmpty())
					return;
				Policy p = (Policy) selectin.getFirstElement();
				Level l = (Level) levelSelectin.getFirstElement();
				String sql = "SELECT DROP_LEVEL('" + p.getName() + "'," + l.getId() + ")";
				// final GPManagerEntity gp = ((ManageComposite)
				// mianComposite).getGp();
				List<Map<String, Object>> isSuccess = policyManageComposite.getMianComposite().getService()
						.executeQuery(policyManageComposite.getGp(), sql, policyManageComposite.getTextUI(), policyManageComposite);
				if (isSuccess != null) {
					List<Level> levels = (List<Level>) levelTv.getInput();
					levels.remove(l);
					levelTv.refresh();
				}

			}
		});
	}

	/**
	 * 创建范围UI
	 * 
	 * @param composte
	 */
	private void createCompartUI(Composite composte) {
		// compartment
		Group compartmentGroup = new Group(composte, SWT.NONE);
		compartmentGroup.setText("compartment");
		compartmentGroup.setLayout(new GridLayout(2, true));
		compartTv = new TableViewer(compartmentGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		compartTv.setContentProvider(new ArrayContentProvider());
		compartTv.setLabelProvider(new CompartmentLabelProvider());
		compartTv.setInput(new ArrayList<Compartment>());
		Table policyTable = compartTv.getTable();
		policyTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		TableLayout policyTLayout = new TableLayout();// 专用于表格的布局
		policyTable.setLayout(policyTLayout);
		policyTable.setHeaderVisible(true);// 设置表格头部是否可见
		policyTable.setLinesVisible(true);// 设置线条是否可见
		policyTLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(policyTable, SWT.NONE).setText("name");
		policyTLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(policyTable, SWT.NONE).setText("ID");
		Button createBtn = new Button(compartmentGroup, SWT.NONE);
		createBtn.setText("create");
		Button deleteBtn = new Button(compartmentGroup, SWT.NONE);
		deleteBtn.setText("delete");
		createBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection selectin = (StructuredSelection) policyTv.getSelection();
				if (selectin.isEmpty())
					return;
				Policy p = (Policy) selectin.getFirstElement();
				AddLevelOrCompartDialog apDlg = new AddLevelOrCompartDialog(policyManageComposite.getShell(), 1);
				if (apDlg.open() == IDialogConstants.CANCEL_ID)
					return;
				Compartment l = (Compartment) apDlg.getOb();
				String sql = "SELECT CREATE_COMPARTMENT('" + p.getName() + "','" + l.getName() + "'," + l.getId() + ")";
				// final GPManagerEntity gp = ((ManageComposite)
				// mianComposite).getGp();
				List<Map<String, Object>> result = policyManageComposite.getMianComposite().getService()
						.executeQuery(policyManageComposite.getGp(), sql, policyManageComposite.getTextUI(), policyManageComposite);
				if (result != null) {
					List<Compartment> compartments = (List<Compartment>) compartTv.getInput();
					compartments.add(l);
					compartTv.refresh();
				}

			}
		});
		deleteBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection selectin = (StructuredSelection) policyTv.getSelection();
				if (selectin.isEmpty())
					return;
				StructuredSelection levelSelectin = (StructuredSelection) compartTv.getSelection();
				if (levelSelectin.isEmpty())
					return;
				Policy p = (Policy) selectin.getFirstElement();
				Compartment l = (Compartment) levelSelectin.getFirstElement();
				String sql = "SELECT DROP_COMPARTMENT('" + p.getName() + "','" + l.getName() + "')";
				// final GPManagerEntity gp = ((ManageComposite)
				// mianComposite).getGp();
				List<Map<String, Object>> result = policyManageComposite.getMianComposite().getService()
						.executeQuery(policyManageComposite.getGp(), sql, policyManageComposite.getTextUI(), policyManageComposite);
				if (result != null) {
					List<Compartment> compartmenta = (List<Compartment>) compartTv.getInput();
					compartmenta.remove(l);
					compartTv.refresh();
				}

			}
		});
	}

	/**
	 * 创建label标签
	 * 
	 * @param composte
	 */
	private void createLabelUI(Composite composte) {
		// compartment
		Group compartmentGroup = new Group(composte, SWT.NONE);
		compartmentGroup.setText("label");
		compartmentGroup.setLayout(new GridLayout(3, true));
		dbLabelTv = new TableViewer(compartmentGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		dbLabelTv.setContentProvider(new ArrayContentProvider());
		dbLabelTv.setLabelProvider(new DbLabelLabelProvider());
		dbLabelTv.setInput(new ArrayList<DbLabel>());
		Table policyTable = dbLabelTv.getTable();
		policyTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		TableLayout policyTLayout = new TableLayout();// 专用于表格的布局
		policyTable.setLayout(policyTLayout);
		policyTable.setHeaderVisible(true);// 设置表格头部是否可见
		policyTable.setLinesVisible(true);// 设置线条是否可见
		policyTLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(policyTable, SWT.NONE).setText("label");
		policyTLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(policyTable, SWT.NONE).setText("id");
		Button createBtn = new Button(compartmentGroup, SWT.NONE);
		createBtn.setText("create");
		Button modifyBtn = new Button(compartmentGroup, SWT.NONE);
		modifyBtn.setText("modify");
		Button deleteBtn = new Button(compartmentGroup, SWT.NONE);
		deleteBtn.setText("delete");
		createBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection selectin = (StructuredSelection) policyTv.getSelection();
				if (selectin.isEmpty())
					return;
				if (levelTv.getInput() == null || ((List<Level>) levelTv.getInput()).size() < 1)
					return;
				Policy p = (Policy) selectin.getFirstElement();
				AddDbLabelDialog apDlg = new AddDbLabelDialog(policyManageComposite.getShell(), null);
				apDlg.setValue((List<Level>) levelTv.getInput(), (List<Compartment>) compartTv.getInput(), null, new ArrayList<Compartment>());

				if (apDlg.open() == IDialogConstants.CANCEL_ID)
					return;
				DbLabel l = (DbLabel) apDlg.getDblabel();
				String sql = "SELECT CREATE_LABEL('" + p.getName() + "','" + l.getName() + "'," + l.getId() + ")";
				// final GPManagerEntity gp = ((ManageComposite)
				// mianComposite).getGp();
				List<Map<String, Object>> isSuccess = policyManageComposite.getMianComposite().getService()
						.executeQuery(policyManageComposite.getGp(), sql, policyManageComposite.getTextUI(), policyManageComposite);
				if (isSuccess != null) {
					List<DbLabel> dbLabels = (List<DbLabel>) dbLabelTv.getInput();
					dbLabels.add(l);
					dbLabelTv.refresh();
				}

			}
		});
		modifyBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection selectin = (StructuredSelection) policyTv.getSelection();
				if (selectin.isEmpty())
					return;
				StructuredSelection labelSelectin = (StructuredSelection) dbLabelTv.getSelection();
				if (labelSelectin.isEmpty())
					return;
				if (levelTv.getInput() == null || ((List<Level>) levelTv.getInput()).size() < 1)
					return;
				Policy p = (Policy) selectin.getFirstElement();
				DbLabel dblabel = (DbLabel) labelSelectin.getFirstElement();
				String name = dblabel.getName();
				String level_name = name.substring(0, name.lastIndexOf(":"));
				String coms = name.substring(name.lastIndexOf(":") + 1, name.length());
				Level selectLevel = null;
				for (Level l : (List<Level>) levelTv.getInput()) {
					if (l.getName().equals(level_name)) {
						selectLevel = l;
						break;
					}
				}
				List<Compartment> compartments = new ArrayList<Compartment>();
				if (coms.length() > 0) {
					String[] comsNames = coms.split(",");
					for (String comName : comsNames) {
						for (Compartment compartment : ((List<Compartment>) compartTv.getInput())) {
							if (compartment.getName().equals(comName)) {
								compartments.add(compartment);
								break;
							}
						}
					}
				}
				AddDbLabelDialog apDlg = new AddDbLabelDialog(policyManageComposite.getShell(), dblabel);
				apDlg.setValue((List<Level>) levelTv.getInput(), (List<Compartment>) compartTv.getInput(), selectLevel, compartments);

				if (apDlg.open() == IDialogConstants.CANCEL_ID)
					return;
				DbLabel l = (DbLabel) apDlg.getDblabel();
				String sql = "SELECT ALTER_LABEL('" + p.getName() + "'," + l.getId() + ",'" + l.getName() + "')";
				// final GPManagerEntity gp = ((ManageComposite)
				// mianComposite).getGp();
				List<Map<String, Object>> isSuccess = policyManageComposite.getMianComposite().getService()
						.executeQuery(policyManageComposite.getGp(), sql, policyManageComposite.getTextUI(), policyManageComposite);
				if (isSuccess != null) {
					dbLabelTv.refresh();
				} else {
					dblabel.setName(name);
				}

			}
		});
		deleteBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection selectin = (StructuredSelection) policyTv.getSelection();
				if (selectin.isEmpty())
					return;
				StructuredSelection labelSelectin = (StructuredSelection) dbLabelTv.getSelection();
				if (labelSelectin.isEmpty())
					return;
				Policy p = (Policy) selectin.getFirstElement();
				DbLabel dblabel = (DbLabel) labelSelectin.getFirstElement();
				String sql = "SELECT DROP_LABEL('" + p.getName() + "'," + dblabel.getId() + ")";
				// final GPManagerEntity gp = ((ManageComposite)
				// mianComposite).getGp();
				List<Map<String, Object>> isSuccess = policyManageComposite.getMianComposite().getService()
						.executeQuery(policyManageComposite.getGp(), sql, policyManageComposite.getTextUI(), policyManageComposite);
				if (isSuccess != null) {
					List<DbLabel> dblabels = (List<DbLabel>) dbLabelTv.getInput();
					dblabels.remove(dblabel);
					dbLabelTv.refresh();
				}

			}
		});
	}

	public TableViewer getPolicyTv() {
		return policyTv;
	}

	public void setPolicyTv(TableViewer policyTv) {
		this.policyTv = policyTv;
	}

}
