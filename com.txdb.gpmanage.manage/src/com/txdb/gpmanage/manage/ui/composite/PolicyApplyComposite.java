package com.txdb.gpmanage.manage.ui.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.entity.DbUser;
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.service.AbstractUIService;
import com.txdb.gpmanage.manage.entity.Compartment;
import com.txdb.gpmanage.manage.entity.Level;
import com.txdb.gpmanage.manage.entity.Policy;

/**
 * 策略应用界面
 * 
 * @author ws
 *
 */
public class PolicyApplyComposite extends Composite {
	private ListViewer userList;
	private ComboViewer policyCombo;
	private PolicyMangeComposite pmc;
	private ComboViewer max_levelCombo, def_levelCombo, row_levelCombo, min_levelCombo;
	private Text readText, max_writeText, defaultText, rowText;
	private Level max_level, min_level, def_level, row_level;
	private List<Compartment> read_compartments = new ArrayList<Compartment>(), max_write_compartments = new ArrayList<Compartment>(), default_compartments = new ArrayList<Compartment>(),
			row_compartments = new ArrayList<Compartment>();
	private List<Compartment> compartments = new ArrayList<Compartment>();

	public PolicyApplyComposite(Composite parent, int style) {
		super(parent, style);
		init();
	}

	public PolicyApplyComposite(Composite parent, PolicyMangeComposite pmc, int style) {
		super(parent, style);
		this.pmc = pmc;
		init();
	}

	/**
	 * 初始化界面
	 */
	private void init() {
		this.setLayout(new FillLayout());
		createUserSet();
		createTableSet();
	}

	/**
	 * 用户设置界面
	 */
	private void createUserSet() {
		Group userGroup = new Group(this, SWT.NONE);
		userGroup.setText("user");
		userGroup.setLayout(new GridLayout(3, false));
		// user list
		userList = new ListViewer(userGroup, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		userList.setUseHashlookup(true);
		userList.getList().setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 2));
		userList.setLabelProvider(new LabelProvider());
		userList.setContentProvider(new ArrayContentProvider());
		// policy list
		policyCombo = new ComboViewer(userGroup, SWT.READ_ONLY);
		policyCombo.setLabelProvider(new LabelProvider());
		policyCombo.setContentProvider(ArrayContentProvider.getInstance());
		policyCombo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		createLevelGroup(userGroup);
		createCompartmentGroup(userGroup);
		policyCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setLevelValues();
			}
		});
		userList.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setLevelValues();
			}
		});
	}

	private void createLevelGroup(Group userGroup) {
		Group levelGroup = new Group(userGroup, SWT.NONE);
		levelGroup.setText("level");
		levelGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		levelGroup.setLayout(new GridLayout(2, false));
		// max_level
		Label max_levelLb = new Label(levelGroup, SWT.NONE);
		max_levelLb.setText("max_level");
		max_levelLb.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		max_levelCombo = new ComboViewer(levelGroup, SWT.READ_ONLY);
		max_levelCombo.setLabelProvider(new LabelProvider());
		max_levelCombo.setContentProvider(ArrayContentProvider.getInstance());
		max_levelCombo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		// min_level
		Label min_levelLb = new Label(levelGroup, SWT.NONE);
		min_levelLb.setText("min_level");
		min_levelLb.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		min_levelCombo = new ComboViewer(levelGroup, SWT.READ_ONLY);
		min_levelCombo.setLabelProvider(new LabelProvider());
		min_levelCombo.setContentProvider(ArrayContentProvider.getInstance());
		min_levelCombo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		// def_level
		Label def_levelLb = new Label(levelGroup, SWT.NONE);
		def_levelLb.setText("def_level");
		def_levelLb.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		def_levelCombo = new ComboViewer(levelGroup, SWT.READ_ONLY);
		def_levelCombo.setLabelProvider(new LabelProvider());
		def_levelCombo.setContentProvider(ArrayContentProvider.getInstance());
		def_levelCombo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		// row_level
		Label row_levelLb = new Label(levelGroup, SWT.NONE);
		row_levelLb.setText("row_level");
		row_levelLb.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		row_levelCombo = new ComboViewer(levelGroup, SWT.READ_ONLY);
		row_levelCombo.setLabelProvider(new LabelProvider());
		row_levelCombo.setContentProvider(ArrayContentProvider.getInstance());
		row_levelCombo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		Button levelBtn = new Button(levelGroup, SWT.NONE);
		levelBtn.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, true, 2, 1));
		levelBtn.setText("sure");
		levelBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
	}

	private void createCompartmentGroup(Group userGroup) {
		Group compartmentGroup = new Group(userGroup, SWT.NONE);
		compartmentGroup.setText("compartment");
		compartmentGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compartmentGroup.setLayout(new GridLayout(2, false));
		// read_compartments
		Label readLb = new Label(compartmentGroup, SWT.NONE);
		readLb.setText("read");
		readLb.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		readText = new Text(compartmentGroup, SWT.BORDER);
		readText.setEditable(false);
		readText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		// max_write_compartments
		Label max_writeLb = new Label(compartmentGroup, SWT.NONE);
		max_writeLb.setText("max_write");
		max_writeLb.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		max_writeText = new Text(compartmentGroup, SWT.BORDER);
		max_writeText.setEditable(false);
		max_writeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		// default_compartments
		Label defaultLb = new Label(compartmentGroup, SWT.NONE);
		defaultLb.setText("default");
		defaultLb.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		defaultText = new Text(compartmentGroup, SWT.BORDER);
		defaultText.setEditable(false);
		defaultText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		// row_level
		Label rowLb = new Label(compartmentGroup, SWT.NONE);
		rowLb.setText("row_level");
		rowLb.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		rowText = new Text(compartmentGroup, SWT.BORDER);
		rowText.setEditable(false);
		rowText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		Button levelBtn = new Button(compartmentGroup, SWT.NONE);
		levelBtn.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, true, 2, 1));
		levelBtn.setText("modify");
	}

	private void createTableSet() {
		Group userGroup = new Group(this, SWT.NONE);
		userGroup.setText("table");
		userGroup.setLayout(new GridLayout(2, false));
	}

	private void setLevelValues() {
		StructuredSelection policySelection = (StructuredSelection) policyCombo.getSelection();
		StructuredSelection userSelection = (StructuredSelection) userList.getSelection();
		if (policySelection.isEmpty() || userSelection.isEmpty())
			return;
		final DbUser user = (DbUser) userSelection.getFirstElement();
		final Policy policy = (Policy) policySelection.getFirstElement();
		final Display display = Display.getCurrent();
		pmc.startBar();
		policyCombo.getCombo().setEnabled(false);
		userList.getList().setEnabled(false);
		final AbstractUIService service = pmc.getMianComposite().getService();
		final GPManagerEntity gp = pmc.getGp();
		final StyledText styledText = pmc.getTextUI();
		max_level = null;
		min_level = null;
		def_level = null;
		row_level = null;
		new Thread(new Runnable() {
			@Override
			public void run() {
				String queryLevel = "select short_name,level_id from mac_levels where policy_name='" + policy.getName() + "'";
				String queryCompartment = "select comp_id,short_name from mac_compartments where policy_name='" + policy.getName() + "'";
				String uer_level = "select max_level,min_level,def_level,row_level from mac_user_levels where policy_name='" + policy.getName() + "' and user_name='" + user.getUserName() + "';";
				String uer_compartment = "select comp,rw_access,def_comp,row_comp from mac_user_compartments where policy_name='" + policy.getName() + "' and user_name='" + user.getUserName() + "';";
				List<Map<String, Object>> levelRs = service.executeQuery(gp, queryLevel, styledText, pmc);
				List<Map<String, Object>> user_levels = service.executeQuery(gp, uer_level, styledText, pmc);

//				String max_level_name, min_level_name, def_level_name, row_level_name;
				final List<Level> levels = new ArrayList<Level>();
				for (Map<String, Object> m : levelRs) {
					Level level = new Level((String) m.get("short_name"), (int) m.get("level_id"));
					levels.add(level);
					for (Map<String, Object> level_name : user_levels) {
						if (level.getName().equals(level_name.get("max_level"))) {
							max_level = level;
						}
						if (level.getName().equals(level_name.get("min_level"))) {
							min_level = level;
						}
						if (level.getName().equals(level_name.get("def_level"))) {
							def_level = level;
						}
						if (level.getName().equals(level_name.get("row_level"))) {
							row_level = level;
						}
					}
				}

				List<Map<String, Object>> compartmentRs = service.executeQuery(gp, queryCompartment, styledText, pmc);
				compartments.clear();
				read_compartments.clear();
				max_write_compartments.clear();
				default_compartments.clear();
				row_compartments.clear();
				List<Map<String, Object>> user_compartments = service.executeQuery(gp, uer_compartment, styledText, pmc);
				for (Map<String, Object> m : compartmentRs) {
					String name = (String) m.get("short_name");
					Compartment comp = new Compartment(name, (int) m.get("comp_id"));
					compartments.add(comp);
					for (Map<String, Object> user_compartment : user_compartments) {
						String user_comp = (String) user_compartment.get("comp");
						if (name.equals(user_comp)) {
							read_compartments.add(comp);
							String rw_access = (String) user_compartment.get("rw_access");
							String def_comp = (String) user_compartment.get("def_comp");
							String row_comp = (String) user_compartment.get("row_comp");
							if (rw_access.equals("WRITE"))
								max_write_compartments.add(comp);
							if (def_comp.equals("Y"))
								default_compartments.add(comp);
							if (row_comp.equals("Y"))
								row_compartments.add(comp);
							break;
						}
					}
				}

				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						max_levelCombo.setInput(levels);
						def_levelCombo.setInput(levels);
						row_levelCombo.setInput(levels);
						min_levelCombo.setInput(levels);
						if (max_level != null)
							max_levelCombo.setSelection(new StructuredSelection(max_level));
						else
							max_levelCombo.setSelection(StructuredSelection.EMPTY);
						if (min_level != null)
							min_levelCombo.setSelection(new StructuredSelection(min_level));
						else
							min_levelCombo.setSelection(StructuredSelection.EMPTY);
						if (def_level != null)
							def_levelCombo.setSelection(new StructuredSelection(def_level));
						else
							def_levelCombo.setSelection(StructuredSelection.EMPTY);
						if (row_level != null)
							row_levelCombo.setSelection(new StructuredSelection(row_level));
						else
							row_levelCombo.setSelection(StructuredSelection.EMPTY);
						readText.setText(compToString(read_compartments));
						max_writeText.setText(compToString(max_write_compartments));
						defaultText.setText(compToString(default_compartments));
						rowText.setText(compToString(row_compartments));;
						pmc.stopBar();
						policyCombo.getCombo().setEnabled(true);
						userList.getList().setEnabled(true);
					}
				});

			}
		}).start();

	}

	private String compToString(List<Compartment> compartments) {
		String compartmentStr = "";
		for (Compartment comp : compartments) {
			compartmentStr += (comp.getName() + ",");
		}
		if (compartmentStr.length() > 1)
			compartmentStr = compartmentStr.substring(0, compartmentStr.lastIndexOf(","));
		return compartmentStr;

	}

	public ListViewer getUserList() {
		return userList;
	}

	public ComboViewer getPolicyCombo() {
		return policyCombo;
	}

}
