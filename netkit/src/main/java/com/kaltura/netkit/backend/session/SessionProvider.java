package com.kaltura.netkit.backend.session;

/**
 * Created by tehilarozin on 13/11/2016.
 */

import com.kaltura.netkit.utils.PrimitiveResult;
import com.kaltura.netkit.utils.OnCompletion;

/**
 * provides session related configuration data to be used by who ever needs to communicate with
 * a remote data provider
 */
public interface SessionProvider {

    String baseUrl();

    void getSessionToken(OnCompletion<PrimitiveResult> completion);

    int partnerId();

}
