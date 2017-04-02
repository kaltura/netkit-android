package com.kaltura.netkit.backend.phoenix.data;

import com.google.gson.annotations.SerializedName;
import com.kaltura.netkit.utils.BaseResult;

/**
 * @hide
 */

public class AssetResult extends BaseResult {

    @SerializedName(value = "result")
    public KalturaMediaAsset asset;

}
