package com.example.sc.testmap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.baidu.mapapi.map.MyLocationConfiguration;

/**
 * Created by sc on 2018/4/8.
 */

public class MyOrientationListener implements SensorEventListener {
    private SensorManager mSensorManager=null;
    private Context mComtext=null;
    private Sensor mSensor=null;

    private float lastX;


    public MyOrientationListener(Context context){
        this.mComtext=context;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
            float x=event.values[SensorManager.DATA_X];

            if(Math.abs(x-lastX)>1.0){
                if(mOnOrientationListener!=null){
                    mOnOrientationListener.onOrientationChanged(x);
                }
            }

            lastX=x;
        }
    }

    private OnOrientationListener mOnOrientationListener=null;

    public void setmOnOrientationListener(OnOrientationListener mOnOrientationListener) {
        this.mOnOrientationListener = mOnOrientationListener;
    }

    public interface OnOrientationListener{
        void onOrientationChanged(float x);
    }

    public void start(){
        mSensorManager=(SensorManager) mComtext.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager!=null){
            mSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        if(mSensor!=null){
            mSensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stop(){
        mSensorManager.unregisterListener(this);
    }
}
