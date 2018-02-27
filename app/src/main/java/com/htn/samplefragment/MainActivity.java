package com.htn.samplefragment;

import android.content.Intent;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String BUNDLE_EXTRA_ORIENTATION = "BUNDLE_EXTRA_ORIENTATION";

    private List<MediaEntity> mMediaEntities = new ArrayList<>();
    private int mOrientation = 0;
    private boolean isOrientationSettingOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), true, rotationObserver);


        mMediaEntities.add(new MediaEntity("image", "http://210.148.155.7:8000/recording/2018/02/5a93db98de61ef7e778a6918/34185368-original.jpg", "http://210.148.155.7:8000/recording/2018/02/5a93db98de61ef7e778a6918/34185368-thumbnail.jpg"));
        mMediaEntities.add(new MediaEntity("audio", "https://gvn.xgg.jp:281//file//201802//27//5c73fa8c-4d00-44e2-a47c-803979f6fe16.wav", "http://210.148.155.7:8000/recording/2018/02/5a93db98de61ef7e778a6918/34185368-thumbnail.jpg"));
        mMediaEntities.add(new MediaEntity("video", "http://210.148.155.7:8000//recording//2018//02//5a93dc65de61ef7e778a691d//28160832.mp4", "http://210.148.155.7:8000/recording/2018/02/5a93db98de61ef7e778a6918/34185368-thumbnail.jpg"));
        mMediaEntities.add(new MediaEntity("image", "http://210.148.155.7:8000/recording/2018/02/5a93db98de61ef7e778a6918/34185368-original.jpg", "http://210.148.155.7:8000/recording/2018/02/5a93db98de61ef7e778a6918/34185368-thumbnail.jpg"));


        MediaPagerAdapter adapter = new MediaPagerAdapter(getSupportFragmentManager());
        adapter.setData(mMediaEntities);

        ViewPager mViewPager = findViewById(R.id.mViewPager);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }

        isOrientationSettingOn = Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(rotationObserver);
        rotationObserver = null;
    }

    private ContentObserver rotationObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            isOrientationSettingOn = Settings.System.getInt(MainActivity.this.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
            Log.e(TAG, "--------------> mAutoRotateSettingChangeReceiver" + isOrientationSettingOn);
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        int newOrientation = mOrientation;
        if (x < 5 && x > -5 && y > 5)
            newOrientation = 0;
        else if (x < -5 && y < 5 && y > -5)
            newOrientation = 90;
        else if (x < 5 && x > -5 && y < -5)
            newOrientation = 180;
        else if (x > 5 && y < 5 && y > -5)
            newOrientation = 270;

        if (mOrientation != newOrientation) {
            mOrientation = newOrientation;
            Log.e(TAG, "--->mOrientation=" + mOrientation + "--> can change orientation");

            Intent intent = new Intent("custom-event-name");
            intent.putExtra(BUNDLE_EXTRA_ORIENTATION, mOrientation);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
