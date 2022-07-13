package com.estiay.tasker.models;

import java.util.Date;

public class NotificationModel {

    private int id;
    private String taskID;
    private String title;
    private String details;
    private Date notifyDate;

    public NotificationModel(int id, String taskID, String title, String details, Date notifyDate) {
        this.id = id;
        this.taskID = taskID;
        this.title = title;
        this.details = details;
        this.notifyDate = notifyDate;
    }
    public NotificationModel(int id, String taskID, String title, String details, long notifyDate) {
        this.id = id;
        this.taskID = taskID;
        this.title = title;
        this.details = details;
        this.notifyDate = new Date(notifyDate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Date getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(Date notifyDate) {
        this.notifyDate = notifyDate;
    }
}
