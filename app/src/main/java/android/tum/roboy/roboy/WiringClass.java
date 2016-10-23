package android.tum.roboy.roboy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import javax.inject.Inject;

import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;


interface IRosBridgeEvent{
    void updateConnectState(boolean connected);
}

interface IMotorEvent{
    void positionChanged(ArrayList<MotorItem> ArrayListMotorItems);
}



/**
 * Created by sebtut on 23.10.16.
 */
public class WiringClass  implements IRosBridgeEvent, IMotorEvent {


    private static final String         DEBUG_TAG = "\t\tRO_WIRING_CLASS";
    private static final boolean        DBG = true;
    private static final String         mTestTopic = "EchoBack";

    private boolean                     m1Topic = false;
    private IWiringEvent                mIWiringEvent;
    private Context                     mContext;

    @Inject ROSBridge                   mRosBridge;
    @Inject Motors                      mMotors;

    public WiringClass(Activity act){
        if(DBG) Log.i(DEBUG_TAG, "Constructor WiringClass");
        ((RoboyApp) act.getApplication()).getNetComponent().inject(this);
        mIWiringEvent = (IWiringEvent) act;
        mContext = act.getApplicationContext();
    }

    public void setupMotors(Activity act) {
        if(DBG) Log.i(DEBUG_TAG, "Setup Motors");
        mMotors.initAdapter(act, R.layout.list_motoritem, this);
        for(int i = 0 ; i < 16; ++i) {
            mMotors.addMotor(i, 0);
        }
    }

    public MotorItemAdapter getAdapter() {
        return mMotors.getAdapter();
    }

    public void setupROS() {
        if(DBG) Log.i(DEBUG_TAG, "Setup ROS");
        mRosBridge.setTopicCallback(new TopicCallback() {
            @Override
            public void handleMessage(Message message) {
                if(DBG) Log.i(DEBUG_TAG, "\t\t\treceived message: " + message.toString());
                mMotors.handleMsgMotorValues(message);
            }
        });
        mRosBridge.setWirActCallback(this);
        mRosBridge.initRos();
    }

    /****************************************** REACT TO EVENTS FROM THE ROSBRIDGE (IRosBridgeEvent) ***************************/

    @Override
    public void updateConnectState(boolean connected){
        if(connected){
            if(DBG) Log.i(DEBUG_TAG, "Activity received connected notification");
            setupTopics();
        }else{
            resetTopics();
            if(DBG) Log.i(DEBUG_TAG, "Activity received disconnect notification");
        }

    }

    /****************************************** REACT TO EVENTS FROM THE MOTORS ***************************/
    @Override
    public void positionChanged(ArrayList<MotorItem> ArrayListMotorItems) {
        if (!mRosBridge.isConnected()) {
            throw new IllegalArgumentException(DEBUG_TAG + "The RosBridge is not connected");
        }

        StringBuilder msgMotorValue = new StringBuilder();
        msgMotorValue.append("{\"data\": \"");
        String delim = "";
        for (MotorItem mi : ArrayListMotorItems) {
            msgMotorValue.append(delim + mi.getPosition());
            delim = ", ";
        }
        msgMotorValue.append("\"}");
        Message motorValues = new Message(msgMotorValue.toString());
        mRosBridge.publishTopic(mTestTopic, motorValues);
    }

    private void setupTopics(){
        if(!m1Topic){
            m1Topic = true;
            if(DBG) Log.i(DEBUG_TAG, "Rosbridge is able to subscribe topics. Subscribe to: " + mTestTopic);
            mRosBridge.addTopic(mTestTopic);
            mIWiringEvent.wiringDone(mContext);
        }
    }

    private void resetTopics(){
        if(m1Topic){
            m1Topic = false;
        }
    }

}
