package com.chiller.bme.posture.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PostureSQLiteHelper extends SQLiteOpenHelper {

  public static final String TABLE_SESSIONS = "sessions";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_USER = "userid";

  private static final String DATABASE_NAME = "posture.db";
  private static final int DATABASE_VERSION = 1;

  // Database creation sql statement
  private static final String DATABASE_CREATE = "create table "
      + TABLE_SESSIONS + "(" + COLUMN_ID
      + " integer primary key autoincrement, " + COLUMN_USER
      + " text not null);";

  public PostureSQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(PostureSQLiteHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
    onCreate(db);
  }

} 
