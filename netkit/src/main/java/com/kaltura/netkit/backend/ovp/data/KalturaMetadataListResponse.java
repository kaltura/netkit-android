package com.kaltura.netkit.backend.ovp.data;

import com.kaltura.netkit.utils.BaseResult;

import java.util.List;
/**
 * @hide
 */

public class KalturaMetadataListResponse extends BaseResult {

    public List<KalturaMetadata> objects;
    int totalCount;
}
