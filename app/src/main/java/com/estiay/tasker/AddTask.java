package com.estiay.tasker;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.estiay.tasker.database.TaskBase;
import com.estiay.tasker.exception.TaskerException;

import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.estiay.tasker.models.TaskModel;
import com.estiay.tasker.receiver.AlertReceiver;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddTask extends AppCompatActivity {
    TaskModel task;
    DatePickerDialog startDateDialog, endDateDialog;
    TextInputLayout startDate, endDate;
    EditText titleET,detailsET,startET, endET;
    ImageView backBtn, submitBtn;

    TaskBase taskBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        taskBase = new TaskBase(this);
        initViews();
        initListeners();
    }

    private void initViews() {
        detailsET = findViewById(R.id.task_details);
        titleET = findViewById(R.id.task_title);
        startDate = findViewById(R.id.startDate_til);
        endDate = findViewById(R.id.endDate_til);
        backBtn = findViewById(R.id.back_btn);
        submitBtn = findViewById(R.id.submit_btn);

        try {
            startET = Objects.requireNonNull(startDate.getEditText());
            endET = Objects.requireNonNull(endDate.getEditText());
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        startET.setInputType(InputType.TYPE_NULL);
        endET.setInputType(InputType.TYPE_NULL);
        detailsET.requestFocus();
    }

    private void initListeners() {
        backBtn.setOnClickListener(v->{
            finish();
        });
        submitBtn.setOnClickListener(v->{
            String title = titleET.getText().toString();
            String details = detailsET.getText().toString();
            String startDateStr = startET.getText().toString();
            String endDateStr = endET.getText().toString();

            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.ENGLISH);
            Date startDate = null;
            Date endDate = null;
            long startDateMil = 0, endDateMil = 0;

            try {
                startDate = df.parse(startDateStr);
                endDate = df.parse(endDateStr);
                startDateMil = Objects.requireNonNull(startDate).getTime();
                endDateMil = Objects.requireNonNull(endDate).getTime();
            } catch (ParseException | NullPointerException e) {
                Toast.makeText(this, "Empty fields!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }

                try {
                // validate
                task = new TaskModel(title, details, startDateMil, endDateMil);
                } catch (TaskerException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                String id = TaskBase.generateID(6);
                int notifId = Integer.parseInt(id, 16);
                // add to db
                boolean result = taskBase.addTask(id, title, details, startDateMil, endDateMil);
                if(result && endDate != null && startDate != null){
                    Calendar alarmDate = Calendar.getInstance();
                    alarmDate.setTime(endDate);
                    startAlarm(alarmDate, notifId, task);
                    Toast.makeText(this, "Added new task!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }

        });
        startET.setOnClickListener(v->{
            initStartDateTime();
        });
        endET.setOnClickListener(v->{
            initEndDateTime();
        });
    }


    private void initStartDateTime(){
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog.OnTimeSetListener timeSetListener = (timePicker, hour, min) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, min);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
                        Objects.requireNonNull(startDate.getEditText()).setText(simpleDateFormat.format(calendar.getTime()));
                    };
                    new TimePickerDialog(AddTask.this, timeSetListener,
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE), false).show();
        };
        new DatePickerDialog(AddTask.this,  dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void initEndDateTime(){

        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog.OnTimeSetListener timeSetListener = (timePicker, hour, min) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, min);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
                        Objects.requireNonNull(endDate.getEditText()).setText(simpleDateFormat.format(calendar.getTime()));
                    };
                    new TimePickerDialog(AddTask.this, timeSetListener,
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE), false).show();
        };
        new DatePickerDialog(AddTask.this,  dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void startAlarm(Calendar c, int id, TaskModel task) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("taskID", id);
        intent.putExtra("Task", task);
        PendingIntent pendingIntent = null;
        Log.d("startingID", String.valueOf(id));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_IMMUTABLE);
        }

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}