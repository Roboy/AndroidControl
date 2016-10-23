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

import edu.wpi.rail.jrosbridge.Ros;

/**
 * Created by sebtut on 12.09.16.
 * Adapter, which provides the MotorItems of Roboy to the ListView in the UI
 *
 */
public class MotorItemAdapter extends ArrayAdapter<MotorItem> {

    private static final String                 DEBUG_TAG = "\t\tRO_ADAPTER_MOTORITEM";
    private static final boolean                DBG = true;

    private ArrayList<MotorItem>                mMotorItems;
    private Context                             mContext;
    private IMotorEvent                       mIMotorEvent;

    public MotorItemAdapter(Context context,int ViewResourceID, ArrayList<MotorItem> objects, IMotorEvent iMotorEvent){
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
            final Integer position = new Integer(motorItem.getPosition());

            final TextView motorName = (TextView) v.findViewById(R.id.textViewMotorID);
            final TextView motorPosition = (TextView) v.findViewById(R.id.textViewCurrentPosition);
            SeekBar slider = (SeekBar) v.findViewById(R.id.seekBarPosition);

            if(null != motorName){
                motorName.setText("Motor ID: " + id.toString());
            }

            if(null != motorPosition){
                motorPosition.setText(position.toString());
            }

            if(null != slider){
                slider.setMax(200);
                slider.setProgress(100);
                slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(fromUser) {
                            Integer position_new = new Integer(progress);
                            position_new -= 100;
                            if (DBG) Log.i(DEBUG_TAG, " \t\t new position: " + position_new
                                    + "\t\t old position:" + position
                                    + "\t\t from motor: " + id);
                            motorPosition.setText(position_new.toString());
                            motorItem.setmPosition(position_new);
                            try {
                                mIMotorEvent.positionChanged(mMotorItems);
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




