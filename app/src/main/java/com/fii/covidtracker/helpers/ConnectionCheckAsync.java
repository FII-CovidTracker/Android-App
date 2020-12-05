package com.fii.covidtracker.helpers;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionCheckAsync extends AsyncTask<Void, Void, Boolean> {

    private Consumer mConsumer;

    public interface Consumer {
        void accept(Boolean serverOn);
    }

    public ConnectionCheckAsync(Consumer consumer) {
        mConsumer = consumer;
        execute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress(Constants.COVID_TRACKER_PLACEHOLDER_API_URL,
                    8080), 1500);
            sock.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    @Override
    protected void onPostExecute(Boolean serverOn) {
        mConsumer.accept(serverOn);
    }
}

