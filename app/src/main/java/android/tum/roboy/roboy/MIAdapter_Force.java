package android.tum.roboy.roboy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sebtut on 12.09.16.
 * Adapter, which provides the MotorItems of Roboy to the ListView in the UI
 *
 */
public class MIAdapter_Force extends ArrayAdapter<MotorItem> {

    private static final String                 DEBUG_TAG = "\t\tRO_ADAPTER_MOTORITEM";
    private static final boolean                DBG = true;
    private static final Integer                Offset = 0;
    private static final Integer                Max = 10; // -50 to +50

    private ArrayList<MotorItem>                mMotorItems;
    private Context                             mContext;
    private IMotorEvent                         mIMotorEvent;

    public MIAdapter_Force(Context context, int ViewResourceID, ArrayList<MotorItem> objects, IMotorEvent iMotorEvent){
        super(context, ViewResourceID, objects);
        if(DBG) Log.i(DEBUG_TAG,  "Constructor called");
        this.mMotorItems = objects;
        mContext = context;
        mIMotorEvent = iMotorEvent;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent){
        View v = convertView;
        if(null == v) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_motoritem, null);
        }

        final MotorItem motorItem = mMotorItems.get(index);

        if(null != motorItem){
            final Integer id = new Integer(motorItem.getID());
            final Integer force = new Integer(motorItem.getForce() - Offset);

            final TextView motorName = (TextView) v.findViewById(R.id.textViewMotorID);
            final TextView motorPosition = (TextView) v.findViewById(R.id.textViewCurrentPosition);
            SeekBar slider = (SeekBar) v.findViewById(R.id.seekBarPosition);

            if(null != motorName){
                motorName.setText("Motor ID: " + id.toString());
            }

            if(null != motorPosition){
                motorPosition.setText(new Integer(0).toString());
            }

            if(null != slider){
                slider.setMax(Max);
                slider.setProgress(Offset);
                slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(fromUser) {
                            Integer force_new = new Integer(progress);
                            if (DBG) Log.i(DEBUG_TAG, " \t\t new Force: " + force_new
                                    + "\t\t old Force:" + force
                                    + "\t\t from motor: " + id);
                            motorPosition.setText(force_new.toString());
                            motorItem.setmForce(force_new);
                            try {
                                mIMotorEvent.forceChanged(motorItem);
                            } catch (Exception e) {
                                if(DBG) Log.e(DEBUG_TAG, e.toString());
                                //TODO: Handle exception properly
                            }
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        }
        return  v;
    }
}




