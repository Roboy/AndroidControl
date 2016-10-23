package android.tum.roboy.roboy;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

/**
 *
 */
@Module
public class D_MotorModule {

    ArrayList<MotorItem> mMotorList;
    Motors mMotors;

    D_MotorModule(){
    }

    @Provides
    MotorItem providesMotorItem(){
        return new MotorItem();
    }

    @Provides
    @Singleton
    ArrayList<MotorItem> providesArrayList(){
        mMotorList = new ArrayList<MotorItem>();
        return mMotorList;

    }

    @Provides
    @Singleton
    Motors provideMotors(ArrayList<MotorItem> motorList, ROSBridge rosBridge){
        mMotors = new Motors(motorList, rosBridge);
        return mMotors;
    }
}

