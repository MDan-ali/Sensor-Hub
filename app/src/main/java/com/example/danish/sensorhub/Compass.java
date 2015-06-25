package com.example.danish.sensorhub;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

public class Compass extends Activity implements SensorEventListener {

    Float azimut;
    CustomDrawableView CustomDrawableView;
    private SensorManager SensorManager;
    Sensor accelerometer;
    Sensor magnet;

    public class CustomDrawableView extends View {
        Paint paint = new Paint();
        public CustomDrawableView(Context context) {
            super(context);
            paint.setColor(Color.MAGENTA);
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(3);
            paint.setAntiAlias(true);
            paint.setTextSize(40);
        };

        protected void onDraw(Canvas canvas) {
            int width = getWidth();
            int height = getHeight();
            int centerx = width/2;
            int centery = height/2;
            if (azimut != null)
                canvas.rotate(-azimut*360/(2*3.14159f), centerx, centery);
            paint.setColor(0xff0000ff);
            canvas.drawLine(centerx, -2000, centerx, +2000, paint);
            canvas.drawLine(-2000, centery, +2000, centery, paint);
            canvas.drawText("N", centerx+15, centery-220, paint);
            canvas.drawText("S", centerx-30, centery+225, paint);
            canvas.drawText("E", centerx+215, centery-20, paint);
            canvas.drawText("W", centerx-220, centery+35, paint);
        }
    }



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomDrawableView = new CustomDrawableView(this);
        setContentView(CustomDrawableView);

        SensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnet = SensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    protected void onResume() {
        super.onResume();
        SensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        SensorManager.registerListener(this, magnet, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        SensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

    float[] Gravity;
    float[] Geomagnetic;
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            Gravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            Geomagnetic = event.values;
        if (Gravity != null && Geomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, Gravity, Geomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0];
            }
        }
        CustomDrawableView.invalidate();
    }
}