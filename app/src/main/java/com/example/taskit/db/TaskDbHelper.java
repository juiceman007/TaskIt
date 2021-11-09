package com.example.taskit.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDbHelper extends SQLiteOpenHelper {

    public TaskDbHelper(Context context) {
        super(context, TaskContract.DB_NAME, null, TaskContract.DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TaskContract.TaskEntry.TABLE + " ( " +
                TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskContract.TaskEntry.COL_TASK_NAME + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.COL_SUB_TASKS + " TEXT , " +
                TaskContract.TaskEntry.COL_DATE + " TEXT , " +
                TaskContract.TaskEntry.COL_NOTES + " TEXT , " +
                TaskContract.TaskEntry.NUM_MILESTONES + " INTEGER, " +
                TaskContract.TaskEntry.COMPLETED_MILESTONES + " INTEGER , " +
                TaskContract.TaskEntry.ITEMS_CHECKED + " TEXT , " +
                TaskContract.TaskEntry.DAYS_BEFORE + " INTEGER ); ";


        sqLiteDatabase.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(i1 > i) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE + ";");
        }
           onCreate(sqLiteDatabase);
    }
}
