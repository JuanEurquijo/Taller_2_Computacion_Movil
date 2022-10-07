package com.edu.compumovil.taller2.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.akexorcist.googledirection.constant.AvoidType;
import com.edu.compumovil.taller2.BuildConfig;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

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
    private LatLng userGoto;

    private Marker userMarker;
    private Marker pushMarker;

    List<Marker> places = new ArrayList<>();

    Polyline currentRoute;

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

                if (!locationIsReady) {
                    locationIsReady = true;
                    centerOnUser();
                }

            }
        });

        // Attach all buttons
        binding.centerButton.setOnClickListener(e -> centerOnUser());
        binding.searchButton.setOnClickListener(e -> findPlaces());
        binding.goButton.setOnClickListener(e -> paintRoute());
    }

    private void findPlaces() {
        if (!locationIsReady) {
            AlertsHelper.shortToast(this, getResources().getString(R.string.wait));
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            places.forEach(Marker::remove);
            places.clear();
            try {
                geocoderService.findPlacesByNameInRadius(Objects.requireNonNull(binding.textInputLayout.getEditText()).getText().toString(), userLocation).forEach(address -> {
                    userGoto = new LatLng(address.getLatitude(), address.getLongitude());
                    places.add(mGoogleMap.addMarker(new MarkerOptions()
                            .title(address.getFeatureName())
                            .snippet(address.getAddressLine(0))
                            .position(userGoto).icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))));

                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userGoto, 15));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!places.isEmpty()) {
                binding.goButton.setEnabled(true);
            }
        }
    }

    private void paintRoute() {
        AlertsHelper.shortSimpleSnackbar(binding.getRoot().getRootView(), getResources().getString(R.string.wait));
        GoogleDirection.withServerKey(BuildConfig.MAPS_API_KEY)
                .from(userLocation).to(userGoto)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(@Nullable Direction direction) {
                        if (direction != null && direction.isOK()) {
                            if (currentRoute != null)
                                currentRoute.remove();

                            // Obtener y dibujar la dirección
                            ArrayList<LatLng> directions = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                            currentRoute = mGoogleMap.addPolyline(DirectionConverter.createPolyline(
                                    getApplicationContext(), directions, 4, Color.MAGENTA
                            ));
                            mGoogleMap.moveCamera(CameraUpdateFactory.zoomOut());

                        } else {
                            AlertsHelper.shortSimpleSnackbar(binding.getRoot().getRootView(), getResources().getString(R.string.is_not_ok));
                        }
                    }

                    @Override
                    public void onDirectionFailure(@NonNull Throwable t) {
                        AlertsHelper.shortSimpleSnackbar(binding.getRoot().getRootView(), getResources().getString(R.string.does_not_exist));
                    }
                });
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
                    .title(addressList.get(0).getFeatureName())
                    .snippet(addressList.get(0).getAddressLine(0))
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    ));

            binding.goButton.setEnabled(true);
            userGoto = latLng;
        });
    }
}