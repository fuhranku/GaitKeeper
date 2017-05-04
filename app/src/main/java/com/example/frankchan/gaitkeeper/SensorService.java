package com.example.frankchan.gaitkeeper;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;


/**
 * Created by frankchan on 4/29/17.
 */

public class SensorService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    StringBuilder sensorData;

    private float acc_x = 0;
    private float acc_y = 0;
    private float acc_z = 0;

    private int count = 0;
    private int cap;

    private Messenger messenger;

    /**
     * Class for clients to access. We assume that this SensorService
     * will always run in the same process as clients, so we don't need
     * to deal with IPC.
     */
    public class LocalBinder extends Binder {
        SensorService getService() {
            return SensorService.this;
        }
    }

    private final IBinder localBinder = new LocalBinder();

    /**
     * Get and register the sensors
     */
    @Override
    public void onCreate() {
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this, accelerometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            acc_x = event.values[0];
            acc_y = event.values[1];
            acc_z = event.values[2];

            count++;
            if(count <= cap) {
                sensorData.append("" + System.currentTimeMillis() + "\t" + acc_x + "\t" + acc_y + "\t" + acc_z + "\n");
            } else {
                try {
                    Message message = Message.obtain();
                    message.arg1 = 1;
                    messenger.send(message);
                } catch(Exception e) {
                    System.out.print("Error while sending message: " + e);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        messenger = (Messenger)intent.getExtras().get("messenger");
        return localBinder;
    }

    public String getRawDataAndStop() {
        sensorManager.unregisterListener(this, accelerometer);
        count = 0;
        return sensorData.toString();
    }

    public void startService(int cap) {
        sensorData = new StringBuilder();
        count = 0;
        this.cap = cap;
        System.out.println("Starting accelerometer sensing with cap of " + cap);
        sensorData.append("accelerometer_timestamp\taccelerometer_x_data\taccelerometer_y_data\taccelerometer_z_data\n");
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

}
