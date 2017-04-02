package com.kaltura.netkit.connect;

/**
 */

public interface RequestConfiguration {

    long getReadTimeout();
    long getWriteTimeout();
    long getConnectTimeout();
    int getRetry();
}
