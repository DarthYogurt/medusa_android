package com.medusa.checkit.android;

public class Step {

	private int order;
	private String name;
	private String type;
	private int id;
	private int checklistId;
	
	public Step(int order, String name, String type, int id, int checklistId) {
		this.order = order;
		this.name = name;
		this.type = type;
		this.id = id;
		this.checklistId = checklistId;
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
	
}
