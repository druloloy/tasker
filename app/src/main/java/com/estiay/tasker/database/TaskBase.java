package com.estiay.tasker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;
import java.util.Random;

public class TaskBase extends SQLiteOpenHelper {
    // CONSTANT VARIABLES
    public static int FINISHED = 1;
    public static int PENDING = 0;
    public static int ARCHIVE = 1;
    public static int ACTIVE = 0;
    // Database ame
    private final static String dbName = "Tasker.db";
    // Tables names
    private final static String taskTitles = "tasks_titles";
    private final static String taskDetails = "tasks_details";
    private final static String taskDates = "tasks_dates";
    private final static String taskStatus = "tasks_status";

    private final static String archivedTasks = "archived_tasks";

    private final static String notifications_title = "notifications_titles";
    private final static String notifications_details = "notifications_details";

    private final static String tasks_view = "tasks_view";

    public TaskBase(Context context) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // tasks table
        sqLiteDatabase.execSQL(String
                .format("CREATE TABLE %s (" +
                        "id TEXT NOT NULL UNIQUE," +
                        "title TEXT NOT NULL," +
                        "creationDate INTEGER NOT NULL," +
                        "PRIMARY KEY(id));", taskTitles));
        sqLiteDatabase.execSQL(String
                .format("CREATE TABLE %s (" +
                        "id TEXT UNIQUE NOT NULL, " +
                        "details TEXT NOT NULL," +
                        "CONSTRAINT FK_taskDetails " +
                        "FOREIGN KEY(id) " +
                        "REFERENCES %s(id) " +
                        ");", taskDetails, taskTitles));
        sqLiteDatabase.execSQL(String
                .format("CREATE TABLE %s (" +
                        "id TEXT UNIQUE NOT NULL," +
                        "startDate INTEGER NOT NULL," +
                        "endDate INTEGER NOT NULL," +
                        "CONSTRAINT FK_taskDates " +
                        "FOREIGN KEY (id) " +
                        "REFERENCES %s (id)" +
                        ");", taskDates, taskTitles));
        sqLiteDatabase.execSQL(String
                .format("CREATE TABLE %s (" +
                        "id TEXT UNIQUE NOT NULL, " +
                        "isFinished INTEGER NOT NULL," +
                        "CONSTRAINT FK_taskStatus " +
                        "FOREIGN KEY (id) " +
                        "REFERENCES %s (id)" +
                        ");", taskStatus, taskTitles));

        // archived tasks table
        sqLiteDatabase.execSQL(String
                .format("CREATE TABLE %s (" +
                        "id TEXT PRIMARY KEY NOT NULL UNIQUE," +
                        "title TEXT NOT NULL," +
                        "details TEXT NOT NULL," +
                        "startDate INTEGER NOT NULL," +
                        "endDate INTEGER NOT NULL," +
                        "isFinished INTEGER NOT NULL DEFAULT 1," +
                        "finishedDate TEXT NOT NULL" +
                        ");", archivedTasks));

        // notifications table
        sqLiteDatabase.execSQL(String
                .format("CREATE TABLE %s (" +
                        "id INTEGER PRIMARY KEY NOT NULL, " +
                        "taskID TEXT NOT NULL, " +
                        "title TEXT NOT NULL," +
                        "notifyDate INTEGER NOT NULL" +
                        ");", notifications_title));
        sqLiteDatabase.execSQL(String
                .format("CREATE TABLE %s (" +
                        "id INTEGER NOT NULL, " +
                        "details TEXT NOT NULL," +
                        "CONSTRAINT FK_notificationsTitle " +
                        "FOREIGN KEY (id) " +
                        "REFERENCES %s (id)" +
                        ");", notifications_details, notifications_title));

        sqLiteDatabase.execSQL("CREATE VIEW IF NOT EXISTS " +
                        "tasks_view AS SELECT " +
                        "tasks_titles.id, " +
                        "tasks_titles.title, " +
                        "tasks_details.details, " +
                        "tasks_dates.startDate, " +
                        "tasks_dates.endDate, " +
                        "tasks_status.isFinished, " +
                        "tasks_titles.creationDate " +
                        "FROM tasks_titles " +
                        "LEFT JOIN tasks_details " +
                        "ON tasks_titles.id = tasks_details.id " +
                        "LEFT JOIN tasks_dates " +
                        "ON tasks_titles.id = tasks_dates.id " +
                        "LEFT JOIN tasks_status " +
                        "ON tasks_titles.id = tasks_status.id");

        sqLiteDatabase.execSQL("CREATE VIEW IF NOT EXISTS " +
                                "notifications_view AS SELECT " +
                                "notifications_titles.id," +
                                "notifications_titles.taskId," +
                                "notifications_titles.title," +
                                "notifications_details.details," +
                                "notifications_titles.notifyDate " +
                                "FROM notifications_titles " +
                                "LEFT JOIN notifications_details " +
                                "ON notifications_titles.id = notifications_details.id");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // current tasks
        sqLiteDatabase.execSQL(String
                .format("DROP TABLE IF EXISTS %S", taskTitles));
        sqLiteDatabase.execSQL(String
                .format("DROP TABLE IF EXISTS %S", taskDetails));
        sqLiteDatabase.execSQL(String
                .format("DROP TABLE IF EXISTS %S", taskDates));
        sqLiteDatabase.execSQL(String
                .format("DROP TABLE IF EXISTS %S", taskStatus));
        // archived tasks
        sqLiteDatabase.execSQL(String
                .format("DROP TABLE IF EXISTS %S", archivedTasks));
        // notifications
        sqLiteDatabase.execSQL(String
                .format("DROP TABLE IF EXISTS %S", notifications_title));
        sqLiteDatabase.execSQL(String
                .format("DROP TABLE IF EXISTS %S", notifications_details));
    }

    // random id generator
    public static String generateID(int size){
        Random r = new Random();
        StringBuilder buffer = new StringBuilder();
        while(buffer.length() < size){
            buffer.append(Integer.toHexString(r.nextInt()));
        }

        return buffer.substring(0, size);
    }

    // add current task
    public boolean addTask(String title, String details, long startDate, long endDate){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String id = generateID(6); // generate id
        long creationDate = new Date().getTime();
        /*
         *
         * TODO: Initialize Content values for each table
         */


        ContentValues contentTitle = new ContentValues();
        // id
        contentTitle.put("id", id);
        // title
        contentTitle.put("title", title);
        // creation date
        contentTitle.put("creationDate", creationDate);


        ContentValues contentDetails = new ContentValues();
        // details
        contentDetails.put("id", id);
        contentDetails.put("details", details);


        ContentValues contentDate = new ContentValues();
        // id
        contentDate.put("id", id);
        // start date
        contentDate.put("startDate", startDate);
        // end date
        contentDate.put("endDate", endDate);


        ContentValues contentStatus = new ContentValues();
        // status
        contentStatus.put("id", id);
        contentStatus.put("isFinished", PENDING);

        /*
         * TODO: Insert to database according to their specific tables
         */

        String notifTitle = "Your task "+title+" is done";
        sqLiteDatabase.insert(taskTitles, null, contentTitle);
        sqLiteDatabase.insert(taskDetails, null, contentDetails);
        sqLiteDatabase.insert(taskDates, null, contentDate);
        sqLiteDatabase.insert(taskStatus, null, contentStatus);
        addNotification(id, notifTitle, "Check your upcoming tasks!", endDate);
        return true;
    }
    // add current task
    public boolean addTask(String id, String title, String details, long startDate, long endDate){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long creationDate = new Date().getTime();
        /*
         *
         * TODO: Initialize Content values for each table
         */


        ContentValues contentTitle = new ContentValues();
        // id
        contentTitle.put("id", id);
        // title
        contentTitle.put("title", title);
        // creation date
        contentTitle.put("creationDate", creationDate);


        ContentValues contentDetails = new ContentValues();
        // details
        contentDetails.put("id", id);
        contentDetails.put("details", details);


        ContentValues contentDate = new ContentValues();
        // id
        contentDate.put("id", id);
        // start date
        contentDate.put("startDate", startDate);
        // end date
        contentDate.put("endDate", endDate);


        ContentValues contentStatus = new ContentValues();
        // status
        contentStatus.put("id", id);
        contentStatus.put("isFinished", PENDING);

        /*
         * TODO: Insert to database according to their specific tables
         */

        String notifTitle = "Your task "+title+" is done";
        sqLiteDatabase.insert(taskTitles, null, contentTitle);
        sqLiteDatabase.insert(taskDetails, null, contentDetails);
        sqLiteDatabase.insert(taskDates, null, contentDate);
        sqLiteDatabase.insert(taskStatus, null, contentStatus);
        addNotification(id, notifTitle, "Go check your upcoming tasks!", endDate);
        return true;
    }

    // get task
    public Cursor getTask(String id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor task = sqLiteDatabase.rawQuery("SELECT * FROM tasks_view WHERE id = ? LIMIT  1",
                new String[]{id});
        return task;
    }

    // get all task
    public Cursor getTasks(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor task = sqLiteDatabase.rawQuery("SELECT * FROM tasks_view", new String[]{});
        return task;
    }

    public boolean updateTask(String id, String title, String details, long startDate, long endDate){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor task = getTask(id);
        if(task.getCount() > 0){

            ContentValues contentTitle = new ContentValues();
            // title
            contentTitle.put("title", title);

            ContentValues contentDetails = new ContentValues();
            // details
            contentDetails.put("details", details);


            ContentValues contentDate = new ContentValues();
            // start date
            contentDate.put("startDate", startDate);
            // end date
            contentDate.put("endDate", endDate);

            ContentValues contentStatus = new ContentValues();
            // status
            contentStatus.put("isFinished", PENDING);

            sqLiteDatabase.update(taskTitles, contentTitle, "id = ?", new String[]{id});
            sqLiteDatabase.update(taskDetails, contentDetails, "id = ?", new String[]{id});
            sqLiteDatabase.update(taskDates, contentDate, "id = ?", new String[]{id});
            sqLiteDatabase.update(taskStatus, contentStatus, "id = ?", new String[]{id});
            updateNotification(id, title, details, endDate);
            return true;
        }
        return false;
    }

    // get task
    public Cursor getArchived(String id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor task = sqLiteDatabase.rawQuery("SELECT * FROM archived_tasks WHERE id = ? LIMIT 1",
                new String[]{id});
        return task;
    }

    // delete task, decalred private for security
    private boolean deleteTask(String id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(taskTitles, "id = ?", new String[]{id});
        sqLiteDatabase.delete(taskDetails, "id = ?", new String[]{id});
        sqLiteDatabase.delete(taskDates, "id = ?", new String[]{id});
        sqLiteDatabase.delete(taskStatus, "id = ?", new String[]{id});
        return true;
    }

    // get all task
    public Cursor getArchivedTasks(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor task = sqLiteDatabase.rawQuery("SELECT * FROM "+ archivedTasks, new String[]{});
        return task;
    }

    // archive current task
    public boolean archiveTask(String id){
        long request = -1;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long now = new Date().getTime();
        Cursor task = getTask(id);
        String title = "", details = "";
        long startDate = 0, endDate = 0;


        if(task != null && task.moveToFirst()){

            title = task.getString(1);
            details = task.getString(2);
            startDate = task.getLong(3);
            endDate = task.getLong(4);

            ContentValues contentArchive = new ContentValues();
            contentArchive.put("id", id);
            contentArchive.put("title", title);
            contentArchive.put("details", details);
            contentArchive.put("startDate", startDate);
            contentArchive.put("endDate", endDate);
            contentArchive.put("isFinished", FINISHED);
            contentArchive.put("finishedDate", now);
            request = sqLiteDatabase.insert(archivedTasks, null, contentArchive);
        }
        boolean result = false;
        if(request > -1){
            result = deleteTask(id);
            deleteNotification(id);
        }
        return result;
    }

    // delete archived task
    public boolean deleteArchived(String id){
        long result = -1;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        result = sqLiteDatabase.delete(archivedTasks, "id = ?", new String[]{id});
        return result>-1;
    }

    public boolean clearArchived(String id){
        long result = -1;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        result = sqLiteDatabase.delete(archivedTasks, "", new String[]{});
        return result>-1;
    }

    // restore archived task
    public boolean restoreArchive(String id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long now = new Date().getTime();
        Cursor task = getArchived(id);
        String title = "",
                details = "";
        long  startDate = 0,
                endDate = 0;
        if(task.moveToFirst()){
            title = task.getString(1);
            details = task.getString(2);
            startDate = task.getLong(3);
            endDate = task.getLong(4);

            boolean res = addTask(title, details, startDate, endDate);

             return res && deleteArchived(id);
        }
        return false;
    }

    // add notification
    public boolean addNotification(String taskID, String title, String details, long notifyDate){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int id = Integer.parseInt(taskID, 16);
        ContentValues contentTitle = new ContentValues();
        contentTitle.put("id", id);
        contentTitle.put("taskID", taskID);
        contentTitle.put("title", title);
        contentTitle.put("notifyDate", notifyDate);

        ContentValues contentDetails = new ContentValues();
        contentDetails.put("id", id);
        contentDetails.put("details", details);

        sqLiteDatabase.insert(notifications_title, null, contentTitle);
        sqLiteDatabase.insert(notifications_details, null, contentDetails);

        return true;
    }

    public Cursor getNotification(String taskID){
        int id = Integer.parseInt(taskID, 16);
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM notifications_view WHERE id = ? LIMIT 1", new String[]{String.valueOf(id)});

    }
    public Cursor getAllNotification(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM notifications_view", new String[]{});
    }

    // delete notification
    public boolean deleteNotification(String taskID){
        int id = Integer.parseInt(taskID, 16);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(notifications_title, "id = ?", new String[]{String.valueOf(id)});
        sqLiteDatabase.delete(notifications_details, "id = ?", new String[]{String.valueOf(id)});

        return true;
    }
    // delete notification
    public boolean updateNotification(String taskID, String title, String details, long notifyDate){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int id = Integer.parseInt(taskID, 16);

        ContentValues contentTitle = new ContentValues();
        contentTitle.put("id", id);
        contentTitle.put("title", title);
        contentTitle.put("taskID", taskID);
        contentTitle.put("notifyDate", notifyDate);

        ContentValues contentDetails = new ContentValues();
        contentDetails.put("details", details);

        sqLiteDatabase.update(notifications_title, contentTitle, "id = ?", new String[]{String.valueOf(id)});
        sqLiteDatabase.update(notifications_details,  contentDetails, "id = ?", new String[]{String.valueOf(id)});

        return true;
    }

}
