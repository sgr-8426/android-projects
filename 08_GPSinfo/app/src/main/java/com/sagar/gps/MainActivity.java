package com.sagar.gps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.*;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient client;
    TextView text;
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.locationText);
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        else getLocation();
    }

    public void onRequestPermissionsResult(int r, @NonNull String[] p, @NonNull int[] g) {
        if (r == 1 && g.length > 0 && g[0] == PackageManager.PERMISSION_GRANTED) getLocation();
    }

    void getLocation() {
        client.getLastLocation().addOnSuccessListener(l -> {
            if (l != null) text.setText("Lat: " + l.getLatitude() + "\nLng: " + l.getLongitude());
        });
    }
}