package com.example.danish.sensorhub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class Flash extends Activity implements SensorEventListener {

    private SensorManager lightManager;
    TextView flashDetail,lightData;
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        flashDetail = (TextView)findViewById(R.id.flashDetail);
        lightData = (TextView)findViewById(R.id.lightData);
        lightManager= (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor= lightManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null){
            flashDetail.setText("No light sensor found");
            lightData.setText("Sorry");
        }
        lightManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        Context context = this;
        PackageManager pm = context.getPackageManager();
        camera = Camera.open();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_LIGHT) {
            float currentLight = event.values[0];
            final Parameters p = camera.getParameters();
            if (currentLight < 15) {
                p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);
                camera.startPreview();
                flashDetail.setText("Flash is ON");
                lightData.setText("Current light is Dim");
            }
            else {
                p.setFlashMode(Parameters.FLASH_MODE_OFF);
                camera.setParameters(p);
                camera.stopPreview();
                flashDetail.setText("Flash is OFF");
                lightData.setText("Current light is Normal");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        lightManager.registerListener(this,lightManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        lightManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (camera != null) {
            camera.release();
        }
    }
    public void back(View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
