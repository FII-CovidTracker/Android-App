package com.fii.covidtracker.network;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

final public class Resource<T> {
    @Nullable
    private T data;
    @Nullable
    private String message;
    @NonNull
    private ResourceStatus status;

    public Resource(@Nullable T data, @Nullable String message, @NonNull ResourceStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(data, null, ResourceStatus.SUCCESS);
    }

    public static <T> Resource<T> error(@NonNull String message, @Nullable T data) {
        return new Resource<>(data, message, ResourceStatus.ERROR);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(data, null, ResourceStatus.LOADING);
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @NonNull
    public ResourceStatus getStatus() {
        return status;
    }
}
