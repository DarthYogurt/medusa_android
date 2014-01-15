package com.medusa.checkit.android;

import java.io.Serializable;

public class Step implements Serializable {

	private static final long serialVersionUID = -1830210019061594179L;
	private int order;
	private String name;
	private String type;
	private int id;
	private int checklistId;
	private String checklistName;
	private boolean finished;
	private double value;
	private String text;
	
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
	
	public boolean getFinished() {
		return finished;
	}
	
	public void setFinished(boolean b) {
		this.finished = b;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double d) {
		this.value = d;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String s) {
		this.text = s;
	}
	
}
