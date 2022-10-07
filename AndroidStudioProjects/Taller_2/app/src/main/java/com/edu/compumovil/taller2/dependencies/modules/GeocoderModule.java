package com.edu.compumovil.taller2.dependencies.modules;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import com.edu.compumovil.taller2.services.GeocoderService;
import lombok.AllArgsConstructor;

@Module
@AllArgsConstructor
public class GeocoderModule {
    private final Application application;

    @Provides
    @Singleton
    Application providesApplication(){
        return application;
    }

    @Provides
    public GeocoderService provideGeocoderService() {
        return new GeocoderService(application.getApplicationContext());
    }
}
