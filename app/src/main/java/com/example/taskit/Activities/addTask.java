package com.example.taskit.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.taskit.Adapters.MilestoneAdapter;
import com.example.taskit.Fragments.DatePickerFragment;
import com.example.taskit.Fragments.timePickerFragment;
import com.example.taskit.Interfaces.RecyclerViewClickInterface;
import com.example.taskit.R;
import com.example.taskit.Service.ReminderBroadcast;
import com.example.taskit.db.TaskContract;
import com.example.taskit.db.TaskDbHelper;

import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static com.example.taskit.App.PREFS_NAME;
import static com.example.taskit.App.RQST_CODE;

public class addTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, RecyclerViewClickInterface {

    Context context = this;

    int numMilestones = 0;
    int completedMilestones = 0;
    private TaskDbHelper mHelper;
    private RecyclerView recyclerView;
    private MilestoneAdapter milestoneAdapter;
    private ArrayList<String> arrayList;

    Calendar calendar = Calendar.getInstance();

    String uploadDate, reminderTitle, reminderDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);


        mHelper = new TaskDbHelper(this);

        final EditText taskName = (EditText) findViewById(R.id.taskName);


        recyclerView =(RecyclerView) findViewById(R.id.subTaskList);


        String[] items = {}; //create array of strings to hold milestones
        arrayList = new ArrayList<>(Arrays.asList(items)); //initialise arrayList using array of Strings



        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //set recycler view layout manager
        milestoneAdapter = new MilestoneAdapter(arrayList, this, this);
        recyclerView.setAdapter(milestoneAdapter);


        Button dateButton = (Button) findViewById(R.id.setDate);  //date picker button
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    //choose date
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        Button timeButton = (Button) findViewById(R.id.setTime); //time picker button
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  //choose time
                DialogFragment timePicker = new timePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });


        final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.daysPicker);
        final TextView textDays = (TextView) findViewById(R.id.daysBefore);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(365);
        numberPicker.setValue(1);
        numberPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                if(numberPicker.getValue() != 1)
                    textDays.setText("days before this task is due");
                else
                    textDays.setText("day before this task is due");
            }
        });

        Button addSubTaskBtn = (Button) findViewById(R.id.addSubTaskBtn);  //button to add milestone
        addSubTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(addTask.this);
                builder.setTitle("Add an objective");
                final EditText input = new EditText(addTask.this);
                builder.setView(input);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {

                            String newSubTask = input.getText().toString();
                            arrayList.add(newSubTask); //add new milestone to list
                            milestoneAdapter.notifyDataSetChanged(); //update display
                            numMilestones++; //increment number of milestones

                    }
                });

                builder.setNegativeButton("Cancel", null); //cancel button
                AlertDialog  alertDialog = builder.create(); //create alert dialog
                alertDialog.show(); //make it visible
            }
        });


        Button addTaskButton = (Button) findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                SimpleDateFormat df = new SimpleDateFormat("dd/M/yyyy kk:mm:ss"); //variable for formatting date and time
                uploadDate = df.format(calendar.getTime());    //get formatted date and time

                TextView date = (TextView) findViewById(R.id.deadline);  //get date and time set
                String addDate = uploadDate;

                EditText notes = (EditText) findViewById(R.id.notesTextArea);  //get notes
                String addNotes = String.valueOf(notes.getText().toString());

                TextView time = (TextView) findViewById(R.id.deadlineTime); //get time only
                String addTime = String.valueOf(time.getText().toString());

                if(taskName.getText().toString().isEmpty()){
                    Toast.makeText(context, "Task Needs to have a name", Toast.LENGTH_LONG).show();  //check that title is set
                }
                else if(date.getText().toString().isEmpty()){
                    Toast.makeText(context, "Need to set Date", Toast.LENGTH_LONG).show(); //check that deadline date is set
                }
                else if(time.getText().toString().isEmpty()){
                    Toast.makeText(context, "Need to set Time", Toast.LENGTH_LONG).show(); //check that deadline time is set
                }
                else {  //if all necessary fields are set then proceed to add task

                    String addTaskTitle = String.valueOf(taskName.getText()); //get title

                    StringBuilder addCheckBoxes = new StringBuilder(); //get boolean values for checkboxes
                    StringBuilder list = new StringBuilder(); //get list of milestones

                    for (int i = 0; i < arrayList.size(); i++) {  //for each string in array list

                        list.append(arrayList.get(i)); //add each string in arrayList to string builder
                        list.append(","); //separator
                        addCheckBoxes.append("false"); //add an initial 'false' value for each string added
                        addCheckBoxes.append(","); //separator
                    }

                    String addSubTasks = list.toString(); //final string to hold milestones
                    String checkBoxes = addCheckBoxes.toString(); //final string to hold checkbox boolean values
                    int addNumMilestones = numMilestones; //number of milestones
                    int milestonesCompleted = completedMilestones; // number of milestones completed




                    //get saved request id
                    SharedPreferences myPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    int saved = myPrefs.getInt("requestNo", 0);

                    if((RQST_CODE == 1) && (saved != 0) ) //if request code was not reset
                        RQST_CODE = saved; //set request code to saved number


                    int setReminderDays = numberPicker.getValue();

                    reminderTitle = addTaskTitle; //take name of task as title for reminder

                    if(setReminderDays == 1)
                    reminderDescription = "This task is due in tomorrow at "+ addTime; //take time of day as message for reminder
                    else
                        reminderDescription = "This task is due in " + setReminderDays + " days at "+ addTime;

                    SQLiteDatabase db = mHelper.getWritableDatabase(); //get database
                    ContentValues values = new ContentValues(); //variable to hold values fro inserting to database


                    //add all values to variable
                    values.put(TaskContract.TaskEntry.COL_TASK_NAME, addTaskTitle);
                    values.put(TaskContract.TaskEntry.COL_SUB_TASKS, addSubTasks);
                    values.put(TaskContract.TaskEntry.COL_DATE, addDate);
                    values.put(TaskContract.TaskEntry.COL_NOTES, addNotes);
                    values.put(TaskContract.TaskEntry.NUM_MILESTONES, addNumMilestones);
                    values.put(TaskContract.TaskEntry.COMPLETED_MILESTONES, milestonesCompleted);
                    values.put(TaskContract.TaskEntry.ITEMS_CHECKED, checkBoxes);
                    values.put(TaskContract.TaskEntry.DAYS_BEFORE, setReminderDays);
                    db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                            null,
                            values,
                    SQLiteDatabase.CONFLICT_REPLACE); //insert all values into database as record
                    db.close(); //close database


                    //Create notification reminder
                    Intent intent = new Intent(addTask.this, ReminderBroadcast.class);
                    intent.putExtra("title", reminderTitle); //send title to broadcast receiver
                    intent.putExtra("description", reminderDescription); //send description to broadcast receiver

                    //create pending intent with unique request code to match id of task
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(addTask.this, RQST_CODE, intent, 0);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE); //get alarm manager service



                    long notifyTime = calendar.getTimeInMillis(); //get date and time set in milliseconds
                    notifyTime = notifyTime - (1000 * 60 * 60 * 24 * setReminderDays); //subtract one day to ensure notification is set for the day before task is due

                    assert alarmManager != null;

                    alarmManager.set(AlarmManager.RTC_WAKEUP, notifyTime, pendingIntent); //set reminder for one day before task is day

                    RQST_CODE++; //update request code to match id of next task added

                  //save updated request code in shared preferences for the next time the app is opened
                    SharedPreferences.Editor editor = myPrefs.edit();
                    editor.putInt("requestNo", RQST_CODE);
                    editor.apply();
                    editor.commit();

                    //go back to main activity
                    startActivity(new Intent(addTask.this, MainActivity.class));
                }
            }

        });
    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

        calendar.set(Calendar.YEAR, i); //set year
        calendar.set(Calendar.MONTH, i1); //set month
        calendar.set(Calendar.DAY_OF_MONTH, i2); //set day

        String currentDateString = DateFormat.getDateInstance().format(calendar.getTime()); //create formatted string of year, month, day



        TextView textView = (TextView) findViewById(R.id.deadline);
        textView.setText(currentDateString); //display string in textView


    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        TextView textView = (TextView) findViewById(R.id.deadlineTime);

        int hour = i;
        int min = i1;

        calendar.set(Calendar.HOUR_OF_DAY, i); //set hour
        calendar.set(Calendar.MINUTE, i1); //set minute

        String time;
        String minF;

        //format minutes
        if(min < 10)
            minF = "0" + min;
        else{
            minF = Integer.toString(min);
        }

        //format hours
        if(hour >= 12){

            if (hour == 12)
            time = hour + ":" + minF + " p.m.";
            else {
                hour = hour - 12;
                time = hour + ":" + minF + " p.m.";
            }
            textView.setText(time);
        }
        else
        {
            if(hour == 0)
                hour = 12;
            time = hour + ":" + minF + " a.m.";
            textView.setText(time);//display formatted time string
        }

    }


    public void goToMain(View view) { //onClick for returning to mainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position) {  //onClick listener for RecyclerView for deleting milestones
        final int delPosition = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(addTask.this);
        builder.setTitle("Remove objective");
        builder.setMessage("Do you want remove this objective?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        arrayList.remove(delPosition); //remove milestone from arrayList
                        milestoneAdapter.notifyDataSetChanged(); //update arrayList
                        Toast.makeText(addTask.this, "Objective deleted", Toast.LENGTH_LONG).show();
                        numMilestones--; // decrement number of milestones
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int id)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog  alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onLongItemClick(int position) {

    }

}


