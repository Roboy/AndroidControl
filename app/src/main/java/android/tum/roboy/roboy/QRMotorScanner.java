package android.tum.roboy.roboy;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;

public class QRMotorScanner extends AppCompatActivity {

    private static final String DEBUG_TAG = "ROBOTY_QT_MOTORSCANNER";
    private static final boolean DBG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrmotor_scanner);
        setupWindowAnimations();
        setupToolbar();
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
    }

    @TargetApi(21)
    private void setupWindowAnimations() {
        if(Build.VERSION.SDK_INT >= 21) {
            Fade fade = new Fade();
            fade.setDuration(2000);
            getWindow().setEnterTransition(fade);
            if(DBG) Log.i(DEBUG_TAG, "TRANSITION CALLED");
        }
    }
}
