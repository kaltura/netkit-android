package com.kaltura.netkit.connect.executor;

public class RertryPolicy {

    private int numRetries;
    private final int DEFAULT_MAX_RETRIES = 5;

    public RertryPolicy() {
        this.numRetries = 3;
    }

    public RertryPolicy(int numRetries) {
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
        return ((long) Math.pow(2, Math.abs(retryCount - numRetries)) * 1000);
    }
}
