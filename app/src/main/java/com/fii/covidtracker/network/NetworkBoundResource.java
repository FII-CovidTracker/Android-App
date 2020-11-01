package com.fii.covidtracker.network;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.fii.covidtracker.network.apis.ApiResponse;

// ResultType - the type stored in our cached database
// RequestType - the type returned by the API call
public abstract class NetworkBoundResource<ResultType, RequestType> {

    private MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();
    private com.fii.covidtracker.AppExecutors appExecutors;

    @MainThread
    public NetworkBoundResource(com.fii.covidtracker.AppExecutors appExecutors) {
        this.appExecutors = appExecutors;

        result.setValue(Resource.loading(null));

        final LiveData<ResultType> dbDataSource = loadFromDb();
        result.addSource(dbDataSource, data -> {
            result.removeSource(dbDataSource);
            if (shouldFetch(data)) {
                fetchFromNetwork(dbDataSource);
            } else {
                result.addSource(dbDataSource, oldData -> setValue(Resource.success(oldData)));
            }
        });
    }

    @MainThread
    private void fetchFromNetwork(final LiveData<ResultType> dbDataSource) {
        final LiveData<ApiResponse<RequestType>> apiDataSource = createCall();

        result.addSource(dbDataSource, dbData -> setValue(Resource.loading(dbData)));
        result.addSource(apiDataSource, unprocessedApiResponse -> {

            result.removeSource(dbDataSource);
            result.removeSource(apiDataSource);

            if (unprocessedApiResponse.isSuccessful()) {
                appExecutors.io().execute(() -> {
                    saveCallResult(processResponse(unprocessedApiResponse));
                    appExecutors.mainThread().execute(() -> {
                        // Single Point of Truth
                        result.addSource(loadFromDb(), newData ->
                                setValue(Resource.success(newData)));
                    });
                });
            } else {
                if (unprocessedApiResponse.isEmptyResponse()) {
                    result.addSource(dbDataSource, oldData ->
                            setValue(Resource.success(oldData)));
                } else {
                    onFetchFailed();
                    result.addSource(dbDataSource, oldData ->
                            setValue(Resource.error(unprocessedApiResponse.message, oldData)));
                }
            }

        });
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (result.getValue() != newValue) {
            result.setValue(newValue);
        }
    }


    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    // Called when the network fetching fails
    protected abstract void onFetchFailed();

    // Casting from ApiResponse<RequestType> to RequestType
    @WorkerThread
    private RequestType processResponse(ApiResponse<RequestType> response) {
        return response.body;
    }

    // Called to save the result of the API response into the database
    @WorkerThread
    protected abstract void saveCallResult(RequestType result);

    // Called on database's data in order to decide if we need to fetch new data
    @MainThread
    protected abstract boolean shouldFetch(ResultType data);

    // Called to get the cached data from the database
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    // Called to create the API call
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();
}
