package com.txdb.gpmanage.manage.ui.listener;

import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;

import com.txdb.gpmanage.core.common.CommonUtil;
import com.txdb.gpmanage.core.entity.impl.GPTreeNode;
import com.txdb.gpmanage.manage.ui.composite.ManageComposite;

/**
 * 树鼠标监听
 * 
 * @author ws
 */
public class TreeMouseListener implements MouseListener {
	private TreeViewer tv;
	private ManageComposite manageComposite;

	public TreeMouseListener(TreeViewer tv, ManageComposite manageComposite) {
		super();
		this.tv = tv;
		this.manageComposite = manageComposite;
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		if (e.button != 1)
			return;
		TreeItem selected = tv.getTree().getItem(new Point(e.x, e.y));
		if (selected != null) {
			TreeSelection selection = (TreeSelection) tv.getSelection();
			GPTreeNode node = (GPTreeNode) selection.getFirstElement();
			if (node.getParent() == null && (node.getChild() == null || node.getChild().size() < 1)) {
				node.setConnection(true);
				tv.refresh();
				manageComposite.addContainerComposite(node);
			}
		}
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (e.button != 3) {
			return;
		}
		TreeItem selected = tv.getTree().getItem(new Point(e.x, e.y));
		if (selected == null) {
			manageComposite.createNoNodeMenu();
		} else {
			TreeSelection selection = (TreeSelection) tv.getSelection();
			GPTreeNode node = (GPTreeNode) selection.getFirstElement();
			if (node.getParent() == null && (node.getChild() == null || node.getChild().size() < 1)) {
				manageComposite.createTopOpenNodeMenu();
			} else if (node.getParent() == null && node.getChild() != null && node.getChild().size() > 0) {
				manageComposite.createTopClosedNodeMenu();
			} else {
				manageComposite.disableAllItem();
			}
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {

	}
}
