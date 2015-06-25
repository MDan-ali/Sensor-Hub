package com.example.danish.sensorhub;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager accsensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    private boolean colour = false;
    private SensorManager proxsensorManager;
    RelativeLayout mainLayout;
    private Sensor proximeter;
    private SensorManager pressensorManager;
    private Sensor barometer;
    private SensorManager lightsensorManager;
    private Sensor light;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accsensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = accsensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accsensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        proxsensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximeter=proxsensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximeter==null){
            TextView proxDetail = (TextView)findViewById(R.id.proxDetail);
            proxDetail.setText("No Proximity Sensor Found!");
        }
        pressensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        barometer = pressensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        pressensorManager.registerListener(this, barometer, SensorManager.SENSOR_DELAY_UI);
        lightsensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        light = lightsensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightsensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_UI);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    getRandomNumber();
                    if (colour) {
                        mainLayout.setBackgroundColor(Color.YELLOW);

                    } else {
                        mainLayout.setBackgroundColor(Color.BLUE);
                    }
                    colour = !colour;
                }
            }

            last_x = x;
            last_y = y;
            last_z = z;
            TextView xValue = (TextView) findViewById(R.id.xValue);
            xValue.setText("" + last_x);
            TextView yValue = (TextView) findViewById(R.id.yValue);
            yValue.setText("" + last_y);
            TextView zValue = (TextView) findViewById(R.id.zValue);
            zValue.setText("" + last_z);
        }
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            if (event.values[0]==0){
                TextView proxDetail = (TextView)findViewById(R.id.proxDetail);
                proxDetail.setText("You are Near: " + String.valueOf(event.values[0]));
                mainLayout.setBackgroundColor(Color.RED);

            }
            else{
                TextView proxDetail = (TextView)findViewById(R.id.proxDetail);
                proxDetail.setText("You are Far: "+String.valueOf(event.values[0]));
                mainLayout.setBackgroundColor(Color.WHITE);
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE){
            float[] values = event.values;
            TextView presValue = (TextView)findViewById(R.id.presValue);
            presValue.setText("" + values[0]);

        }
        if(event.sensor.getType()==Sensor.TYPE_LIGHT) {
            float currentLight = event.values[0];
            TextView lightDetail = (TextView)findViewById(R.id.lightDetail);
            if(currentLight<1){lightDetail.setText("Current light: " + "no Light");}
            else if(currentLight<5){lightDetail.setText("Current light is Dim:" + String.valueOf(currentLight));}
            else if(currentLight<10){lightDetail.setText("Current light is Normal:" + String.valueOf(currentLight));}
            else if(currentLight<100){lightDetail.setText("Current light is Bright(Room):" + String.valueOf(currentLight));}
            else lightDetail.setText("Current light is Bright(Sun):" + String.valueOf(currentLight));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void getRandomNumber() {
        ArrayList numbersGenerated = new ArrayList();

        for (int i = 0; i < 6; i++) {
            Random randNumber = new Random();
            int iNumber = randNumber.nextInt(48) + 1;

            if(!numbersGenerated.contains(iNumber)) {
                numbersGenerated.add(iNumber);
            } else {
                i--;
            }
        }

        TextView accText = (TextView)findViewById(R.id.accText);
        accText.setText("" + numbersGenerated.get(0));
    }

    protected void onPause() {
        super.onPause();
        accsensorManager.unregisterListener(this);
        proxsensorManager.unregisterListener(this);
        pressensorManager.unregisterListener(this);
        lightsensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        accsensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        proxsensorManager.registerListener(this, proxsensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
        pressensorManager.registerListener(this, pressensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_NORMAL);
        lightsensorManager.registerListener(this, lightsensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void Compass(View view){
        Intent i = new Intent(this, Compass.class);
        startActivity(i);
    }

    public void Flash(View view){
        Intent i = new Intent(this, Flash.class);
        startActivity(i);
    }

    public void Gyroscope(View view){
        Intent i = new Intent(this, Gyroscope.class);
        startActivity(i);
    }

    public void Exit(View view){
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}



