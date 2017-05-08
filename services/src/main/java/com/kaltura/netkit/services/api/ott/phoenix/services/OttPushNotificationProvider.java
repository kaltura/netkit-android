package com.kaltura.netkit.services.api.ott.phoenix.services;

import android.text.TextUtils;
import android.util.Log;

import com.kaltura.netkit.connect.executor.APIOkRequestsExecutor;
import com.kaltura.netkit.connect.response.ResponseElement;
import com.kaltura.netkit.services.api.ott.phoenix.session.OttSessionProvider;
import com.kaltura.netkit.utils.ErrorElement;
import com.kaltura.netkit.utils.OnRequestCompletion;

/**
 * Created by eladplotski on 08/05/2017.
 */

public class OttPushNotificationProvider extends OttSessionProvider{

    private static final String TAG = "OttPushProvider";

    public OttPushNotificationProvider(String baseUrl, int partnerId) {
        super(baseUrl, partnerId);
    }

    /*Push Notification Section*/

    public void setDevicePushToken(String pushToken){

        String ks = validateSession();
        if(TextUtils.isEmpty(ks))
        {
            ks = getSessionToken();
        }
        APIOkRequestsExecutor.getSingleton().queue(OttPushNotificationService.setDevicePushToken(apiBaseUrl, pushToken,ks)
                .completion(new OnRequestCompletion() {
                    @Override
                    public void onComplete(ResponseElement response) {

                        ErrorElement error = null;
                        if (response != null && response.isSuccess()) {
                            Log.d(TAG, "push token registartion : Succsess.");
                        } else {
                            error = response.getError() != null ? response.getError() : ErrorElement.GeneralError.message("Push Token Registration Failed");
                            Log.e(TAG, "push token reßgistartion : Failed with error - " + error.getMessage());
                        }

                    }
                }).build());

    }
    public void getPushNotificationStates(){

        String ks = validateSession();
        APIOkRequestsExecutor.getSingleton().queue(OttPushNotificationService.getNotificationSettingsStatus(apiBaseUrl,ks)
                .completion(new OnRequestCompletion() {
                    @Override
                    public void onComplete(ResponseElement response) {

                        ErrorElement error = null;
                        if (response != null && response.isSuccess()) {
                            Log.d(TAG, "push states : Succsess.");
                        } else {
                            error = response.getError() != null ? response.getError() : ErrorElement.GeneralError.message("Push Token states Failed");
                            Log.e(TAG, "push states : Failed with error - " + error.getMessage());
                        }

                    }
                }).build());

    }

    public void setPushNotificationStates(boolean allowNotification,boolean allowFollowNotification ){

        String ks = validateSession();
        APIOkRequestsExecutor.getSingleton().queue(OttPushNotificationService.setNotificationSettingsStatus(apiBaseUrl,ks,allowNotification,allowFollowNotification)
                .completion(new OnRequestCompletion() {
                    @Override
                    public void onComplete(ResponseElement response) {

                        ErrorElement error = null;
                        if (response != null && response.isSuccess()) {
                            Log.d(TAG, "push state update : Succsess.");
                        } else {
                            error = response.getError() != null ? response.getError() : ErrorElement.GeneralError.message("Push Token states Failed");
                            Log.e(TAG, "push state update : Failed with error - " + error.getMessage());
                        }

                    }
                }).build());

    }
}
