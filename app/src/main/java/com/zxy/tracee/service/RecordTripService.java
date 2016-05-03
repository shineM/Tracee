package com.zxy.tracee.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.zxy.tracee.model.Diary;
import com.zxy.tracee.model.RoutePoint;
import com.zxy.tracee.ui.PermissonUtil;

import net.tsz.afinal.FinalDb;

/**
 * Created by zxy on 16/4/30.
 */
public class RecordTripService extends Service {

    private FinalDb finalDb;

    private LocationManager locationManager;

    private LocationListener locationListener;

    private String provider;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        finalDb = FinalDb.create(this);
        listenLocationChange();
    }

    private void listenLocationChange() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = getLocationProvider(locationManager);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    insertLocation(location);
                } else {
                    return;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    private String getLocationProvider(LocationManager locationManager) {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);
        criteria.setAltitudeRequired(false);

        String lp = locationManager.getBestProvider(criteria, true);
        return lp;
    }

    private void insertLocation(Location location) {
        finalDb.save(new RoutePoint(location.getLatitude(), location.getLongitude(), System.currentTimeMillis()));
    }

    @Override
    public void onStart(Intent intent, int startId) {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }

}
