package com.kaltura.netkit.connect.request;

/**
 */

public interface RequestConfiguration {

    long getReadTimeoutMs();

    long getWriteTimeoutMs();

    long getConnectTimeoutMs();

    long getRetryDelayMs(int retryCount);

    int getRetryAttempts();
}
