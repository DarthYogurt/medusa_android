package com.medusa.checkit.android;

import android.os.Parcel;
import android.os.Parcelable;

public class Checklist implements Parcelable {

	private int id;
	private String name;
	private int groupId;
	private int numOfSteps;
	
	public Checklist(int id, String name, int groupId) {
		this.id = id;
		this.name = name;
		this.groupId = groupId;
	}
	
	public Checklist(Parcel in) {
		readFromParcel(in);
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
	
	public void setNumOfSteps(int i) {
		this.numOfSteps = i; 
	}
	
	public int getNumOfSteps() {
		return numOfSteps;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeInt(groupId);
		dest.writeInt(numOfSteps);
	}
	
	private void readFromParcel(Parcel in) {
		id = in.readInt();
		name = in.readString();
		groupId = in.readInt();
		numOfSteps = in.readInt();
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
