package com.kaltura.netkit.connect.executor;


import com.kaltura.netkit.connect.request.RequestConfiguration;
import com.kaltura.netkit.connect.request.RequestElement;
import com.kaltura.netkit.connect.response.ResponseElement;
import com.kaltura.netkit.utils.NetworkErrorEventListener;

public interface RequestQueue {

    void setRequestConfiguration(RequestConfiguration config);

    String queue(RequestElement request);

    String queue(RequestElement request, int retryCount);

    ResponseElement execute(RequestElement request);

    void cancelRequest(String reqId);

    //boolean hasRequest(String reqId);

    void clearRequests();

    boolean isEmpty();

    void setNetworkErrorEventListener(NetworkErrorEventListener networkErrorEventListener);
}
