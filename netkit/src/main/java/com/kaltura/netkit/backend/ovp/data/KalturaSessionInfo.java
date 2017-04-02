package com.kaltura.netkit.backend.ovp.data;

import com.kaltura.netkit.utils.BaseResult;

/**
 * @hide
 */

public class KalturaSessionInfo extends BaseResult {

    String sessionType;
    long expiry;
    String userId;


    public long getExpiry() {
        return expiry;
    }

    public String getUserId() {
        return userId;
    }
}
