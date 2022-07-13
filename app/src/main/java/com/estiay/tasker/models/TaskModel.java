package com.estiay.tasker.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.estiay.tasker.exception.TaskerException;

import java.io.Serializable;
import java.util.Date;

public class TaskModel implements Serializable {
    private String id;
    private String title;
    private String details;
    private Date startDate;
    private Date endDate;
    private boolean isFinished = false;
    private Date createdDate;
    public static TaskModel instance;

    public TaskModel(String id, String title, String details, Date startDate, Date endDate, Date createdDate) throws TaskerException {
        Date now = new Date();
        this.id = id;
        this.title = title;
        this.details = details;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdDate = createdDate;

        if(startDate.after(endDate))
            throw new TaskerException("Start date must never be after the end date.");
        if(endDate.before(startDate))
            throw new TaskerException("Start date must never be after the end date.");

        if(now.after(endDate)) isFinished = true;

        if(title.length() == 0){
            throw new TaskerException("Empty title!");
        }


        if(title.length() > 25){
            throw new TaskerException("Title exceeded 25 characters");
        }
        if(details.length() > 80){
            throw new TaskerException("Details exceeded 80 characters");
        }
    }

    public TaskModel(String id, String title, String details, long startDate, long endDate, long createdDate) throws TaskerException {
        Date now = new Date();

        this.id = id;
        this.title = title;
        this.details = details;
        this.startDate = new Date(startDate);
        this.endDate = new Date(endDate);
        this.createdDate = new Date(createdDate);

        if(this.startDate.after(this.endDate))
            throw new TaskerException("Start date must never be after the end date.");
        if(this.endDate.before(this.startDate))
            throw new TaskerException("Start date must never be after the end date.");

        if(now.after(this.endDate)) isFinished = true;

        if(title.length() == 0){
            throw new TaskerException("Empty title!");
        }

        if(title.length() > 25){
            throw new TaskerException("Title exceeded 25 characters");
        }
        if(details.length() > 80){
            throw new TaskerException("Details exceeded 80 characters");
        }
    }
    public TaskModel(String title, String details, long startDate, long endDate) throws TaskerException {
        Date now = new Date();

        this.title = title;
        this.details = details;
        this.startDate = new Date(startDate);
        this.endDate = new Date(endDate);

        if(this.startDate.after(this.endDate))
            throw new TaskerException("Start date must never be after the end date.");
        if(this.endDate.before(this.startDate))
            throw new TaskerException("Start date must never be after the end date.");

        if(now.after(this.endDate)) isFinished = true;

        if(title.length() == 0){
            throw new TaskerException("Empty title!");
        }
        if(title.length() > 25){
            throw new TaskerException("Title exceeded 25 characters");
        }
        if(details.length() > 80){
            throw new TaskerException("Details exceeded 80 characters");
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) throws TaskerException {
        if(endDate != null && startDate.after(endDate))
            throw new TaskerException("Start date must never be after the end date.");

        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) throws TaskerException {
        if(startDate != null && endDate.before(startDate))
            throw new TaskerException("Start date must never be after the end date.");

        this.endDate = endDate;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Intent getTaskModelSerializable(Context context, Class<?> activity){
        return new Intent(context, activity).putExtra("Task", this);
    }
    public static TaskModel getPastActivityInstance(Activity activity){
        return (TaskModel) activity.getIntent().getSerializableExtra("Task");
    }

}
