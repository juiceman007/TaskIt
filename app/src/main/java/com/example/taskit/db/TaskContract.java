package com.example.taskit.db;

import android.provider.BaseColumns;

public class TaskContract {

    public static final String DB_NAME = "com.example.taskit.db";
    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        //table name
        public static final String TABLE = "tasks";

        //fields
        public static final String COL_TASK_NAME = "name";
        public static final String COL_SUB_TASKS = "milestones";
        public static final String COL_DATE = "due_date";
        public static final String COL_NOTES = "Notes";
        public static final String NUM_MILESTONES = "num_milestones";
        public static final String COMPLETED_MILESTONES = "milestones_completed";
        public static final String ITEMS_CHECKED = "items_checked";
        public static final String DAYS_BEFORE = "reminder";

    }
}
