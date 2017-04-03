package com.kaltura.netkit.services.api.ovp;

import com.kaltura.netkit.services.api.ovp.services.OvpService;
import com.kaltura.netkit.connect.request.RequestBuilder;
import com.kaltura.netkit.connect.request.RequestElement;

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
