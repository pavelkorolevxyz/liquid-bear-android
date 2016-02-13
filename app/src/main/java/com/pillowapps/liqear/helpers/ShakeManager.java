package com.pillowapps.liqear.helpers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.pillowapps.liqear.listeners.OnShakeListenerImpl;

import javax.inject.Inject;

import rx.Observable;

public class ShakeManager {

    private Context context;
    private PreferencesScreenManager preferencesScreenManager;
    private SensorManager sensorManager;
    private OnShakeListenerImpl onShakeListenerImpl;

    @Inject
    public ShakeManager(Context context, PreferencesScreenManager preferencesScreenManager) {
        this.context = context;
        this.preferencesScreenManager = preferencesScreenManager;
    }

    public Observable<Object> initShakeDetector() {
        return Observable.create(subscriber -> {
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            Sensor mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            onShakeListenerImpl = new OnShakeListenerImpl();
            onShakeListenerImpl.setOnShakeListener(subscriber::onNext);
            sensorManager.registerListener(onShakeListenerImpl, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        });

    }

    public void destroyShake() {
        if (sensorManager == null || onShakeListenerImpl == null) {
            return;
        }
        sensorManager.unregisterListener(onShakeListenerImpl);
    }

    public Observable<Object> updateShake() {
        if (preferencesScreenManager.isShakeEnabled()) {
            return initShakeDetector();
        } else {
            destroyShake();
            return Observable.empty();
        }
    }
}
