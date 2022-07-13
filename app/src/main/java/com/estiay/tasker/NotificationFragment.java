package com.estiay.tasker;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.estiay.tasker.adapters.NotificationAdapter;
import com.estiay.tasker.database.TaskBase;
import com.estiay.tasker.models.NotificationModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
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
    RecyclerView recyclerView;
    TaskBase taskBase;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notification, container, false);
        initViews(view);
        initNotifications();
        return view;
    }

    private void initViews(View v){
        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
    }
    private void initNotifications(){
        ArrayList<NotificationModel> notificationArray = notifications(getActivity());

        NotificationAdapter myTaskAdapter = new NotificationAdapter(notificationArray, NotificationFragment.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(myTaskAdapter);
    }

    private ArrayList<NotificationModel> notifications(Context context){
        taskBase = new TaskBase(context);
        ArrayList<NotificationModel> notificationContainer = new ArrayList<>();
        Cursor notif = taskBase.getAllNotification();
        int id;
        String taskID, title, details;
        long notifyDate;

        while(notif.moveToNext()){
            notifyDate = notif.getLong(4);
            if(notifyDate <= new Date().getTime()){
                id = notif.getInt(0);
                taskID = notif.getString(1);
                title = notif.getString(2);
                details = notif.getString(3);

                notificationContainer.add(new NotificationModel(id, taskID, title, details, notifyDate));
            }
        }
//        int visibility = archiveContainer.size() == 0 ? View.VISIBLE : View.GONE;
//        emptyDisp.setVisibility(visibility);
//        emptyImage.setVisibility(visibility);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationContainer.sort(Comparator.comparing(NotificationModel::getNotifyDate).reversed());
        }
        return notificationContainer;
    }
}