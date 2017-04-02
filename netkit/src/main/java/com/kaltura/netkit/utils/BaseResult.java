package com.kaltura.netkit.utils;

/**
 */

public class BaseResult {
    public double executionTime;
    public ErrorElement error;

    public BaseResult() {
    }

    public BaseResult(ErrorElement error) {
        this.error = error;
    }
}
