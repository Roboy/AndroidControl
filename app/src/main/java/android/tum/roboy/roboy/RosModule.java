package android.tum.roboy.roboy;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.wpi.rail.jrosbridge.Ros;

@Module
public class RosModule{
    Ros mrosBridge;

    ROSModule(Ros rosBridge){
        mrosBridge = rosBridge;
    }

    @Provides
    @Singleton
    Ros provideROSBridge(){
        return mrosBridge;
    }
}
