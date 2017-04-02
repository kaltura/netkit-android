package com.kaltura.netkit.backend.ovp.data;

import com.kaltura.netkit.utils.BaseResult;

import java.util.List;

/**
 * @hide
 */

public class KalturaBaseEntryListResponse extends BaseResult {

    public List<KalturaMediaEntry> objects;
    int totalCount;
}
