package android.tum.roboy.roboy;

import android.app.Application;
import edu.wpi.rail.jrosbridge.Ros;

/**
 * Created by sebtut on 21.10.16.
 */
public class RoboyApp extends Application {
    private D_AppComponent mAppComponent;

    @Override
    public void onCreate(){
        super.onCreate();

        mAppComponent = DaggerD_AppComponent.builder()
                .d_MotorModule(new D_MotorModule())
                .d_RosModule(new D_RosModule(new Ros("192.168.2.109", 9090)))
                .build();
    }

    public D_AppComponent getNetComponent(){
        return mAppComponent;
    }
}
