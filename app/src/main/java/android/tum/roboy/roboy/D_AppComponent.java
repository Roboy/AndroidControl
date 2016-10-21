package android.tum.roboy.roboy;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {MotorModule.class, RosModule.class})
public interface D_AppComponent {
    void inject(WiringMotorAcitivity activity);
}
