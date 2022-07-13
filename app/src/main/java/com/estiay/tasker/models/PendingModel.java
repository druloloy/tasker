package com.estiay.tasker.models;

public class PendingModel {

    private String pending_id;
    private String pending_title;
    private String details;

    public PendingModel(String pending_title, String details) {
        this.pending_title = pending_title;
        this.details = details;
    }


    public String getPending_title() {
        return pending_title;
    }

    public void setPending_title(String pending_title) {
        this.pending_title = pending_title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}