package android.tum.roboy.roboy;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.wpi.rail.jrosbridge.Ros;

@Module
public class D_RosModule{
    Ros mrosBridge;

    D_RosModule(Ros rosBridge){
        mrosBridge = rosBridge;
    }

    @Provides
    @Singleton
    Ros provideROSBridge(){
        return mrosBridge;
    }
}
