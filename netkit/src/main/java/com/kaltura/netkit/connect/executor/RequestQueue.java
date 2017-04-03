package com.kaltura.netkit.connect.executor;


import com.kaltura.netkit.connect.request.RequestElement;
import com.kaltura.netkit.connect.response.ResponseElement; /**
 */
public interface RequestQueue {

    String queue(RequestElement request);

    ResponseElement execute(RequestElement request);

    void cancelRequest(String reqId);

    //boolean hasRequest(String reqId);

    void clearRequests();

    boolean isEmpty();
}
