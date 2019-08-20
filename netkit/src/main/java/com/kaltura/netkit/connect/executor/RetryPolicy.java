package com.kaltura.netkit.connect.executor;

import android.text.format.DateUtils;

public class RetryPolicy {

    private int numRetries;
    private int readTimeoutMs;
    private int writeTimeoutMs;
    private int connectTimeoutMs;
    private final int DEFAULT_MAX_RETRIES = 10;

    public RetryPolicy() {
        this.numRetries = 4;
        this.readTimeoutMs = 20000;
        this.writeTimeoutMs = 20000;
        this.connectTimeoutMs = 10000;
    }

    public RetryPolicy(int numRetries, int readTimeoutMs, int writeTimeoutMs, int connectTimeoutMs) {
        setNumRetries(numRetries);
        this.readTimeoutMs = readTimeoutMs;
        this.writeTimeoutMs = writeTimeoutMs;
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public int getNumRetries() {
        return numRetries;
    }

    public RetryPolicy setNumRetries(int numRetries) {
        if (numRetries > DEFAULT_MAX_RETRIES) {
            this.numRetries = DEFAULT_MAX_RETRIES;
        } else {
            this.numRetries = numRetries;
        }
        return this;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public RetryPolicy setReadTimeoutMs(int readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
        return this;
    }

    public int getWriteTimeoutMs() {
        return writeTimeoutMs;
    }

    public RetryPolicy setWriteTimeoutMs(int writeTimeoutMs) {
        this.writeTimeoutMs = writeTimeoutMs;
        return this;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public RetryPolicy setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
        return this;
    }

    public int getMaxRetries() {
        return DEFAULT_MAX_RETRIES;
    }

    public long getDelayMS(int retryCount) {
        return ((long) Math.pow(2, Math.abs(retryCount - numRetries)) * DateUtils.SECOND_IN_MILLIS);
    }
}
