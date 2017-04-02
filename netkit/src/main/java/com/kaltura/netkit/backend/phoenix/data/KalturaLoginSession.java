package com.kaltura.netkit.backend.phoenix.data;

import com.kaltura.netkit.utils.BaseResult;

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
