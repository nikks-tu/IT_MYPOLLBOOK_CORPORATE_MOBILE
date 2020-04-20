package com.corporate.contusfly_corporate.api_interface;

import com.corporate.contus_Corporate.app.Constants;
import com.corporate.contusfly_corporate.model.AppVersionMainObject;
import com.corporate.contusfly_corporate.model.AppVersionPostParameters;
import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface AppVersionDataInterface {

    @POST(Constants.APP_VERSION_CHECK)
   // Call<CurrentDataMainObject> fetchCurrentData(@Part("deviceId") String deviceId, @Part("userId") String userId);

    Call<JsonElement> getStringScalar(@Header("authKey") String headerValue, @Body AppVersionPostParameters postParameter);

}
