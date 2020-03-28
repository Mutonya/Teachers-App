package com.klickit.kcpeteacherapp.api.services;


import com.klickit.kcpeteacherapp.models.SmsRequest;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/services/sendsms/")
    Single<ResponseBody> sendSms(@Body SmsRequest request);


}
