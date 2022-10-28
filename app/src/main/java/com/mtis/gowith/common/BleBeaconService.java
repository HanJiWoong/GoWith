package com.mtis.gowith.common;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;
import java.util.UUID;

public class BleBeaconService extends IntentService {
    protected static final String TAG = "BleBeaconService";
    private static final int ADVERTISE_TIMEOUT = 2000;

    public BleBeaconService() {
        super("BleBeaconService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int memberId = 0;
        if (intent.hasExtra("memberId"))
            memberId = intent.getExtras().getInt("memberId");

        if (memberId > 0 && memberId < 65536) {
            advertiseInfoShoot(memberId);
            try {
                Thread.sleep(ADVERTISE_TIMEOUT);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Log.e(TAG, "Send beacon info with memberId(" + memberId + ") now");
        } else {
            Log.e(TAG, "Error: memberId not received. Could not send beacon info.");
        }
    }

    @SuppressLint("MissingPermission")
    private void advertiseInfoShoot(int memberId) {
        BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
        AdvertiseSettings advertiseSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false)
                .setTimeout(ADVERTISE_TIMEOUT)
                .build();

        String s = String.format("19697466-0000-0000-0000-00000000%02X%02X", memberId/256, memberId%256);
        ParcelUuid pUuid = new ParcelUuid(UUID.fromString(s));
        AdvertiseData mAdvertiseData = new AdvertiseData.Builder().addServiceUuid(pUuid).build();

        advertiser.startAdvertising(advertiseSettings, mAdvertiseData, advertiseCallback);
    }

    AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.e(TAG, "onStartSuccess");
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.e(TAG, "onStartFailure: " + errorCode );
        }
    };
}