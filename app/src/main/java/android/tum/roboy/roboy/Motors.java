package android.tum.roboy.roboy;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.ArrayList;

import javax.inject.Inject;

import edu.wpi.rail.jrosbridge.messages.Message;

/**
 * Created by sebtut on 21.10.16.
 * Wrapper class to provide an defined API to the motors (MotorItemAdapter + MotorItem)
 * of Roboy
 */
interface IMotors{
    void addMotor(int id, int position);
    void initAdapter(Context context, int ViewResourceID, Fragment frag);
    void setContext(Context context);
    MotorItemAdapter getAdapter();
}

interface IMessageRequest{
    void handleMsgMotorValues(Message message);
}

public class Motors implements IMotors, IMessageRequest{
    private static final String             DEBUG_TAG = "\t\tRO_MOTOR_WRAPPER";
    private static final boolean            DBG = true;

    private ArrayList<MotorItem>            mMotorItems;
    private Context                         mContext;
    private MotorItemAdapter                mMotorItemAdapter;
    private IMotorEvent                     mIWirMotors;

    /***********************************  CONSTRUCTORS *************************************/

    @Inject
    Motors(ArrayList<MotorItem> motorItems){
        if(DBG) Log.v(DEBUG_TAG, "Constructor called");
        mMotorItems = motorItems;
    }

    /***********************************  MOTORS INTERFACE (IMotors) *************************************/

    @Override
    public void initAdapter(Context context, int ViewResourceID, Fragment frag){
        if(null == context){
            throw new IllegalArgumentException(DEBUG_TAG + "setAdapter: context is null");
        }
        setContext(context);
        try{
            mIWirMotors = (IMotorEvent) frag;
        }catch (ClassCastException e){
            throw new ClassCastException(DEBUG_TAG + frag.toString() + "must implement the IMotorEvent Interface!");
        }
        mMotorItemAdapter = new MotorItemAdapter(mContext, ViewResourceID, mMotorItems, mIWirMotors);
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

    /***********************************  RECEIVING MESSAGE INTERFACE *************************************/

    @Override
    public void handleMsgMotorValues(Message message){
        if(DBG) Log.i(DEBUG_TAG, "\t\t\treceived message: " + message.toString());
    }
}
