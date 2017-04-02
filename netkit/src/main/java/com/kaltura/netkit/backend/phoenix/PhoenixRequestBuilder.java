package com.kaltura.netkit.backend.phoenix;


import com.kaltura.netkit.backend.phoenix.services.PhoenixService;
import com.kaltura.netkit.connect.RequestBuilder;
import com.kaltura.netkit.connect.RequestElement;

/**
 */

public class PhoenixRequestBuilder extends RequestBuilder<PhoenixRequestBuilder> {

    @Override
    public RequestElement build() {
        addParams(PhoenixService.getPhoenixConfigParams());
        return super.build();
    }
}
