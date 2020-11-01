package com.fii.covidtracker;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

@Singleton
public class AppExecutors {

    private final Executor mIO = Executors.newSingleThreadExecutor();

    private final Executor mMainThreadExecutor = new MainThreadExecutor();

    public Executor io() {
        return mIO;
    }

    public Executor mainThread() {
        return mMainThreadExecutor;
    }

    private static class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
