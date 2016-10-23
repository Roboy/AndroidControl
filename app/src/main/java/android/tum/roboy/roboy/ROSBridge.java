package android.tum.roboy.roboy;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.java_websocket.handshake.ServerHandshake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
interface IROS{
    boolean isConnected();
    void initRos(final Activity CallingActivity);
    void setConnectionCallback(ConnectionCallback connectionCallback);

    //TODO: change library or write wrapper function in oder to change the ROSBridge client during runtime
    /*void setPort(int port);
    void setHostname(String hostname);*/
    String getHostname();
    int getPort();
}

interface IHandlePositionRequest{
    void positionChanged(ArrayList<MotorItem> ArrayListMotorItems);
}

public class ROSBridge implements IROS, IHandlePositionRequest{

    private static final String         DEBUG_TAG = "\t\tROBOY_ROS_WRAPPER";
    private static final boolean        DBG = true;
    private static final String         mTestTopic = "EchoBack";
    private static final ObjectMapper   mObjectMapper = new ObjectMapper();

    private ConnectionCallback          mConnectionCallback;
    private Ros                         mRos;
    private HashMap<String,Topic>       mTopicMap;

    @Inject
    ROSBridge(Ros ros, HashMap<String, Topic> topicMap){
        if(DBG) Log.v(DEBUG_TAG, "Constructor called");
        mRos = ros;
        mTopicMap = topicMap;
    }

    @Override
    @Inject
    public void setConnectionCallback(ConnectionCallback connectionCallback){
        mConnectionCallback = connectionCallback;
    }

    @Override
    public boolean isConnected(){
        return mRos.isConnected();
    }

    @Override
    public void initRos(final Activity CallingActivity){
//        Toast.makeText(CallingActivity, "ROS IP: " + mRos.getHostname()
//                + "\t ROS PORT: " + mRos.getPort() ,
//                Toast.LENGTH_LONG).show();
        if(null == mRos){
            throw new IllegalArgumentException(DEBUG_TAG + "initRos: ROS is null");
        } else if ( null == mConnectionCallback) {
            throw new IllegalArgumentException(DEBUG_TAG + "initRos: ConnectionCallback is null");
        }
        mRos.connect(mConnectionCallback);
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
            throw new IllegalArgumentException(DEBUG_TAG + "getPort: ROS is null");
        }
        return mRos.getHostname();
    }

    @Override
    public void positionChanged(ArrayList<MotorItem> ArrayListMotorItems) {
        if (!mRos.isConnected()) {
            throw new IllegalArgumentException(DEBUG_TAG + "positionChanged: ROS is not connected");
        }
        StringBuilder msgMotorValue = new StringBuilder();
        msgMotorValue.append("{\"data\": \"");
        String delim = "";
        for (MotorItem mi : ArrayListMotorItems) {
            msgMotorValue.append(delim + mi.getPosition());
            delim = ", ";
        }
        msgMotorValue.append("\"}");
        Topic motorPosition = mTopicMap.get(mTestTopic);
        Message motorValues = new Message(msgMotorValue.toString());
        motorPosition.publish(motorValues);

        if (DBG) Log.i(DEBUG_TAG, "\t\t\t publish: " + msgMotorValue.toString());
    }

    public void initConnectionCallback(final Motors motors){
        mConnectionCallback = new ConnectionCallback() {
            public void onOpen(ServerHandshake serverHandshake) {
                if(DBG) Log.i(DEBUG_TAG, "ROSBridge Connection open : " + mRos.isConnected());
                final Topic topic = new Topic(mRos, mTestTopic, "std_msgs/String");
                mTopicMap.put(mTestTopic, topic);
                topic.subscribe(new TopicCallback() {
                    public void handleMessage(Message message) {
                        if(DBG) Log.i(DEBUG_TAG, "\t\t\treceived message: " + message.toString());
                        motors.handleMessage(message, topic);
                    }
                });
            }

            public void onClose(int i, String s, boolean b) {

                System.out.println("on close");
            }

            public void onError(Exception e) {
                System.out.println("on error");
                e.printStackTrace();
            }
        };
    }
}
