package com.example.th.Chat;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/prompt")
    Call<PromtResponse> sendPrompt(@Body PromtRequest promptRequest);
}
