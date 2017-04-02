package com.kaltura.netkit.backend.phoenix.data;

import com.kaltura.netkit.backend.data.BasePlaybackSource;

/**
 * Created by tehilarozin on 13/02/2017.
 */

public class KalturaPlaybackSource extends BasePlaybackSource {
    private int assetId;
    private int id;
    private String type; //Device types as defined in the system (MediaFileFormat)
    private long duration;
    private String externalId;

    public int getId() {
        return id;
    }

    public long getDuration() {
        return duration;
    }

    public String getType() {
        return type;
    }
}
