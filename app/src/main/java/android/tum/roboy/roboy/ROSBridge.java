package android.tum.roboy.roboy;

import android.support.v4.app.Fragment;
import android.util.Log;

import org.java_websocket.handshake.ServerHandshake;

import java.util.HashMap;

import javax.inject.Inject;

import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.ConnectionCallback;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;

/**
 * Created by sebtut on 21.10.16.
 * API to the ROS Bridge. Manages the ros-connection, sending and receiving of topics
 */
interface IRosBridge{

    void initRos();
    void addTopic(String topic);
    void publishTopic(String topic, Message message);

    void setTopicCallback(TopicCallback topicCallback);
    void setConnectionCallback(ConnectionCallback cCallback);

    boolean isConnected();
    String getHostname();
    int getPort();

    //TODO: change library or write wrapper function in oder to change the ROSBridge client during runtime
    /*void setPort(int port);
    void setHostname(String hostname);*/
}

public class ROSBridge implements IRosBridge{

    private static final String         DEBUG_TAG = "\t\tRO_ROS_BRIDGE";
    private static final boolean        DBG = true;

    private ConnectionCallback          mConnectionCallback;
    private Ros                         mRos;
    private HashMap<String,Topic>       mTopicMap;
    private IRosBridgeEvent             mIWirAct;
    private RosCallbacks                mRosCallback;
    private Topic                       mTopic;
    private TopicCallback               mTopicCallback;


    private class RosCallbacks implements ConnectionCallback{
        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            if(DBG) Log.i(DEBUG_TAG, "ROSBridge Connection open : " + mRos.isConnected());
            ROSBridge.this.mIWirAct.updateConnectState(true);
        }

        @Override
        public void onClose(int i, String s, boolean b) {
            if(DBG) Log.i(DEBUG_TAG, "Disconnected from ROS Server / Websocket");
        }

        @Override
        public void onError(Exception e) {
            System.out.println("on error");
            e.printStackTrace();
        }
    }

    /********************************** CONSTRUCTOR *****************************************/

    @Inject
    ROSBridge(Ros ros, HashMap<String, Topic> topicMap){
        if(DBG) Log.v(DEBUG_TAG, "Constructor called");
        mRos = ros;
        mTopicMap = topicMap;
        mRosCallback = new RosCallbacks();
    }

    @Override
    public void initRos(){
        if(null == mRos){
            throw new IllegalArgumentException(DEBUG_TAG + "initRos: ROS is null");
        } else if ( null == mRosCallback) {
            throw new IllegalArgumentException(DEBUG_TAG + "initRos: ConnectionCallback is null");
        }
        if(DBG) Log.i(DEBUG_TAG, "Trying to connect....");
        mRos.connect(mRosCallback);
    }

    @Override
    public void publishTopic(String topic, Message message){
        Topic motorPosition = mTopicMap.get(topic);
        motorPosition.publish(message);
        if (DBG) Log.i(DEBUG_TAG, "\t\t\t\t publish: " + message.toString());
    }

    @Override
    public void addTopic(String topic){
        mTopic = new Topic(mRos, topic, "std_msgs/String");
        mTopicMap.put(topic, mTopic);
        mTopic.subscribe(mTopicCallback);
        if(DBG) Log.i(DEBUG_TAG, "Added Topic: " + topic);
    }

    /************************************** SETTER **************************************/

    @Override
    public void setConnectionCallback(final ConnectionCallback connectionCallback){
        if(DBG) Log.v(DEBUG_TAG, "Setting Callback for (ROS) connectionCallbacks.");
        try {
            mConnectionCallback = connectionCallback;
        } catch (ClassCastException e) {
            throw new ClassCastException(connectionCallback.toString()
                    + " must implement ConnectionCallback interface");
        }
    }

    public void setWirActCallback(final Fragment frag){
        if(DBG) Log.v(DEBUG_TAG, "Setting Callback for WiringActivity.");
        try{
            mIWirAct = (IRosBridgeEvent) frag;
        }catch(ClassCastException e) {
            throw new ClassCastException(frag.toString()  +"must implmemnt IRosBridgeEvent interface");
        }
    }

    @Override
    public void setTopicCallback(TopicCallback topicCallback){
        mTopicCallback = topicCallback;
    }

    /************************************** GETTER **************************************/

    @Override
    public boolean isConnected(){
        return mRos.isConnected();
    }

    @Override
    public int getPort(){
        if(null == mRos){
            throw new IllegalArgumentException(DEBUG_TAG + "getPort: ROS is null");
        }
        return mRos.getPort();
    }

    @Override
    public String getHostname(){
        if(null == mRos){
            throw new IllegalArgumentException(DEBUG_TAG + " getPort: ROS is null");
        }
        return mRos.getHostname();
    }
}
