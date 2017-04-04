package com.kaltura.netkit.services.api.ott.phoenix.model;

import com.kaltura.netkit.services.api.common.BasePlaybackContext;

import java.util.ArrayList;

/**
 * Created by tehilarozin on 02/11/2016.
 */

public class KalturaPlaybackContext extends BasePlaybackContext {

    private ArrayList<KalturaPlaybackSource> sources;


    public ArrayList<KalturaPlaybackSource> getSources() {
        return sources;
    }



}
