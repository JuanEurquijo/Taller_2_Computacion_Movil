package com.edu.compumovil.taller2.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import com.edu.compumovil.taller2.App;
import com.edu.compumovil.taller2.services.LocationService;
import com.edu.compumovil.taller2.utils.AlertsHelper;
import com.edu.compumovil.taller2.utils.PermissionHelper;


public class BasicActivity extends AppCompatActivity {
    @Inject
    AlertsHelper alertsHelper;

    @Inject
    PermissionHelper permissionHelper;

    @Inject
    LocationService locationService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((App) getApplicationContext()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
    }
}