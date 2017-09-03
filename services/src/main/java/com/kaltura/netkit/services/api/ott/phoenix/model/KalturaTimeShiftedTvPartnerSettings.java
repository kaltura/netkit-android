package com.kaltura.netkit.services.api.ott.phoenix.model;

import com.google.gson.annotations.SerializedName;
import com.kaltura.netkit.connect.response.BaseResult;

/**
 * Created by zivilan on 31/07/2017.
 */

public class KalturaTimeShiftedTvPartnerSettings extends BaseResult {

    @SerializedName(value = "catchUpEnabled")
    boolean catchUpEnabled;

    @SerializedName(value = "startOverEnabled")
    boolean startOverEnabled;

    @SerializedName(value = "trickPlayEnabled")
    boolean trickPlayEnabled;

    @SerializedName(value = "cdvrEnabled")
    boolean cdvrEnabled;

    int catchUpBufferLength;

    int trickPlayBufferLength;


    public boolean isCatchUpEnabled() {
        return catchUpEnabled;
    }

    public boolean isStartOverEnabled() {
        return startOverEnabled;
    }

    public boolean isTrickPlayEnabled() {
        return trickPlayEnabled;
    }

    public boolean isCdvrEnabled() {
        return cdvrEnabled;
    }

    public int getCatchUpBufferLength() {
        return catchUpBufferLength;
    }

    public int getTrickPlayBufferLength() {
        return trickPlayBufferLength;
    }

}