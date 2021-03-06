package com.kaltura.netkit.services.api.ovp.services;

import com.google.gson.JsonObject;
import com.kaltura.netkit.services.api.ovp.OvpRequestBuilder;

/**
 * @hide
 */

public class OvpSessionService extends OvpService {

    public static OvpRequestBuilder anonymousSession(String baseUrl, int partnerId){
        JsonObject params = new JsonObject();
        params.addProperty("widgetId", "_"+partnerId);

        return new OvpRequestBuilder()
                .service("session")
                .action("startWidgetSession")
                .method("POST")
                .url(baseUrl)
                .tag("session-startWidget")
                .params(params);
    }

    public static OvpRequestBuilder get(String baseUrl, String ks){
        JsonObject params = new JsonObject();
        params.addProperty("ks", ks);

        return new OvpRequestBuilder()
                .service("session")
                .action("get")
                .method("POST")
                .url(baseUrl)
                .tag("session-get")
                .params(params);
    }

    public static OvpRequestBuilder end(String baseUrl, String ks) {
        JsonObject params = new JsonObject();
        params.addProperty("ks", ks);

        return new OvpRequestBuilder()
                .service("session")
                .action("end")
                .method("POST")
                .url(baseUrl)
                .tag("session-end")
                .params(params);
    }
}
