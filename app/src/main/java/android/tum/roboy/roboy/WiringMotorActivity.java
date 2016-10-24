package android.tum.roboy.roboy;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


interface IWiringEvent{
    void wiringDone(Context context);
}

/**
 *  Instantiates the ROSbridge which is used to retrieve and send values to the ROS Master node
 *  via a defined Websocket protocol. The public Interface to ROS is defined via the ROSBridge class.
 *  To update and display different motors the Activitiy creates a ListView which is filled with
 *  the data provided through the Motors class. The Motors class provides a public Interface to the Roboy
 *  Motors.
 *  The WiringMotor-Activity thus is the first place / activity gathering the ROSbridge , the Motors and the UserInterface providing
 *  a central controlling unit.
 */
public class WiringMotorActivity extends AppCompatActivity implements IWiringEvent{

    private static final String         DEBUG_TAG = "\t\tRO_WIRING_ACTIVITY";
    private static final boolean        DBG = true;

    private WiringFragment              mSliders;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(DBG) Log.i(DEBUG_TAG, "onCreate called");
        setContentView(R.layout.fmotors);

        mSliders = new WiringFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, mSliders);
        fragmentTransaction.commit();
    }

    @Override
    public void wiringDone(Context context){
        if(DBG) Log.i(DEBUG_TAG, "Wiring done!");

        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mSliders.toggleVisibilityAdapter(true);
            }
        });

    }
}
