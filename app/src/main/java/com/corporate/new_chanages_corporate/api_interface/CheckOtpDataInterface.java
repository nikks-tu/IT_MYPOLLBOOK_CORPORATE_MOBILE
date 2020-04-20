package com.corporate.new_chanages_corporate.api_interface;


import com.corporate.contus_Corporate.app.Constants;
import com.google.gson.JsonElement;
import com.corporate.new_chanages_corporate.post_parameters.OTPValidatePostParameters;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CheckOtpDataInterface {

    @POST("api/v1"+ Constants.SMS_VERIFY_API)
   // Call<CurrentDataMainObject> fetchCurrentData(@Part("deviceId") String deviceId, @Part("userId") String userId);
   // Call<CheckOTPMainObject> getStringScalar(@Body OTPValidatePostParameters postParameter);
    Call<JsonElement> getStringScalar(@Body OTPValidatePostParameters postParameter);

}
