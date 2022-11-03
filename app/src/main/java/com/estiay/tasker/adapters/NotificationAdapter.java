package com.estiay.tasker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.estiay.tasker.NotificationFragment;
import com.estiay.tasker.R;
import com.estiay.tasker.models.NotificationModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    ArrayList<NotificationModel> notificationList;
    NotificationFragment context;

    public NotificationAdapter(ArrayList<NotificationModel> notificationList, NotificationFragment activity) {
        this.notificationList = notificationList;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_notification, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        final NotificationModel notif = notificationList.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm aa", Locale.ENGLISH);
        String notifDate = sdf.format(notif.getNotifyDate());

        holder.task_name.setText(notif.getTitle());
        holder.deadline.setText("Your ask ended on "+notifDate);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context.getContext(), notificationList., Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView task_name;
        TextView deadline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            task_name = itemView.findViewById(R.id.task_name);
            deadline = itemView.findViewById(R.id.deadline);
        }
    }
}
