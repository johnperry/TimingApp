package org.jp.timingapp;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TimingView timingView = null;
    private SensorManager sensorManager = null;
    private Sensor accelerometer = null;
    V3 avgGrav = null;
    int interval = 10000;
    long updateInterval = 150;
    long lastTime = 0;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        timingView = (TimingView)findViewById(R.id.timing_view);
        avgGrav = V3.zero();
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, interval, interval);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void setATDC(View v) {
        timingView.setATDC();
    }

    public void setCenter(View v) { timingView.setCenter(); }

    public void setBTDC(View v) {
        timingView.setBTDC();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            V3 g = new V3(event.values);
            g = g.normalize();
            if (count == 0) avgGrav = g;
            else avgGrav = avgGrav.add(g);

            count++;
            long time = System.currentTimeMillis();
            if ((time - lastTime) > updateInterval) {
                double r = 1.0 / count;
                avgGrav = avgGrav.scale(r);
                avgGrav.z = 0.0;
                avgGrav = avgGrav.normalize();

                double gx = avgGrav.x;
                double gy = avgGrav.y;

                double angle = 0;
                if (gx > 0.0) angle = Math.acos(gy);
                else if (gx < 0.0) angle = -Math.acos(gy);
                else if (gy >= 0.0) angle = 0.0;
                else angle = Math.PI;

                timingView.setAngle(angle);
                lastTime = time;
                count = 0;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
