package com.edu.compumovil.taller2.dependencies.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import com.edu.compumovil.taller2.utils.PermissionHelper;

@Module
public class PermissionModule {

    @Singleton
    @Provides
    public PermissionHelper providePermissionHelper() {
        return new PermissionHelper();
    }
}