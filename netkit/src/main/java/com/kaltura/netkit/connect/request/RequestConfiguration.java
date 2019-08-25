package com.kaltura.netkit.connect.request;

import android.text.format.DateUtils;


public class RequestConfiguration  {

    private long readTimeoutMs;
    private long writeTimeoutMs;
    private long connectTimeoutMs;
    private int maxRetries;
    private final int MAX_RETRIES_THRESHOLD = 10;

    public RequestConfiguration() {
        this.maxRetries = 4;
        this.readTimeoutMs = 20000;
        this.writeTimeoutMs = 20000;
        this.connectTimeoutMs = 10000;
    }

    public RequestConfiguration(int maxRetries, int readTimeoutMs, int writeTimeoutMs, int connectTimeoutMs) {
        setMaxRetries(maxRetries);
        this.readTimeoutMs = readTimeoutMs;
        this.writeTimeoutMs = writeTimeoutMs;
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public int getRetryAttempts() {
        return maxRetries;
    }

    public RequestConfiguration setMaxRetries(int maxRetries) {
        if (maxRetries > MAX_RETRIES_THRESHOLD) {
            this.maxRetries = MAX_RETRIES_THRESHOLD;
        } else {
            this.maxRetries = maxRetries;
        }
        return this;
    }

    public long getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public RequestConfiguration setReadTimeoutMs(long readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
        return this;
    }

    public long getWriteTimeoutMs() {
        return writeTimeoutMs;
    }

    public RequestConfiguration setWriteTimeoutMs(long writeTimeoutMs) {
        this.writeTimeoutMs = writeTimeoutMs;
        return this;
    }

    public long getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public RequestConfiguration setConnectTimeoutMs(long connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
        return this;
    }

    public long getRetryDelayMs(int retryCounter) {
        return ((long) Math.pow(2, Math.abs(retryCounter - maxRetries)) * DateUtils.SECOND_IN_MILLIS);
    }

    public int getMaxRetries() {
        return MAX_RETRIES_THRESHOLD;
    }
}
