package com.sagar.alarm;

import android.content.*;
import android.media.*;
import android.net.Uri;
import android.os.PowerManager;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    private static MediaPlayer mediaPlayer;
    private static PowerManager.WakeLock wakeLock;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Acquire wake lock
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK |
            PowerManager.ACQUIRE_CAUSES_WAKEUP |
            PowerManager.ON_AFTER_RELEASE,
            "Alarm:WakeLock"
        );
        wakeLock.acquire(10*60*1000L); // 10 minutes timeout

        Toast.makeText(context, "Wake up! Alarm is ringing!", Toast.LENGTH_LONG).show();

        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmSound == null) {
                // If alarm sound not found, try notification sound
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }

            mediaPlayer = new MediaPlayer();
            
            // Set audio attributes
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
            
            mediaPlayer.setAudioAttributes(audioAttributes);
            
            // Set maximum volume
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
            
            mediaPlayer.setDataSource(context, alarmSound);
            mediaPlayer.setLooping(true);
            
            // Handle errors
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(context, "Error playing alarm sound", Toast.LENGTH_SHORT).show();
                stopAlarm();
                return true;
            });

            // Prepare asynchronously
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error setting up alarm sound: " + e.getMessage(), 
                         Toast.LENGTH_SHORT).show();
            stopAlarm();
        }
    }

    public static void stopAlarm() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } finally {
                mediaPlayer = null;
            }
        }
        
        if (wakeLock != null && wakeLock.isHeld()) {
            try {
                wakeLock.release();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                wakeLock = null;
            }
        }
    }
}