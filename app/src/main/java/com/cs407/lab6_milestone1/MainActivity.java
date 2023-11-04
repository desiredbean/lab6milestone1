package com.cs407.lab6_milestone1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.jetbrains.annotations.NonNls;

public class MainActivity extends FragmentActivity {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5;

    // Somewhere in australia

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private final LatLng mDestinationLatLng = new LatLng(43.0766,-89.4125 );
//    private final LatLng mDestinationLatLng = new LatLng(-33.8523341, 151.2106085);
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);

        supportMapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            googleMap.addMarker(new MarkerOptions().position(mDestinationLatLng).title("Destination"));

        });

        // obtain a fused location provider client
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        displayMyLocation();
    }

    private void displayMyLocation() {
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        Log.i("CALEB", "display my location was called");

        // ask for permission if not given yet
        if (permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            Log.i("CALEB","permission was denied");

        } else {

            Log.i("CALEB","permission was granted");

            // display users current location
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(this, task -> {
                Location mLasKnownLocation = task.getResult();

                Log.i("CALEB", "task was run");
                Log.i("CALEB","task success?: "+task.isSuccessful());
                Log.i("CALEB","last location: "+mLasKnownLocation);

                if (task.isSuccessful() && mLasKnownLocation != null) {
                    Log.i("CALEB", "task was successful");

                    mMap.addPolyline(new PolylineOptions().add(new LatLng(mLasKnownLocation.getLatitude(), mLasKnownLocation.getLongitude()),mDestinationLatLng));
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayMyLocation();
            }
        }
    }
}