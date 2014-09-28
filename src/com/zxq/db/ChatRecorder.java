package com.zxq.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zxq on 2014/9/24.
 */
public class ChatRecorder {









    private class ChatRecordHelper extends SQLiteOpenHelper{

        public ChatRecordHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
          //  db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + ChatConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + ChatConstants.DATE + " INTEGER," + ChatConstants.DIRECTION + " INTEGER," + ChatConstants.JID + " TEXT," + ChatConstants.MESSAGE + " TEXT," + ChatConstants.DELIVERY_STATUS + " INTEGER," + ChatConstants.PACKET_ID + " TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
