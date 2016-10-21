package android.tum.roboy.roboy;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface NetComponent {
    void inject(MainActivity activity);
}
