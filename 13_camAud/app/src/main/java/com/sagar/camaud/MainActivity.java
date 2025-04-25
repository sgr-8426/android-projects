package com.sagar.camaud;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.*;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.widget.*;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final int VIDEO_CAPTURE = 101;
    private static final int AUDIO_CAPTURE = 102;
    private Uri videoUri = null;
    private Uri audioUri = null;
    private VideoView videoView;
    private Button btnRecordVideo, btnRecordAudio, btnPlayVideo, btnPlayAudio;
    private static final int PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        videoView = findViewById(R.id.videoView);
        btnRecordVideo = findViewById(R.id.btnRecordVideo);
        btnRecordAudio = findViewById(R.id.btnRecordAudio);
        btnPlayVideo = findViewById(R.id.btnPlayVideo);
        btnPlayAudio = findViewById(R.id.btnPlayAudio);

        // Request permissions
        requestPermissions();

        // Set click listeners
        btnRecordVideo.setOnClickListener(v -> startVideoRecording());
        btnRecordAudio.setOnClickListener(v -> startAudioRecording());
        btnPlayVideo.setOnClickListener(v -> playVideo());
        btnPlayAudio.setOnClickListener(v -> playAudio());
    }

    private void requestPermissions() {
        String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        };

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
                break;
            }
        }
    }

    private void startVideoRecording() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File mediaFile = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "video_record.mp4");
        videoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", mediaFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, VIDEO_CAPTURE);
    }

    private void startAudioRecording() {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, AUDIO_CAPTURE);
    }

    private void playVideo() {
        if (videoUri != null) {
            videoView.setVideoURI(videoUri);
            videoView.start();
        } else {
            Toast.makeText(this, "Record a video first", Toast.LENGTH_SHORT).show();
        }
    }

    private void playAudio() {
        if (audioUri != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(audioUri, "audio/*");
            startActivity(intent);
        } else {
            Toast.makeText(this, "Record audio first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == VIDEO_CAPTURE) {
                Toast.makeText(this, "Video recorded successfully", Toast.LENGTH_SHORT).show();
            } else if (requestCode == AUDIO_CAPTURE) {
                audioUri = data.getData();
                Toast.makeText(this, "Audio recorded successfully", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Recording cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}