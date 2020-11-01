package com.fii.covidtracker.network.apis;

import android.util.Log;

import java.io.IOException;

import retrofit2.Response;

public final class ApiResponse<T> {
    private static final String TAG = "ApiResponse";
    private final int code;
    public final T body;
    public final String message;

    public ApiResponse(Throwable error) {
        code = 500;
        body = null;
        message = error.getMessage();
    }

    public ApiResponse(Response<T> response) {
        code = response.code();

        if (response.isSuccessful()) {
            body = response.body();
            message = null;
            Log.i(TAG, "Response was successful.");
        } else {
            if (response.body() == null || response.code() == 204) {
                body = null;
                message = null;
                Log.i(TAG, "Response was empty.");
            } else {
                body = null;
                String errorMessage = null;
                if (response.errorBody() != null) {
                    try {
                        errorMessage = response.errorBody().string();
                    } catch (IOException ignored) {
                        Log.e(TAG, "Error while reading the errorBody of the response.");
                    }
                }
                if (errorMessage == null || errorMessage.trim().length() == 0) {
                    errorMessage = response.message();
                }
                this.message = errorMessage;
                Log.i(TAG, "Response was an error.");
            }
        }
    }

    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }

    public boolean isEmptyResponse() {
        return code == 204 || (body == null && message == null);
    }
}
