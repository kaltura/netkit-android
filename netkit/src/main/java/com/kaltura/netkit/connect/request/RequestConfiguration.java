package com.kaltura.netkit.connect.request;

/**
 */

public interface RequestConfiguration {

    long getReadTimeout();
    long getWriteTimeout();
    long getConnectTimeout();
    int getRetry();
    boolean printLogs();
}
