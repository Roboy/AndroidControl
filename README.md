# Roboy_Android

########## SETUP 

The projekt uses Android Studio (https://developer.android.com/studio/index.html) and the provided SDK tools as IDE. Additionally the Dependency Injection framework (https://google.github.io/dagger/) has been used to resolve dependencies for this project. 

########## FUNCTIONALLY

The Android / Java application talks to a ROS node via rosbridge (https://github.com/Ant1Zykl0n/jrosbridge) which basically is a websocket talking to another websocket. One master and one client. The Android app acts as a client.  Via this websocket you can exchange data according to the JSON based "rosbridge protocol" (https://github.com/RobotWebTools/rosbridge_suite/blob/groovy-devel/ROSBRIDGE_PROTOCOL.md). Currently the target IP address and target port of the rosbridge master are hard coded in the "RoboyApp.java" file. This is self-infliced since I wanted to play around with dependency injection and screwed it up a little (thus feel free to change :) ). 

The App is build up modular with two main classes: `Motors.java` and `ROSBridge.java`. The class `WiringActivity.java` instantiates `WiringMotorActivity` which handles the communication between the classes using the respective interfaces. 
If the rosbridge connection is setted up succesfully, several sliders will be displayed in the wiring fragment. Otherwise they will be hidden in order to prevent the user from undesired interaction. If you change the orientation of your smartphone (e.g. landscape -> portrait) the sliders won't be redrawn since the connection stays open and the respective callback is not called. This is obviously a bug which will need a fix from me someday. If it annoys you, uncomment line 97 @ `WiringFragment.java`.
However, if the sliders are there and the rosbridge master runs the motor control ROS node you should be able to send commands to set the positions of the legs. 

A working QR Scanner for e.g. adding motors labeld with a QR Code is already implemented. However, the respective callback interfaces are missing atm. 
