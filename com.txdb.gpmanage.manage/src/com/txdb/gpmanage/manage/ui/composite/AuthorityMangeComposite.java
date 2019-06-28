package com.txdb.gpmanage.manage.ui.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.core.entity.DbUser;
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.manage.entity.ObjectAuth;
import com.txdb.gpmanage.manage.entity.SystenAuth;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.service.ManageUiService;
import com.txdb.gpmanage.manage.ui.provider.AuthMangeTableLabelProvider;

/**
 * 权限管理面板
 * 
 * @author ws
 *
 */
public class AuthorityMangeComposite extends IupperComposite {
	/**
	 * 用户列表
	 */
	private ListViewer userList;
	/**
	 * database list Combo
	 */
	// private Combo dbCombo;
	/**
	 * 权限列表
	 */
	private TableViewer tv;

	/**
	 * 当前连接数据库
	 */
	private String currentDbName;

	AuthorityMangeComposite(ManageComposite mianComposite, Composite parent, CompositeCode code) {
		super(mianComposite, parent, code, ResourceHandler.getValue("authority_title"), ResourceHandler.getValue("authority_desc"));

	}

	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout(3, false));
		Group systemAuthorityGroup = new Group(composte, SWT.NONE);
		// 系统权限
		systemAuthorityGroup.setText(ResourceHandler.getValue("authority_system"));
		systemAuthorityGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		createSystemAuthorityUi(systemAuthorityGroup);
		// separator
		Label sep = new Label(composte, SWT.SEPARATOR);
		sep.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 2));
		// grant to users
		Group users = new Group(composte, SWT.NONE);
		users.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 2));
		users.setText(ResourceHandler.getValue("grant"));
		createUserUi(users);
		// 对象权限
		Group objectAuthorityGroup = new Group(composte, SWT.NONE);
		objectAuthorityGroup.setText(ResourceHandler.getValue("authority_object"));
		objectAuthorityGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		createObjectAuthorityUi(objectAuthorityGroup);

	}

	private void createUserUi(Group users) {
		users.setLayout(new GridLayout(3, false));
		// 用户查询
		Label userNameLb = new Label(users, SWT.NONE);
		userNameLb.setText(ResourceHandler.getValue("user_name"));
		userNameLb.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		final Text userNameText = new Text(users, SWT.BORDER);
		userNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Button searchNameBtn = new Button(users, SWT.PUSH);
		searchNameBtn.setText(ResourceHandler.getValue("search"));
		searchNameBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		// 机器列表list
		userList = new ListViewer(users, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.CHECK);
		userList.setUseHashlookup(true);
		final UserViewerFilter filter = new UserViewerFilter();
		userList.addFilter(filter);
		userList.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		userList.setLabelProvider(new LabelProvider());
		userList.setContentProvider(new ArrayContentProvider());
		// 确定
		final Button grantBtn = new Button(users, SWT.PUSH);
		grantBtn.setText(ResourceHandler.getValue("grant"));
		grantBtn.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, true, false, 3, 1));
		searchNameBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				filter.setPattern(userNameText.getText());
				userList.refresh();
			}
		});
		grantBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				startBar();
				grantBtn.setEnabled(false);
				StructuredSelection userSelection = (StructuredSelection) userList.getSelection();
				int itemCount = tv.getTable().getItemCount();
				final List<ObjectAuth> auths = new ArrayList<ObjectAuth>();
				for (int i = 0; i < itemCount; i++) {
					boolean isChecked = tv.getTable().getItem(i).getChecked();
					if (isChecked)
						auths.add((ObjectAuth) tv.getElementAt(i));
				}

				boolean isUserEmpty = userSelection.isEmpty();
				boolean isAuthEmpty = auths.size() < 1;
				boolean isSuper = superBtn.getSelection();
				boolean isCreateDb = createDbBtn.getSelection();
				boolean isCreateRole = createroleBtn.getSelection();
				boolean isCanlogin = canloginBtn.getSelection();
				boolean isInherit = inheritBtn.getSelection();
				boolean isReplication = replicationBtn.getSelection();
				boolean systemAuth = isSuper || isCreateDb || isCreateRole || isCanlogin || isInherit || isReplication;
				if ((isAuthEmpty && !systemAuth) || isUserEmpty) {
					grantBtn.setEnabled(true);
					stopBar();
					return;
				}
				Object[] objects = userSelection.toArray();
				final List<Object> users = new ArrayList<>(objects.length);
				Collections.addAll(users, objects);
				final SystenAuth sysAuth = new SystenAuth();
				sysAuth.setRolcanlogin(isCanlogin);
				sysAuth.setRolcreatedb(isCreateDb);
				sysAuth.setRolcreaterole(isCreateRole);
				sysAuth.setRolinherit(isInherit);
				sysAuth.setRolreplication(isReplication);
				sysAuth.setSuper(isSuper);
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((ManageUiService) getService()).grantAuth(gp, currentDbName, sysAuth, auths, users, text, AuthorityMangeComposite.this);
						display.asyncExec(new Runnable() {

							@Override
							public void run() {
								userList.setSelection(new StructuredSelection());
								int itemCount = tv.getTable().getItemCount();
								for (int i = 0; i < itemCount; i++) {
									boolean isChecked = tv.getTable().getItem(i).getChecked();
									if (isChecked)
										tv.getTable().getItem(i).setChecked(false);
								}
								superBtn.setSelection(false);
								createDbBtn.setSelection(false);
								createroleBtn.setSelection(false);
								inheritBtn.setSelection(false);
								canloginBtn.setSelection(false);
								replicationBtn.setSelection(false);
								grantBtn.setEnabled(true);
								stopBar();
							}
						});

					}
				}).start();

			}
		});
	}

	private Button superBtn;
	private Button createDbBtn;
	private Button createroleBtn;
	private Button canloginBtn;
	private Button inheritBtn;
	private Button replicationBtn;

	/**
	 * 系统权限
	 * 
	 * @param systemAuthorityGroup
	 */
	private void createSystemAuthorityUi(Group systemAuthorityGroup) {
		systemAuthorityGroup.setLayout(new GridLayout(6, true));
		// super权限
		superBtn = new Button(systemAuthorityGroup, SWT.CHECK);
		superBtn.setText("SUPERUSER");
		superBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		// 创建数据库权限
		createDbBtn = new Button(systemAuthorityGroup, SWT.CHECK);
		createDbBtn.setText("CREATEDB");
		createDbBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		// 创建角色权限
		createroleBtn = new Button(systemAuthorityGroup, SWT.CHECK);
		createroleBtn.setText("CREATEROLE");
		createroleBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		// 可登陆权限
		canloginBtn = new Button(systemAuthorityGroup, SWT.CHECK);
		canloginBtn.setText("LOGIN");
		canloginBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		// inherit权限
		inheritBtn = new Button(systemAuthorityGroup, SWT.CHECK);
		inheritBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		inheritBtn.setText("INHERIT");
		// replication权限
		replicationBtn = new Button(systemAuthorityGroup, SWT.CHECK);
		replicationBtn.setText("REPLICATION");
		replicationBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		// bypassrls权限
		// Button bypassrlsBtn = new Button(systemAuthorityGroup, SWT.CHECK);
		// bypassrlsBtn.setText("BYPASSRLS");
		// bypassrlsBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
		// true, 1, 1));
	}

	/**
	 * 对象权限
	 * 
	 * @param systemAuthorityGroup
	 */
	private void createObjectAuthorityUi(Group objectAuthorityGroup) {
		objectAuthorityGroup.setLayout(new GridLayout());
		// 查询条件
		Composite searchComp = new Composite(objectAuthorityGroup, SWT.NONE);
		searchComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		AuthTableViewerFilter filter = new AuthTableViewerFilter();
		createSearchUi(searchComp, filter);
		createAuthTable(objectAuthorityGroup, filter);
	}

	/**
	 * 创建权限列表
	 * 
	 * @param objectAuthorityGroup
	 */
	private void createAuthTable(Group objectAuthorityGroup, AuthTableViewerFilter filter) {
		tv = new TableViewer(objectAuthorityGroup, SWT.V_SCROLL | SWT.H_SCROLL | SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION);
		Table table = tv.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		TableLayout tLayout = new TableLayout();// 专用于表格的布局
		table.setLayout(tLayout);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		tLayout.addColumnData(new ColumnWeightData(15));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("database"));
		tLayout.addColumnData(new ColumnWeightData(15));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("schema"));
		tLayout.addColumnData(new ColumnWeightData(25));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("object_type"));
		tLayout.addColumnData(new ColumnWeightData(15));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("object_name"));
		tLayout.addColumnData(new ColumnWeightData(15));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("privilege_type"));
		tLayout.addColumnData(new ColumnWeightData(15));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("grantable"));
		tv.setContentProvider(new ArrayContentProvider());
		tv.setLabelProvider(new AuthMangeTableLabelProvider());
		tv.addFilter(filter);
	}

	/**
	 * 查询条件
	 * 
	 * @param searchComp
	 */
	private void createSearchUi(Composite searchComp, final AuthTableViewerFilter filter) {
		GridLayout gd_searchComp = new GridLayout(5, false);
		gd_searchComp.horizontalSpacing = 5;
		searchComp.setLayout(gd_searchComp);
		// db
		// Composite dbComp = new Composite(searchComp, SWT.NONE);
		// dbComp.setLayout(new GridLayout(2, false));
		// dbComp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
		// true));
		// Label dbLabel = new Label(dbComp, SWT.NONE);
		// dbLabel.setText("database:");
		// dbCombo = new Combo(dbComp, SWT.READ_ONLY);
		// schame
		Composite schameComp = new Composite(searchComp, SWT.NONE);
		schameComp.setLayout(new GridLayout(2, false));
		schameComp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true));
		Label schameLabel = new Label(schameComp, SWT.NONE);
		schameLabel.setText(ResourceHandler.getValue("schema"));
		final Text schameCombo = new Text(schameComp, SWT.BORDER);
		// type
		Composite typeComp = new Composite(searchComp, SWT.NONE);
		typeComp.setLayout(new GridLayout(2, false));
		typeComp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true));
		Label typeLabel = new Label(typeComp, SWT.NONE);
		typeLabel.setText(ResourceHandler.getValue("object_type"));
		final Combo typeCombo = new Combo(typeComp, SWT.NONE);
		typeCombo.setItems(new String[] { "TABLE", "FUNCTION", "COLLATION", "DOMAIN", "FOREIGN DATA WRAPPER", "FOREIGN SERVER", "SEQUENCE" });
		// type
		Composite privilegeTypeComp = new Composite(searchComp, SWT.NONE);
		privilegeTypeComp.setLayout(new GridLayout(2, false));
		privilegeTypeComp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true));
		Label privilegeTypeLabel = new Label(privilegeTypeComp, SWT.NONE);
		privilegeTypeLabel.setText(ResourceHandler.getValue("privilege_type"));
		final Combo privilegeTypeCombo = new Combo(privilegeTypeComp, SWT.NONE);
		privilegeTypeCombo.setItems(new String[] { "SELECT", "INSERT", "DELETE", "UPDATE", "TRUNCATE", "REFERENCES", "TRIGGER", "REFERENCES", "USAGE", "EXECUTE" });
		// objectName
		Composite nameComp = new Composite(searchComp, SWT.NONE);
		nameComp.setLayout(new GridLayout(2, false));
		nameComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		Label nameLabel = new Label(nameComp, SWT.NONE);
		nameLabel.setText(ResourceHandler.getValue("object_name"));
		final Text nameText = new Text(nameComp, SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		// search bbutton
		Button searchBtn = new Button(searchComp, SWT.PUSH);
		searchBtn.setText(ResourceHandler.getValue("search"));
		searchBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true));
		searchBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				startBar();
				// final String dbName = dbCombo.getText();
				// if (dbName.equals(currentDbName)) {
				filter.setName(nameText.getText());
				filter.setSchema(schameCombo.getText());
				filter.setType(typeCombo.getText());
				filter.setPrivilege(privilegeTypeCombo.getText());
				tv.refresh();
				stopBar();
				// }
				// else {
				// final GPManagerEntity gp = ((ManageComposite)
				// mianComposite).getGp();
				// currentDbName = dbName;
				// new Thread(new Runnable() {
				// @Override
				// public void run() {
				// final List<ObjectAuth> auths = ((ManageUiService)
				// getService()).loadAuth(gp, dbName, text,
				// AuthorityMangeComposite.this);
				// display.asyncExec(new Runnable() {
				// @Override
				// public void run() {
				// tv.setInput(auths);
				// filter.setName(nameText.getText());
				// filter.setSchema(schameCombo.getText());
				// filter.setType(typeCombo.getText());
				// filter.setPrivilege(privilegeTypeCombo.getText());
				// tv.refresh();
				// stopBar();
				// }
				// });
				// }
				// }).start();
				// }

			}
		});
	}

	/**
	 * 加载权限信息
	 * 
	 * @param dbName
	 */
	public void loadData(List<DbUser> dbUsers) {
		loadUsers(dbUsers);
		loadObjectAuth();
		loadSystemAuth();
	}

	/**
	 * 加载用户信息
	 */
	private void loadUsers(final List<DbUser> dbUsers) {
		// GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
		// final List<DbUser> dbUsers = ((ManageUiService)
		// getService()).loadUser(gp, text, AuthorityMangeComposite.this);
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				userList.setInput(dbUsers);
				userList.refresh();
			}
		});
	}

	public void refreshUserList() {
		userList.refresh();
	}

	/**
	 * 加载对象权限
	 * 
	 * @param dbName
	 */
	private void loadObjectAuth() {
		GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
		final List<ObjectAuth> auths = ((ManageUiService) getService()).loadAuth(gp, text, AuthorityMangeComposite.this);
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				tv.setInput(auths);
				tv.refresh();
			}
		});
	}

	/**
	 * 加载系统权限
	 * 
	 * @param dbName
	 */
	private void loadSystemAuth() {
		GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
		final SystenAuth auth = ((ManageUiService) getService()).loadSystemAuth(gp, text, AuthorityMangeComposite.this);
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				// private Button replicationBtn;
				superBtn.setEnabled(auth.getRolsuper().equals("D"));
				createDbBtn.setEnabled(auth.isRolcreatedb());
				createroleBtn.setEnabled(auth.isRolcreaterole());
				inheritBtn.setEnabled(auth.isRolinherit());
				canloginBtn.setEnabled(auth.isRolcanlogin());
				replicationBtn.setEnabled(auth.isRolreplication());
			}
		});
	}

	/**
	 * 加载所有数据库
	 * 
	 * @param dbName
	 */
	// private void loadDatabase(final String dbName) {
	// GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
	// final List<String> database = ((ManageUiService)
	// getService()).loaddatabase(gp, dbName, text,
	// AuthorityMangeComposite.this);
	// this.currentDbName = dbName;
	// display.syncExec(new Runnable() {
	// @Override
	// public void run() {
	// dbCombo.setItems(database.toArray(new String[] {}));
	// dbCombo.setText(dbName);
	//
	// }
	// });
	// }

	private class UserViewerFilter extends ViewerFilter {
		private String pattern;

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			DbUser user = (DbUser) element;
			if (pattern != null && !pattern.isEmpty())
				return user.getUserName().toLowerCase().contains(pattern.toLowerCase());
			return true;
		}

		public void setPattern(String pattern) {
			this.pattern = pattern;
		}
	}

	private class AuthTableViewerFilter extends ViewerFilter {
		private String name;
		private String schema;
		private String type;
		private String privilege;

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			ObjectAuth auth = (ObjectAuth) element;
			boolean isSchema = (schema != null && !schema.isEmpty()) ? auth.getSchemaName().toLowerCase().contains(schema.toLowerCase()) : true;
			boolean isType = (type != null && !type.isEmpty()) ? auth.getObject_type().toLowerCase().contains(type.toLowerCase()) : true;
			boolean isName = (name != null && !name.isEmpty()) ? auth.getObjectName().toLowerCase().contains(name.toLowerCase()) : true;
			boolean isPrivilege = (privilege != null && !privilege.isEmpty()) ? auth.getPrivilege_type().toLowerCase().contains(privilege.toLowerCase()) : true;
			return isSchema && isType && isName && isPrivilege;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setSchema(String schema) {
			this.schema = schema;
		}

		public void setType(String type) {
			this.type = type;
		}

		public void setPrivilege(String privilege) {
			this.privilege = privilege;
		}

	}
}
