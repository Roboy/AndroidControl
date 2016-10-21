package android.tum.roboy.roboy;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

/**
 * provide Application Context reference with through Dagger DI framework
 */
@Module
public class MotorModule {

    String mtestString;

    public AppModule(String testString){
        mtestString = testString;
    }

    @Provides
    @Singleton
    String providesTestString(){
        return mtestString  ;
    }
}

