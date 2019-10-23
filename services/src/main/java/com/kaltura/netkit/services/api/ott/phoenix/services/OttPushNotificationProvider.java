package com.kaltura.netkit.services.api.ott.phoenix.services;



import com.kaltura.netkit.connect.executor.APIOkRequestsExecutor;
import com.kaltura.netkit.connect.response.PrimitiveResult;
import com.kaltura.netkit.connect.response.ResponseElement;
import com.kaltura.netkit.utils.ErrorElement;
import com.kaltura.netkit.utils.NKLog;
import com.kaltura.netkit.utils.OnCompletion;
import com.kaltura.netkit.utils.OnRequestCompletion;
import com.kaltura.netkit.utils.SessionProvider;

/**
 * Created by eladplotski on 08/05/2017.
 */

public class OttPushNotificationProvider {

    private static final NKLog log = NKLog.get("OttPushProvider");

    private static String apiBaseUrl = "";
    private SessionProvider sp;

    public OttPushNotificationProvider(String baseUrl,SessionProvider sp) {
        this.apiBaseUrl = baseUrl;
        this.sp = sp;
    }

    /*Push Notification Section*/

    public void setDevicePushToken(final String pushToken){

        sp.getSessionToken(new OnCompletion<PrimitiveResult>() {
            @Override
            public void onComplete(PrimitiveResult response) {

                String ks = response.getResult();

                APIOkRequestsExecutor.getSingleton().queue(OttPushNotificationService.setDevicePushToken(apiBaseUrl, pushToken,ks)
                        .completion(new OnRequestCompletion() {
                            @Override
                            public void onComplete(ResponseElement response) {

                                ErrorElement error = null;
                                if (response != null && response.isSuccess()) {
                                    log.d("push token registartion : Succsess.");
                                } else {
                                    error = response.getError() != null ? response.getError() : ErrorElement.GeneralError.message("Push Token Registration Failed");
                                    log.e("push token reßgistartion : Failed with error - " + error.getMessage());
                                }

                            }
                        }).build());
            }
        });
    }

    public void addFollowTVSeries(final int mediaid, final OnCompletion<ResponseElement> pushStateCallback){

        sp.getSessionToken(new OnCompletion<PrimitiveResult>() {
            @Override
            public void onComplete(PrimitiveResult response) {

                String ks = response.getResult();

                APIOkRequestsExecutor.getSingleton().queue(OttPushNotificationService.addFollowTVSeries(apiBaseUrl,ks,mediaid)
                        .completion(new OnRequestCompletion() {
                            @Override
                            public void onComplete(ResponseElement response) {

                                ErrorElement error = null;
                                if (response != null && response.isSuccess()) {
                                    log.d("addFollowTVSeries : Succsess.");
                                    pushStateCallback.onComplete(response);
                                } else {
                                    error = response.getError() != null ? response.getError() : ErrorElement.GeneralError.message("Push Token states Failed");
                                    pushStateCallback.onComplete(response);
                                    log.e("addFollowTVSeries : Failed with error - " + error.getMessage());
                                }

                            }
                        }).build());
            }});
    }

    public void deleteFollowTVSeries(final int mediaid,final OnCompletion<ResponseElement> pushStateCallback){

        sp.getSessionToken(new OnCompletion<PrimitiveResult>() {
            @Override
            public void onComplete(PrimitiveResult response) {

                String ks = response.getResult();

                APIOkRequestsExecutor.getSingleton().queue(OttPushNotificationService.deleteFollowTVSeries(apiBaseUrl,ks,mediaid)
                        .completion(new OnRequestCompletion() {
                            @Override
                            public void onComplete(ResponseElement response) {

                                ErrorElement error = null;
                                if (response != null && response.isSuccess()) {
                                    log.d("addFollowTVSeries : Succsess.");
                                    pushStateCallback.onComplete(response);
                                } else {
                                    error = response.getError() != null ? response.getError() : ErrorElement.GeneralError.message("Push Token states Failed");
                                    pushStateCallback.onComplete(response);
                                    log.e("addFollowTVSeries : Failed with error - " + error.getMessage());
                                }

                            }
                        }).build());
            }});
    }

    public void getPushNotificationStates(final OnCompletion<ResponseElement> pushStateCallback){

        sp.getSessionToken(new OnCompletion<PrimitiveResult>() {
            @Override
            public void onComplete(PrimitiveResult response) {

                String ks = response.getResult();

                APIOkRequestsExecutor.getSingleton().queue(OttPushNotificationService.getNotificationSettingsStatus(apiBaseUrl,ks)
                        .completion(new OnRequestCompletion() {
                            @Override
                            public void onComplete(ResponseElement response) {

                                ErrorElement error = null;
                                if (response != null && response.isSuccess()) {
                                    log.d("push states : Succsess.");
                                    pushStateCallback.onComplete(response);
                                } else {
                                    error = response.getError() != null ? response.getError() : ErrorElement.GeneralError.message("Push Token states Failed");
                                    pushStateCallback.onComplete(response);
                                    log.e("push states : Failed with error - " + error.getMessage());
                                }

                            }
                        }).build());
            }});
    }

    public void setPushNotificationStates(final boolean allowNotification, final boolean allowFollowNotification, final OnCompletion<ResponseElement> pushStateCallback){

        sp.getSessionToken(new OnCompletion<PrimitiveResult>() {
            @Override
            public void onComplete(PrimitiveResult response) {

                String ks = response.getResult();

                APIOkRequestsExecutor.getSingleton().queue(OttPushNotificationService.setNotificationSettingsStatus(apiBaseUrl,ks,allowNotification,allowFollowNotification)
                        .completion(new OnRequestCompletion() {
                            @Override
                            public void onComplete(ResponseElement response) {

                                ErrorElement error = null;
                                if (response != null && response.isSuccess()) {
                                    log.d("push state update : Succsess.");
                                    pushStateCallback.onComplete(response);
                                } else {
                                    pushStateCallback.onComplete(response);
                                    error = response.getError() != null ? response.getError() : ErrorElement.GeneralError.message("Push Token states Failed");
                                    log.e("push state update : Failed with error - " + error.getMessage());
                                }
                            }
                        }).build());
            }});
    }
}
