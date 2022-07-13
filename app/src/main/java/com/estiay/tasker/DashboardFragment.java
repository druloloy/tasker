package com.estiay.tasker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.estiay.tasker.database.TaskBase;
import com.estiay.tasker.adapters.OngoingTaskAdapter;
import com.estiay.tasker.exception.TaskerException;
import com.estiay.tasker.models.TaskModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DashboardFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    OngoingTaskAdapter ongoingTaskAdapter;
    Button addBtn, pendingBtn, archivedBtn;
    TextView emptyDisp;
    ImageView emptyImage;
    TaskBase taskBase;
    ArrayList<TaskModel> ongoingTaskArray = new ArrayList<>();
    RecyclerView ongoingTaskView;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        taskBase = new TaskBase(view.getContext());

        initViews(view);
        initListeners();
        initOngoing(view);
        return view;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initOngoing(View view){
        try {
            ongoingTaskArray = ongoingTaskData(view.getContext());
        } catch (TaskerException e) {
            e.printStackTrace();
        }
        ongoingTaskView = view.findViewById(R.id.ongoing_view);
        ongoingTaskAdapter = new OngoingTaskAdapter(ongoingTaskArray);
        ongoingTaskView.setNestedScrollingEnabled(false);
        ongoingTaskView.setHasFixedSize(true);
        ongoingTaskView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        ongoingTaskView.setAdapter(ongoingTaskAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList<TaskModel> ongoingTaskData(Context context) throws TaskerException {
        ArrayList<TaskModel> tasksContainer = new ArrayList<>();
        Cursor tasks = taskBase.getTasks();
        String id, title, details;
        long startDate, endDate, createdDate;
        long now = new Date().getTime();
        while(tasks.moveToNext()){
            startDate = tasks.getLong(3);
            endDate = tasks.getLong(4);

            if(now >= startDate){
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
    private void initViews(View view){
        pendingBtn = view.findViewById(R.id.btn_pending_tasks);
        archivedBtn = view.findViewById(R.id.btn_archived_task);
        addBtn = view.findViewById(R.id.btn_add_task);
        emptyDisp = view.findViewById(R.id.empty_text_display);
        emptyImage = view.findViewById(R.id.empty_image);
    }
    private void initListeners(){
        addBtn.setOnClickListener(v->{
            startActivity(new Intent(getActivity(), AddTask.class));
        });
        pendingBtn.setOnClickListener(v->{
            startActivity(new Intent(getActivity(), Pending.class));
        });
        archivedBtn.setOnClickListener(v->{
            startActivity(new Intent(getActivity(), Archive.class));
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        initOngoing(requireView());
    }

}