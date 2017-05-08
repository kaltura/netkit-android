package com.kaltura.netkit.services.api.common;

import android.support.annotation.StringDef;

import com.kaltura.netkit.utils.SessionProvider;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by eladplotski on 08/5/2017.
 */

public interface PushProviderInterface {

    String baseUrl();

    void setDevicePushToken(String pushToken,OnCompletion<PrimitiveResult> completion);

}
