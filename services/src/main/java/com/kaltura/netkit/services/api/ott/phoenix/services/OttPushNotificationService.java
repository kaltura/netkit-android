package com.kaltura.netkit.services.api.ott.phoenix.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.kaltura.netkit.services.api.ott.phoenix.PhoenixRequestBuilder;

/**
 * @hide
 */

public class OttPushNotificationService extends PhoenixService {

//    public static PhoenixRequestBuilder setDevicePushToken(String baseUrl, String token){
//        return setDevicePushToken(baseUrl, token);
//    }
//
//
    public static PhoenixRequestBuilder setDevicePushToken(String baseUrl, String token){
        JsonObject params = new JsonObject();
        params.addProperty("token", token);

        return new PhoenixRequestBuilder()
                .service("ottPush")
                .action("register")
                .method("POST")
                .url(baseUrl)
                .tag("ottuser-register")
                .params(params);

    }
}
