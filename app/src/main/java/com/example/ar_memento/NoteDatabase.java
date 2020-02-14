package com.example.ar_memento;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class NoteDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "noteDb";
    private static final String DATABASE_TABLE = "notesTable";

    // column names for DB
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";

    NoteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create table
        String query = "CREATE TABLE " + DATABASE_TABLE + "(" + KEY_ID + " INT PRIMARY KEY,"+
                KEY_TITLE + " TEXT,"+
                KEY_CONTENT + " TEXT,"+
                KEY_DATE + " TEXT,"+
                KEY_TIME + " TEXT" + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion)
            return;
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

    public long addNote(NoteData note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(KEY_TITLE,note.getTitle());
        v.put(KEY_CONTENT,note.getContent());
        v.put(KEY_DATE,note.getDate());
        v.put(KEY_TIME,note.getTime());

        // inserting data into db
        long ID = db.insert(DATABASE_TABLE,null,v);
        return  ID;
    }

    public NoteData getNote(long id) {
        // select * from table where id = 1
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE, new String[]{KEY_ID,KEY_TITLE,KEY_CONTENT,KEY_DATE,KEY_TIME}, KEY_ID+"=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        // saves id title details date and time
        NoteData note = new NoteData(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4));

        return new NoteData(
                Long.parseLong(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4));
    }

    // saves all notes
    public List<NoteData> getNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<NoteData> allNotes = new ArrayList<>();
        // select * from db
        String query = "SELECT * FROM " + DATABASE_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            do {
                NoteData note = new NoteData();
                note.setId(cursor.getLong(0));
                note.setTitle(cursor.getString(1));
                note.setContent(cursor.getString(2));
                note.setDate(cursor.getString(3));
                note.setTime(cursor.getString(4));

                allNotes.add(note);
            } while (cursor.moveToNext());
        }

        return allNotes;
    }
}
