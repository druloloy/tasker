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

import com.estiay.tasker.Archive;
import com.estiay.tasker.R;
import com.estiay.tasker.database.TaskBase;
import com.estiay.tasker.exception.TaskerException;
import com.estiay.tasker.models.ArchivedModel;
import com.estiay.tasker.models.TaskModel;
import com.estiay.tasker.prompts.DeletePrompt;
import com.estiay.tasker.receiver.AlertReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ArchivedAdapter extends RecyclerView.Adapter<ArchivedAdapter.ArchivedHolder> {

    TaskBase taskBase;
    ArrayList<ArchivedModel> archivedTasks;
    Archive context;

    public ArchivedAdapter(ArrayList<ArchivedModel> archivedTasks, Archive activity){
        this.archivedTasks = archivedTasks;
        this.context = activity;

    }

    @NonNull
    @Override
    public ArchivedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.archived_list, parent, false);
        return new ArchivedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchivedHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        final ArchivedModel archivedModel = archivedTasks.get(position);

        final ArchivedModel task = archivedTasks.get(position);
        taskBase = new TaskBase(context);
        SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy, hh:mm:aa", Locale.ENGLISH);

        if(DateUtils.isToday(task.getFinishedDate().getTime())){
            df = new SimpleDateFormat("hh:mm:aa", Locale.ENGLISH);
        }

        holder.archived_title.setText( archivedModel.getTitle());
        holder.details.setText(archivedModel.getDetails());
        holder.dateFinished.setText(df.format(task.getFinishedDate()));


        holder.restoreBtn.setOnClickListener(v->{
            try{
                boolean result = taskBase.restoreArchive(archivedTasks.get(position).getId());

                if(result){
                    TaskModel taskNotif = new TaskModel(archivedModel.getId(),
                            archivedModel.getTitle(),
                            archivedModel.getDetails(),
                            archivedModel.getStartDate(),
                            archivedModel.getEndDate(),
                            new Date());

                    Calendar c = Calendar.getInstance();
                    c.setTime(archivedModel.getEndDate());
                    archivedTasks.remove(position);
                    startAlarm(c, Integer.parseInt(archivedModel.getId(), 16), taskNotif);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                }
            }catch (IndexOutOfBoundsException e){
                notifyItemRangeChanged(position, getItemCount());
            } catch (TaskerException e) {
                e.printStackTrace();
            }
        });

        DeletePrompt deletePrompt = new DeletePrompt(context, () -> {

        boolean result = taskBase.deleteArchived(archivedTasks.get(position).getId());
        if(result){
            archivedTasks.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, archivedTasks.size());
        }

        });
        holder.deleteBtn.setOnClickListener(v->{
            try{
                deletePrompt.show();
            }catch (IndexOutOfBoundsException e){
                notifyItemRangeChanged(position, getItemCount());
            }
        });

    }

    @Override
    public int getItemCount() {
        return archivedTasks.size();
    }


    public static class ArchivedHolder extends RecyclerView.ViewHolder{

        TextView archived_title;
        TextView details;
        TextView dateFinished;

        ImageView restoreBtn;
        ImageView deleteBtn;

        public ArchivedHolder(@NonNull View itemView) {
            super(itemView);

            archived_title = itemView.findViewById(R.id.archived_title);
            details = itemView.findViewById(R.id.archived_details);
            dateFinished = itemView.findViewById(R.id.archived_date_finished);
            restoreBtn = itemView.findViewById(R.id.restore_btn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
        }
    }
    private void startAlarm(Calendar c, int id, TaskModel task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("taskID", id);
        intent.putExtra("Task", task);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE);
        }

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }
}