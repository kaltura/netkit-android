package com.kaltura.netkit.connect.executor;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kaltura.netkit.connect.request.ExecutedRequest;
import com.kaltura.netkit.connect.request.RequestConfiguration;
import com.kaltura.netkit.connect.request.RequestElement;
import com.kaltura.netkit.connect.request.RequestIdFactory;
import com.kaltura.netkit.connect.response.ResponseElement;
import com.kaltura.netkit.utils.ErrorElement;
import com.kaltura.netkit.utils.NetworkEventListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.EventListener;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * @hide
 */
public class APIOkRequestsExecutor implements RequestQueue {

    public interface IdFactory {
        String factorId(String factor);
    }

    public static final String TAG = "APIOkRequestsExecutor";
    public static RetryPolicy retryPolicy = new RetryPolicy();

    static final MediaType JSON_MediaType = MediaType.parse("application/json");

    private RequestConfiguration defaultConfiguration = new RequestConfiguration() {
        @Override
        public long getReadTimeout() {
            return retryPolicy.getReadTimeoutMs();
        }

        @Override
        public long getWriteTimeout() {
            return retryPolicy.getWriteTimeoutMs();
        }

        @Override
        public long getConnectTimeout() {
            return retryPolicy.getConnectTimeoutMs();
        }

        @Override
        public int getRetry() {
            return retryPolicy.getNumRetries();
        }
    };

    private static APIOkRequestsExecutor self;
    private static OkHttpClient.Builder mClientBuilder;
    private NetworkEventListener networkEventListener;

    private OkHttpClient mOkClient;
    private boolean addSig;
    private IdFactory idFactory = new RequestIdFactory(); // default
    private boolean enableLogs = true;

    public static APIOkRequestsExecutor getSingleton() {
        if (self == null) {
            self = new APIOkRequestsExecutor();
        }
        return self;
    }

    //private IdInterceptor idInterceptor = new IdInterceptor();
    // private GzipInterceptor gzipInterceptor = new GzipInterceptor();

    public APIOkRequestsExecutor() {
        mOkClient = configClient(createOkClientBuilder(), defaultConfiguration).build();
    }

    public APIOkRequestsExecutor(RequestConfiguration defaultConfiguration) {
        setDefaultConfiguration(defaultConfiguration);
    }


    public APIOkRequestsExecutor setRequestIdFactory(IdFactory factory) {
        this.idFactory = factory;
        return this;
    }

    /**
     * in case of specific request configurations, pass newly built client based on mOkClient instance.
     *
     * @param configuration
     * @return
     */
    private OkHttpClient getOkClient(RequestConfiguration configuration) {

        if (configuration != null) {
            // returns specific client for configuration
            return configClient(mOkClient.newBuilder(), configuration).build();
        }
        //default configurable client instance
        return mOkClient;
    }

    @NonNull
    private OkHttpClient.Builder createOkClientBuilder() {
        if (mClientBuilder != null) {
            return mClientBuilder;
        }
        return new OkHttpClient.Builder().connectionPool(new ConnectionPool()); // default connection pool - holds 5 connections up to 5 minutes idle time
    }

    private OkHttpClient.Builder configClient(OkHttpClient.Builder builder, RequestConfiguration config) {

        builder.followRedirects(true).connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(config.getWriteTimeout(), TimeUnit.MILLISECONDS)
                .eventListener(new EventListener() {
                    @Override
                    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
//                        String msg = "connectStart ";
//                        if (call != null && call.request() != null) {
//                            msg += call.request().url();
//                        }
//                        Log.d(TAG, msg);
                        super.connectStart(call, inetSocketAddress, proxy);
                    }

                    @Override
                    public void connectEnd(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol) {
//                        Log.d(TAG, "connectEnd");
//                        super.connectEnd(call, inetSocketAddress, proxy, protocol);
                    }

                    @Override
                    public void callFailed(Call call, IOException ioe) {
                        String msg = "okhttp callFailed ";
                        if (ioe != null) {
                            msg += ioe.toString();
                        }
                        Log.e(TAG, msg);
                        if (networkEventListener != null) {
                            networkEventListener.onError(ErrorElement.ServiceUnavailableError.addMessage(msg));
                        }
                        super.callFailed(call, ioe);
                    }

                    @Override
                    public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol, IOException ioe) {
                        String msg = "okhttp connectFailed ";
                        if (ioe != null) {
                            msg += ioe.toString();
                        }
                        Log.e(TAG, msg);
                        if (networkEventListener != null) {
                            networkEventListener.onError(ErrorElement.ServiceUnavailableError.addMessage(msg));
                        }
                        super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
                    }
                })
                .retryOnConnectionFailure(config.getRetry() > 0);

        return builder;
    }

    @Override
    public void setDefaultConfiguration(RequestConfiguration defaultConfiguration) {
        this.defaultConfiguration = defaultConfiguration;
        mOkClient = configClient(createOkClientBuilder(), defaultConfiguration).build();
    }

    @Override
    public void enableLogs(boolean enable) {
        this.enableLogs = enable;
    }

    @Override
    public void setNetworkEventListener(NetworkEventListener networkEventListener) {
        this.networkEventListener = networkEventListener;
    }

    private RequestBody buildMultipartBody(HashMap<String, String> params) {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        for (String key : params.keySet()) {
            bodyBuilder.addFormDataPart(key, params.get(key));
        }
        return bodyBuilder.build();
    }

    private RequestBody buildFormBody(HashMap<String, String> params) {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        for (String key : params.keySet()) {
            bodyBuilder.add(key, params.get(key));
        }
        return bodyBuilder.build();
    }

    @Override
    public String queue(final RequestElement requestElement, final int retryCount) {
        final Request request = buildRestRequest(requestElement, BodyBuilder.Default);
        return queue(request, requestElement, retryCount);
    }

    @Override
    public String queue(final RequestElement requestElement) {
        final Request request = buildRestRequest(requestElement, BodyBuilder.Default);
        return queue(request, requestElement, defaultConfiguration.getRetry());
    }

    private String queue(final Request request, final RequestElement action, final int retryCount) {
        //Log.d(TAG, "Start queue");

        try {
            Call call = getOkClient(action.config()).newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) { //!! in case of request error on client side

                    if (call.isCanceled()) {
                        //Log.w(TAG, "onFailure: call " + call.request().tag() + " was canceled. not passing results");
                        return;
                    }
                    // handle failures: create response from exception
                    action.onComplete(new ExecutedRequest().error(e).success(false));
                    Log.e(TAG, "enqueued request finished with failure, results passed to callback");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (call.isCanceled()) {
                        //Log.w(TAG, "call " + call.request().tag() + " was canceled. not passing results");
                        return;
                    }

                    if (response.code() >= HttpURLConnection.HTTP_BAD_REQUEST && retryCount > 0) {
                        Log.d(TAG, "enqueued request finished with failure, retryCount = " + retryCount + " response = " + response.message());
                        if (networkEventListener != null) {
                            ErrorElement errorElement = ErrorElement.fromCode(response.code(), response.message());
                            if (response.request() != null && response.request().url() != null) {
                                errorElement.addMessage("url=" + response.request().url().toString());
                            }
                            networkEventListener.onError(errorElement);
                        }

                        new Handler(Looper.getMainLooper()).postDelayed (() -> {
                            //Log.v(TAG, "queue delay = " + retryPolicy.getDelayMS(retryCount));
                            queue(request, action, retryCount - 1);
                        }, retryPolicy.getDelayMS(retryCount));
                        return;
                    }

                    // pass parsed response to action completion block
                    ResponseElement responseElement = onGotResponse(response, action);
                    action.onComplete(responseElement);
                    //Log.v(TAG, "enqueued request finished with success = " + response.isSuccessful() + " , results passed to callback");
                }
            });
            return (String) call.request().tag();

        } catch (Exception e) {
            e.printStackTrace();
            ExecutedRequest responseElement = new ExecutedRequest().response(getErrorResponse(e)).success(false);
            action.onComplete(responseElement);

        }
        return null; // no call id to return.
    }

    private String getErrorResponse(Exception e) {
        return e.getClass().getName() + ": " + e.getMessage();
    }

    @Override
    public ResponseElement execute(RequestElement request) {
        try {
            Response response = getOkClient(request.config()).newCall(buildRestRequest(request, BodyBuilder.Default)).execute();
            return onGotResponse(response, request);

        } catch (IOException e) {
            // failure on request execution - create error response
            return new ExecutedRequest().response(getErrorResponse(e)).success(false);
        }
    }

    //TODO: cancel check on executor + null check on provider

    //@Override
    public boolean hasRequest(String reqId) {
        Dispatcher dispatcher = getOkClient(null).dispatcher();

        Call call = findCall(reqId, dispatcher.queuedCalls());
        if (call != null) {
            return true;
        }
        call = findCall(reqId, dispatcher.runningCalls());
        return call != null;
    }

    @Override
    public void cancelRequest(String reqId) {
        Dispatcher dispatcher = getOkClient(null).dispatcher();

        Call call = findCall(reqId, dispatcher.queuedCalls());
        if (call != null) {
            call.cancel();
            Log.d(TAG, "call canceled:" + call.request().tag());
        }
        call = findCall(reqId, dispatcher.runningCalls());
        if (call != null) {
            call.cancel();
            Log.d(TAG, "call canceled:" + call.request().tag());
        }
    }

    private Call findCall(String reqId, List<Call> calls) {
        for (Call call : calls) {
            if (call.request().tag().equals(reqId)) {
                return call;
            }
        }
        return null;
    }

    @Override
    public void clearRequests() {
        if (mOkClient != null) {
            mOkClient.dispatcher().cancelAll();
        }
    }

    @Override
    public boolean isEmpty() {
        return mOkClient == null || mOkClient.dispatcher().queuedCallsCount() == 0;
    }

    public static void setClientBuilder(OkHttpClient.Builder clientBuilder) {
        mClientBuilder = clientBuilder;
    }

    private ResponseElement onGotResponse(Response response, RequestElement action) {
        String requestId = getRequestId(response);

        if (!response.isSuccessful()) { // in case response has failure status
            return new ExecutedRequest().requestId(requestId).error(ErrorElement.fromCode(response.code(), response.message())).success(false);

        } else {

            String responseString = null;
            try {
                responseString = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                //logger.error("failed to retrieve the response body!");
            }

            //logger.debug("response body:\n" + responseString);

            return new ExecutedRequest().requestId(requestId).response(responseString).code(response.code()).success(responseString != null);
        }
    }

    private String getRequestId(Response response) {
        try {
            return response.request().tag().toString();
        } catch (NullPointerException e) {
            return "";
        }
    }

    private String getContentType(String header) {
        return header.contains("application/xml") ? "xml" : "json";
    }

    private interface BodyBuilder {
        RequestBody build(RequestElement requestElement);

        BodyBuilder Default = new BodyBuilder() {
            @Override
            public RequestBody build(RequestElement requestElement) {
                return requestElement.getBody() != null ? RequestBody.create(JSON_MediaType, requestElement.getBody().getBytes()) : null;
            }
        };
    }

    private Request buildRestRequest(RequestElement request, BodyBuilder bodyBuilder) {

        String url = request.getUrl();

        if (enableLogs) {
            Log.d(TAG, "request url: " + url + "\nrequest body:\n" + request.getBody() + "\n");
        }

        RequestBody body = bodyBuilder.build(request);// RequestBody.create(JSON_MediaType, action.getBody().getBytes());

        return new Request.Builder()
                .headers(Headers.of(request.getHeaders()))
                .method(request.getMethod(), body)
                .url(url)
                .tag(idFactory.factorId(request.getTag()))
                .build();
    }

    public static String getRequestBody(Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
