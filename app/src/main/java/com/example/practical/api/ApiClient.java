package com.example.practical.api;

import com.example.practical.core.CoreApp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final int OKHTTP_TIMEOUT = 60; // seconds
    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient;

    /**
     * You can create multiple methods for different BaseURL
     *
     * @return {@link Retrofit} object
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(WebService.BaseLink + WebService.Version)
                    .client(getOKHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }


    /**
     * settings like caching, Request Timeout, Logging can be configured here.
     *
     * @return {@link OkHttpClient}
     */
    private static OkHttpClient getOKHttpClient() {
        if (okHttpClient == null) {
            Cache cache = new Cache(new File(CoreApp.getInstance().getCacheDir(), "http")
                    , DISK_CACHE_SIZE);
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(OKHTTP_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(OKHTTP_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(OKHTTP_TIMEOUT, TimeUnit.SECONDS)
                    .cache(cache);


            setupBasicAuth(builder);
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    private static void setupBasicAuth(OkHttpClient.Builder builder) {
        builder.addInterceptor(chain -> {
            Request original = chain.request();

            Request.Builder requestBuilder = original.newBuilder()
                    .header("Accept", "application/json")
                    .method(original.method(), original.body());

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
    }
}

interface WebService {
    String BaseLink = "https://www.monclergroup.com/wp-json/mobileApp/";
    String Version = "v1/";
}
