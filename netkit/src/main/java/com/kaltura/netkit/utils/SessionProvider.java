package com.kaltura.netkit.utils;

/**
 * Created by tehilarozin on 13/11/2016.
 */

import com.kaltura.netkit.connect.response.PrimitiveResult;

/**
 * provides session related configuration data to be used by who ever needs to communicate with
 * a remote data provider
 */
public interface SessionProvider {

    String baseUrl();

    void getSessionToken(OnCompletion<PrimitiveResult> completion);

    int partnerId();

}
