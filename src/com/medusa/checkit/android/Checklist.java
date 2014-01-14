package com.medusa.checkit.android;

import java.io.Serializable;

public class Checklist implements Serializable {

	private static final long serialVersionUID = 6306794285994296143L;
	private int id;
	private String name;
	private int groupId;
	
	public Checklist(int id, String name, int groupId) {
		this.id = id;
		this.name = name;
		this.groupId = groupId;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getGroupId() {
		return groupId;
	}
}
