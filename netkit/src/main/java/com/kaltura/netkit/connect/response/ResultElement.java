package com.kaltura.netkit.connect.response;

import com.kaltura.netkit.utils.ErrorElement;

/**
 * Created by tehilarozin on 06/09/2016.
 */
public interface ResultElement<T> {

    T getResponse();

    boolean isSuccess();

    ErrorElement getError();

}
