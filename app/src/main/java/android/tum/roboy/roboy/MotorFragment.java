package android.tum.roboy.roboy;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by sebtut on 24.10.16.
 */
public class MotorFragment extends Fragment{

    public ListView mLV;

    public MotorFragment(){

    }

    public MotorFragment(ListView lV){
        mLV = lV;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
//        return inflater.inflate(R.layout.list_motoritem, container, false);
          return mLV;
    }
}
