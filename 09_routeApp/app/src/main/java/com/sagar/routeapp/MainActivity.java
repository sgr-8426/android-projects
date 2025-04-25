package com.sagar.routeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.*;
import android.util.Log;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import okhttp3.*;
import com.google.gson.*;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LatLng pointA = new LatLng(21.7155965, 72.1133107);
    private LatLng pointB = new LatLng(21.7043341, 72.1241230);
    private String googleMapsApiKey = "AIzaSyAUq7oBtXOW8PWNDqFIH85pZHWCpRAsgLw"; // Use your API key here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Fetch route in a background thread
        new FetchRouteTask().execute(pointA.latitude, pointA.longitude, pointB.latitude, pointB.longitude);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Add markers
        mMap.addMarker(new MarkerOptions().position(pointA).title("Start Point"));
        mMap.addMarker(new MarkerOptions().position(pointB).title("End Point"));

        // Move camera to start point
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointA, 12f));
    }

    private class FetchRouteTask extends AsyncTask<Double, Void, ArrayList<LatLng>> {

        @Override
        protected ArrayList<LatLng> doInBackground(Double... params) {
            // Fetch the route in the background thread
            double originLat = params[0];
            double originLng = params[1];
            double destLat = params[2];
            double destLng = params[3];

            // Google Maps Directions API URL
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + originLat + "," + originLng +
                    "&destination=" + destLat + "," + destLng + "&key=" + googleMapsApiKey;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            ArrayList<LatLng> routePoints = new ArrayList<>();

            try (Response response = client.newCall(request).execute()) {
                String responseData = response.body().string();
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);
                JsonArray routes = jsonObject.getAsJsonArray("routes");

                if (routes != null && routes.size() > 0) {
                    JsonObject route = routes.get(0).getAsJsonObject();
                    JsonArray legs = route.getAsJsonArray("legs");

                    if (legs != null && legs.size() > 0) {
                        JsonObject leg = legs.get(0).getAsJsonObject();
                        JsonArray steps = leg.getAsJsonArray("steps");

                        // Extract points from the steps
                        for (int i = 0; i < steps.size(); i++) {
                            JsonObject step = steps.get(i).getAsJsonObject();
                            JsonObject endLocation = step.getAsJsonObject("end_location");
                            double lat = endLocation.get("lat").getAsDouble();
                            double lng = endLocation.get("lng").getAsDouble();
                            routePoints.add(new LatLng(lat, lng));
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("FetchRouteTask", "Error fetching route", e);
            }
            return routePoints;
        }

        @Override
        protected void onPostExecute(ArrayList<LatLng> routePoints) {
            // Draw polyline on the map
            if (routePoints != null && !routePoints.isEmpty()) {
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(routePoints);
                polylineOptions.width(8).color(0xFF007AFF); // Blue color
                mMap.addPolyline(polylineOptions);
            }
        }
    }
}
