package com.kaltura.netkit.connect;


import com.kaltura.netkit.utils.ResponseElement; /**
 */
public interface RequestQueue {

    String queue(RequestElement request);

    ResponseElement execute(RequestElement request);

    void cancelRequest(String reqId);

    //boolean hasRequest(String reqId);

    void clearRequests();

    boolean isEmpty();
}
