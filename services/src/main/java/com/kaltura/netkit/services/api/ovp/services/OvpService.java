package com.kaltura.netkit.services.api.ovp.services;

import com.google.gson.JsonObject;
import com.kaltura.netkit.connect.request.MultiRequestBuilder;

/**
 * @hide
 */
public class OvpService {

    private static String sClientTag;
    private static String sApiVersion;

    public static void setClientConfigs(String clientTag, String apiVersion){
        sClientTag = clientTag;
        sApiVersion = apiVersion;
    }

    public static String[] getRequestConfigKeys(){
        return new String[]{"clientTag", "apiVersion", "format"};
    }

    public static JsonObject getOvpConfigParams(){
        JsonObject params = new JsonObject();
        params.addProperty("clientTag", sClientTag);
        params.addProperty("apiVersion", sApiVersion);
        params.addProperty("format",1); //json format

        return params;
    }

    public static MultiRequestBuilder getMultirequest(String baseUrl, String ks){
        return getMultirequest(baseUrl, ks, -1);
    }

    public static MultiRequestBuilder getMultirequest(String baseUrl, String ks, int partnerId){
        JsonObject ovpParams = OvpService.getOvpConfigParams();
        ovpParams.addProperty("ks", ks);
        if(partnerId > 0) {
            ovpParams.addProperty("partnerId", partnerId);
        }
        return (MultiRequestBuilder) new MultiRequestBuilder().method("POST")
                .url(baseUrl)
                .params(ovpParams)
                .service("multirequest");
    }
}
