package com.kaltura.netkit.services.api.common;

import androidx.annotation.StringDef;
import com.kaltura.netkit.utils.SessionProvider;

import java.lang.annotation.Retention;

import static com.kaltura.netkit.services.api.common.BaseSessionProvider.UserSessionType.Anonymous;
import static com.kaltura.netkit.services.api.common.BaseSessionProvider.UserSessionType.None;
import static com.kaltura.netkit.services.api.common.BaseSessionProvider.UserSessionType.User;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by tehilarozin on 28/11/2016.
 */

public abstract class BaseSessionProvider implements SessionProvider {

    protected static final int Unset = -1;

    @Retention(SOURCE)
    @StringDef(value = {User, Anonymous, None})
    public @interface UserSessionType {
        String User = "user";
        String Anonymous = "anonymous";
        String None = "none";
    }


    protected String baseUrl;
    protected String apiBaseUrl;
    private String sessionToken;
    protected long expiryDate;
    private @UserSessionType String userSessionType = None;


    protected BaseSessionProvider(String baseUrl, String apiPrefix){
        this.baseUrl = baseUrl;
        this.apiBaseUrl =  baseUrl+ apiPrefix;
    }


    /*public void setSessionProviderListener(SessionProviderListener listener){
        this.sessionListener = listener;
    }*/

    protected void endSession(){
        clearSession();
    }

    public void clearSession() {
        sessionToken = null;
        expiryDate = 0;
    }


    @Override
    public abstract int partnerId();

    @Override
    public String baseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    protected void setSession(String sessionToken, long expiry, String userId){
        this.sessionToken = sessionToken;
        this.expiryDate = expiry;
        this.userSessionType = userId.equals("0") ? Anonymous : User;
    }

    protected String getSessionToken() {
        return sessionToken;
    }

    public boolean hasActiveSession(){
        return sessionToken != null;
    }

    protected abstract String validateSession();

    protected @UserSessionType String getUserSessionType(){
        return userSessionType;
    }
}
