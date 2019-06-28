package com.txdb.gpmanage.application.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.txdb.gpmanage.core.entity.impl.GPTreeNode;

/**
 * 导航内容提供器
 * 
 * @author ws
 *
 */
public class TreeContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	@Override
	public Object[] getElements(Object inputElement) {

		return ((List<GPTreeNode>) inputElement).toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {

		return ((GPTreeNode) parentElement).getChild().toArray();
	}

	@Override
	public Object getParent(Object element) {
		return ((GPTreeNode) element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		return ((GPTreeNode) element).getChild() != null
				&& ((GPTreeNode) element).getChild().size() > 0;
	}

}
