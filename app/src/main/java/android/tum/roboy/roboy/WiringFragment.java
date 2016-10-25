package android.tum.roboy.roboy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import javax.inject.Inject;

import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;


interface IRosBridgeEvent{
    void updateConnectState(boolean connected);
}

interface IMotorEvent {
    void positionChanged(ArrayList<MotorItem> ArrayListMotorItems);
}


/**
 * Created by sebtut on 23.10.16.
 */
public class WiringFragment extends Fragment implements IRosBridgeEvent, IMotorEvent {

    private static final String         DEBUG_TAG = "\t\tRO_WIRING_FRAGMENT";
    private static final boolean        DBG = true;
    private static final String         mTestTopic = "roboy/motor_cmd";

    private boolean                     m1Topic = false;
    private IWiringEvent_Slider         mIWir_Slider;
    private Context                     mContext;
    private Activity                    mCallingActivity;
    public ListView                     mLVMotors;
    private View                        mRootView;


    @Inject ROSBridge                   mRosBridge;
    @Inject Motors                      mMotors;

    public WiringFragment(){
        if(DBG) Log.i(DEBUG_TAG, "Constructor!");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallingActivity = getActivity();
        mContext = context;
        ((RoboyApp) mCallingActivity.getApplication()).getNetComponent().inject(this);

        try{
            mIWir_Slider = (IWiringEvent_Slider) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){

        mRootView = inflater.inflate(R.layout.motorslidersfragment, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);

        setupMotors();
        mLVMotors = new ListView(mContext);
        mLVMotors = (ListView) mRootView.findViewById(R.id.ListView_Motors);
        mLVMotors.setAdapter(getAdapter());
        mLVMotors.setVisibility(View.INVISIBLE);

        Button startQrScanner = (Button) mRootView.findViewById(R.id.Button_ScanMotors);
        startQrScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIWir_Slider.setQRFragment(mContext);
            }
        });
        setupROS();
    }


    private void setupMotors() {
        if(DBG) Log.i(DEBUG_TAG, "Setup Motors");

        if(mMotors == null){
            return ;
        }
        if(DBG) Log.i(DEBUG_TAG, "Motors.toString(): " + mMotors.toString());
        mMotors.initAdapter(mContext, R.layout.list_motoritem, this);
        for(int i = 0 ; i < 16; ++i) {
            mMotors.addMotor(i, 0);
        }
    }

    private MotorItemAdapter getAdapter() {
        return mMotors.getAdapter();
    }

    private void setupROS() {
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

    public void toggleVisibilityAdapter(boolean visible){
        if(visible){
            mLVMotors.setVisibility(View.VISIBLE);
        }else{
            mLVMotors.setVisibility(View.INVISIBLE);
        }
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
            mIWir_Slider.wiringDone(mContext);
        }
    }

    private void resetTopics(){
        if(m1Topic){
            m1Topic = false;
        }
    }

}
