package com.fii.covidtracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.fii.covidtracker.bluetooth.storage.SecureStorage;
import com.fii.covidtracker.bluetooth.util.NotificationUtil;
import com.fii.covidtracker.di.DaggerCovidTrackerAppComponent;
import com.fii.covidtracker.helpers.Constants;

import org.dpppt.android.sdk.DP3T;
import org.dpppt.android.sdk.InfectionStatus;
import org.dpppt.android.sdk.TracingStatus;
import org.dpppt.android.sdk.backend.models.ApplicationInfo;
import org.dpppt.android.sdk.internal.database.models.ExposureDay;
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

        if (ProcessUtil.isMainProcess(this)) {
            registerReceiver(contactUpdateReceiver, DP3T.getUpdateIntentFilter());

            PublicKey publicKey = SignatureUtil.getPublicKeyFromBase64OrThrow(
                    "TUZrd0V3WUhLb1pJemowQ0FRWUlLb1pJemowREFRY0RRZ0FFa0VBdlc5REtQb25TVXJXMzMvZUQvNUFmZHM1R20xTTY1NDhwTkxmNXZpamVib0VkNUhVOGNBMTBiK0tuZ3c0TGJ1a0hqUXFadW9YUFdQNVAvQ0lQNmc9PQ==");
            DP3T.init(this, new ApplicationInfo(
                            "demo.dpppt.org",
                            "http://192.168.1.34:8080",
                            "http://192.168.1.34:8080"),
                    publicKey);

            CertificatePinner certificatePinner = new CertificatePinner.Builder()
                    .add("demo.dpppt.org", "sha256/YLh1dUR9y6Kja30RrAn7JKnbQG/uEtLMkBgFF2Fuihg=")
                    .build();
            DP3T.setCertificatePinner(certificatePinner);
        }
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

    private BroadcastReceiver contactUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SecureStorage secureStorage = SecureStorage.getInstance(context);
            TracingStatus status = DP3T.getStatus(context);
            if (status.getInfectionStatus() == InfectionStatus.EXPOSED) {
                ExposureDay exposureDay = null;
                long dateNewest = 0;
                for (ExposureDay day : status.getExposureDays()) {
                    if (day.getExposedDate().getStartOfDayTimestamp() > dateNewest) {
                        exposureDay = day;
                        dateNewest = day.getExposedDate().getStartOfDayTimestamp();
                    }
                }
                if (exposureDay != null && secureStorage.getLastShownContactId() != exposureDay.getId()) {
                    createNewContactNotifaction(context, exposureDay.getId());
                }
            }
        }
    };

    private void createNewContactNotifaction(Context context, int contactId) {
        SecureStorage secureStorage = SecureStorage.getInstance(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.createNotificationChannel(context);
        }

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.setAction(MainActivity.ACTION_GOTO_REPORTS);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification =
                new NotificationCompat.Builder(context, NotificationUtil.NOTIFICATION_CHANNEL_ID)
                        .setContentTitle(context.getString(R.string.push_exposed_title))
                        .setContentText(context.getString(R.string.push_exposed_text))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSmallIcon(R.drawable.ic_begegnungen)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationUtil.NOTIFICATION_ID_CONTACT, notification);

        secureStorage.setHotlineCallPending(true);
        secureStorage.setReportsHeaderAnimationPending(true);
        secureStorage.setLastShownContactId(contactId);
    }

    @Override
    public void onTerminate() {
        savePreferances();
        super.onTerminate();
    }

    private void savePreferances() {
        SharedPreferences.Editor editor = cachingSharedPref.edit();
        editor.putLong(getString(R.string.caching_ttl), Constants.getCacheTTLMilis());
        editor.apply();
    }
}
