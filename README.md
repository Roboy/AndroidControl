# Roboy_Android

########## SETUP 

The projekt uses Android Studio (https://developer.android.com/studio/index.html) and the provided SDK tools as IDE. Additionally the Dependency Injection framework (https://google.github.io/dagger/) has been used to resolve dependencies for this project. 

########## FUNCTIONALLY

The Android / Java application talks to a ROS node via rosbridge (https://github.com/Ant1Zykl0n/jrosbridge) which basically is a websocket talking to another websocket. One master and one client. The Android app acts as a client.  Via this websocket you can exchange data according to the JSON based "rosbridge protocol" (https://github.com/RobotWebTools/rosbridge_suite/blob/groovy-devel/ROSBRIDGE_PROTOCOL.md). Currently the target IP Address and target port of the rosbridge master are hard coded in the "RoboyApp.java" file. This is self-infliced since I wanted to play around with dependency injection and screwed it up a little (thus feel free to change :) ). 

The App is build up modular with two main classes: "Motors.java" and "ROSBridge.java". ". The class "WiringActivity.java" instantiates "WiringMotorActivity" which handles the communication between the classes using the respectively provided interfaces of all classes. 
In case the rosbridge connection is setted up succesfully, the Fragment displays various sliders otherwise they will be hidden. If you change orientation (landscape -> portrait) the sliders won't be redrawn since the connection is stays open and the respective callback is not called. This is obviously a bug which will need a fix. Originally I wanted to prevent the user clicking around if there's no valid connection. If it annoys you, uncomment line 97 / WiringFragment.java . However, if the sliders are there and the correct ROS node is running the rosbridge master you should be able to send and receive commands to set the position of the legs. 

A working QR Scanner for e.g. adding motors labeld with a QR Code is already implemented. However, the respective callback interfaces are missing atm. 
