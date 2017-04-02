package com.kaltura.netkit.backend.ovp.data;

import com.kaltura.netkit.backend.data.BasePlaybackContext;

import java.util.ArrayList;

/**
 * @hide
 */

public class KalturaPlaybackContext extends BasePlaybackContext {

    private ArrayList<KalturaPlaybackSource> sources;
    private ArrayList<KalturaFlavorAsset> flavorAssets;

    public KalturaPlaybackContext() {
    }

    public ArrayList<KalturaPlaybackSource> getSources() {
        return sources;
    }

    public ArrayList<KalturaFlavorAsset> getFlavorAssets() {
        return flavorAssets;
    }

}
