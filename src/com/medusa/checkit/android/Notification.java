package com.medusa.checkit.android;

public class Notification {

	private int slateId;
	private String userName;
	private String checklist;
	private String stepName;
	private String notifyName;
	private String note;
	private String imgUrl;
	
	public Notification(int slateId, String userName, String checklist, String stepName, String notifyName,
						String note, String imgUrl) {
		this.slateId = slateId;
		this.userName = userName;
		this.checklist = checklist;
		this.stepName = stepName;
		this.notifyName = notifyName;
		this.note = note;
		this.imgUrl = imgUrl;
	}
	
	public int getSlateId() { return slateId; }
	
	public String getUserName() { return userName; }
	
	public String getChecklist() { return checklist; }
	
	public String getStepName() { return stepName; }
	
	public String getNotifyName() { return notifyName; }
	
	public String getNote() { return note; }
	
	public String getImgUrl() { return imgUrl; }
	
}
