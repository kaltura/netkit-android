package com.kaltura.netkit.services.api.ott.phoenix.model;

import android.support.annotation.StringDef;

import com.kaltura.netkit.services.api.common.BasePlaybackSource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.kaltura.netkit.services.api.ott.phoenix.model.KalturaPlaybackSource.AdsPolicy.KeepAds;
import static com.kaltura.netkit.services.api.ott.phoenix.model.KalturaPlaybackSource.AdsPolicy.NoAds;

/**
 * Created by tehilarozin on 13/02/2017.
 */

public class KalturaPlaybackSource extends BasePlaybackSource {
    private int assetId;
    private int id;
    private String type; //Device types as defined in the system (MediaFileFormat)
    private long duration;
    private String externalId;
    private String adsPolicy;

    public int getId() {
        return id;
    }

    public long getDuration() {
        return duration;
    }

    public String getType() {
        return type;
    }

    public String getAdsPolicy() {
        return adsPolicy;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({NoAds, KeepAds})
    public @interface AdsPolicy {
        String NoAds = "NO_ADS";
        String KeepAds = "KEEP_ADS";
    }
}
