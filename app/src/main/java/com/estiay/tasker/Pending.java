package com.estiay.tasker;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.estiay.tasker.adapters.PendingAdapter;
import com.estiay.tasker.database.TaskBase;
import com.estiay.tasker.exception.TaskerException;
import com.estiay.tasker.models.TaskModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Pending extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView backBtn, emptyImage;
    TextView emptyDisp;
    TaskBase taskBase;
    static ArrayList<TaskModel> tasks;
    static PendingAdapter pendingAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);
        taskBase = new TaskBase(this);

        initViews();
        try {
            initPending();
        } catch (TaskerException e) {
            e.printStackTrace();
        }
        initListeners();

    }

    private void initPending() throws TaskerException {
        tasks = pendingTasks();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2 , StaggeredGridLayoutManager.VERTICAL));
        pendingAdapter = new PendingAdapter(tasks, Pending.this);
        recyclerView.setAdapter(pendingAdapter);
    }
    private void initViews(){
        backBtn = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.recycler_view);
        emptyImage = findViewById(R.id.empty_image);
        emptyDisp = findViewById(R.id.empty_text_display);

    }
    private void initListeners(){
        backBtn.setOnClickListener(v->{
            finish();
        });
    }
    private ArrayList<TaskModel> pendingTasks() throws TaskerException {

        ArrayList<TaskModel> tasksContainer = new ArrayList<>();
        Cursor tasks = taskBase.getTasks();
        String id, title, details;
        long startDate, endDate, createdDate;
        long now = new Date().getTime();

        while(tasks.moveToNext()){
            startDate = tasks.getLong(3);
            endDate = tasks.getLong(4);

            if(now <= startDate){
                id = tasks.getString(0);
                title = tasks.getString(1);
                details = tasks.getString(2);
                createdDate = tasks.getLong(6);

                tasksContainer.add(new TaskModel(id, title, details, startDate, endDate, createdDate));
            }
        }
        int visibility = tasksContainer.size() == 0 ? View.VISIBLE : View.GONE;
        emptyDisp.setVisibility(visibility);
        emptyImage.setVisibility(visibility);

        tasksContainer.sort(Comparator.comparing(TaskModel::getEndDate));

        return tasksContainer;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            initPending();
        } catch (TaskerException e) {
            e.printStackTrace();
        }
    }
}