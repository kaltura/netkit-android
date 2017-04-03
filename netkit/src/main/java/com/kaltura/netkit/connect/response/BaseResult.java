package com.kaltura.netkit.connect.response;

import com.kaltura.netkit.utils.ErrorElement;

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
