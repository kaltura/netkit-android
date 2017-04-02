package com.kaltura.netkit.backend.ovp;

import com.kaltura.netkit.backend.ovp.services.OvpService;
import com.kaltura.netkit.connect.RequestBuilder;
import com.kaltura.netkit.connect.RequestElement;

/**
 * @hide
 */

public class OvpRequestBuilder extends RequestBuilder<OvpRequestBuilder> {

    @Override
    public RequestElement build() {
        addParams(OvpService.getOvpConfigParams());
        return super.build();
    }
}
