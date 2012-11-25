package com.chiller.bme.posture.db;

public class SessionRecord {
	  private long id;
	  private String userid;

	  public long getId() {
	    return id;
	  }

	  public void setId(long id) {
	    this.id = id;
	  }

	  public String getComment() {
	    return userid;
	  }

	  public void setComment(String comment) {
	    this.userid = comment;
	  }

	  // Will be used by the ArrayAdapter in the ListView
	  @Override
	  public String toString() {
	    return userid;
	  }
	} 