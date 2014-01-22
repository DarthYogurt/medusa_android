package com.medusa.checkit.android;

import android.os.Parcel;
import android.os.Parcelable;

public class Step implements Parcelable {

	private int order;
	private String name;
	private String type;
	private int id;
	private int notifyUserId;
	private boolean ifValueTrue;
	private boolean ifValueFalse;
	private double ifLessThan;
	private double ifEqualTo;
	private double ifGreaterThan;
	private boolean reqText;
	private boolean reqImage;
	private int checklistId;
	private String checklistName;
	private boolean isStepFinished;
	private boolean yesOrNo;
	private double value;
	private String text;
	private String imageFilename;
	
	public Step(int order, String name, String type, int id, int notifyUserId,
				boolean ifValueTrue, boolean ifValueFalse, double ifLessThan,
				double ifEqualTo, double ifGreaterThan, boolean reqText,
				boolean reqImage, int checklistId, String checklistName) {
		this.order = order;
		this.name = name;
		this.type = type;
		this.id = id;
		this.notifyUserId = notifyUserId;
		this.ifValueTrue = ifValueTrue;
		this.ifValueFalse = ifValueFalse;
		this.ifLessThan = ifLessThan;
		this.ifEqualTo = ifEqualTo;
		this.ifGreaterThan = ifGreaterThan;
		this.reqText = reqText;
		this.reqImage = reqImage;
		this.checklistId = checklistId;
		this.checklistName = checklistName;
		this.isStepFinished = false;
		this.yesOrNo = false;
		this.value = 0;
		this.text = "";
		this.imageFilename = "";
	}
	
	public Step(Parcel in) { readFromParcel(in); }
	
	public int getOrder() { return order; }
	
	public String getName() { return name; }
	
	public String getType() { return type; }
	
	public int getId() { return id; }
	
	public int getNotifyUserId() { return notifyUserId; }
	
	public boolean getIfValueTrue() { return ifValueTrue; }
	
	public boolean getIfValueFalse() { return ifValueFalse; }
	
	public double ifLessThan() { return ifLessThan; }
	
	public double ifEqualTo() { return ifEqualTo; }
	
	public double ifGreaterThan() { return ifGreaterThan; }
	
	public boolean getReqText() { return reqText; }
	
	public boolean getReqImage() { return reqImage; }
	
	public int getChecklistId() { return checklistId; }
	
	public String getChecklistName() { return checklistName; }
	
	public boolean getIsStepFinished() { return isStepFinished; }
	
	public void setIsStepFinished(boolean b) { this.isStepFinished = b; }
	
	public boolean getYesOrNo() { return yesOrNo; }
	
	public void setYesOrNo(boolean b) { this.yesOrNo = b; }
	
	public double getValue() { return value; }
	
	public void setValue(double d) { this.value = d; }
	
	public String getText() { return text; }
	
	public void setText(String s) {	this.text = s; }
	
	public String getImageFilename() { return imageFilename; }
	
	public void setImageFilename(String s) { this.imageFilename = s; }
	
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
		dest.writeInt(notifyUserId);
		dest.writeByte((byte)(ifValueTrue ? 1 : 0));
		dest.writeByte((byte)(ifValueFalse ? 1 : 0));
		dest.writeDouble(ifLessThan);
		dest.writeDouble(ifEqualTo);
		dest.writeDouble(ifGreaterThan);
		dest.writeByte((byte)(reqText ? 1 : 0));
		dest.writeByte((byte)(reqImage ? 1 : 0));
		dest.writeInt(checklistId);
		dest.writeString(checklistName);
		dest.writeByte((byte)(isStepFinished ? 1 : 0));
		dest.writeByte((byte)(yesOrNo ? 1 : 0));
		dest.writeDouble(value);
		dest.writeString(text);
		dest.writeString(imageFilename);
	}
	
	private void readFromParcel(Parcel in) {
		order = in.readInt();
		name = in.readString();
		type = in.readString();
		id = in.readInt();
		notifyUserId = in.readInt();
		ifValueTrue = in.readByte() != 0;
		ifValueFalse = in.readByte() != 0;
		ifLessThan = in.readDouble();
		ifEqualTo = in.readDouble();
		ifGreaterThan = in.readDouble();
		reqText = in.readByte() != 0;
		reqImage = in.readByte() != 0;
		checklistId = in.readInt();
		checklistName = in.readString();
		isStepFinished = in.readByte() != 0;
		yesOrNo = in.readByte() != 0;
		value = in.readDouble();
		text = in.readString();
		imageFilename = in.readString();
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
