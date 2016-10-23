package android.tum.roboy.roboy;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.java_websocket.handshake.ServerHandshake;
import java.util.ArrayList;
import javax.inject.Inject;

import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.ConnectionCallback;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;

/**
 *  Instantiates the ROSbridge which is used to retrieve and send values to the ROS Master node
 *  via a defined Websocket protocol. The public Interface to ROS is defined via the ROSBridge class.
 *  To update and display different motors the Activitiy creates the a ListView which is filled with
 *  the data provided through the Motors class. The Motors class provides a public Interface to the Roboy
 *  Motors.
 *  The WiringMotor-Activity thus is the first place / activity gatering the ROSbridge , the Motors and the UserInterface providing
 *  further actions.
 */
public class WiringMotorActivity extends AppCompatActivity{

    private static final String     DEBUG_TAG = "\t\tROBOY_WIRING_MOTORS";
    private static final boolean    DBG = true;
    private ListView                listView;

    @Inject ROSBridge               mRosBridge;
    @Inject Motors                  mMotors;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(DBG) Log.i(DEBUG_TAG, "onCreate called");

        super.onCreate(savedInstanceState);
        ((RoboyApp) getApplication()).getNetComponent().inject(this);

        setupToolbar();
        setupWindowAnimations();
        setupROS();
        initMotors();

        setContentView(R.layout.activity_chillout_lobby);
        listView = (ListView) findViewById(R.id.ListView_Motors);
        listView.setAdapter(mMotors.getAdapter());

        Button addMotorButton = (Button) findViewById(R.id.Button_ScanMotors);
        addMotorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DBG) Log.i(DEBUG_TAG, "Adding a motor via QR-Scanner");
                //startActivity(intent);
            }
        });
    }

    private void initMotors() {
        if(DBG) Log.i(DEBUG_TAG, "Init Motors");
        //TODO: Get MotorItems via the rosbridge
        mMotors.initAdapter(this, R.layout.list_motoritem);
        for(int i = 0 ; i < 16; ++i){
            mMotors.addMotor(i, 0);
        }
    }

    private void setupROS() {
        mRosBridge.initConnectionCallback(mMotors);
        mRosBridge.initRos(this);
        if(mRosBridge.isConnected()){
            if(DBG) Log.i(DEBUG_TAG, "Connected to ROS Master");
        } else {
            if(DBG) Log.i(DEBUG_TAG, "No valid ROS Master");
        }

    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
    }

    @TargetApi(21)
    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= 21) {
            Fade fade = new Fade();
            fade.setDuration(2000);
            getWindow().setExitTransition(fade);
            if(DBG) Log.i(DEBUG_TAG, "TRANSITION CALLED");
        }
    }
}
