package com.txdb.gpmanage.application.composite;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.txdb.gpmanage.application.Activator;
import com.txdb.gpmanage.application.provider.TreeContentProvider;
import com.txdb.gpmanage.application.provider.TreeLabelProvider;
import com.txdb.gpmanage.core.entity.impl.GPTreeNode;
import com.txdb.gpmanage.core.service.AbstractUIService;

/**
 * 各tab页主界面父类
 * @author ws
 */
public abstract class AbstractComposite extends Composite implements ISelectionChangedListener {

	/** 安装界面服务类 */
	protected AbstractUIService service;

	/** 导航树 */
	protected TreeViewer tv;

	/** 导航列表 */
	protected ListViewer listViewer;

	/** 内容区域 */
	protected Composite right;
	/**
	 *左侧导航是否含有SashForm
	 */
	protected boolean isLeftSashForm;
	public AbstractComposite(Composite parent, int style) {
		this(parent, style, false);
	}

	public AbstractComposite(Composite parent, int style, boolean isLeftSashForm) {
		super(parent, style);
		this.setBackgroundMode(SWT.INHERIT_FORCE);
		this.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		this.isLeftSashForm = isLeftSashForm;
		initComposite();
	}

	/**
	 * 右侧功能区
	 * @param right
	 */
	public abstract void createmain();

	/**
	 * 右侧功能区内容
	 * @param right
	 */
	protected abstract void setInput();

	/**
	 * 右侧功能区
	 * @param right
	 */
	protected void setListViewerInput() {
	}

	/**
	 * 左侧树下方区域
	 * @param left
	 */
	public void createItem(Composite left) {
	}

	/**
	 * 左侧树导航
	 * @param left
	 */
	public void createTree(Composite left) {
		left.setLayout(new FillLayout());
		tv = new TreeViewer(left);
		tv.setLabelProvider(new TreeLabelProvider());
		tv.setContentProvider(new TreeContentProvider());
		tv.addSelectionChangedListener(this);
//		tv.getTree().setBackground(bg_color);
		setInput();
	}

	/**
	 * 绘制界面
	 */
	public void initComposite() {
		this.setLayout(new FillLayout());
		SashForm sashForm = new SashForm(this, SWT.HORIZONTAL);
		if (isLeftSashForm) {
			SashForm leftSashForm = new SashForm(sashForm, SWT.V_SCROLL);
			Composite left_top = new Composite(leftSashForm, SWT.NONE);
			Composite left_bottom = new Composite(leftSashForm, SWT.BORDER);
			createTree(left_top);
			createItem(left_bottom);
			leftSashForm.setWeights(new int[] { 5, 1 });
		} else {
			Composite left = new Composite(sashForm, SWT.NONE);
			createTree(left);
		}
		right = new Composite(sashForm, SWT.NONE | SWT.BORDER);
		right.setBackgroundMode(SWT.INHERIT_FORCE);
		createmain();
		sashForm.setWeights(new int[] { 1, 5 });
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		changeComposite(event);

	}

	public AbstractUIService getService() {
		return service;
	}

	/**
	 * 右侧功能区切换
	 * @param right
	 */
	protected void changeComposite(SelectionChangedEvent event) {
		TreeSelection selection = (TreeSelection) event.getSelection();
		GPTreeNode node = (GPTreeNode) selection.getFirstElement();
		if (node == null)
			return;
		if (node.getCompositeCode() == null) {
			if (node.getChild() == null || node.getChild().size() < 1)
				return;
			tv.expandToLevel(node, 1);
			tv.setSelection(new StructuredSelection(new Object[] { node.getChild().get(0) }));
			List<GPTreeNode> nodes = (List<GPTreeNode>) tv.getInput();
			for (GPTreeNode children : nodes) {
				if (!children.equals(node))
					tv.collapseToLevel(children, 1);
			}
			return;
		}
		if (right == null)
			return;
		Control[] child = right.getChildren();
		for (Control children : child) {
			if (((IupperComposite) children).getCode() == node.getCompositeCode()) {
				StackLayout layout = (StackLayout) right.getLayout();
				if (!layout.topControl.equals((Composite) children)) {
					layout.topControl = (Composite) children;
					right.layout();
				}
				break;
			}
		}
	}
}
