package com.medusa.checkit.android;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

	private int id;
	private String name;
	
	public User(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public User(Parcel in) { 
		readFromParcel(in); 
	}
	
	public int getId() { return id;	}
	
	public String getName() { return name; }
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
	}
	
	private void readFromParcel(Parcel in) {
		id = in.readInt();
		name = in.readString();

	}
	
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel in) {
			return new User(in);
		}
		
		public User[] newArray(int size) {
			return new User[size];
		}
	};
	
}
