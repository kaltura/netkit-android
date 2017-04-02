package com.kaltura.netkit.backend.ovp.data;

import com.kaltura.netkit.utils.BaseResult;

import java.util.ArrayList;

/**
 * @hide
 */

public class KalturaEntryContextDataResult extends BaseResult {

    ArrayList<KalturaFlavorAsset> flavorAssets;


    public ArrayList<KalturaFlavorAsset> getFlavorAssets() {
        return flavorAssets;
    }


}
