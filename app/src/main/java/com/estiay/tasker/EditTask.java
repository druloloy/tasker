package com.estiay.tasker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.estiay.tasker.database.TaskBase;
import com.estiay.tasker.exception.TaskerException;
import com.estiay.tasker.models.TaskModel;
import com.estiay.tasker.prompts.EditingPrompt;
import com.estiay.tasker.receiver.AlertReceiver;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class EditTask extends AppCompatActivity {

    EditText titleET, detailsET, startDateET, endDateET;
    TextInputLayout startTIL, endTIL;
    TaskModel task;
    ImageView backBtn, submitBtn;
    TaskBase taskBase;

    boolean hasChange = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        taskBase = new TaskBase(this);
        task = TaskModel.getPastActivityInstance(EditTask.this);
        initViews();
        populateFields();
        try {
            initListener();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    public void initViews(){

        titleET = findViewById(R.id.task_title);
        detailsET = findViewById(R.id.task_details);
        startTIL = findViewById(R.id.startDate_til);
        endTIL = findViewById(R.id.endDate_til);
        startDateET = startTIL.getEditText();
        endDateET = endTIL.getEditText();

        startDateET.setInputType(InputType.TYPE_NULL);
        endDateET.setInputType(InputType.TYPE_NULL);

        submitBtn = findViewById(R.id.submit_btn);
        backBtn = findViewById(R.id.back_btn);
        submitBtn.setVisibility(View.GONE);
    }
    public void initListener() throws NullPointerException{
        submitBtn.setOnClickListener(v->{
            try {
                submitEdit();
            } catch (ParseException | TaskerException e) {
                e.printStackTrace();
            }
        });
        startDateET.setOnClickListener(this::initStartDateTime);
        endDateET.setOnClickListener(this::initEndDateTime);

        titleET.addTextChangedListener(inputWatcher);
        detailsET.addTextChangedListener(inputWatcher);
        startDateET.addTextChangedListener(inputWatcher);
        endDateET.addTextChangedListener(inputWatcher);
        backBtn.setOnClickListener(v->{
            if(hasChange){
                new EditingPrompt(this).show();
            }else{
                finish();
            }
        });
    }

    TextWatcher inputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            applyChange();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            applyChange();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            applyChange();
        }
    };
    public void populateFields(){
        // format dates

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
        String _startDate = sdf.format(task.getStartDate());
        String _endDate = sdf.format(task.getEndDate());

        titleET.setText(task.getTitle());
        detailsET.setText(task.getDetails());
        startDateET.setText(_startDate);
        endDateET.setText(_endDate);
    }

    private void  applyChange(){
        String title = titleET.getText().toString();
        String details = detailsET.getText().toString();
        String startDate = startDateET.getText().toString();
        String endDate = endDateET.getText().toString();

        analyzeChange(title, details, startDate, endDate);
    }
    private void analyzeChange(String title, String details, String startDate, String endDate){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);

        String taskTitle = task.getTitle();
        String taskDetails = task.getDetails();
        String taskStart = sdf.format(task.getStartDate());
        String taskEnd = sdf.format(task.getEndDate());

        if(!title.equals(taskTitle) |
                !details.equals(taskDetails) |
                !startDate.equals(taskStart) |
                !endDate.equals(taskEnd)){
            hasChange = true;
            submitBtn.setVisibility(View.VISIBLE);
        }else{
            hasChange = false;
            submitBtn.setVisibility(View.GONE);
        }
    }
    private void submitEdit() throws ParseException, TaskerException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);

        String title = titleET.getText().toString();
        String details = detailsET.getText().toString();
        long startDate = Objects.requireNonNull(sdf.parse(startDateET.getText().toString())).getTime();
        long endDate = Objects.requireNonNull(sdf.parse(endDateET.getText().toString())).getTime();



        int notifID = Integer.parseInt(task.getId(), 16);
        boolean result = taskBase.updateTask(task.getId(), title, details, startDate, endDate);
        if(result){
            try {
                task = new TaskModel(task.getId(), title, details, startDate, endDate, task.getCreatedDate().getTime());
            }
            catch(TaskerException e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar c = Calendar.getInstance();
            c.setTime(new Date(endDate));
            startAlarm(c, notifID, task);
            Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show();
            applyChange();
        }
    }

    private void initStartDateTime(View view){
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(task.getStartDate());
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog.OnTimeSetListener timeSetListener = (timePicker, hour, min) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, min);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
                startDateET.setText(simpleDateFormat.format(calendar.getTime()));
            };
            new TimePickerDialog(this, timeSetListener,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), false).show();
        };
        new DatePickerDialog(this,  dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void initEndDateTime(View view){

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(task.getEndDate());
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog.OnTimeSetListener timeSetListener = (timePicker, hour, min) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, min);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
                endDateET.setText(simpleDateFormat.format(calendar.getTime()));
            };
            new TimePickerDialog(this, timeSetListener,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), false).show();
        };
        new DatePickerDialog(this,  dateSetListener,
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
}