package com.example.danish.sensorhub;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class Gyroscope extends Activity implements SensorEventListener {

    public SensorManager gyrsensorManager;
    TextView gyrDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);

        gyrDetail=(TextView)findViewById(R.id.gyrDetail);
        gyrsensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        gyrDetail.setText("Orientation X (Roll) :"+ Float.toString(event.values[2]) +"\n"+ "Orientation Y (Pitch) :" + Float.toString(event.values[1]) +"\n"+ "Orientation Z (Yaw) :"+ Float.toString(event.values[0]));
    }

    @Override
    protected void onPause() {
        super.onPause();
        gyrsensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gyrsensorManager.registerListener(this, gyrsensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void Back(View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
