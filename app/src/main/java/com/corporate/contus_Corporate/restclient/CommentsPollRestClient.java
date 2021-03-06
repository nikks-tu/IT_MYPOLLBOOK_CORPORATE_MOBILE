/**
 * @category TeasorTrailor
 * @package com.contus.restclient
 * @version 1.0
 * @author Contus Team <developers@contus.in>
 * @copyright Copyright (C) 2015 Contus. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */
package com.corporate.contus_Corporate.restclient;

import com.corporate.contus_Corporate.apiinterface.CommentsApiInterface;
import com.corporate.contus_Corporate.app.Constants;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by user on 9/24/2015.
 */
public class CommentsPollRestClient {
    private static CommentsApiInterface sWelcomeApiInterface; /** The s settings api interface. */
    private static String root = Constants.LIVE_BASE_URL+"api/v1"; /** The root. */
    static {
        setupRestClient();
    }
    /**
     * Instantiates a new settings rest client.
     */
    private CommentsPollRestClient() {
    }
    /**
     * Gets the.
     *
     * @return the settings api interface
     */
    public static CommentsApiInterface getInstance() {
        return sWelcomeApiInterface;
    }
    /**
     * Setup rest client.
     */
    private static void setupRestClient() {
        //HTTP is the way modern applications network. It’s how we exchange data & media.
        // Doing HTTP efficiently makes your stuff load faster and saves bandwidth.
        OkHttpClient okHttp = new OkHttpClient();
        //set connect time out
        okHttp.setConnectTimeout(180, TimeUnit.SECONDS);
        //The REST adapter allows your store to communicate with an HTTP server by transmitting JSON via XHR.
        // Most Ember.js apps that consume a JSON API should use the REST adapter.
        RestAdapter.Builder builder = new RestAdapter.Builder().setEndpoint(root).setLogLevel(RestAdapter.LogLevel.FULL).setClient(new OkClient(okHttp));
        RestAdapter restAdapter = builder.build();
        //create the rest adapter
        sWelcomeApiInterface = restAdapter.create(CommentsApiInterface.class);
    }
}
