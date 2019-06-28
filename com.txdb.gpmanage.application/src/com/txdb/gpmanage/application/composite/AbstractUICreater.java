package com.txdb.gpmanage.application.composite;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public abstract class AbstractUICreater {
	private String name;
	private String id;
	private int location;
	private CTabItem item;
	private CTabFolder folder;
	public abstract Button createTitleButton(Composite top);
public abstract Label createWelButton(Composite top);
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CTabFolder getFolder() {
		return folder;
	}

	public void setFolder(CTabFolder folder) {
		this.folder = folder;
	}

	public CTabItem getItem() {
		return item;
	}

	public void setItem(CTabItem item) {
		this.item = item;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}
}
