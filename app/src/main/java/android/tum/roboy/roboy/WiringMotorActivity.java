package android.tum.roboy.roboy;

import android.annotation.TargetApi;
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

import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.ConnectionCallback;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;

/**
 *  Instantiates the ROSbridge which is used to retrieve and send values to the ROS Master node
 *  via a defined Websocket protocol. To update and display different motors the Activitiy
 *  creates the a ListView with and the underlying Adapter, which provides the data for each Motor
 *  It thus is the first place / activity wiring the ROSbridge, Motors and the UserInterface providing
 *  further actions.
 */
public class WiringMotorAcitivity extends AppCompatActivity{

    private static final String DEBUG_TAG = "ROBOY_WIRING_MOTORS";
    private static final boolean DBG = true;
    private ListView listView;

    private MotorItemAdapter mMotorItemAdapter;
    @Inject ArrayList<MotorItem> mMotorItems;
    @Inject Ros mrosBridge;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(DBG) Log.i(DEBUG_TAG, "Constructor called");
        super.onCreate(savedInstanceState);
        // inject the instances with DI framework
        ((RoboyApp) getApplication()).getNetComponent().inject(this);

        setupToolbar();
        setupWindowAnimations();
        setupROS();
        setupMotors();

        setContentView(R.layout.activity_chillout_lobby);
        listView = (ListView) findViewById(R.id.ListView_Motors);
        listView.setAdapter(mMotorItemAdapter);

        Button addMotorButton = (Button) findViewById(R.id.Button_ScanMotors);
        addMotorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DBG) Log.i(DEBUG_TAG, "Adding a motor via QR-Scanner");
                //startActivity(intent);
            }
        });
    }

    private void setupMotors() {
        if(DBG) Log.i(DEBUG_TAG, "Setting up motors");
        //TODO: Get MotorItems via the rosbridge
        mMotorItems.add(new MotorItem(0, 20));
        mMotorItems.add(new MotorItem(1, 50));
        mMotorItems.add(new MotorItem(2, 80));
        mMotorItemAdapter = new MotorItemAdapter(this , R.layout.list_motoritem, mMotorItems);
    }

    private void setupROS() {
        if(DBG) Log.i(DEBUG_TAG, "Setting up ROS");
        mrosBridge.connect(new ConnectionCallback() {
            public void onOpen(ServerHandshake serverHandshake) {
                Topic topic = new Topic(mrosBridge, "/echo_back", "std_msgs/String");
                topic.subscribe(new TopicCallback() {
                    public void handleMessage(Message message) {
                        System.out.println(message);
                        if(DBG) Log.i(DEBUG_TAG, "MESSAGE RECEIVED: " + message.toString());
                    }
                });
                Message toSend= new Message("{\"data\": \"hello, world!\"}");
                topic.publish(toSend);
                if(DBG) Log.i(DEBUG_TAG, "Connection open");
            }

            public void onClose(int i, String s, boolean b) {
                System.out.println("on close");
            }

            public void onError(Exception e) {
                System.out.println("on error");
                e.printStackTrace();
            }
        });
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
