package com.chiller.bme.posture.db;

import java.util.ArrayList;
import java.util.List;

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
		  PostureSQLiteHelper.COLUMN_USER };

  public SessionDAO(Context context) {
    dbHelper = new PostureSQLiteHelper(context);
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
	Log.i("PostureService","DAO Closed");
    dbHelper.close();
  }

  public SessionRecord createRecord(String record) {
    ContentValues values = new ContentValues();
    values.put(PostureSQLiteHelper.COLUMN_USER, record);
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
    SessionRecord comment = new SessionRecord();
    comment.setId(cursor.getLong(0));
    comment.setComment(cursor.getString(1));
    return comment;
  }
} 