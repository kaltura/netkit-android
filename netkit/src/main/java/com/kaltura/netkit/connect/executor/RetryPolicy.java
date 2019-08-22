package com.kaltura.netkit.connect.executor;

import android.text.format.DateUtils;

import com.kaltura.netkit.connect.request.RequestConfiguration;

public class RetryPolicy implements RequestConfiguration {

    private long readTimeoutMs;
    private long writeTimeoutMs;
    private long connectTimeoutMs;
    private int retryAttemps;
    private final int DEFAULT_MAX_RETRIES = 10;

    public RetryPolicy() {
        this.retryAttemps = 4;
        this.readTimeoutMs = 20000;
        this.writeTimeoutMs = 20000;
        this.connectTimeoutMs = 10000;
    }

    public RetryPolicy(int retryAttemps, int readTimeoutMs, int writeTimeoutMs, int connectTimeoutMs) {
        setRetryAttemps(retryAttemps);
        this.readTimeoutMs = readTimeoutMs;
        this.writeTimeoutMs = writeTimeoutMs;
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public int getRetryAttempts() {
        return retryAttemps;
    }

    public RetryPolicy setRetryAttemps(int retryAttemps) {
        if (retryAttemps > DEFAULT_MAX_RETRIES) {
            this.retryAttemps = DEFAULT_MAX_RETRIES;
        } else {
            this.retryAttemps = retryAttemps;
        }
        return this;
    }

    @Override
    public long getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public RetryPolicy setReadTimeoutMs(long readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
        return this;
    }

    @Override
    public long getWriteTimeoutMs() {
        return writeTimeoutMs;
    }

    public RetryPolicy setWriteTimeoutMs(long writeTimeoutMs) {
        this.writeTimeoutMs = writeTimeoutMs;
        return this;
    }

    @Override
    public long getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public RetryPolicy setConnectTimeoutMs(long connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
        return this;
    }

    @Override
    public long getRetryDelayMs(int retryCount) {
        return ((long) Math.pow(2, Math.abs(retryCount - retryAttemps)) * DateUtils.SECOND_IN_MILLIS);
    }

    public int getMaxRetries() {
        return DEFAULT_MAX_RETRIES;
    }
}
