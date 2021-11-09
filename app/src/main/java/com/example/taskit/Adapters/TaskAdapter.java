package com.example.taskit.Adapters;

import android.app.AlarmManager;
import android.app.AlertDialog;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import com.example.taskit.Activities.EditTask;
import com.example.taskit.Activities.MainActivity;
import com.example.taskit.R;
import com.example.taskit.Service.ReminderBroadcast;
import com.example.taskit.db.TaskContract;
import com.example.taskit.db.TaskDbHelper;
import com.example.taskit.model.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class TaskAdapter extends BaseAdapter {
    Context context;
    ArrayList<Task> tasks;
    private int checked, total, progress;
    private TaskDbHelper mHelper;
    private static LayoutInflater inflater = null;

    public TaskAdapter( Context context, ArrayList<Task> tasks) {

        this.context = context;
        this.tasks = tasks;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = view;
        itemView = (itemView == null) ? inflater.inflate(R.layout.item_todo, null): itemView;
        TextView title = (TextView) itemView.findViewById(R.id.task_title);
        final TextView milestones = (TextView) itemView.findViewById(R.id.milestone_count);

        final ProgressBar pb = (ProgressBar) itemView.findViewById(R.id.progressBar);

        final Task selectedTask = tasks.get(i);

        mHelper = new TaskDbHelper(context);

        title.setText(selectedTask.getName());

        //getting date
        TextView date = (TextView) itemView.findViewById(R.id.dateText);

        ImageButton editBtn = (ImageButton) itemView.findViewById(R.id.edit_button);
        ImageButton deleteBtn = (ImageButton) itemView.findViewById(R.id.delete_button);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int task = selectedTask.getTaskID();
                String id = Integer.toString(task);

                Intent intent = new Intent(context, EditTask.class);
                intent.putExtra("message", id);
                context.startActivity(intent);

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Are you sure you want to delete this task?";
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
                deleteDialog.setTitle(selectedTask.getName());
                deleteDialog.setMessage(message);
                deleteDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TaskDbHelper mHelper = new TaskDbHelper(context);
                        SQLiteDatabase db = mHelper.getReadableDatabase();
                        int broadID = selectedTask.getTaskID();
                        String id = Integer.toString(selectedTask.getTaskID());
                        String where = TaskContract.TaskEntry._ID + " = ?";
                        String deleteMsg = selectedTask.getName() + " successfully deleted";
                        Toast.makeText(context,deleteMsg,Toast.LENGTH_SHORT).show();
                        db.delete(TaskContract.TaskEntry.TABLE, where, new String[] {id});



                        //Delete notification for task when task is deleted
                        Intent intent = new Intent(context, ReminderBroadcast.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, broadID, intent, 0);
                        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        assert alarm != null;
                        alarm.cancel(pendingIntent);


                        //restart main activity to call updateUI
                        context.startActivity(new Intent(context, MainActivity.class));
                    }
                });

                deleteDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int id)
                    {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = deleteDialog.create(); //create alert dialog
                alertDialog.show(); //make dialog visible
            }
        });

            String dueDate = selectedTask.getDueDate();
        Date endDate = null;
        try {
            endDate = stringToDate(dueDate, "dd/M/yyyy kk:mm:ss");
            System.out.println(endDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        //test that string is changed

//-------------------------------------------------------------------------------------------------------------------------------------------

        if(endDate != null) { //get number of days left
            Date today = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd/M/yyyy kk:mm:ss");
            String formatted = df.format(today);
            Date todayFormatted = null;
            try {
                todayFormatted = stringToDate(formatted, "dd/M/yyyy kk:mm:ss");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert todayFormatted != null;

            long differenceInMilli = endDate.getTime() - todayFormatted.getTime();
            System.out.println(differenceInMilli);

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = differenceInMilli / daysInMilli ; //number of days left
            differenceInMilli = differenceInMilli % daysInMilli;

            long elapsedHours = differenceInMilli / hoursInMilli; //number of hours left
            differenceInMilli = differenceInMilli % hoursInMilli;

            long elapsedMinutes = differenceInMilli / minutesInMilli; //number of minutes left
            //differenceInMilli = differenceInMilli % minutesInMilli;

            //long elapsedSeconds = differenceInMilli /secondsInMilli;


            String dayString; //string to format Days for display
            String hourString; //string to format Hours for display
            String minString; //string to format Minutes for display
            String dateText; //string to hold text for display of time left

            if(elapsedDays <= 1 )
                dayString = "Day";
            else
                dayString = "Days";

            if(elapsedHours == 1 )
                hourString = "Hr";
            else
                hourString = "Hrs";

            if(elapsedMinutes == 1)
                minString = "Min";
            else minString = "Mins";


            if(elapsedHours == 0) {
                dateText = "Due in " + elapsedDays + " " + dayString + " " + elapsedMinutes + " " + minString;
            }
            else if(elapsedMinutes == 0){
                dateText = "Due in " + elapsedDays + " " + dayString + " " + elapsedHours + " " + hourString ;
            }
            else {
                dateText = "Due in " + elapsedDays + " " + dayString + " " + elapsedHours + " " + hourString + " " + elapsedMinutes + " " + minString;
            }
            date.setText(dateText); //display time left



            if(elapsedDays >= 7){
                date.setTextColor(Color.rgb(34,139,34));
            }
            if((elapsedDays < 7) && (elapsedDays >= 2)){
                date.setTextColor(Color.rgb(255,140,0));
            }
            if(elapsedDays < 2){
                date.setTextColor(Color.RED);
            }
            if(elapsedDays == 0){
                if(elapsedHours == 0){
                    if(elapsedMinutes < 0)
                        date.setText("Overdue by " + elapsedMinutes*-1 + " mins" );
                    else
                    date.setText("Due Today  in " + elapsedMinutes + " mins" );
                }
                else if(elapsedHours < 0){
                    if(elapsedMinutes < 0)
                        date.setText("Overdue by " + elapsedHours*-1 + " Hrs " + elapsedMinutes*-1 + " mins" );
                    else
                        date.setText("Overdue by " + elapsedHours*-1 + " Hrs" );
                }
                    else
                    date.setText("Due Today  in " + elapsedHours + " Hrs and " + elapsedMinutes + " mins" );

                date.setTextColor(Color.RED);
//-------------------------------------------------------------------------------------------------------------------------------------------------


   //-----------------------------------------------------------------------------------------------------------------------------------------------
            }
            if(elapsedDays < 0){
                long overdueDays = elapsedDays * -1; //change to positive number

                String overdueMessage;

                if(overdueDays == 1){
                    overdueMessage = "This task is Overdue by " + overdueDays + " day";
                }
                else{
                    overdueMessage = "This task is Overdue by " + overdueDays + " days";
                }

                date.setText(overdueMessage);
                date.setTextColor(Color.RED);
//-------------------------------------------------------------------------------------------------------------------------------------------------

   //--------------------------------------------------------------------------------------------------------------------------------------------

            }
        }


        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                builder.setTitle("Notes");
                final TextView notes = new TextView(context);
                builder.setMessage(selectedTask.getNotes());

                builder.setView(notes);


                builder.setNegativeButton("Done", null);
                androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });


        if(selectedTask.getNumSubTasks() > 0) {


              final String[] items = selectedTask.getSubTasks().clone();

             final boolean[] itemsChecked = selectedTask.getItemsChecked().clone();


            checked = selectedTask.getSubTasksCompleted();
             total = selectedTask.getNumSubTasks();
             progress = (checked * 100) / total;

            String s = selectedTask.getSubTasksCompleted() + "/" + selectedTask.getNumSubTasks() + " objectives completed (" + progress + "%)";
            milestones.setText(s);

            pb.setMax(100);
            pb.setProgress(progress, true);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(selectedTask.getName());
                    builder.setMultiChoiceItems(items, itemsChecked, new DialogInterface.OnMultiChoiceClickListener() {
                       @Override
                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {

                           AlertDialog dialog = ((AlertDialog) dialogInterface);

                           if(b){
                               itemsChecked[i] = true;
                           }
                           else{
                               itemsChecked[i] = false;
                           }
                                checked = dialog.getListView().getCheckedItemCount();
                                progress = (checked * 100) / total;
                                System.out.println(progress);
                                pb.setProgress(progress, true);
                           SQLiteDatabase db = mHelper.getWritableDatabase();
                           ContentValues values = new ContentValues();

                           String s = boolToString(itemsChecked);
                           values.put(TaskContract.TaskEntry.ITEMS_CHECKED, s);
                           values.put(TaskContract.TaskEntry.COMPLETED_MILESTONES, checked);

                           String id = Integer.toString(selectedTask.getTaskID());
                           String where = TaskContract.TaskEntry._ID + " = ?";
                           db.update(TaskContract.TaskEntry.TABLE, values, where, new String[] {id});

                            db.close();
                           String newText = checked + "/" + total + " objectives completed (" + progress + "%)";
                           milestones.setText(newText);
                        }
                    });
                    builder.setNegativeButton("Done", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });
        }
        else
        {
            milestones.setText(R.string.no_milestones);
            milestones.setTextSize(18);
        }

        return itemView;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int i) {
        return tasks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private Date stringToDate(String date, String format) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.parse(date);
    }

    private String boolToString(boolean[] b){  //change boolean array to string
        StringBuilder s = new StringBuilder();

        for(int k=0; k < b.length; k++){
            s.append(b[k]);
            s.append(",");
        }
        return s.toString();
    }

    }
