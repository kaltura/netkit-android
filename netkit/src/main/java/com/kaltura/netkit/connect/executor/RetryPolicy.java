package com.kaltura.netkit.connect.executor;

import android.text.format.DateUtils;
import android.util.TimeUtils;

public class RetryPolicy {

    private int numRetries;
    private final int DEFAULT_MAX_RETRIES = 5;

    public RetryPolicy() {
        this.numRetries = 3;
    }

    public RetryPolicy(int numRetries) {
        setNumRetries(numRetries);
    }

    public int getNumRetries() {
        return numRetries;
    }

    public void setNumRetries(int numRetries) {
        if (numRetries > DEFAULT_MAX_RETRIES) {
            this.numRetries = DEFAULT_MAX_RETRIES;
        } else {
            this.numRetries = numRetries;
        }
    }

    public int getMaxRetries() {
        return DEFAULT_MAX_RETRIES;
    }

    public long getDelayMS(int retryCount) {
        return ((long) Math.pow(2, Math.abs(retryCount - numRetries)) * DateUtils.SECOND_IN_MILLIS);
    }
}
