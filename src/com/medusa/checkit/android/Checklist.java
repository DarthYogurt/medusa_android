package com.medusa.checkit.android;

import android.os.Parcel;
import android.os.Parcelable;

public class Checklist implements Parcelable {

	private int groupId;
	private int id;
	private String name;
	private int numOfSteps;
	private String timeStarted;
	private String timeFinished;
	
	public Checklist(int groupId, int id, String name, int numOfSteps) {
		this.groupId = groupId;
		this.id = id;
		this.name = name;
		this.numOfSteps = numOfSteps;
		this.timeStarted = "";
		this.timeFinished = "";
	}
	
	public Checklist(Parcel in) {
		readFromParcel(in);
	}
	
	public int getGroupId() { return groupId; }
	
	public int getId() { return id; }
	
	public String getName() { return name; }
	
	public int getNumOfSteps() { return numOfSteps; }
	
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
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeInt(numOfSteps);
		dest.writeInt(groupId);
		dest.writeString(timeStarted);
		dest.writeString(timeFinished);
	}
	
	private void readFromParcel(Parcel in) {
		id = in.readInt();
		name = in.readString();
		numOfSteps = in.readInt();
		groupId = in.readInt();
		timeStarted = in.readString();
		timeFinished = in.readString();
	}
	
	public static final Parcelable.Creator<Checklist> CREATOR = new Parcelable.Creator<Checklist>() {
		public Checklist createFromParcel(Parcel in) {
			return new Checklist(in);
		}
		
		public Checklist[] newArray(int size) {
			return new Checklist[size];
		}
	};
}
