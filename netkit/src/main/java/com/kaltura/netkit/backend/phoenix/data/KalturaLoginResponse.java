package com.kaltura.netkit.backend.phoenix.data;

import com.kaltura.netkit.utils.BaseResult;
import com.kaltura.netkit.utils.ErrorElement;

/**
 * @hide
 */

public class KalturaLoginResponse extends BaseResult {

    private KalturaLoginSession loginSession;
    private KalturaOTTUser user;

    public KalturaLoginResponse(ErrorElement error) {
        super(error);
    }

    public KalturaLoginResponse() {
        super();
    }

    public KalturaLoginSession getLoginSession() {
        return loginSession;
    }

    public KalturaOTTUser getUser() {
        return user;
    }
}
