package com.example.hellofreshreminder;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;


import com.example.hellofreshreminder.databinding.ActivityMainBinding;

import android.provider.AlarmClock;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.S)
public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;

    private static final int MY_PERMISSIONS_REQUEST_SET_EXACT_ALARM = 1;

    private TextView textView;

    private String lastSms;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private AlarmManager alarmManager;

    AlarmSchedulerImpl scheduler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        scheduler = new AlarmSchedulerImpl(this);

        // SMS Stuff  ||
        //            \/

        // Check and request permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermission();
        } else {
            readSMS();
        }

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    private void checkAndRequestPermission() {
        // Check if the READ_SMS permission is not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_SMS},
                    MY_PERMISSIONS_REQUEST_READ_SMS);
        } else {
            // Permission is already granted, proceed to read SMS
            readSMS();
        }
    }

    private void readSMS() {
        List<String> smsList = new ArrayList<>();
        try {
            // Read the latest SMS
            Uri uri = Uri.parse("content://sms/inbox");
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
            Cursor cursor = getContentResolver().query(uri, projection, "address='Chronofresh'", null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int index_Address = cursor.getColumnIndex("address");
                int index_Person = cursor.getColumnIndex("person");
                int index_Body = cursor.getColumnIndex("body");
                int index_Date = cursor.getColumnIndex("date");
                int index_Type = cursor.getColumnIndex("type");
                do {
                    String strAddress = cursor.getString(index_Address);
                    int intPerson = cursor.getInt(index_Person);
                    String strbody = cursor.getString(index_Body);
                    long longDate = cursor.getLong(index_Date);

                    smsList.add("[ " +
                        strAddress + ", " +
                        intPerson + ", " +
                        strbody + ", " +
                        longDate + ", " +
                    " ]\n\n");
                } while (cursor.moveToNext());

                lastSms = smsList.get(2);
                String hour = extractHourFromSms(lastSms, "aujourd'hui entre ");
                textView.setText(hour);
                int hourInt = Integer.parseInt(hour);


                // TODO set the alarm here
                LocalDateTime time = LocalDateTime.of(LocalDate.now(), LocalTime.of(hourInt, 0));
                checkAndRequestPermissionAlarm(time, "Wake up! \nHello fresh incoming");

                if (!cursor.isClosed()) {
                    cursor.close();
                    cursor = null;
                }
            } else {
                textView.setText("no result!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkAndRequestPermissionAlarm(LocalDateTime time, String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM},
                    MY_PERMISSIONS_REQUEST_SET_EXACT_ALARM);
        } else {
            setAlarm(time, message);
        }
    }

    public void setAlarm(LocalDateTime time, String message) {
        scheduler.schedule(new AlarmItem(time, message));
    }

    public String extractHourFromSms(String sms, String uniqueSubstring) {
        int index = 0;
        if (sms.contains(uniqueSubstring)) {
            index = sms.indexOf(uniqueSubstring);
            index += uniqueSubstring.length();
            String hour = sms.substring(index, index + 2);
            if (hour.substring(0, 1).equals("0"))
                return hour.substring(1);
            return hour;
        }
        return "";
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_SMS) {
            // Check if the permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, you can proceed to read SMS
                readSMS();
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
                textView.setText("Permission denied. Cannot read SMS.");
            }
        }
    }

}