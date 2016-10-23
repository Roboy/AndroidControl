package android.tum.roboy.roboy;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import javax.inject.Inject;


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

    private WiringClass                 mWiringClass;
    public ListView                     mLVMotors;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(DBG) Log.i(DEBUG_TAG, "onCreate called");


        mWiringClass = new WiringClass(this);
        mWiringClass.setupMotors(this);

        setContentView(R.layout.activity_chillout_lobby);
        mLVMotors = new ListView(this);
        mLVMotors = (ListView) findViewById(R.id.ListView_Motors);
        mLVMotors.setAdapter(mWiringClass.getAdapter());
        mLVMotors.setVisibility(View.INVISIBLE);

        mWiringClass.setupROS();


//        Button addMotorButton = (Button) findViewById(R.id.Button_ScanMotors);
//        addMotorButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(DBG) Log.i(DEBUG_TAG, "Adding a motor via QR-Scanner");
//                //startActivity(intent);
//            }
//        });
    }

    @Override
    public void wiringDone(Context context){
        if(DBG) Log.i(DEBUG_TAG, "Wiring done!");
        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
//                mLVMotors.setVisibility(View.VISIBLE);
            }
        });

    }
}
