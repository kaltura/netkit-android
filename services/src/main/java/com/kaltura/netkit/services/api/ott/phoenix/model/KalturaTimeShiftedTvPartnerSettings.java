package com.kaltura.netkit.services.api.ott.phoenix.model;

import com.kaltura.netkit.connect.response.BaseResult;

/**
 * Created by zivilan on 31/07/2017.
 */

public class KalturaTimeShiftedTvPartnerSettings extends BaseResult {

    boolean catchUpEnabled;

    boolean startOverEnabled;

    boolean trickPlayEnabled;

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