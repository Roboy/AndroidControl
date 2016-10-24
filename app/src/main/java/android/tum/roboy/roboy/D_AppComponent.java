package android.tum.roboy.roboy;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {D_MotorModule.class, D_RosModule.class})
public interface D_AppComponent {
//    void inject(WiringMotorActivity activity);
    void inject(WiringFragment WiringFragment);
}
