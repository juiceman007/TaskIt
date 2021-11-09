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
import android.database.Cursor;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class EditTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, RecyclerViewClickInterface {


    int numMilestones, completedMilestones, broadID;
    private TaskDbHelper mHelper;
    private ArrayList<String> arrayList;
    private RecyclerView recyclerView;
    private MilestoneAdapter milestoneAdapter;
    Context context = this;
    String uploadDate, message, reminderTitle, reminderDescription;
    Calendar calendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        Intent intent = getIntent();
        message = intent.getStringExtra("message"); //get id of the task selected for editing

        mHelper = new TaskDbHelper(this);

        //construct a query to get record from the database searching for it by id
        String query = "Select * from " + TaskContract.TaskEntry.TABLE + " where " + TaskContract.TaskEntry._ID + " = '" + message + "'";

        SQLiteDatabase db = mHelper.getReadableDatabase();
        final Cursor c = db.rawQuery(query, null); //cursor to traverse database fields


        if (c.moveToFirst()) { //get the information of task from database
            int id = c.getColumnIndex(TaskContract.TaskEntry._ID);  //index of id
            int title = c.getColumnIndex(TaskContract.TaskEntry.COL_TASK_NAME); //index of title
            int subTasks = c.getColumnIndex(TaskContract.TaskEntry.COL_SUB_TASKS); //index of string containing milestones
            final int datePick = c.getColumnIndex(TaskContract.TaskEntry.COL_DATE); //index of date
            int notes = c.getColumnIndex(TaskContract.TaskEntry.COL_NOTES); //index of notes
            int numSubTasks = c.getColumnIndex(TaskContract.TaskEntry.NUM_MILESTONES); //index of number of milestones
            int completed = c.getColumnIndex(TaskContract.TaskEntry.COMPLETED_MILESTONES); //index of number of milestones completed
            int checks = c.getColumnIndex(TaskContract.TaskEntry.ITEMS_CHECKED);
            int days = c.getColumnIndex(TaskContract.TaskEntry.DAYS_BEFORE);

            numMilestones = c.getInt(numSubTasks);
            completedMilestones = 0;
            uploadDate = c.getString(datePick);

            final EditText taskName = (EditText) findViewById(R.id.editTaskName);
            taskName.setText(c.getString(title));

            recyclerView = (RecyclerView) findViewById(R.id.editSubTaskList);



            broadID = c.getInt(id);



            Date date = null;
            try {
                date = stringToDate(c.getString(datePick), "dd/M/yyyy kk:mm:ss");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            calendar.setTime(date);

            String dayNum = (String) android.text.format.DateFormat.format("dd", date); //extract date
            String month = (String) android.text.format.DateFormat.format("MMMM", date); //extract month
            String year = (String) android.text.format.DateFormat.format("yyyy", date); //extract year

            String deadlineDateStr = month + " " + dayNum + ", " + year; //format date, month, year to display

            String hr = (String) android.text.format.DateFormat.format("kk", date); //extract hour
            int hour = Integer.parseInt(hr);
            String min = (String) android.text.format.DateFormat.format("mm", date); //extract minute
            int minutes = Integer.parseInt(min);

            String minF;

            if(minutes < 10)
                minF = "0" + minutes;
            else
                minF = min;

            String deadlineTimeStr; //format the hour and minute for display
            if(hour == 0) {
                deadlineTimeStr = "12 : " + minF + " a.m.";
            }
            else if(hour < 12){
                deadlineTimeStr = hour + " : " + minF + " a.m.";
            }
            else {
                if(hour > 12)
                    hour = hour - 12;

                deadlineTimeStr = hour + " : " + minF + " p.m.";
            }


            TextView editDeadline = (TextView) findViewById(R.id.editDeadline);
            editDeadline.setText(deadlineDateStr); //display date from database in date section
            TextView editDeadlineTime = (TextView) findViewById(R.id.editDeadlineTime);
            editDeadlineTime.setText(deadlineTimeStr); //display time from database in time section

            EditText editNotes = (EditText) findViewById(R.id.editNotesTextArea);
            editNotes.setText(c.getString(notes)); //display notes from database in notes section


            String[] items; //string array to hold milestones form database

            if(c.getInt(numSubTasks) == 0) {
                items = new String[]{}; //if no milestones were found then initialize array as empty
            }
            else{
                items = stringToArray(c.getString(subTasks)); //else take milestones from database
            }
            arrayList = new ArrayList<>(Arrays.asList(items)); //create arrayList from string array




            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            milestoneAdapter = new MilestoneAdapter(arrayList, this, this);
            recyclerView.setAdapter(milestoneAdapter); //set recyclerView to display contents of arrayList



            Button dateButton = (Button) findViewById(R.id.editDate);
            dateButton.setText(R.string.editDeadlineTime);
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment datePicker = new DatePickerFragment();
                    datePicker.show(getSupportFragmentManager(), "date picker");
                }
            });

            Button timeButton = (Button) findViewById(R.id.editTime);
            timeButton.setText(R.string.editDeadline);
            timeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment timePicker = new timePickerFragment();
                    timePicker.show(getSupportFragmentManager(), "time picker");
                }
            });

            final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.editDaysPicker);
            final TextView textDays = (TextView) findViewById(R.id.editDaysBefore);

            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(365);
            numberPicker.setValue(c.getInt(days));

            if(c.getInt(days) == 1)
                textDays.setText("day before this task is due");
            else
                textDays.setText("days before this task is due");

            numberPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
                @Override
                public void onScrollStateChange(NumberPicker view, int scrollState) {
                    if(numberPicker.getValue() != 1)
                        textDays.setText("days before this task is due");
                    else
                        textDays.setText("day before this task is due");
                }
            });


            Button addSubTaskBtn = (Button) findViewById(R.id.editSubTaskBtn);
            addSubTaskBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditTask.this);
                    builder.setTitle("Add an objective");
                    final EditText input = new EditText(EditTask.this);
                    builder.setView(input);

                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(!input.getText().toString().equals(" ")) {
                                String newSubTask = input.getText().toString();
                                arrayList.add(newSubTask);
                                milestoneAdapter.notifyDataSetChanged();
                                numMilestones++;
                            }
                        }
                    });

                    builder.setNegativeButton("Cancel", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });


            Button addTaskButton = (Button) findViewById(R.id.editTaskButton);
            addTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //Delete original notification for this task
                  //  Intent intent = new Intent(context, ReminderBroadcast.class);
                   // PendingIntent pendingIntent = PendingIntent.getBroadcast(context, broadID , intent, PendingIntent.FLAG_CANCEL_CURRENT);
                   // AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                   // assert alarm != null;
                   // alarm.cancel(pendingIntent);



                    SimpleDateFormat df = new SimpleDateFormat("dd/M/yyyy kk:mm:ss");
                    uploadDate = df.format(calendar.getTime());

                    TextView date = (TextView) findViewById(R.id.editDeadline);
                    String addDate = uploadDate;

                    EditText notes = (EditText) findViewById(R.id.editNotesTextArea);


                    String addNotes = notes.getText().toString();

                    TextView time = (TextView) findViewById(R.id.editDeadlineTime);
                    String addTime = time.getText().toString();





                    if (taskName.getText().toString().isEmpty()) {
                        Toast.makeText(context, "Task Needs to have a name", Toast.LENGTH_LONG).show();
                    } else if (date.getText().toString().isEmpty()) {
                        Toast.makeText(context, "Need to set Date", Toast.LENGTH_LONG).show();
                    } else if (time.getText().toString().isEmpty()) {
                        Toast.makeText(context, "Need to set Time", Toast.LENGTH_LONG).show();
                    } else {

                        String addTaskTitle = String.valueOf(taskName.getText());

                        reminderTitle = addTaskTitle;
                        reminderDescription = "This task is due tomorrow at " + addTime;


                        StringBuilder addCheckBoxes = new StringBuilder();
                        StringBuilder list = new StringBuilder();
                        for (int i = 0; i < arrayList.size(); i++) {

                            list.append(arrayList.get(i));
                            list.append(",");
                            addCheckBoxes.append("false");
                            addCheckBoxes.append(",");
                        }
                        String addSubTasks = list.toString();
                        String checkBoxes = addCheckBoxes.toString();
                        int addNumMilestones = numMilestones;
                        int milestonesCompleted = completedMilestones;

                        SQLiteDatabase db = mHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();

                        int setDaysBefore = numberPicker.getValue();


                        values.put(TaskContract.TaskEntry.COL_TASK_NAME, addTaskTitle);
                        values.put(TaskContract.TaskEntry.COL_SUB_TASKS, addSubTasks);
                        values.put(TaskContract.TaskEntry.COL_DATE, addDate);
                        values.put(TaskContract.TaskEntry.COL_NOTES, addNotes);
                        values.put(TaskContract.TaskEntry.NUM_MILESTONES, addNumMilestones);
                        values.put(TaskContract.TaskEntry.COMPLETED_MILESTONES, milestonesCompleted);
                        values.put(TaskContract.TaskEntry.ITEMS_CHECKED, checkBoxes);
                        values.put(TaskContract.TaskEntry.DAYS_BEFORE, setDaysBefore);
                        String where = TaskContract.TaskEntry._ID + " = ?";
                        db.update(TaskContract.TaskEntry.TABLE, values, where, new String[]{message});
                        db.close();



                       //create new notification for edited task
                        Intent intent1 = new Intent(EditTask.this, ReminderBroadcast.class);
                        intent1.putExtra("title", addTaskTitle);
                        intent1.putExtra("description", reminderDescription);
                        PendingIntent newPendingIntent = PendingIntent.getBroadcast(EditTask.this, broadID, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                        long timeAtButtonCLick = System.currentTimeMillis();

                        long notifyTime = calendar.getTimeInMillis();
                        notifyTime = notifyTime - (1000 * 60 * 60 * 24 * setDaysBefore);

                        assert alarmManager != null;

                        alarmManager.set(AlarmManager.RTC_WAKEUP, notifyTime, newPendingIntent);

                        startActivity(new Intent(EditTask.this, MainActivity.class));
                    }
                }

            });
        }
        c.close();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

        calendar.set(Calendar.YEAR, i);
        calendar.set(Calendar.MONTH, i1);
        calendar.set(Calendar.DAY_OF_MONTH, i2);

        String currentDateString = DateFormat.getDateInstance().format(calendar.getTime());



        TextView textView = (TextView) findViewById(R.id.editDeadline);
        textView.setText(currentDateString);


    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        TextView textView = (TextView) findViewById(R.id.editDeadlineTime);
        int hour = i;
        int min = i1;

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        String time;

        String minF;
        //format minutes
        if (min < 10)
            minF = "0" + min;
        else {
            minF = Integer.toString(min);
        }

        //format hours
        if (hour >= 12) {

            if (hour == 12)
                time = hour + ":" + minF + " p.m.";
            else {
                hour = hour - 12;
                time = hour + ":" + minF + " p.m.";
            }
            textView.setText(time);
        } else {
            if (hour == 0)
                hour = 12;
            time = hour + ":" + minF + " a.m.";
            textView.setText(time);//display formatted time string
        }
    }


    public void goToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public String[] stringToArray(String s){
        return s.split(",");
    }


    @Override
    public void onItemClick(int position) {
        final int delPosition = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(EditTask.this);
        builder.setTitle("Remove objective");
        builder.setMessage("Do you want remove this objective?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        arrayList.remove(delPosition);
                        milestoneAdapter.notifyDataSetChanged();
                        Toast.makeText(EditTask.this, "Objective deleted", Toast.LENGTH_LONG).show();
                        numMilestones--;
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

    private Date stringToDate(String date, String format) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.parse(date);
    }

}
