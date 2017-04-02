package com.kaltura.netkit.backend.phoenix.data;

import android.text.TextUtils;
import com.kaltura.netkit.utils.BaseResult;


/**
 * @hide
 */

public class KalturaLicensedUrl extends BaseResult {
    String mainUrl = "";
    String altUrl = "";

    public String getMainUrl() {
        return mainUrl;
    }

    public String getLicensedUrl() {
        return TextUtils.isEmpty(mainUrl) ? altUrl : mainUrl;
    }

    public String getAltUrl() {
        return altUrl;
    }
}
