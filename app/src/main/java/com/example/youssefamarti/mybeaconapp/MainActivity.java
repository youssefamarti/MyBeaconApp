package com.example.youssefamarti.mybeaconapp;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Region;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MybeaconApp extends Application implements BootstrapNotifier {
    private static final String TAG = ".MyApplicationName";
    private RegionBootstrap regionBootstrap;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App started up");
        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        Region region = new Region("com.example.myapp.boostrapRegion", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }

    @Override
    public void didDetermineStateForRegion(int arg0, Region arg1) {
        // Don't care
    }

    @Override
    public void didEnterRegion(Region arg0) {
        Log.d(TAG, "Got a didEnterRegion call");
        // This call to disable will make it so the activity below only gets launched the first time a beacon is seen (until the next time the app is launched)
        // if you want the Activity to launch every single time beacons come into view, remove this call.
        regionBootstrap.disable();
        Intent intent = new Intent(this, MainActivity.class);
        // IMPORTANT: in the AndroidManifest.xml definition of this activity, you must set android:launchMode="singleInstance" or you will get two instances
        // created when a user launches the activity manually and it gets launched from here.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    @Override
    public void didExitRegion(Region arg0) {
        // Don't care
    }
}

public class MonitoringActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state);
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }

    public class BeaconManager {
        public static BeaconManager getInstanceForApplication(MonitoringActivity monitoringActivity) {


        }

    public class RangingActivity extends Activity implements BeaconConsumer {
            protected static final String TAG = "RangingActivity";
            private BeaconManager beaconManager;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_ranging);
                beaconManger = BeaconManager.getInstanceForApplication(this);
                beaconManager.bind(this);
            }
            @Override
            protected void onDestroy() {
                super.onDestroy();
                beaconManager.unbind(this);
            }
            @Override
            public void onBeaconServiceConnect() {
                beaconManager.setRangeNotifier(new RangeNotifier() {
                    @Override
                    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                        if (beacons.size() > 0) {
                            Log.i(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
                        }
                    }
                });

                try {
                    beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
                } catch (RemoteException e) {    }
            }
        }

        public class MyApplication extends Application implements BootstrapNotifier {
            private BackgroundPowerSaver backgroundPowerSaver;

            public void onCreate() {
                super.onCreate();
                // Simply constructing this class and holding a reference to it in your custom Application class
                // enables auto battery saving of about 60%
                backgroundPowerSaver = new BackgroundPowerSaver(this);
            }
        }
    }
}