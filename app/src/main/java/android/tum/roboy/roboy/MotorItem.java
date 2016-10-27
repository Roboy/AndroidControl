package android.tum.roboy.roboy;

import android.util.Log;

/**
 * Created by sebtut on 12.09.16.
 * Each MotorItem should respectively represent one of many Motors from the Roboy-corpus
 * Used together
 */
public class MotorItem {
    private int mID;
    private int mPosition;
    private int mVelocity;
    private int mForce;
    private boolean mIint;

    private static final String DEBUG_TAG = "\t\tROBOY_MOTORITEM";
    private static final boolean DBG = true;

    public MotorItem() {
        if (DBG) Log.i(DEBUG_TAG, "Constructor called");
    }

    public MotorItem(int id, int position, int force, int velocity) {
        if (DBG) Log.i(DEBUG_TAG, "Constructor called");
        this.mID = id;
        this.mPosition = position;
        this.mVelocity = velocity;
        this.mForce = force;
        this.mIint = true;
    }

    // Setter methods
    public void setID(int id) {
        this.mID = id;
    }
    public void setmPosition(int position) {
        this.mPosition = position;
    }
    public void setVelocity(int veloctiy) { this.mVelocity = veloctiy;}
    public void setmForce(int force) { this.mForce = force;}
    public void setInit(boolean init) {this.mIint = init;}

    //Getter methods

    public boolean getInit() { return this.mIint;}
    public int getID() {
        return this.mID;
    }
    public int getPosition() {return this.mPosition;}
    public int getVelocity(){return this.mVelocity;}
    public int getForce(){ return this.mForce;}
}

