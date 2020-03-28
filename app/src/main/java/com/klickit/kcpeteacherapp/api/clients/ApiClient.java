package com.klickit.kcpeteacherapp.api.clients;

import androidx.annotation.NonNull;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.klickit.kcpeteacherapp.api.services.ApiService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.klickit.kcpeteacherapp.api.ApiConstants.API_BASE_URL;
import static com.klickit.kcpeteacherapp.api.ApiConstants.CONNECT_TIMEOUT;
import static com.klickit.kcpeteacherapp.api.ApiConstants.READ_TIMEOUT;
import static com.klickit.kcpeteacherapp.api.ApiConstants.WRITE_TIMEOUT;


public class ApiClient {
    private Retrofit retrofit;
    private String mAuthToken;
    private boolean isDebug = false;

    public ApiClient() {

    }

    public ApiClient setAuthToken(String authToken) {
        this.mAuthToken = authToken;
        return this;
    }

    private String getmAuthToken() {
        return mAuthToken;
    }

    private Retrofit getRestAdapter() {

        OkHttpClient.Builder okhttpBuilder = getOkHttpClientBuilder();


        Retrofit.Builder builder = new Retrofit.Builder();

        builder.baseUrl(API_BASE_URL);
        builder.client(okhttpBuilder.build());
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        builder.addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();

        return retrofit;
    }


    private OkHttpClient.Builder getOkHttpClientBuilder() {
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (isDebug) {
            // httpClient.addInterceptor(loggingInterceptor);
        }

        if (mAuthToken != null && !mAuthToken.equals("")) {

            httpClient.addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + mAuthToken)
                        .addHeader("Content-Type", "application/json")
                        .build();
                return chain.proceed(request);
            });
        }
        return httpClient;
    }

    @NonNull
    public ApiService apiService() {
        return getRestAdapter().create(ApiService.class);
    }


}
