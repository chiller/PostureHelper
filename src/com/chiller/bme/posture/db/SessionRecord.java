package com.chiller.bme.posture.db;

public class SessionRecord {
	  private long id;
	  private String userid;
	  private String timestamp;
	  private String event;
	  private boolean synced;
	  private String username;
	  
	  public long getId() {
	    return id;
	  }

	  public void setId(long id) {
	    this.id = id;
	  }

	  public String getRecord() {
	    return userid;
	  }

	  public void setRecord(String record) {
	    this.userid = record;
	  }

	  // Will be used by the ArrayAdapter in the ListView
	  @Override
	  public String toString() {
	    return userid+ " "+ timestamp+ " " + event + " "+ synced+ " " + username;
	  }

	  public void setTimestamp(String timestamp) {
			// TODO Auto-generated method stub
		this.timestamp = timestamp;
	  }

	  public void setEvent(String event) {
		// TODO Auto-generated method stub
		  this.event = event;
		
	  }

	public void setSynced(String string) {
		// TODO Auto-generated method stub
		this.synced = Boolean.parseBoolean(string);
	}

	public void setUsername(String username) {
		// TODO Auto-generated method stub
		this.username = username;
	}
	} 