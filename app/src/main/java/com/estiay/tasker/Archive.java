package com.estiay.tasker;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.estiay.tasker.adapters.ArchivedAdapter;
import com.estiay.tasker.database.TaskBase;
import com.estiay.tasker.exception.TaskerException;
import com.estiay.tasker.models.ArchivedModel;

import java.util.ArrayList;
import java.util.Comparator;

public class Archive extends AppCompatActivity {
    ImageView backBtn, emptyImage;
    RecyclerView recyclerView;
    TextView emptyDisp;
    TaskBase taskBase;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        initViews();
        try {
            initArchived();
        } catch (TaskerException e) {
            e.printStackTrace();
        }
        initListeners();
    }
    private void initViews() {
        backBtn = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        emptyImage = findViewById(R.id.empty_image);
        emptyDisp = findViewById(R.id.empty_text_display);
    }
    private void initListeners() {
        backBtn.setOnClickListener(v->{
            finish();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initArchived() throws TaskerException {
        ArrayList<ArchivedModel> archivedList = archivedTasks(Archive.this);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2 , StaggeredGridLayoutManager.VERTICAL));
        ArchivedAdapter archivedAdapter = new ArchivedAdapter(archivedList, Archive.this);
        recyclerView.setAdapter(archivedAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList<ArchivedModel> archivedTasks(Context context) throws TaskerException {
        taskBase = new TaskBase(context);
        ArrayList<ArchivedModel> archiveContainer = new ArrayList<>();
        Cursor tasks = taskBase.getArchivedTasks();
        String id, title, details;
        long startDate, endDate, createdDate;

        while(tasks.moveToNext()){
            startDate = tasks.getLong(3);
            endDate = tasks.getLong(4);

            id = tasks.getString(0);
            title = tasks.getString(1);
            details = tasks.getString(2);
            createdDate = tasks.getLong(6);

            archiveContainer.add(new ArchivedModel(id, title, details, startDate, endDate, createdDate));
        }
        int visibility = archiveContainer.size() == 0 ? View.VISIBLE : View.GONE;
        emptyDisp.setVisibility(visibility);
        emptyImage.setVisibility(visibility);

        archiveContainer.sort(Comparator.comparing(ArchivedModel::getFinishedDate).reversed());
        return archiveContainer;
    }
}