package com.edu.compumovil.taller2.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.edu.compumovil.taller2.R;
import com.edu.compumovil.taller2.databinding.ActivityMapBinding;
import com.edu.compumovil.taller2.services.GeocoderService;
import com.edu.compumovil.taller2.utils.AlertsHelper;
import com.edu.compumovil.taller2.utils.PermissionHelper;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

@SuppressLint("MissingPermission")
public class MapActivity extends BasicActivity implements OnMapReadyCallback {

    public static final String TAG = MainActivity.class.getName();
    private ActivityMapBinding binding;

    // Maps related
    private GoogleMap mGoogleMap;
    private final int USER_ZOOM_LEVEL = 18;
    private boolean locationIsReady = false;

    private LatLng userLocation;
    private Marker userMarker;
    private Marker pushMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Create and inflate
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Append Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);


        // Location Callback
        locationService.setLocationCallback(new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                setUserLocation(locationResult);

                if (!locationIsReady) centerOnUser();
                locationIsReady = true;
            }
        });

        // Attach all buttons
        binding.centerButton.setOnClickListener(e -> centerOnUser());
    }

    private void centerOnUser() {
        if (!locationIsReady) {
            AlertsHelper.shortToast(this, getResources().getString(R.string.wait));
            return;
        }

        if (userMarker != null)
            userMarker.remove();

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(USER_ZOOM_LEVEL));

        userMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(userLocation)
                .title(getResources().getString(R.string.position)));
        if (userMarker != null)
            userMarker.setVisible(true);
    }

    private void setUserLocation(LocationResult result) {
        this.userLocation = new LatLng(Objects.requireNonNull(result.getLastLocation()).getLatitude(),
                result.getLastLocation().getLongitude());
    }

    @Override
    protected void onStart() {
        super.onStart();
        permissionHelper.getLocationPermission(this);
        if (permissionHelper.isMLocationPermissionGranted()) {
            locationService.startLocation();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationService.stopLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.PERMISSIONS_LOCATION) {
            permissionHelper.getLocationPermission(this);
            if (permissionHelper.isMLocationPermissionGranted()) {
                locationService.startLocation();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_day_style));

        mGoogleMap.setOnMapLongClickListener(latLng -> {
            List<Address> addressList = new ArrayList<>();

            try {
                addressList = geocoderService.findPosition(latLng);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (pushMarker != null)
                pushMarker.remove();

            pushMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(addressList.get(0)
                            .getAddressLine(0)));
        });
    }
}