package com.sagar.alarm;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.provider.Settings;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private Button buttonSetAlarm;
    private Button buttonCancelAlarm;
    private TextView textViewStatus;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private static final int ALARM_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        timePicker = findViewById(R.id.timePicker);
        buttonSetAlarm = findViewById(R.id.buttonSetAlarm);
        buttonCancelAlarm = findViewById(R.id.buttonCancelAlarm);
        textViewStatus = findViewById(R.id.textViewStatus);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Request notification permission for Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, 
                new String[]{"android.permission.POST_NOTIFICATIONS"}, 
                101);
        }

        buttonSetAlarm.setOnClickListener(v -> setAlarm());
        buttonCancelAlarm.setOnClickListener(v -> cancelAlarm());
    }

    private void setAlarm() {
        int hour, minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If time is in past, add a day
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(
            this, 
            ALARM_REQUEST_CODE, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(settingsIntent);
                Toast.makeText(this, "Please allow exact alarms in settings", Toast.LENGTH_LONG).show();
                return;
            }
            
            alarmManager.setAlarmClock(
                new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent),
                pendingIntent
            );
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
            );
        }

        String minuteStr = String.format("%02d", minute);
        updateStatus("Alarm set for " + hour + ":" + minuteStr);
        Toast.makeText(this, "Alarm set for " + hour + ":" + minuteStr, Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm() {
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            AlarmReceiver.stopAlarm();
            updateStatus("Alarm cancelled");
            Toast.makeText(this, "Alarm cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStatus(String status) {
        textViewStatus.setText(status);
    }
}