package com.medusa.checkit.android;

public class Step {

	private int order;
	private String name;
	private String type;
	private int id;
	private int checklistId;
	private String checklistName;
	
	public Step(int order, String name, String type, int id, int checklistId, String checklistName) {
		this.order = order;
		this.name = name;
		this.type = type;
		this.id = id;
		this.checklistId = checklistId;
		this.checklistName = checklistName;
	}
	
	public int getOrder() {
		return order;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public int getId() {
		return id;
	}
	
	public int getChecklistId() {
		return checklistId;
	}
	
	public String getChecklistName() {
		return checklistName;
	}
	
}