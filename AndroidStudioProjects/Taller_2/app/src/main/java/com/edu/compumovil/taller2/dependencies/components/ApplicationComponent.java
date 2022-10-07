package com.edu.compumovil.taller2.dependencies.components;

import javax.inject.Singleton;

import dagger.Component;
import com.edu.compumovil.taller2.activities.BasicActivity;
import com.edu.compumovil.taller2.dependencies.modules.AlertsModule;
import com.edu.compumovil.taller2.dependencies.modules.GeoInfoModule;
import com.edu.compumovil.taller2.dependencies.modules.GeocoderModule;
import com.edu.compumovil.taller2.dependencies.modules.LocationModule;
import com.edu.compumovil.taller2.dependencies.modules.PermissionModule;

@Singleton
@Component(modules = {AlertsModule.class, PermissionModule.class,
        GeoInfoModule.class, GeocoderModule.class, LocationModule.class})
public interface ApplicationComponent {
    void inject(BasicActivity activity);
}
