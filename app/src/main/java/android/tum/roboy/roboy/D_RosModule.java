package android.tum.roboy.roboy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;

@Module
public class D_RosModule{
    HashMap<String,Topic> mMapTopic;
    ROSBridge mRosBridge;
    Ros mRos;
    Topic mTopic;

    D_RosModule(Ros ros){
        mRos = ros;
    }

    @Provides
    @Singleton
    HashMap<String,Topic> provideTopicMap(){
        mMapTopic = new HashMap<>();
        return mMapTopic;
    }

    @Provides
    @Singleton
    Ros provideRos(){
        return mRos;
    }

    @Provides
    @Singleton
    ROSBridge provideROSBridge(Ros ros, HashMap<String,Topic> topicMap){
        mRosBridge = new ROSBridge(ros, topicMap);
        return mRosBridge;
    }
}
