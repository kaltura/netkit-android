package com.kaltura.netkit.services.utils.session;

import com.kaltura.netkit.connect.response.PrimitiveResult;
import com.kaltura.netkit.utils.OnCompletion;

/**
 * Created by tehilarozin on 11/12/2016.
 */

public class TvpapiSessionProvider extends BaseSessionProvider {

    private int partnerId;

    public TvpapiSessionProvider(String baseUrl, int partnerId) {
        super(baseUrl, "");
        this.partnerId = partnerId;
    }

    @Override
    public void getSessionToken(OnCompletion<PrimitiveResult> completion) {
        // for now does nothing!
    }

    @Override
    public int partnerId() {
        return this.partnerId;
    }



    @Override
    protected String validateSession() {
        return null;
    }
}
