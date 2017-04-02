package com.kaltura.netkit.backend.data;

import com.kaltura.netkit.utils.BaseResult;

/**
 * Created by tehilarozin on 13/02/2017.
 */

public class KalturaDrmPlaybackPluginData extends BaseResult {
    private String scheme;
    private String certificate;
    private String licenseURL;

    public String getLicenseURL() {
        return licenseURL;
    }

    public String getScheme() {
        return scheme;
    }
}
