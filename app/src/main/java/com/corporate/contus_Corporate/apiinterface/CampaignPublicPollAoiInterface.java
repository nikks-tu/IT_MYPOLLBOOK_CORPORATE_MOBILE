package com.corporate.contus_Corporate.apiinterface;

import com.corporate.contus_Corporate.app.Constants;
import com.corporate.contus_Corporate.responsemodel.UserPollResponseModel;

import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;

/**
 * Created by user on 9/10/2015.
 */
public interface CampaignPublicPollAoiInterface {
    // annotation used to post the data
    @Multipart
    @POST(Constants.PUBLIC_POLL_API)//An object can be specified for use as an HTTP request body with the @Body annotation.
    //Request for the public poll
    void postCampaignPublicPoll(@Part(Constants.ACTION) String action, @Part(Constants.CAMPAIGN_UNIQUE_ID) String campaignId, @Part(Constants.PAGE) Integer page, @Part(Constants.USER_ID) String userId, Callback<UserPollResponseModel> callback);
}
