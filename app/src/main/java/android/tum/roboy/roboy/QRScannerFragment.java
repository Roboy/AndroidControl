package android.tum.roboy.roboy;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

/**
 * Created by sebtut on 25.10.16.
 */
public class QRScannerFragment extends Fragment implements QRCodeReaderView.OnQRCodeReadListener {
    private TextView resultTextView;
    private QRCodeReaderView mydecoderview;

    private static final String         DEBUG_TAG = "\t\tRO_QR_FRAGMENT";
    private static final boolean        DBG = true;

    private Context                     mContext;
    private Activity                    mCallingActivity;
    private View                        mRootView;
    private IWiringEvent_QR             mIWir_QR;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(DBG) Log.i(DEBUG_TAG, "onAttach() called");

        mCallingActivity = getActivity();
        mContext = context;

        try{
            mIWir_QR = (IWiringEvent_QR) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        if(DBG) Log.i(DEBUG_TAG, "onCreateView() called");

        mRootView = inflater.inflate(R.layout.qrscanner_fragment, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        if(DBG) Log.i(DEBUG_TAG, "onActivityCreated() called");

        try {
            mydecoderview = (QRCodeReaderView) mRootView.findViewById(R.id.qrdecoderview);
        }catch (RuntimeException e){
            if(DBG) Log.e(DEBUG_TAG, "RuntimeException: " + e.toString());
        }

        mydecoderview.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        mydecoderview.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        mydecoderview.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        mydecoderview.setTorchEnabled(true);

        // Use this function to set front camera preview
//        mydecoderview.setFrontCamera();

        // Use this function to set back camera preview
        mydecoderview.setBackCamera();
    }

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed in View
    @Override
    public void onQRCodeRead(String text, PointF[] points) {
//        resultTextView.setText(text);
        if(DBG)Log.i(DEBUG_TAG, "Detected QR String: " + text);
        mIWir_QR.motorDetected(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        mydecoderview.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mydecoderview.stopCamera();
    }
}

