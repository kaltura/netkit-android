package com.kaltura.netkit.services.api.ott.phoenix.model;

import com.kaltura.netkit.connect.response.BaseResult;

/**
 * @hide
 */

public class KalturaLoginSession extends BaseResult {
    String refreshToken;
    String ks;

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getKs() {
        return ks;
    }
}
