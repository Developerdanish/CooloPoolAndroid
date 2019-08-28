package com.coolopool.coolopool.Interface;

import com.coolopool.coolopool.Backend.Model.User;
import com.coolopool.coolopool.Class.HomePageBlog;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface TripClient {

    @POST("user")
    Call<ResponseBody> createAccount(@Body User user);

    @Multipart
    @POST("upload")
    Call<ResponseBody> uploadTrip(
            @Part List<MultipartBody.Part> images,
            @Body RequestBody body
    );

    @GET("trip.json")
    Call<List<HomePageBlog>> getBlogs();
}
