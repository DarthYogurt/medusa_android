package com.medusa.checkit.android;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Step implements Parcelable {

	private int order;
	private String name;
	private String type;
	private int id;
	private int checklistId;
	private String checklistName;
	private ArrayList<User> users;
	private Boolean yesOrNo;
	private Double number;
	private String text;
	private String imageFilename;
	private String extraNote;
	private String extraImageFilename;
	private Boolean ifBoolValueIs;
	private Double ifLessThan;
	private Double ifEqualTo;
	private Double ifGreaterThan;
	private int notifyUserId;
	private boolean reqNote;
	private boolean reqPicture;
	private boolean isReqNoteFinished;
	private boolean isReqPictureFinished;
	private boolean isStepFinished;
	private boolean isAllFinished;
	private String timeStarted;
	private String timeFinished;
	
	public Step(int order, String name, String type, int id, int checklistId, String checklistName, 
				ArrayList<User> users, boolean reqNote, boolean reqPicture) {
		this.order = order;
		this.name = name;
		this.type = type;
		this.id = id;
		this.checklistId = checklistId;
		this.checklistName = checklistName;
		this.users = users;
		this.reqNote = reqNote;
		this.reqPicture = reqPicture;
		this.notifyUserId = 0;
		this.ifBoolValueIs = null;
		this.ifLessThan = null;
		this.ifEqualTo = null;
		this.ifGreaterThan = null;
		this.yesOrNo = null;
		this.number = null;
		this.text = "";
		this.imageFilename = "";
		this.extraNote = "";
		this.extraImageFilename = "";
		this.isReqNoteFinished = false;
		this.isReqPictureFinished = false;
		this.isStepFinished = false;
		this.isAllFinished = false;
		this.timeStarted = "";
		this.timeFinished = "";
	}
	
	public Step(Parcel in) { 
		readFromParcel(in); 
	}
	
	public int getOrder() { return order; }
	
	public String getName() { return name; }
	
	public String getType() { return type; }
	
	public int getId() { return id; }
	
	public int getChecklistId() { return checklistId; }
	
	public String getChecklistName() { return checklistName; }
	
	public ArrayList<User> getUsers() { return users; }
	
	public boolean getReqNote() { return reqNote; }
	
	public boolean getReqPicture() { return reqPicture; }
	
	public int getNotifyUserId() { return notifyUserId; }
	public void setNotifyUserId(int i) { this.notifyUserId = i; }
	
	public Boolean getIfBoolValueIs() { return ifBoolValueIs; }
	public void setIfBoolValueIs(boolean b) { this.ifBoolValueIs = b; }
	
	public Double getIfLessThan() { return ifLessThan; }
	public void setIfLessThan(double d) { this.ifLessThan = d; }
	
	public Double getIfEqualTo() { return ifEqualTo; }
	public void setIfEqualTo(double d) { this.ifEqualTo = d; }
	
	public Double getIfGreaterThan() { return ifGreaterThan; }
	public void setIfGreaterThan(double d) { this.ifGreaterThan = d; }
	
	public Boolean getYesOrNo() { return yesOrNo; }
	public void setYesOrNo(boolean b) { this.yesOrNo = b; }
	
	public Double getNumber() { return number; }
	public void setNumber(double d) { this.number = d; }
	
	public String getText() { return text; }
	public void setText(String s) {	this.text = s; }
	
	public String getImageFilename() { return imageFilename; }
	public void setImageFilename(String s) { this.imageFilename = s; }
	
	public String getExtraNote() { return extraNote; }
	public void setExtraNote(String s) { this.extraNote = s; }
	
	public String getExtraImageFilename() { return extraImageFilename; }
	public void setExtraImageFilename(String s) { this.extraImageFilename = s; }
	
	public boolean getIsReqNoteFinished() { return isReqNoteFinished; }
	public void setIsReqNoteFinished(boolean b) { this.isReqNoteFinished = b; }
	
	public boolean getIsReqPictureFinished() { return isReqPictureFinished; }
	public void setIsReqPictureFinished(boolean b) { this.isReqPictureFinished = b; }
	
	public boolean getIsStepFinished() { return isStepFinished; }
	public void setIsStepFinished(boolean b) { this.isStepFinished = b; }
	
	public boolean getIsAllFinished() { return isAllFinished; }
	public void setIsAllFinished(boolean b) { this.isAllFinished = b; }
	
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
		dest.writeInt(checklistId);
		dest.writeString(checklistName);
		dest.writeTypedList(users);
		
		dest.writeByte((byte)(reqNote ? 1 : 0));
		dest.writeByte((byte)(reqPicture ? 1 : 0));
		dest.writeInt(notifyUserId);
		
		dest.writeValue(ifBoolValueIs);
		dest.writeValue(ifLessThan);
		dest.writeValue(ifEqualTo);
		dest.writeValue(ifGreaterThan);
		
		dest.writeValue(yesOrNo);
		dest.writeValue(number);
		dest.writeString(text);
		dest.writeString(imageFilename);
		dest.writeString(extraNote);
		dest.writeString(extraImageFilename);
		dest.writeByte((byte)(isReqNoteFinished ? 1 : 0));
		dest.writeByte((byte)(isReqPictureFinished ? 1 : 0));
		dest.writeByte((byte)(isStepFinished ? 1 : 0));
		dest.writeByte((byte)(isAllFinished ? 1 : 0));
		dest.writeString(timeStarted);
		dest.writeString(timeFinished);
	}
	
	private void readFromParcel(Parcel in) {
		order = in.readInt();
		name = in.readString();
		type = in.readString();
		id = in.readInt();
		checklistId = in.readInt();
		checklistName = in.readString();
		users = new ArrayList<User>();
		in.readTypedList(users, User.CREATOR);
		
		reqNote = in.readByte() != 0;
		reqPicture = in.readByte() != 0;
		notifyUserId = in.readInt();
		
		Object ifBoolValueIsObj = in.readValue(null);
		if (ifBoolValueIsObj == null) { ifBoolValueIs = null; }
		else { ifBoolValueIs = (Boolean) ifBoolValueIsObj; }
		
		Object ifLessThanObj = in.readValue(null);
		if (ifLessThanObj == null) { ifLessThan = null; }
		else { ifLessThan = (Double) ifLessThanObj; }
		
		Object ifEqualToObj = in.readValue(null);
		if (ifEqualToObj == null) { ifEqualTo = null; }
		else { ifEqualTo = (Double) ifEqualToObj; }

		Object ifGreaterThanObj = in.readValue(null);
		if (ifGreaterThanObj == null) { ifGreaterThan = null; }
		else { ifGreaterThan = (Double) ifGreaterThanObj; }
		
		Object yesOrNoObj = in.readValue(null);
		if (yesOrNoObj == null) { yesOrNo = null; }
		else { yesOrNo = (Boolean) yesOrNoObj; }
		
		Object numberObj = in.readValue(null);
		if (numberObj == null) { number = null; }
		else { number = (Double) numberObj; }
		
		text = in.readString();
		imageFilename = in.readString();
		extraNote = in.readString();
		extraImageFilename = in.readString();
		isReqNoteFinished = in.readByte() != 0;
		isReqPictureFinished = in.readByte() != 0;
		isStepFinished = in.readByte() != 0;
		isAllFinished = in.readByte() != 0;
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
