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
 */
public class MotorItemAdapter extends ArrayAdapter<MotorItem> {

    private ArrayList<MotorItem> mObjects;
    private static final String DEBUG_TAG = "MOTORITEM_ADAPTER";
    private static final boolean DBG = true;

    public MotorItemAdapter(Context context,int ViewResourceID, ArrayList<MotorItem> objects){
        super(context, ViewResourceID, objects);
        this.mObjects = objects;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent){
        View v = convertView;
        if(null == v) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_motoritem, null);
        }

        MotorItem i = mObjects.get(index);
        if(i != null){
            final TextView motorName = (TextView) v.findViewById(R.id.textViewMotorID);
            final TextView motorPosition = (TextView) v.findViewById(R.id.textViewCurrentPosition);
            SeekBar slider = (SeekBar) v.findViewById(R.id.seekBarPosition);

            if(null != motorName){
                Integer id = new Integer(i.getID());
                motorName .setText("Motor ID: " + id.toString());
            }

            if(null != motorPosition){
                Integer position = new Integer(i.getPosition());
                motorPosition.setText(position.toString());
            }

            if(null != slider){
                Integer position = new Integer(i.getPosition());
                slider.setProgress(position);
                slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        Integer position = new Integer(progress);
                        motorPosition.setText(position.toString());
                        // TODO: send new values via the rosbridge
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




