package com.kaltura.netkit.services.api.ott.phoenix.services;

import com.google.gson.JsonObject;
import com.kaltura.netkit.services.api.ott.phoenix.PhoenixRequestBuilder;

/**
 * Created by zivilan on 31/07/2017.
 */

public class TimeShiftedService extends PhoenixService {

    public static PhoenixRequestBuilder actionGet(String baseUrl, int partnerId, String ks) {
        return new PhoenixRequestBuilder()
                .service("timeshiftedtvpartnersettings")
                .action("get")
                .method("POST")
                .url(baseUrl)
                .tag("timeshifted-action-get")
                .params(getTimeShiftedGetReqParams(ks));
    }

    private static JsonObject getTimeShiftedGetReqParams(String ks) {
        JsonObject getParams = new JsonObject();
        getParams.addProperty("ks", ks);

        return getParams;
    }
}