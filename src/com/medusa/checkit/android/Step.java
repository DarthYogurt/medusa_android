package com.medusa.checkit.android;

import android.os.Parcel;
import android.os.Parcelable;

public class Step implements Parcelable {

	private int order;
	private String name;
	private String type;
	private int id;
	private int notifyUserId;
	private int checklistId;
	private String checklistName;
	private boolean reqText;
	private boolean reqImage;
	private Boolean ifValueTrue;
	private Boolean ifValueFalse;
	private Double ifLessThan;
	private Double ifEqualTo;
	private Double ifGreaterThan;
	private boolean yesOrNo;
	private double number;
	private String text;
	private String imageFilename;
	private boolean isStepFinished;
	private String timeStarted;
	private String timeFinished;
	
	public Step(int order, String name, String type, int id, int notifyUserId,
				int checklistId, String checklistName, boolean reqText, boolean reqImage) {
		this.order = order;
		this.name = name;
		this.type = type;
		this.id = id;
		this.notifyUserId = notifyUserId;
		this.checklistId = checklistId;
		this.checklistName = checklistName;
		this.reqText = reqText;
		this.reqImage = reqImage;
		this.ifValueTrue = null;
		this.ifValueFalse = null;
		this.ifLessThan = null;
		this.ifEqualTo = null;
		this.ifGreaterThan = null;
		this.yesOrNo = false;
		this.number = 0;
		this.text = "";
		this.imageFilename = "";
		this.isStepFinished = false;
		this.timeStarted = "";
		this.timeFinished = "";
	}
	
	public Step(Parcel in) { readFromParcel(in); }
	
	public int getOrder() { return order; }
	
	public String getName() { return name; }
	
	public String getType() { return type; }
	
	public int getId() { return id; }
	
	public int getNotifyUserId() { return notifyUserId; }
	
	public int getChecklistId() { return checklistId; }
	
	public String getChecklistName() { return checklistName; }
	
	public boolean getReqText() { return reqText; }
	
	public boolean getReqImage() { return reqImage; }
	
	public Boolean getIfValueTrue() { return ifValueTrue; }
	public void setIfValueTrue(boolean b) { this.ifValueTrue = b; }
	
	public Boolean getIfValueFalse() { return ifValueFalse; }
	public void setIfValueFalse(boolean b) { this.ifValueFalse = b; }
	
	public Double getIfLessThan() { return ifLessThan; }
	public void setIfLessThan(double d) { this.ifLessThan = d; }
	
	public Double getIfEqualTo() { return ifEqualTo; }
	public void setIfEqualTo(double d) { this.ifEqualTo = d; }
	
	public Double getIfGreaterThan() { return ifGreaterThan; }
	public void setIfGreaterThan(double d) { this.ifGreaterThan = d; }
	
	public boolean getIsStepFinished() { return isStepFinished; }
	public void setIsStepFinished(boolean b) { this.isStepFinished = b; }
	
	public boolean getYesOrNo() { return yesOrNo; }
	public void setYesOrNo(boolean b) { this.yesOrNo = b; }
	
	public double getNumber() { return number; }
	public void setNumber(double d) { this.number = d; }
	
	public String getText() { return text; }
	public void setText(String s) {	this.text = s; }
	
	public String getImageFilename() { return imageFilename; }
	public void setImageFilename(String s) { this.imageFilename = s; }
	
	public String getTimeStarted() { return timeStarted; }
	public void setTimeStarted(String s) { this.timeStarted = s; }
	
	public String getTimeFinished() { return timeFinished; }
	public void setTimeFinished(String s) { this.timeFinished = s; }
	
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
		dest.writeInt(checklistId);
		dest.writeString(checklistName);
		dest.writeByte((byte)(reqText ? 1 : 0));
		dest.writeByte((byte)(reqImage ? 1 : 0));
		
		dest.writeValue(ifValueTrue);
		dest.writeValue(ifValueFalse);
		dest.writeValue(ifLessThan);
		dest.writeValue(ifEqualTo);
		dest.writeValue(ifGreaterThan);
		
		dest.writeByte((byte)(yesOrNo ? 1 : 0));
		dest.writeDouble(number);
		dest.writeString(text);
		dest.writeString(imageFilename);
		dest.writeByte((byte)(isStepFinished ? 1 : 0));
		dest.writeString(timeStarted);
		dest.writeString(timeFinished);
	}
	
	private void readFromParcel(Parcel in) {
		order = in.readInt();
		name = in.readString();
		type = in.readString();
		id = in.readInt();
		notifyUserId = in.readInt();
		checklistId = in.readInt();
		checklistName = in.readString();
		reqText = in.readByte() != 0;
		reqImage = in.readByte() != 0;
		
		Object ifValueTrueObj = in.readValue(null);
		if (ifValueTrueObj == null) { ifValueTrue = null; }
		else { ifValueTrue = (Boolean) ifValueTrueObj; }
		
		Object ifValueFalseObj = in.readValue(null);
		if (ifValueFalseObj == null) { ifValueFalse = null; }
		else { ifValueFalse = (Boolean) ifValueFalseObj; }
		
		Object ifLessThanObj = in.readValue(null);
		if (ifLessThanObj == null) { ifLessThan = null; }
		else { ifLessThan = (Double) ifLessThanObj; }
		
		Object ifEqualToObj = in.readValue(null);
		if (ifEqualToObj == null) { ifEqualTo = null; }
		else { ifEqualTo = (Double) ifEqualToObj; }

		Object ifGreaterThanObj = in.readValue(null);
		if (ifGreaterThanObj == null) { ifGreaterThan = null; }
		else { ifGreaterThan = (Double) ifGreaterThanObj; }
		
		yesOrNo = in.readByte() != 0;
		number = in.readDouble();
		text = in.readString();
		imageFilename = in.readString();
		isStepFinished = in.readByte() != 0;
		timeStarted = in.readString();
		timeFinished = in.readString();
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
