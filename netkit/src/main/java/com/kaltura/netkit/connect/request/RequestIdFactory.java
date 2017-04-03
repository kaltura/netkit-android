package com.kaltura.netkit.connect.request;

import com.kaltura.netkit.connect.executor.APIOkRequestsExecutor;

import java.util.UUID;

/**
 * @hide
 */
public class RequestIdFactory implements APIOkRequestsExecutor.IdFactory {
    @Override
    public String factorId(String factor) {
        return UUID.randomUUID().toString() + "::" + (factor!=null ? factor : System.currentTimeMillis());
    }
}
