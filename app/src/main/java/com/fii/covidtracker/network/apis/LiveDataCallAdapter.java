package com.fii.covidtracker.network.apis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<ApiResponse<R>>> {
    private final Type responseType;

    private LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @NonNull
    @Override
    public Type responseType() {
        return responseType;
    }

    @NonNull
    @Override
    public LiveData<ApiResponse<R>> adapt(@NonNull final Call<R> call) {
        return new LiveData<ApiResponse<R>>() {
            AtomicBoolean started = new AtomicBoolean(false);

            @Override
            protected void onActive() {
                super.onActive();
                if (started.compareAndSet(false, true)) {
                    call.enqueue(new Callback<R>() {
                        @Override
                        public void onResponse(@NonNull Call<R> call,
                                               @NonNull Response<R> response) {
                            postValue(new ApiResponse<>(response));
                        }

                        @Override
                        public void onFailure(@NonNull Call<R> call, @NonNull Throwable t) {
                            postValue(new ApiResponse<>(t));
                        }
                    });
                }
            }
        };
    }

    public static class Factory extends CallAdapter.Factory {
        private static Factory instance = null;

        public static Factory create() {
            if (instance == null) {
                instance = new Factory();
            }
            return instance;
        }

        private Factory() {
        }

        @Nullable
        @Override
        public CallAdapter<?, ?> get(
                @NonNull Type returnType,
                @NonNull Annotation[] annotations,
                @NonNull Retrofit retrofit) {
            if (getRawType(returnType) != LiveData.class) {
                return null;
            }
            Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
            Class<?> rawObservableType = getRawType(observableType);
            if (rawObservableType != ApiResponse.class) {
                throw new IllegalArgumentException(
                        "The admitted structure for retrofit return values is " +
                                "LiveData<ApiResource<T>>");
            }
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalArgumentException(
                        "The admitted structure for retrofit return values is " +
                                "LiveData<ApiResource<T>>");
            }
            Type bodyType = getParameterUpperBound(0, (ParameterizedType) observableType);
            return new LiveDataCallAdapter<>(bodyType);
        }
    }
}
