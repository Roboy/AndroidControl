package android.tum.roboy.roboy;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


interface IWiringEvent_Slider{
    void wiringDone(Context context);
    void setQRFragment(Context context);
}

interface IWiringEvent_QR{
    void motorDetected(int id);
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
public class WiringMotorActivity extends AppCompatActivity implements IWiringEvent_Slider, IWiringEvent_QR{

    private static final String         DEBUG_TAG = "\t\tRO_WIRING_ACTIVITY";
    private static final boolean        DBG = true;

    private WiringFragment              mSliders;
    private QRScannerFragment           mQRScanner;

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

    @Override
    public void setQRFragment(Context context) {
        if (DBG) Log.i(DEBUG_TAG, "Setting the QR Scanner fragment");

        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mQRScanner = new QRScannerFragment();
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, mQRScanner);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public void motorDetected(int id){
        if(DBG) Log.i(DEBUG_TAG, "New Motor detected (QR CODE SCANNED!");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_emergency:
                if(DBG) Log.i(DEBUG_TAG, "item id:" +  item.toString());
                if(DBG) Log.i(DEBUG_TAG, "Emergency detected!");
                return true;

            case R.id.Position:
                if(DBG) Log.i(DEBUG_TAG, "item id:" +  item.toString());
                if(DBG) Log.i(DEBUG_TAG, "Position detected!");
                mSliders.setAdapter(Motors.MotorAdapter.POSITION);
                mSliders.setToolbarTitle("POSITION");
                return true;

            case R.id.Force:
                if(DBG) Log.i(DEBUG_TAG, "item id:" +  item.toString());
                if(DBG) Log.i(DEBUG_TAG, "Velocity detected!");
                mSliders.setAdapter(Motors.MotorAdapter.FORCE);
                mSliders.setToolbarTitle("FORCE");
                return true;

            case R.id.Velocity:
                if(DBG) Log.i(DEBUG_TAG, "item id:" +  item.toString());
                if(DBG) Log.i(DEBUG_TAG, "Force detected!");
                mSliders.setAdapter(Motors.MotorAdapter.VELOCITY);
                mSliders.setToolbarTitle("VELOCITY");
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.slidercontrol_menu, menu);
        return  true;
    }
}
