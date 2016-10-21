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

    ArrayList<MotorItem> mMotors;

    MotorModule(){
    }

    @Provides
    MotorItem providesMotorItem(){
        return new MotorItem();
    }

    @Provides
    @Singleton
    ArrayList<MotorItem> providesArrayList(){
        mMotors = new ArrayList<MotorItem>();
        return mMotors;

    }
}

