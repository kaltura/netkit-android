package com.kaltura.netkit.services.api.ovp.session;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.kaltura.netkit.services.api.common.BaseSessionProvider;
import com.kaltura.netkit.services.api.ovp.KalturaOvpParser;
import com.kaltura.netkit.services.api.ovp.OvpConfigs;
import com.kaltura.netkit.services.api.ovp.model.KalturaSessionInfo;
import com.kaltura.netkit.services.api.ovp.services.OvpService;
import com.kaltura.netkit.services.api.ovp.services.OvpSessionService;
import com.kaltura.netkit.services.api.ovp.services.UserService;
import com.kaltura.netkit.connect.executor.APIOkRequestsExecutor;
import com.kaltura.netkit.connect.response.BaseResult;
import com.kaltura.netkit.utils.ErrorElement;
import com.kaltura.netkit.utils.GsonParser;
import com.kaltura.netkit.connect.request.MultiRequestBuilder;
import com.kaltura.netkit.utils.NKLog;
import com.kaltura.netkit.utils.OnCompletion;
import com.kaltura.netkit.utils.OnRequestCompletion;
import com.kaltura.netkit.connect.response.PrimitiveResult;
import com.kaltura.netkit.connect.request.RequestBuilder;
import com.kaltura.netkit.connect.response.ResponseElement;

import java.util.List;

import static java.lang.System.currentTimeMillis;

/**
 * Created by tehilarozin on 27/11/2016.
 */

public class OvpSessionProvider extends BaseSessionProvider {

    private static final NKLog log = NKLog.get("OvpSessionProvider");

    private static final int DefaultSessionExpiry = 24 * 60 * 60; //in seconds

    /**
     * defines the time before expiration, to restart session on.
     */
    public static final long ExpirationDelta = 10 * 60;//hour in seconds

    private OvpSessionParams sessionParams;


    public OvpSessionProvider(String baseUrl) {
        super(baseUrl, OvpConfigs.ApiPrefix);
    }


    /**
     * starts anonymous session
     */
    public void startAnonymousSession(int partnerId, final OnCompletion<PrimitiveResult> completion) {
        sessionParams = new OvpSessionParams().setPartnerId(partnerId);
        final long expiration = System.currentTimeMillis() / 1000 + DefaultSessionExpiry;
        RequestBuilder requestBuilder = OvpSessionService.anonymousSession(apiBaseUrl, sessionParams.partnerId())
                .completion(new OnRequestCompletion() {
                    @Override
                    public void onComplete(ResponseElement response) {
                        handleAnonymousResponse(response, expiration, completion);
                    }
                });
        APIOkRequestsExecutor.getSingleton().queue(requestBuilder.build());
    }

    private void handleAnonymousResponse(ResponseElement response, long expiration, OnCompletion<PrimitiveResult> completion) {
        ErrorElement error = null;

        if (response != null && response.isSuccess()) {

            try {
                JsonElement responseElement = GsonParser.toJson(response.getResponse());
                String ks = responseElement.getAsJsonObject().getAsJsonPrimitive("ks").getAsString();
                setSession(ks, expiration, "0");// sets a "dummy" session expiration, since we can't get the actual expiration from the server
                if (completion != null) {
                    completion.onComplete(new PrimitiveResult(ks));
                }

            } catch (JsonSyntaxException e) {
                error = ErrorElement.SessionError.message("got response but failed to parse it");
            }

        } else { // failed to start session
            error = response.getError() != null ? response.getError() : ErrorElement.SessionError;
        }

        if (error != null) {
            clearSession(); //clears current saved data - app can try renewSession with the current credentials. or endSession/startSession
            if (completion != null) {
                completion.onComplete(new PrimitiveResult(error)); // in case we can't login - app should provide a solution.
            }
        }
    }

    /**
     * starts new user session
     *
     * @param username - user's email that identifies the user for login (username)
     * @param password
     */
    public void startSession(@NonNull String username, @NonNull String password, int partnerId, final OnCompletion<PrimitiveResult> completion) {
        // login user
        //get session data for expiration time
        this.sessionParams = new OvpSessionParams().setPassword(password).setUsername(username).setPartnerId(partnerId);

        MultiRequestBuilder multiRequest = OvpService.getMultirequest(apiBaseUrl, null);
        multiRequest.add(UserService.loginByLoginId(apiBaseUrl, sessionParams.username, sessionParams.password, sessionParams.partnerId()),
                OvpSessionService.get(apiBaseUrl, "{1:result}")).
                completion(new OnRequestCompletion() {
                    @Override
                    public void onComplete(ResponseElement response) {
                        handleStartSession(response, completion);
                    }
                });

        APIOkRequestsExecutor.getSingleton().queue(multiRequest.build());
    }


    /*
    * !! in case the ks expired we need to relogin
    *
    * in case the login fails - or the second request fails message will be passed to the using app
    * session is not valid.
    *
    * */


    private void handleStartSession(ResponseElement response, OnCompletion<PrimitiveResult> completion) {

        ErrorElement error = null;

        if (response != null && response.isSuccess()) {
            List<BaseResult> responses = KalturaOvpParser.parse(response.getResponse()); // parses KalturaLoginResponse, KalturaSession

            if (responses.get(0).error != null) { //!- failed to login
                //?? clear session?
                error = ErrorElement.SessionError;

            } else {
                // first response is the "ks" itself, second response contains the session data (no ks)
                if (responses.get(1).error == null) { // get session data success
                    KalturaSessionInfo session = (KalturaSessionInfo) responses.get(1);
                    String ks = ((PrimitiveResult) responses.get(0)).getResult();
                    setSession(ks, session.getExpiry(), session.getUserId()); // save new session

                    if (completion != null) {
                        completion.onComplete(new PrimitiveResult(ks));
                    }
                } else {
                    error = ErrorElement.SessionError;
                }

            }
        } else { // failed to start session - ?? what to do in case this was a renew session action.
            error = response.getError() != null ? response.getError() : ErrorElement.SessionError;
        }

        if (error != null) {
            clearSession(); //clears current saved data - app can try renewSession with the current credentials. or endSession/startSession
            if (completion != null) {
                completion.onComplete(new PrimitiveResult(error)); // in case we can't login - app should provide a solution.
            }
        }
    }

    /**
     * try to re-login with current credentials
     */
    private void renewSession(OnCompletion<PrimitiveResult> completion) {
        if (sessionParams != null) {
            if (sessionParams.username != null) {
                startSession(sessionParams.username, sessionParams.password, sessionParams.partnerId, completion);
            } else {
                startAnonymousSession(sessionParams.partnerId, completion);
            }
        } else {
            log.e("Session was ended or failed to start when this was called.\nCan't recover session if not started before");
            if (completion != null) {
                completion.onComplete(new PrimitiveResult().error(ErrorElement.SessionError.message("Session expired")));
            }
        }
    }

    /**
     * Ends current active session. if it's a {@link BaseSessionProvider.UserSessionType#User} session
     * logout, if {@link BaseSessionProvider.UserSessionType#Anonymous} will return, since
     * logout on anonymous session doesn't make the session invalid.
     * <p>
     * If logout was activated, session params are cleared.
     */
    public void endSession(final OnCompletion<BaseResult> completion) {

        if (hasActiveSession()) {

            if (getUserSessionType().equals(UserSessionType.Anonymous)) {
                if (completion != null) {
                    completion.onComplete(new BaseResult(null));
                }
                return;
            }

            APIOkRequestsExecutor.getSingleton().queue(
                    OvpSessionService.end(apiBaseUrl, getSessionToken())
                            .addParams(OvpService.getOvpConfigParams())
                            .completion(new OnRequestCompletion() {
                                @Override
                                public void onComplete(ResponseElement response) {
                                    ErrorElement error = null;
                                    if (response != null && response.isSuccess()) {//!! end session with success returns null
                                        log.i("endSession: logout user session success. clearing session data.");
                                    } else {
                                        log.e("endSession: session logout failed. clearing session data. " + (response.getError() != null ? response.getError().getMessage() : ""));
                                        error = response.getError() != null ? response.getError() : ErrorElement.GeneralError.message("failed to end session");
                                    }
                                    endSession();
                                    sessionParams = null;

                                    if (completion != null) {
                                        completion.onComplete(new BaseResult(error));
                                    }

                                }
                            }).build());

        } else {
            sessionParams = null;
        }
    }

    @Override
    public int partnerId() {
        return sessionParams != null ? sessionParams.partnerId : 0;
    }

    @Override
    public void getSessionToken(final OnCompletion<PrimitiveResult> completion) {
        String ks = validateSession();
        if (ks != null) {
            if (completion != null) {
                completion.onComplete(new PrimitiveResult(ks));
            }
        } else {
            renewSession(completion);
        }
    }

    protected String validateSession() {
        long currentDate = currentTimeMillis() / 1000;
        long timeLeft = expiryDate - currentDate;
        String sessionToken = null;
        if (timeLeft > 0) {
            sessionToken = getSessionToken();

            if (timeLeft < ExpirationDelta) {
                renewSession(null); // token about to expired - we need to restart session
            }
        }

        return sessionToken;
    }


    class OvpSessionParams {
        private String username;
        private String password;
        public int partnerId;

        public String username() {
            return username;
        }

        public OvpSessionParams setUsername(String username) {
            this.username = username;
            return this;
        }

        public String password() {
            return password;
        }

        public OvpSessionParams setPassword(String password) {
            this.password = password;
            return this;
        }

        public int partnerId() {
            return partnerId;
        }

        public OvpSessionParams setPartnerId(int partnerId) {
            this.partnerId = partnerId;
            return this;
        }
    }

}
