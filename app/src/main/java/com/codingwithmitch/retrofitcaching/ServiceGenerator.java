package com.codingwithmitch.retrofitcaching;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServiceGenerator {

    private static final String TAG = "ServiceGenerator";
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_PRAGMA = "Pragma";

    private static ServiceGenerator instance;

    public static ServiceGenerator getInstance(){
        if(instance == null){
            instance = new ServiceGenerator();
        }
        return instance;
    }

    private static final long cacheSize = 5 * 1024 * 1024; // 5 MB


    private static Retrofit retrofit(){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static OkHttpClient okHttpClient(){
        return new OkHttpClient.Builder()
                .cache(cache())
                .addInterceptor(httpLoggingInterceptor())
                .addNetworkInterceptor(networkInterceptor())
                .addInterceptor(offlineInterceptor())
                .build();
    }

    private static Cache cache(){
        return new Cache(new File(MyApplication.getInstance().getCacheDir(),"someIdentifier"), cacheSize);
    }

    /**
     * This interceptor will be called both if the network is available and if the network is not available
     * @return
     */
    private static Interceptor offlineInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Log.d(TAG, "offline interceptor: called.");
                Request request = chain.request();

                if (!MyApplication.hasNetwork()) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS)
                            .build();

                    request = request.newBuilder()
                            .removeHeader(HEADER_PRAGMA)
                            .removeHeader(HEADER_CACHE_CONTROL)
                            .cacheControl(cacheControl)
                            .build();
                }

                return chain.proceed(request);
            }
        };
    }

    /**
     * This interceptor will be called ONLY if the network is available
     * @return
     */
    private static Interceptor networkInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Log.d(TAG, "network interceptor: called.");

                Response response = chain.proceed(chain.request());

                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(5, TimeUnit.SECONDS)
                        .build();

                return response.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .removeHeader(HEADER_CACHE_CONTROL)
                        .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }

    private static HttpLoggingInterceptor httpLoggingInterceptor ()
    {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor( new HttpLoggingInterceptor.Logger()
                {
                    @Override
                    public void log (String message)
                    {
                        Log.d(TAG, "log: http log: " + message);
                    }
                } );
        httpLoggingInterceptor.setLevel( HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }

    public static Api getApi(){
        return retrofit().create(Api.class);
    }

}





















