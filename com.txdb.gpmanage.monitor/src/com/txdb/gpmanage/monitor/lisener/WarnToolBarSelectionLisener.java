package com.txdb.gpmanage.monitor.lisener;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolItem;

import com.txdb.gpmanage.core.common.SqliteDao;
import com.txdb.gpmanage.core.entity.IGPEntity;
import com.txdb.gpmanage.core.entity.SqlWhere;
import com.txdb.gpmanage.core.entity.impl.GPMonitorEntity;
import com.txdb.gpmanage.core.entity.impl.GPTreeNode;
import com.txdb.gpmanage.core.entity.impl.MailEntity;
import com.txdb.gpmanage.core.entity.impl.SystemWarningEntity;
import com.txdb.gpmanage.monitor.ui.dialog.MailDialog;
import com.txdb.gpmanage.monitor.ui.dialog.SystemWarningDialog;

public class WarnToolBarSelectionLisener extends SelectionAdapter {
	
	public static final String OPERATION_ADD = "ADD";
	public static final String OPERATION_REMOVE = "REMOVE";
	public static final String OPERATION_EDIT = "EDIT";
	public static final String OPERATION_EMAIL = "EMAIL";
	
	private TreeViewer tv;
	private ListViewer listViewer;

	public WarnToolBarSelectionLisener(TreeViewer tv, ListViewer listViewer) {
		this.tv = tv;
		this.listViewer = listViewer;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		ToolItem item = (ToolItem) e.getSource();
		if (item.getData().equals(OPERATION_ADD))
			add_event();
		else if (item.getData().equals(OPERATION_REMOVE))
			delete_event();
		else if (item.getData().equals(OPERATION_EDIT))
			editor_event();
		else
			mail_event();

	}

	private void add_event() {
		TreeSelection selection = (TreeSelection) tv.getSelection();
		GPTreeNode node = (GPTreeNode) selection.getFirstElement();
		if (node == null)
			return;
		
		GPTreeNode parentNode = getTreeRootNode(node);
		// String monitorName = parentNode.getName();
		SystemWarningDialog dialog = new SystemWarningDialog(tv.getTree().getShell(), null);
		int code = dialog.open();
		if (code == IDialogConstants.CANCEL_ID)
			return;
		
		SystemWarningEntity entity = dialog.getWarn();
		entity.setMonitorName(parentNode.getName());
		SqliteDao.getInstance().insertGPEntity(entity);
		List<SystemWarningEntity> warns = ((GPMonitorEntity) parentNode.getGPEntity()).getWarns();
		if (warns == null) {
			warns = new ArrayList<SystemWarningEntity>();
			((GPMonitorEntity) parentNode.getGPEntity()).setWarns(warns);
			listViewer.setInput(warns);
			listViewer.refresh(true);
		}
		warns.add(entity);
		listViewer.refresh();
	}

	private void editor_event() {
		StructuredSelection listSelection = (StructuredSelection) listViewer.getSelection();
		if (listSelection.isEmpty())
			return;
		
		SystemWarningEntity entity = (SystemWarningEntity) listSelection.getFirstElement();
		String name = entity.getWarningName();
		SystemWarningDialog dialog = new SystemWarningDialog(tv.getTree().getShell(), entity);
		int code = dialog.open();
		if (code == IDialogConstants.CANCEL_ID)
			return;
		
		SqlWhere where = new SqlWhere();
		where.addWhere("warningName", "=", name);
		SqliteDao.getInstance().updateGPEntity(entity, where);
		listViewer.refresh();
	}

	private void delete_event() {
		StructuredSelection listSelection = (StructuredSelection) listViewer.getSelection();
		if (listSelection.isEmpty())
			return;
		
		SystemWarningEntity entity = (SystemWarningEntity) listSelection.getFirstElement();
		String name = entity.getWarningName();
		SqlWhere where = new SqlWhere();
		where.addWhere("warningName", "=", name);
		SqliteDao.getInstance().deleteGPEntity(entity, where);

		TreeSelection selection = (TreeSelection) tv.getSelection();
		GPTreeNode node = (GPTreeNode) selection.getFirstElement();
		if (node == null)
			return;
		
		GPTreeNode parentNode = getTreeRootNode(node);
		List<SystemWarningEntity> warns = ((GPMonitorEntity) parentNode.getGPEntity()).getWarns();
		warns.remove(entity);
		listViewer.refresh();
	}

	private void mail_event() {
		List<IGPEntity> mails = SqliteDao.getInstance().queryGPEntity(new MailEntity(), null);
		MailEntity mail;
		if (mails == null | mails.size() < 1)
			mail = new MailEntity();
		else
			mail = (MailEntity) mails.get(0);
		
		MailDialog dialog = new MailDialog(listViewer.getList().getShell(), mail);
		int code = dialog.open();
		if (code == IDialogConstants.CANCEL_ID)
			return;
		
		if (mails == null | mails.size() < 1)
			SqliteDao.getInstance().insertGPEntity(mail);
		else {
			SqlWhere where = new SqlWhere();
			where.addWhere("id", "=", "1");
			SqliteDao.getInstance().updateGPEntity(mail, where);
		}
	}

	public GPTreeNode getTreeRootNode(GPTreeNode node) {
		GPTreeNode parentNode = node.getParent();
		if (parentNode == null)
			return node;
		IGPEntity entity = parentNode.getGPEntity();
		if (entity == null)
			return getTreeRootNode(parentNode);
		return parentNode;
	}
}
