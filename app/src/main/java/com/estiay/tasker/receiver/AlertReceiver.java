package com.estiay.tasker.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.estiay.tasker.helpers.NotificationHelper;
import com.estiay.tasker.models.TaskModel;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TaskModel task = (TaskModel) intent.getSerializableExtra("Task");
        int id = (int) intent.getSerializableExtra("taskID");
        Log.d("receiverID", String.valueOf(id));
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(task, id);

        notificationHelper.getManager().notify(id, nb.build());
    }
}