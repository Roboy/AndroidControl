package android.tum.roboy.roboy;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    private static final String DEBUG_TAG = "ROBOY_MOTOR_SLIDERS";
    private static final boolean DBG = true;
    private MotorItemAdapter adapter;
    private ListView listView;

    private ArrayList<MotorItem> mMotors = new ArrayList<MotorItem>();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = new Intent(this, QRMotorScanner.class);
        if(DBG) Log.i(DEBUG_TAG, "Constructor called");

        //TODO: Get MotorItems via the rosbridge
        mMotors.add(new MotorItem(0, 20));
        mMotors.add(new MotorItem(1, 50));
        mMotors.add(new MotorItem(2, 80));


        setContentView(R.layout.activity_chillout_lobby);


        adapter = new MotorItemAdapter(this , R.layout.list_motoritem, mMotors);
        listView = (ListView) findViewById(R.id.ListView_Motors);
        listView.setAdapter(adapter);

        Button addMotorButton = (Button) findViewById(R.id.Button_ScanMotors);
        addMotorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Start the QR Scanner Activity

                if(DBG) Log.i(DEBUG_TAG, "ADD MOTOR CALLED");
                startActivity(intent);
            }
        });
        setupToolbar();
        setupWindowAnimations();
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
    }

    @TargetApi(21)
    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= 21) {
            Fade fade = new Fade();
            fade.setDuration(2000);
            getWindow().setExitTransition(fade);
            if(DBG) Log.i(DEBUG_TAG, "TRANSITION CALLED");
        }
    }
}
