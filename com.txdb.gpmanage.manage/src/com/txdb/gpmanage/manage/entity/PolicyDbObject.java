package com.txdb.gpmanage.manage.entity;

public class PolicyDbObject {
	private String name;
	private int id;

	public PolicyDbObject(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return getName() + ":" + getId();
	}
}
