package com.txdb.gpmanage.manage.entity;

public class GPData {
	protected boolean isSelection = false;
	public static final String ALl_NAME = "ALL";
	protected String name = ALl_NAME;

	public boolean isSelection() {
		return isSelection;
	}

	public void setSelection(boolean isSelection) {
		this.isSelection = isSelection;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name != null && !name.isEmpty() ? name : "";
	}
}
