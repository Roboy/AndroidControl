package android.tum.roboy.roboy;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import javax.inject.Inject;

import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;


interface IRosBridgeEvent{
    void updateConnectState(boolean connected);
}

interface IMotorEvent {
    void positionChanged(MotorItem motorItem);
    void velocityChanged(MotorItem motorItem);
    void forceChanged(MotorItem motorItem);
}


/**
 * Created by sebtut on 23.10.16.
 */
public class WiringFragment extends Fragment implements IRosBridgeEvent, IMotorEvent {

    private static final String         DEBUG_TAG = "\t\tRO_WIRING_FRAGMENT";
    private static final boolean        DBG = true;
    private static final String         mVeloctiyT = "/roboy/motor_cmd_vel";
    private static final String         mForceT = "/roboy/motor_cmd_pos";
    private static final String         mpositionT = "/roboy/motor_cmd_force";

    private boolean                     m1Topic = false;
    private IWiringEvent_Slider         mIWir_Slider;
    private Context                     mContext;
    private AppCompatActivity           mCallingActivity;
    public ListView                     mLVMotors;
    private View                        mRootView;
    private Toolbar                     mToolbar;


    @Inject ROSBridge                   mRosBridge;
    @Inject Motors                      mMotors;

    public WiringFragment(){
        if(DBG) Log.i(DEBUG_TAG, "Constructor!");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mCallingActivity = (AppCompatActivity) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must be an AppCompatActivity");
        }

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
        mLVMotors = (ListView) mRootView.findViewById(R.id.ListView_Motors);
        setAdapter(Motors.MotorAdapter.POSITION);
        mLVMotors.setVisibility(View.INVISIBLE);

        mToolbar =  (Toolbar) mRootView.findViewById(R.id.MotorSlider_toolbar);
        this.setToolbarTitle("POSITION");
        mCallingActivity.setSupportActionBar(mToolbar);

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
        for(int i = mMotors.size() ; i < 16; ++i) {
            mMotors.addMotor(i, 0, 0, 0);
        }
    }

    public void setAdapter(Motors.MotorAdapter mode) {
        try{
            mLVMotors.setAdapter(mMotors.getAdapter(mode));
        }catch (NullPointerException e){
            if(DBG) Log.i(DEBUG_TAG, e.toString() + "ListView " + mLVMotors.toString() + "not valid");
        }
    }

    public void setToolbarTitle(String name){
        mToolbar.setTitle(name);
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

    private class MotorCommand{
        public double   setpoint;
        public int      id;

        public MotorCommand(int value, int id_){
            if(DBG) Log.i(DEBUG_TAG, "Create Motor Command with: " + value + "id: " + id);
            id = new Integer(id_);
            setpoint = new Integer(value);
        }
    }

    @Override
    public void positionChanged(MotorItem motorItem) {
        if (!mRosBridge.isConnected()) {
            throw new IllegalArgumentException(DEBUG_TAG + "The RosBridge is not connected");
        }

        String msgMotorValue = new String();
        MotorCommand cmnd = new MotorCommand(motorItem.getPosition(), motorItem.getID());
        ObjectMapper om = new ObjectMapper();
        try{
            msgMotorValue = om.writeValueAsString(cmnd);
        }catch(Exception e){
            if(DBG) Log.i(DEBUG_TAG, "cannot convert " + cmnd.toString() + "to JSON Object");
        }
        Message motorValues = new Message(msgMotorValue);
        mRosBridge.publishTopic(mpositionT, motorValues);
    }

    @Override
    public void velocityChanged(MotorItem motorItem) {
        if (!mRosBridge.isConnected()) {
            throw new IllegalArgumentException(DEBUG_TAG + "The RosBridge is not connected");
        }

        String msgMotorValue = new String();
        MotorCommand cmnd = new MotorCommand(motorItem.getVelocity(), motorItem.getID());
        ObjectMapper om = new ObjectMapper();
        try{
            msgMotorValue = om.writeValueAsString(cmnd);
        }catch(Exception e){
            if(DBG) Log.i(DEBUG_TAG, "cannot convert " + cmnd.toString() + "to JSON Object");
        }
        Message motorValues = new Message(msgMotorValue);
        mRosBridge.publishTopic(mVeloctiyT, motorValues);
    }

    @Override
    public void forceChanged(MotorItem motorItem) {
        if (!mRosBridge.isConnected()) {
            throw new IllegalArgumentException(DEBUG_TAG + "The RosBridge is not connected");
        }

        String msgMotorValue = new String();
        MotorCommand cmnd = new MotorCommand(motorItem.getForce(), motorItem.getID());
        ObjectMapper om = new ObjectMapper();
        try{
            msgMotorValue = om.writeValueAsString(cmnd);
        }catch(Exception e){
            if(DBG) Log.i(DEBUG_TAG, "cannot convert " + cmnd.toString() + "to JSON Object");
        }
        Message motorValues = new Message(msgMotorValue);
        mRosBridge.publishTopic(mForceT, motorValues);
    }

    private void setupTopics(){
        if(!m1Topic){
            m1Topic = true;
            mIWir_Slider.wiringDone(mContext);
        }
    }

    private void resetTopics(){
        if(m1Topic){
            m1Topic = false;
        }
    }

}
