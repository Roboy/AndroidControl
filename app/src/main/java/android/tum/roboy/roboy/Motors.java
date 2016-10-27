package android.tum.roboy.roboy;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

import edu.wpi.rail.jrosbridge.messages.Message;

/**
 * Created by sebtut on 21.10.16.
 * Wrapper class to provide an defined API to the motors (MotorItemAdapter + MotorItem)
 * of Roboy
 */

interface IMotors{
    void addMotor(int id, int position, int force, int velocity);
    void initAdapter(Context context, int ViewResourceID, Fragment frag);
    void setContext(Context context);
    ArrayAdapter<MotorItem> getAdapter(Motors.MotorAdapter mode);
    int size();
}


interface IMessageRequest{
    void handleMsgMotorValues(Message message);
}

public class Motors implements IMotors, IMessageRequest{
    private static final String             DEBUG_TAG = "\t\tRO_MOTOR_WRAPPER";
    private static final boolean            DBG = true;

    public
    enum MotorAdapter{
        FORCE,
        POSITION,
        VELOCITY,
    };

    private ArrayList<MotorItem>            mMotorItems;
    private Context                         mContext;
    private MotorItemAdapter                mMotorItemAdapter;
    private MIAdapter_Position              mMIAdpater_Position;
    private MIAdapter_Velocity              mMIAdpater_Velocity;
    private MIAdapter_Force                 mMIAdpater_Force;
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
        mMotorItemAdapter       = new MotorItemAdapter(mContext, ViewResourceID, mMotorItems, mIWirMotors);
        mMIAdpater_Force        = new MIAdapter_Force(mContext, ViewResourceID, mMotorItems, mIWirMotors);
        mMIAdpater_Position     = new MIAdapter_Position(mContext, ViewResourceID, mMotorItems, mIWirMotors);
        mMIAdpater_Velocity     = new MIAdapter_Velocity(mContext, ViewResourceID, mMotorItems, mIWirMotors);
    }

    @Override
    public void addMotor(int id, int position, int force, int velocity){
        if(null == mMotorItemAdapter){
            throw new IllegalArgumentException(DEBUG_TAG + "addMotor: Adapter is null / not initialized");
        }
        //TODO: Check Dagger2 Documentation / Tutorial in order to avoid the new keyword / new MotorItem
        mMotorItemAdapter.add(new MotorItem(id, position, force, velocity));
    }

    @Override
    public int size(){
        return mMotorItemAdapter.getCount();
    }
    @Override
    public void setContext(Context context){
        mContext = context;
    }

    @Override
    public ArrayAdapter<MotorItem> getAdapter(MotorAdapter mode){
        switch (mode){
            case FORCE:
                return mMIAdpater_Force;
            case POSITION:
                return mMIAdpater_Position;
            case VELOCITY:
                return mMIAdpater_Velocity;
        }
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
