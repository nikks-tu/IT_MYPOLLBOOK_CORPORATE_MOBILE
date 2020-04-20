package com.corporate.new_chanages_corporate.api_interface;


import com.corporate.contus_Corporate.app.Constants;
import com.corporate.new_chanages_corporate.models.MobileExistMainObject;
import com.corporate.new_chanages_corporate.post_parameters.MobileExistPostParameters;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MobileExistDataInterface {

    @POST(Constants.MOBILE_EXIST)
   // Call<CurrentDataMainObject> fetchCurrentData(@Part("deviceId") String deviceId, @Part("userId") String userId);
    Call<MobileExistMainObject> getStringScalar(@Body MobileExistPostParameters postParameter);

}
