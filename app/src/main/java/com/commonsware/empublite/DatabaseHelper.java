package com.commonsware.empublite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.os.Process;

import de.greenrobot.event.EventBus;

/**
 * Created by abc on 3/27/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "empublite.db";
    private static final int SCHEMA_VERSION = 1;
    private static DatabaseHelper singleton = null;

    private DatabaseHelper(Context ctxt) {
        super(ctxt, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    synchronized static DatabaseHelper getInstance(Context ctxt) {
        if(singleton == null) {
            singleton = new DatabaseHelper(ctxt.getApplicationContext());
        }
        return singleton;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE notes (position INTEGER PRIMARY KEY, prose TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        throw new RuntimeException("This should not be called");
    }

    public void loadNote(int position) {
        new LoadThread(position).start();
    }

    public void updateNote(int position, String prose) {
        new UpdateThread(position, prose).start();
    }

    private class LoadThread extends Thread {
        private int position = -1;
        LoadThread(int position) {
            super();
            this.position = position;
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        }

        @Override
        public void run() {
            String[] args = {String.valueOf(position)};
            Cursor c = getReadableDatabase().rawQuery("SELECT prose FROM notes WHERE position = ?", args);
            if(c.getCount() > 0) {
                c.moveToFirst();
                EventBus.getDefault().post(new NoteLoadedEvent(position, c.getString(0)));
            }
            c.close();
        }
    }

    private class UpdateThread extends Thread {
        private int position = -1;
        private String prose = null;

        UpdateThread(int position, String prose) {
            super();
            this.position = position;
            this.prose = prose;
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        }

        @Override
        public void run() {
            String[] args = { String.valueOf(position), prose};

            getWritableDatabase().execSQL("INSERT OR REPLACE INTO notes (position, prose) VALUES (?, ?)", args);
        }
    }
}
