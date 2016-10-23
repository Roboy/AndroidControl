package android.tum.roboy.roboy;

import android.content.Context;
import android.util.Log;

import junit.framework.Assert;

import java.util.ArrayList;

import javax.inject.Inject;

import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.messages.Message;

/**
 * Created by sebtut on 21.10.16.
 * Wrapper class to provide an defined API the motors (MotorItemAdapter + MotorItem)
 * of Roboy
 */
interface IMotors{
    void addMotor(int id, int position);
    void initAdapter(Context context, int ViewResourceID);
    void setContext(Context context);
    MotorItemAdapter getAdapter();
}

interface IMessageRequest{
    void handleMessage(Message message, Topic topic);
}

public class Motors implements IMotors, IMessageRequest{
    private static final String DEBUG_TAG = "\t\tROBOY_MOTOR_WRAPPER";
    private static final boolean DBG = true;

    private ArrayList<MotorItem> mMotorItems;
    private Context mContext;
    private MotorItemAdapter mMotorItemAdapter;
    private ROSBridge mRosBridge;


    @Inject
    Motors(ArrayList<MotorItem> motorItems, ROSBridge rosBridge){
        if(DBG) Log.v(DEBUG_TAG, "Constructor called");
        mMotorItems = motorItems;
        mRosBridge = rosBridge;
    }

    @Override
    public void initAdapter(Context context, int ViewResourceID){
        if(null == context){
            throw new IllegalArgumentException(DEBUG_TAG + "setAdapter: context is null");
        }
        setContext(context);
        mMotorItemAdapter = new MotorItemAdapter(mContext, ViewResourceID, mMotorItems, mRosBridge);
    }

    @Override
    public void handleMessage(Message message, Topic topic){
        if(DBG) Log.i(DEBUG_TAG, "\t\treceived message: " + message.toString()
                + " from topic: " + topic);
    }

    @Override
    public void addMotor(int id, int position){
        if(null == mMotorItemAdapter){
            throw new IllegalArgumentException(DEBUG_TAG + "addMotor: Adapter is null / not initialized");
        }
        //TODO: Check Dagger2 Documentation / Tutorial in order to avoid the new keyword / new MotorItem
        mMotorItemAdapter.add(new MotorItem(id, position));
    }

    @Override
    public void setContext(Context context){
        mContext = context;
    }

    @Override
    public MotorItemAdapter getAdapter(){
        if(null == mMotorItemAdapter){
            throw new IllegalArgumentException(DEBUG_TAG + "getAdapter: Adapter is null / not initialized");
        }
        return mMotorItemAdapter;
    }

}
