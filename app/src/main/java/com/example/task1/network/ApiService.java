package com.example.task1.network;

import com.example.task1.model.LoginRequest;
import com.example.task1.model.LoginResponse;
import com.example.task1.model.Transaction;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

import java.util.List;

public interface ApiService {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("transactions")
    Call<List<Transaction>> getTransactions(@Header("Authorization") String token);
}
