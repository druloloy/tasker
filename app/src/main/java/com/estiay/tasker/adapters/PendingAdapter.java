package com.estiay.tasker.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.estiay.tasker.EditTask;
import com.estiay.tasker.Pending;
import com.estiay.tasker.R;
import com.estiay.tasker.database.TaskBase;
import com.estiay.tasker.models.TaskModel;
import com.estiay.tasker.receiver.AlertReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.PendingHolder> {

    ArrayList<TaskModel> tasks;
    Context context;
    TaskBase taskBase;
    public PendingAdapter(ArrayList<TaskModel> tasks, Pending activity) {

        this.tasks = tasks;
        this.context = activity;

    }

    @NonNull
    @Override
    public PendingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.pending_item_list, parent, false);
        return new PendingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        final TaskModel task = tasks.get(position);
        taskBase = new TaskBase(context);
        SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy, hh:mm:aa", Locale.ENGLISH);
        if(DateUtils.isToday(task.getCreatedDate().getTime())){
            df = new SimpleDateFormat("hh:mm:aa", Locale.ENGLISH);
        }

        holder.pending_title.setText(task.getTitle());
        holder.details.setText(task.getDetails());
        holder.pending_date_created.setText(df.format(task.getCreatedDate()));
        int notifID = Integer.parseInt(task.getId(), 16);
        holder.archiveBtn.setOnClickListener(v->{
            try {
                boolean result = taskBase.archiveTask(tasks.get(position).getId());
                if(result){
                    tasks.remove(position);
                    cancelAlarm(notifID, task);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                }
            }catch (IndexOutOfBoundsException e){
                notifyItemRangeChanged(position, getItemCount());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    context.startActivity(task.getTaskModelSerializable(context, EditTask.class)
                            .putExtra("itemPos", position));
                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }


    public static class PendingHolder extends RecyclerView.ViewHolder {
        TextView pending_title;
        TextView details;
        TextView pending_date_created;
        ImageView archiveBtn;
        public PendingHolder(@NonNull View itemView) {
            super(itemView);
            pending_title = itemView.findViewById(R.id.pending_title);
            details = itemView.findViewById(R.id.pending_details);
            pending_date_created = itemView.findViewById(R.id.pending_date_created);
            archiveBtn = itemView.findViewById(R.id.archive_btn);
        }
    }
    private void cancelAlarm(int notifId, TaskModel task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("taskID", notifId);
        intent.putExtra("Task", task);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notifId, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
    }
}