package com.chiller.bme.posture.db;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SessionDAO {

  // Database fields
  private SQLiteDatabase database;
  private PostureSQLiteHelper dbHelper;
  private String[] allColumns = { PostureSQLiteHelper.COLUMN_ID,
		  PostureSQLiteHelper.COLUMN_DATA,
		  PostureSQLiteHelper.COLUMN_TS,
		  PostureSQLiteHelper.COLUMN_EVENT,
		  PostureSQLiteHelper.COLUMN_SYNCED,
		  PostureSQLiteHelper.COLUMN_USER
		  };

  public SessionDAO(Context context) {
    dbHelper = new PostureSQLiteHelper(context);
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
	Log.w("PostureService","DAO Closed");
    dbHelper.close();
  }

  public SessionRecord createRecord(String record, String event) {
    ContentValues values = new ContentValues();
    
    Long tsLong = System.currentTimeMillis()/1000;
    String ts = tsLong.toString();
    
    values.put(PostureSQLiteHelper.COLUMN_DATA, record);
    values.put(PostureSQLiteHelper.COLUMN_TS, ts);
    values.put(PostureSQLiteHelper.COLUMN_EVENT, event);
    values.put(PostureSQLiteHelper.COLUMN_SYNCED, "false");

    values.put(PostureSQLiteHelper.COLUMN_USER, "Endre");
    long insertId = database.insert(PostureSQLiteHelper.TABLE_SESSIONS, null,
        values);
    Cursor cursor = database.query(PostureSQLiteHelper.TABLE_SESSIONS,
        allColumns, PostureSQLiteHelper.COLUMN_ID + " = " + insertId, null,
        null, null, null);
    cursor.moveToFirst();
    SessionRecord newRecord = cursorToRecord(cursor);
    cursor.close();
    return newRecord;
  }

  public void deleteRecord(SessionRecord record) {
    long id = record.getId();
    System.out.println("Record deleted with id: " + id);
    database.delete(PostureSQLiteHelper.TABLE_SESSIONS, PostureSQLiteHelper.COLUMN_ID
        + " = " + id, null);
  }

  public List<SessionRecord> getAllRecords() {
    List<SessionRecord> records = new ArrayList<SessionRecord>();

    Cursor cursor = database.query(PostureSQLiteHelper.TABLE_SESSIONS,
        allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      SessionRecord record = cursorToRecord(cursor);
      records.add(record);
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    return records;
  }

  private SessionRecord cursorToRecord(Cursor cursor) {
    SessionRecord record = new SessionRecord();
    record.setId(cursor.getLong(0));
    record.setRecord(cursor.getString(1));
    record.setTimestamp(cursor.getString(2));
    record.setEvent(cursor.getString(3));
    record.setSynced(cursor.getString(4));
    record.setUsername(cursor.getString(5));
    return record;
  }

  	public void deleteAllRecords() {

	  database.delete(PostureSQLiteHelper.TABLE_SESSIONS, null , null);
  	}

  	public List<SessionRecord> getAllUnsynced() {
	    List<SessionRecord> records = new ArrayList<SessionRecord>();

	    Cursor cursor = database.query(PostureSQLiteHelper.TABLE_SESSIONS,
	        allColumns, PostureSQLiteHelper.COLUMN_SYNCED + " = "+ "\"false\"", null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      SessionRecord record = cursorToRecord(cursor);
	      records.add(record);
	      cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return records;
	  }
  
	public String getUnsyncedJson() {
		// TODO Auto-generated method stub
	  
	  JSONArray results = new JSONArray();
	  for (SessionRecord sr: getAllUnsynced()){
		  JSONObject object = new JSONObject();
		  try {
		    object.put("id", sr.getId());
		    object.put("data", sr.getRecord() );
		    object.put("timestamp", sr.getTimestamp());
		    object.put("event",  sr.getEvent());
		    object.put("username", sr.getUsername());
		    results.put(object);
		  } catch (JSONException e) {
		    e.printStackTrace();
		  }
		 } 
	  Log.i("PostureService",results.toString());
	  return results.toString();
	  }

	public void markAllRecords() {
		// TODO Auto-generated method stub
		String strFilter = "synced=\"false\"";
		ContentValues args = new ContentValues();
		args.put(PostureSQLiteHelper.COLUMN_SYNCED, "true");
		database.update(PostureSQLiteHelper.TABLE_SESSIONS, args, strFilter, null);
	}
	
} 