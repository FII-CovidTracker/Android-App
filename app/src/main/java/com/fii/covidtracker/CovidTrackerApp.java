package com.fii.covidtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.fii.covidtracker.bluethoot.util.NotificationUtil;
import com.fii.covidtracker.bluethoot.util.PreferencesUtil;
import com.fii.covidtracker.di.DaggerCovidTrackerAppComponent;
import com.fii.covidtracker.helpers.Constants;

import org.dpppt.android.sdk.DP3T;
import org.dpppt.android.sdk.backend.models.ApplicationInfo;
import org.dpppt.android.sdk.internal.logger.LogLevel;
import org.dpppt.android.sdk.internal.logger.Logger;
import org.dpppt.android.sdk.internal.util.ProcessUtil;
import org.dpppt.android.sdk.util.SignatureUtil;

import java.security.PublicKey;
import java.util.concurrent.TimeUnit;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import okhttp3.CertificatePinner;

public class CovidTrackerApp extends DaggerApplication {

    private SharedPreferences cachingSharedPref;

    @Override
    public void onCreate() {
        super.onCreate();
        lookupPreferences();
        startDP3T();
    }

    private void startDP3T() {
        if (ProcessUtil.isMainProcess(this)) {
            registerReceiver(sdkReceiver, DP3T.getUpdateIntentFilter());
            initDP3T(this);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.createNotificationChannel(this);
        }
        Logger.init(getApplicationContext(), LogLevel.DEBUG);
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerCovidTrackerAppComponent.builder().application(this).build();
    }

    private void lookupPreferences() {
        SharedPreferences cachingSharedPref = getApplicationContext()
                .getSharedPreferences("Caching", Context.MODE_PRIVATE);
        long cachingTTL = cachingSharedPref.getLong(getString(R.string.caching_ttl), 0);
        if (cachingTTL > 0) {
            Constants.setCacheTTLMilis(cachingTTL);
        } else {
            Constants.setCacheTTLMilis(TimeUnit.MINUTES.toMillis(10));
        }
    }


    @Override
    public void onTerminate() {
        savePreferances();
        if (ProcessUtil.isMainProcess(this)) {
            unregisterReceiver(sdkReceiver);
        }
        super.onTerminate();
    }

    private void savePreferances() {
        SharedPreferences.Editor editor = cachingSharedPref.edit();
        editor.putLong(getString(R.string.caching_ttl), Constants.getCacheTTLMilis());
        editor.apply();
    }

    public static void initDP3T(Context context) {
        PublicKey publicKey = SignatureUtil.getPublicKeyFromBase64OrThrow(Constants.DP3TKey);
        DP3T.init(context, new ApplicationInfo(
                        Constants.DP3TAppName,
                        Constants.COVID_TRACKER_TRACKING_ENDPOINT,
                        Constants.COVID_TRACKER_TRACKING_ENDPOINT),
                        publicKey);

        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add(Constants.DP3TAppName, Constants.DP3TKeyCheck)
                .build();
        DP3T.setCertificatePinner(certificatePinner);
    }

    private BroadcastReceiver sdkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DP3T.getStatus(context).getExposureDays().size() > 0 &&
                    !PreferencesUtil.isExposedNotificationShown(context)) {
                NotificationUtil.showNotification(context, R.string.push_exposed_title,
                        R.string.push_exposed_text, R.drawable.ic_handshakes);
                PreferencesUtil.setExposedNotificationShown(context);
            }
        }
    };

}
