package com.example.hellofreshreminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.AlarmClock;

import androidx.annotation.RequiresApi;

import java.time.ZoneId;

@RequiresApi(api = Build.VERSION_CODES.M)
public class AlarmSchedulerImpl implements AlarmScheduler {

    private Context context;

    private AlarmManager alarmManager = context.getSystemService(android.app.AlarmManager.class);
    public AlarmSchedulerImpl(Context context) {
        this.context = context;
    }


    @SuppressLint("ScheduleExactAlarm")
    @Override
    public void schedule(AlarmItem item) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(context, AlarmReceiver.class)
                    .putExtra(AlarmClock.EXTRA_MESSAGE, item.message);
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                item.time.atZone(ZoneId.systemDefault()).toEpochSecond() + 1000,
                    PendingIntent.getBroadcast(
                            context,
                            item.hashCode(),
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT

                    )
            );
        }
    }

    @Override
    public void cancel(AlarmItem item) {
        alarmManager.cancel(
                PendingIntent.getBroadcast(
                        context,
                        item.hashCode(),
                        new Intent(context, AlarmReceiver.class),
                        PendingIntent.FLAG_UPDATE_CURRENT

                )
        );
    }
}
