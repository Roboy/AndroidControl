package android.tum.roboy.roboy;

import android.app.Application;
import android.content.Context;
import android.widget.SeekBar;

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
    ArrayList<MotorItem> providesArrayListMotorItem(){
        mMotorList = new ArrayList<>();
        return mMotorList;
    }

    @Provides
    Motors provideMotors(ArrayList<MotorItem> motorList){
        mMotors = new Motors(motorList);
        return mMotors;
    }
}

