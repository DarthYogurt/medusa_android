package com.medusa.checkit.android;

import android.os.Parcel;
import android.os.Parcelable;

public class Step implements Parcelable {

	private int order;
	private String name;
	private String type;
	private int id;
	private int checklistId;
	private String checklistName;
	private boolean isStepFinished;
	private boolean yesOrNo;
	private double value;
	private String text;
	
	public Step(int order, String name, String type, int id, int checklistId, String checklistName) {
		this.order = order;
		this.name = name;
		this.type = type;
		this.id = id;
		this.checklistId = checklistId;
		this.checklistName = checklistName;
		this.isStepFinished = false;
		this.yesOrNo = false;
		this.value = 0;
		this.text = "";
	}
	
	public Step(Parcel in) {
		readFromParcel(in);
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
	
	public boolean getIsStepFinished() {
		return isStepFinished;
	}
	
	public void setIsStepFinished(boolean b) {
		this.isStepFinished = b;
	}
	
	public boolean getYesOrNo() {
		return yesOrNo;
	}
	
	public void setYesOrNo(boolean b) {
		this.yesOrNo = b;
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
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(order);
		dest.writeString(name);
		dest.writeString(type);
		dest.writeInt(id);
		dest.writeInt(checklistId);
		dest.writeString(checklistName);
		dest.writeByte((byte)(isStepFinished ? 1 : 0));
		dest.writeByte((byte)(yesOrNo ? 1 : 0));
		dest.writeDouble(value);
		dest.writeString(text);
	}
	
	private void readFromParcel(Parcel in) {
		order = in.readInt();
		name = in.readString();
		type = in.readString();
		id = in.readInt();
		checklistId = in.readInt();
		checklistName = in.readString();
		isStepFinished = in.readByte() != 0;
		yesOrNo = in.readByte() != 0;
		value = in.readDouble();
		text = in.readString();
	}
	
	public static final Parcelable.Creator<Step> CREATOR = new Parcelable.Creator<Step>() {
		public Step createFromParcel(Parcel in) {
			return new Step(in);
		}
		
		public Step[] newArray(int size) {
			return new Step[size];
		}
	};
	
}
