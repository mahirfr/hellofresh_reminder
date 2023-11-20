package com.example.hellofreshreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE);
        if (!message.equals(""))
            System.out.println(message);
    }
}
