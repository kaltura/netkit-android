package com.kaltura.netkit.services.api.ott.phoenix.services;

import android.text.TextUtils;
import android.util.Log;

import com.kaltura.netkit.connect.executor.APIOkRequestsExecutor;
import com.kaltura.netkit.connect.response.PrimitiveResult;
import com.kaltura.netkit.connect.response.ResponseElement;
import com.kaltura.netkit.services.api.common.BaseSessionProvider;
import com.kaltura.netkit.services.api.ott.phoenix.session.OttSessionProvider;
import com.kaltura.netkit.utils.ErrorElement;
import com.kaltura.netkit.utils.OnCompletion;
import com.kaltura.netkit.utils.OnRequestCompletion;

/**
 * Created by eladplotski on 08/05/2017.
 */

public class OttPushNotificationProvider {

    private static final String TAG = "OttPushProvider";
    private static String apiBaseUrl = "";
    private static String ks = "";

    public OttPushNotificationProvider(String baseUrl, String ks) {
        this.apiBaseUrl = baseUrl;
        this.ks = ks;
    }

    /*Push Notification Section*/

    public void setDevicePushToken(final String pushToken){

//        ks = validateSession();

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

    public void addFollowTVSeries(int mediaid,final OnCompletion<ResponseElement> pushStateCallback){

        APIOkRequestsExecutor.getSingleton().queue(OttPushNotificationService.addFollowTVSeries(apiBaseUrl,ks,mediaid)
                .completion(new OnRequestCompletion() {
                    @Override
                    public void onComplete(ResponseElement response) {

                        ErrorElement error = null;
                        if (response != null && response.isSuccess()) {
                            Log.d(TAG, "addFollowTVSeries : Succsess.");
                            pushStateCallback.onComplete(response);
                        } else {
                            error = response.getError() != null ? response.getError() : ErrorElement.GeneralError.message("Push Token states Failed");
                            pushStateCallback.onComplete(response);
                            Log.e(TAG, "addFollowTVSeries : Failed with error - " + error.getMessage());
                        }

                    }
                }).build());

    }

    public void getPushNotificationStates(final OnCompletion<ResponseElement> pushStateCallback){

//        String ks = getSessionToken();
        APIOkRequestsExecutor.getSingleton().queue(OttPushNotificationService.getNotificationSettingsStatus(apiBaseUrl,ks)
                .completion(new OnRequestCompletion() {
                    @Override
                    public void onComplete(ResponseElement response) {

                        ErrorElement error = null;
                        if (response != null && response.isSuccess()) {
                            Log.d(TAG, "push states : Succsess.");
                            pushStateCallback.onComplete(response);
                        } else {
                            error = response.getError() != null ? response.getError() : ErrorElement.GeneralError.message("Push Token states Failed");
                            pushStateCallback.onComplete(response);
                            Log.e(TAG, "push states : Failed with error - " + error.getMessage());
                        }

                    }
                }).build());

    }

    public void setPushNotificationStates(boolean allowNotification,boolean allowFollowNotification, final OnCompletion<ResponseElement> pushStateCallback){

//        String ks = validateSession();
        APIOkRequestsExecutor.getSingleton().queue(OttPushNotificationService.setNotificationSettingsStatus(apiBaseUrl,ks,allowNotification,allowFollowNotification)
                .completion(new OnRequestCompletion() {
                    @Override
                    public void onComplete(ResponseElement response) {

                        ErrorElement error = null;
                        if (response != null && response.isSuccess()) {
                            Log.d(TAG, "push state update : Succsess.");
                            pushStateCallback.onComplete(response);
                        } else {
                            pushStateCallback.onComplete(response);
                            error = response.getError() != null ? response.getError() : ErrorElement.GeneralError.message("Push Token states Failed");
                            Log.e(TAG, "push state update : Failed with error - " + error.getMessage());
                        }

                    }
                }).build());

    }
}
