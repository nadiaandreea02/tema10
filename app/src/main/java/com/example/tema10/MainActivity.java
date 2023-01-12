package com.example.tema10;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Provider;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
SensorManager sensorManager;
Sensor sensor;
Context context;
TextView textView;
boolean succes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.textView);

        sensorManager= (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

    }

protected void onPause() {
    super.onPause();
   sensorManager.unregisterListener(this);
}
protected void onResume() {

    super.onResume();
    sensorManager.registerListener(this,sensor,sensorManager.SENSOR_DELAY_NORMAL);
}
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
context=getApplicationContext();
if (sensorEvent.sensor.getType()==Sensor.TYPE_LIGHT){
    textView.setText(""+sensorEvent.values[0]);
    if(sensorEvent.values[0]<15){
        permission();
        setBrightness(240);
    }else if(sensorEvent.values[0]>80){
        permission();
        setBrightness(50);
    }
}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    private void permission(){
        boolean value;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            value= Settings.System.canWrite(getApplicationContext());
            if(value){
                succes=true;
            }else{
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:"+getApplicationContext().getPackageName()));
                startActivityForResult(intent,100);
            }
        }
    }
    protected void onActivityResult(int requesCode, int resultCode, Intent intent) {
        super.onActivityResult(requesCode, resultCode, intent);
        if (requesCode == 100) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean value = Settings.System.canWrite(getApplicationContext());
                if (value) {
                    succes = true;
                } else {
                    Toast.makeText(context, "Permission is not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void setBrightness(int brightness){
        if(brightness<0){
            brightness=0;
        }else if(brightness>255){
            brightness=255;
        }
        ContentResolver contentResolver=getApplicationContext().getContentResolver();
        Settings.System.putInt(contentResolver,Settings.System.SCREEN_BRIGHTNESS,brightness);
    }
}