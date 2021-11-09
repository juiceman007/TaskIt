package com.example.taskit.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.example.taskit.Adapters.TaskAdapter;

import com.example.taskit.R;
import com.example.taskit.db.TaskContract;
import com.example.taskit.db.TaskDbHelper;
import com.example.taskit.model.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Context context = this;
    TaskAdapter adapter;

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplication().setTheme(R.style.AppTheme_dark_blue);
        setContentView(R.layout.activity_main);

            updateUI();
    }

    private void updateUI()  {

        TextView noTasks = (TextView) findViewById(R.id.noTask);

        ListView listViewTasks = (ListView) findViewById(R.id.listOfTasks);
        ArrayList<Task> taskList = new ArrayList<Task>();
        TaskDbHelper mHelper = new TaskDbHelper(this);
        SQLiteDatabase db = mHelper.getReadableDatabase();
        final Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_NAME, TaskContract.TaskEntry.COL_SUB_TASKS,
                        TaskContract.TaskEntry.COL_DATE, TaskContract.TaskEntry.COL_NOTES, TaskContract.TaskEntry.NUM_MILESTONES,
                        TaskContract.TaskEntry.COMPLETED_MILESTONES, TaskContract.TaskEntry.ITEMS_CHECKED, TaskContract.TaskEntry.DAYS_BEFORE}, null, null, null, null, null);

        while(cursor.moveToNext()) {
            int id = cursor.getColumnIndex(TaskContract.TaskEntry._ID);
            int title = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_NAME);
            int subTasks = cursor.getColumnIndex(TaskContract.TaskEntry.COL_SUB_TASKS);
            int date = cursor.getColumnIndex(TaskContract.TaskEntry.COL_DATE);
            int notes = cursor.getColumnIndex(TaskContract.TaskEntry.COL_NOTES);
            int numSubTasks = cursor.getColumnIndex(TaskContract.TaskEntry.NUM_MILESTONES);
            int completed = cursor.getColumnIndex(TaskContract.TaskEntry.COMPLETED_MILESTONES);
            int checks = cursor.getColumnIndex(TaskContract.TaskEntry.ITEMS_CHECKED);
            int days = cursor.getColumnIndex(TaskContract.TaskEntry.DAYS_BEFORE);

            String[] listOfSubTasks = stringToArray(cursor.getString(subTasks)).clone();
            boolean[] itemCheckBoxes = stringToBoolean(cursor.getString(checks)).clone();

            Task newTask = new Task(cursor.getInt(id), cursor.getString(title), listOfSubTasks, cursor.getString(date),
                    cursor.getString(notes), cursor.getInt(numSubTasks), cursor.getInt(completed), itemCheckBoxes, cursor.getInt(days));

            taskList.add(newTask);


        }

        if(taskList.size() == 0){
            noTasks.setText(R.string.NoTasks);
        }
        else{
            noTasks.setText("Hint: Tap on task to see objectives, hold task to see notes.");
            noTasks.setTextSize(18);
        }

         adapter = new TaskAdapter(this, taskList);
        listViewTasks.setAdapter(adapter);

        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Milestones:");


            }
        });

        cursor.close();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_add_task:
                Log.d(TAG, "Add a new task");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void goToAddTask(View view) {
        Intent intent = new Intent(this, addTask.class);
        startActivity(intent);
    }

    public String[] stringToArray(String s){
        return s.split(",");
    }

    public boolean[] stringToBoolean(String b){
        String[] values = b.split(",");
        boolean[] intermediate = new boolean[values.length];
        for (int j = 0; j < values.length; j++){
            if(values[j].equals("false")) {
                intermediate[j] = false;
            }
            else{
                intermediate[j] = true;
            }
        }
        return intermediate;
    }

    private Date stringToDate(String date, String format) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.parse(date);
    }
}
