package com.edu.compumovil.taller2.dependencies.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import com.edu.compumovil.taller2.utils.AlertsHelper;

@Module
public class AlertsModule {
    @Singleton
    @Provides
    public AlertsHelper provideAlertHelper() {
        return new AlertsHelper();
    }
}