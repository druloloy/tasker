package com.estiay.tasker.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.estiay.tasker.R;
import com.estiay.tasker.EditTask;
import com.estiay.tasker.database.TaskBase;
import com.estiay.tasker.models.TaskModel;
import com.estiay.tasker.receiver.AlertReceiver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class OngoingTaskAdapter extends RecyclerView.Adapter<OngoingTaskAdapter.ViewHolder> {
    ArrayList<TaskModel> tasks;
    public OngoingTaskAdapter(ArrayList<TaskModel> tasks) {
        this.tasks = tasks;
    }
    Context context;
    private TaskBase taskBase;
    @NonNull
    @Override
    public OngoingTaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_ongoingtasks, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OngoingTaskAdapter.ViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        TaskModel task = tasks.get(position);
        long startDate = task.getStartDate().getTime();
        long endDate = task.getEndDate().getTime();

        if(endDate <= new Date().getTime())
            holder.itemView.setBackgroundResource(R.drawable.singularity);
        else
            holder.itemView.setBackgroundResource(R.drawable.ongoin_task_layout);

        String pattern = "MMM dd, hh:mm aa";
        DateFormat df = new SimpleDateFormat(pattern, Locale.ENGLISH);
        String start, end;

        start = df.format(startDate);
        end = df.format(endDate);


        holder.title.setText(tasks.get(position).getTitle());

        String today = "";
        if(DateUtils.isToday(startDate)){
            pattern = "hh:mm aa";
            df = new SimpleDateFormat(pattern, Locale.ENGLISH);
            start = df.format(startDate);
            today = "Today, ";
        }

        holder.startingNote.setText(new StringBuilder()
                        .append("Started at ")
                        .append(today)
                        .append(start));
        today = "";
        if(DateUtils.isToday(endDate)){
            pattern = "hh:mm aa";
            df = new SimpleDateFormat(pattern, Locale.ENGLISH);
            end = df.format(endDate);
            today = "Today, ";
        }
        holder.endingNote.setText(new StringBuilder()
                        .append("Should end at ")
                        .append(today)
                        .append(end));
        taskBase = new TaskBase(context);
        int notifID = Integer.parseInt(task.getId(), 16);
        holder.setFinished.setOnClickListener(v->{
            boolean result = taskBase.archiveTask(task.getId());
            if(result){
                tasks.remove(position);
                cancelAlarm(notifID, task);
                notifyItemRemoved(position);
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

    public static class ViewHolder extends  RecyclerView.ViewHolder {
        public TextView title;
        public TextView startingNote;
        public TextView endingNote;
        public Button setFinished;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.ongoing_task_title);
            startingNote = itemView.findViewById(R.id.ongoing_task_started);
            endingNote = itemView.findViewById(R.id.ongoing_task_end);
            setFinished = itemView.findViewById(R.id.set_finish_btn);
        }
    };
    private void cancelAlarm(int notifId, TaskModel task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("taskID", notifId);
        intent.putExtra("Task", task);

        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context, notifId, intent, PendingIntent.FLAG_IMMUTABLE);
        }
        alarmManager.cancel(pendingIntent);
    }
}
