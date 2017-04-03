package com.kaltura.netkit.services.api.ott.phoenix;


import com.kaltura.netkit.services.api.ott.phoenix.services.PhoenixService;
import com.kaltura.netkit.connect.request.RequestBuilder;
import com.kaltura.netkit.connect.request.RequestElement;

/**
 */

public class PhoenixRequestBuilder extends RequestBuilder<PhoenixRequestBuilder> {

    @Override
    public RequestElement build() {
        addParams(PhoenixService.getPhoenixConfigParams());
        return super.build();
    }
}
