package com.example.taskit.model;

import java.util.Arrays;

public class Task {
    private int taskID;
    private String name;
    private String dueDate;
    private String notes;
    private String[] subTasks;
    private int subTasksCompleted;
    private int numSubTasks;
    private boolean[] itemsChecked;
    private int days_before;


    public Task(int id, String title, String[] milestones, String date, String note, int noOfSubTasks, int completed, boolean[] itemsCompleted, int days){
        taskID = id;
        name = title;
        subTasks = Arrays.copyOf(milestones, milestones.length);
        dueDate = date;
        notes = note;
        numSubTasks = noOfSubTasks;
        subTasksCompleted = completed;
        itemsChecked = Arrays.copyOf(itemsCompleted, itemsCompleted.length);
        days_before = days;

    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String[] getSubTasks() {
        return subTasks;
    }


    public void setSubTasks(String[] subTasks) {
        this.subTasks = subTasks;
    }

    public int getSubTasksCompleted() {
        return subTasksCompleted;
    }

    public int getNumSubTasks() {
        return numSubTasks;
    }

    public void setNumSubTasks(int numSubTasks) {
        this.numSubTasks = numSubTasks;
    }

    public void setSubTasksCompleted(int subTasksCompleted) {
        this.subTasksCompleted = subTasksCompleted;
    }

    public boolean[] getItemsChecked() {
        return itemsChecked;
    }

    public void setItemsChecked(boolean[] itemsChecked) {
        this.itemsChecked = itemsChecked;
    }

    public int getDays_before() {
        return days_before;
    }

    public void setDays_before(int days_before) {
        this.days_before = days_before;
    }

}
