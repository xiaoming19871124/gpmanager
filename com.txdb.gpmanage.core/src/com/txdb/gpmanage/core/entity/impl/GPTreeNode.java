package com.txdb.gpmanage.core.entity.impl;

import java.util.ArrayList;
import java.util.List;

import com.txdb.gpmanage.core.entity.IGPEntity;
import com.txdb.gpmanage.core.exception.CompositeCode;

/**
 * 导航节点
 * @author ws
 */
public class GPTreeNode {
	
	/** 是否已连接 */
	private boolean isConnection = false;
	
	/** 父节点 */
	private GPTreeNode parent;
	
	/** 子节点集合 */
	private List<GPTreeNode> child;
	
	/** 对应界面 Code */
	private CompositeCode compositeCode;
	
	/** 对应的 gpEntity */
	private IGPEntity gpEntity;

	public CompositeCode getCompositeCode() {
		return compositeCode;
	}

	public void setCompositeCode(CompositeCode compositeCode) {
		this.compositeCode = compositeCode;
	}

	/** name */
	private String name;

	/**
	 * 添加子节点
	 * @param children 子节点
	 */
	public void addChildren(GPTreeNode children) {
		if (child == null)
			child = new ArrayList<GPTreeNode>();
		children.setParent(this);
		child.add(children);
	}

	/**
	 * 删除子节点
	 * @param children
	 */
	public void removeChildren(GPTreeNode children) {
		if (child.contains(children))
			child.remove(children);
	}

	/**
	 * 删除子节点
	 * @param children
	 */
	public void removeAllChildren() {
		if (child != null && child.size() > 0)
			child.clear();
	}

	public GPTreeNode getParent() {
		return parent;
	}

	public void setParent(GPTreeNode parent) {
		this.parent = parent;
	}

	public List<GPTreeNode> getChild() {
		return child;
	}

	public void setChild(List<GPTreeNode> child) {
		this.child = child;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IGPEntity getGPEntity() {
		return gpEntity;
	}

	public void setGPEntity(IGPEntity gp) {
		this.gpEntity = gp;
	}

	public boolean hasChild() {
		return child != null && child.size() > 0;
	}

	public boolean isConnection() {
		return isConnection;
	}

	public void setConnection(boolean isConnection) {
		this.isConnection = isConnection;
	}
}
