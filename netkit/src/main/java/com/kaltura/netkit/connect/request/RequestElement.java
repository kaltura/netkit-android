package com.kaltura.netkit.connect.request;


import android.support.annotation.NonNull;

import com.kaltura.netkit.connect.response.ResponseElement;import java.util.Map;

/**
 */
public interface RequestElement {

    String getMethod();

    String getUrl();

    String getBody();

//    HashMap<String, String> getParams();

    String getTag();

    @NonNull Map<String, String> getHeaders();

    String getId();

    RequestConfiguration config();

    void onComplete(ResponseElement responseElement);
}
