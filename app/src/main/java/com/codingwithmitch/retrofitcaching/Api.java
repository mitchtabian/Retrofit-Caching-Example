package com.codingwithmitch.retrofitcaching;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Api {

    @GET("{url}")
    Call<List<Photo>> searchPhotos(
            @Path("url") String url
    );

    @GET("photos/{id}")
    Call<Photo> searchPhoto(
            @Path("id") String id
    );

}










