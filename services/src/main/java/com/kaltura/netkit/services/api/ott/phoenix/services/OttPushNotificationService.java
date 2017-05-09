package com.kaltura.netkit.services.api.ott.phoenix.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.kaltura.netkit.services.api.ott.phoenix.PhoenixRequestBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @hide
 */

public class OttPushNotificationService extends PhoenixService {

//    public static PhoenixRequestBuilder setDevicePushToken(String baseUrl, String token){
//        return setDevicePushToken(baseUrl, token);
//    }
//
//
    public static PhoenixRequestBuilder setDevicePushToken(String baseUrl, String token,String ks){
        JsonObject params = new JsonObject();
        params.addProperty("ks",ks);
        params.addProperty("pushToken", token);

        return new PhoenixRequestBuilder()
                .service("Notification")
                .action("setDevicePushToken")
                .method("POST")
                .url(baseUrl)
                .tag("ottuser-register")
                .params(params);

    }

    public static PhoenixRequestBuilder addFollowTVSeries(String baseUrl, String ks ,int assetId){
        JsonObject params = new JsonObject();
        params.addProperty("ks",ks);
        params.addProperty("asset_id",assetId);
        return new PhoenixRequestBuilder()
                .service("followTvSeries")
                .action("add")
                .method("POST")
                .url(baseUrl)
                .tag("ottuser-register")
                .params(params);

    }

    public static PhoenixRequestBuilder getNotificationSettingsStatus(String baseUrl, String ks){
        JsonObject params = new JsonObject();
        params.addProperty("ks",ks);
        return new PhoenixRequestBuilder()
                .service("notificationsSettings")
                .action("get")
                .method("POST")
                .url(baseUrl)
                .tag("ottuser-push-states")
                .params(params);

    }

    public static PhoenixRequestBuilder setNotificationSettingsStatus(String baseUrl, String ks, boolean allowNotification, boolean allowFollowNotification){
        JsonObject params = new JsonObject();
        params.addProperty("ks",ks);
        params.addProperty("settings",getSettingsJson(allowFollowNotification,allowNotification));


        return new PhoenixRequestBuilder()
                .service("notificationssettings")
                .action("update")
                .method("POST")
                .url(baseUrl)
                .tag("ottuser-update")
                .params(params);

    }
    private static String getSettingsJson(boolean allow,boolean follow) {

        try {
            return new JSONObject("{pushNotificationEnabled : "+ allow +  "," + "pushFollowEnabled : " + follow  + "}").toString().replace("\\","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";

    }
}
