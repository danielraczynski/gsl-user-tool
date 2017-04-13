package com.roche.idm.model;

public class Group {

	private String name;

	public Group() {
	}

	public Group(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Group{" +
				"name='" + name + '\'' +
				'}';
	}
}
