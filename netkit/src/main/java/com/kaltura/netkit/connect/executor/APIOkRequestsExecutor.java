package com.kaltura.netkit.connect.executor;


import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.kaltura.netkit.connect.request.ExecutedRequest;
import com.kaltura.netkit.connect.request.RequestConfiguration;
import com.kaltura.netkit.connect.request.RequestElement;
import com.kaltura.netkit.connect.request.RequestIdFactory;
import com.kaltura.netkit.connect.response.ResponseElement;
import com.kaltura.netkit.utils.ErrorElement;
import com.kaltura.netkit.utils.NKLog;
import com.kaltura.netkit.utils.NetworkErrorEventListener;

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

    private static final NKLog log = NKLog.get("APIOkRequestsExecutor");

    static final MediaType JSON_MediaType = MediaType.parse("application/json");

    private RequestConfiguration requestConfiguration = new RequestConfiguration();

    private static APIOkRequestsExecutor self;
    private static OkHttpClient.Builder mClientBuilder;
    private NetworkErrorEventListener networkErrorEventListener;

    private OkHttpClient mOkClient;
    private boolean addSig;
    private IdFactory idFactory = new RequestIdFactory(); // default

    public static APIOkRequestsExecutor getSingleton() {
        if (self == null) {
            self = new APIOkRequestsExecutor();
        }
        return self;
    }

    //private IdInterceptor idInterceptor = new IdInterceptor();
    // private GzipInterceptor gzipInterceptor = new GzipInterceptor();

    public APIOkRequestsExecutor() {
        mOkClient = configClient(createOkClientBuilder(), requestConfiguration).build();
    }

    public APIOkRequestsExecutor(RequestConfiguration requestConfiguration) {
        setRequestConfiguration(requestConfiguration);
    }


    public APIOkRequestsExecutor setRequestIdFactory(IdFactory factory) {
        this.idFactory = factory;
        return this;
    }

    public RequestConfiguration getRequestConfiguration() {
        return requestConfiguration;
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

        builder.followRedirects(true).connectTimeout(config.getConnectTimeoutMs(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeoutMs(), TimeUnit.MILLISECONDS)
                .writeTimeout(config.getWriteTimeoutMs(), TimeUnit.MILLISECONDS)
                .eventListener(new EventListener() {
                    @Override
                    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
//                        String msg = "connectStart ";
//                        if (call != null && call.request() != null) {
//                            msg += call.request().url();
//                        }
//                        log.d(msg);
                        super.connectStart(call, inetSocketAddress, proxy);
                    }

                    @Override
                    public void connectEnd(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol) {
//                        log.d("connectEnd");
                        super.connectEnd(call, inetSocketAddress, proxy, protocol);
                    }

                    @Override
                    public void callFailed(Call call, IOException ioe) {
                        String msg = "okhttp callFailed ";
                        if (ioe != null) {
                            msg += ioe.toString();
                        }
                        log.e(msg);
                        if (networkErrorEventListener != null) {
                            networkErrorEventListener.onError(ErrorElement.ServiceUnavailableError.addMessage(msg));
                        }
                        super.callFailed(call, ioe);
                    }

                    @Override
                    public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol, IOException ioe) {
                        String msg = "okhttp connectFailed ";
                        if (ioe != null) {
                            msg += ioe.toString();
                        }
                        log.e(msg);
                        if (networkErrorEventListener != null) {
                            networkErrorEventListener.onError(ErrorElement.ServiceUnavailableError.addMessage(msg));
                        }
                        super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
                    }
                })
                .retryOnConnectionFailure(config.getMaxRetries() > 0);

        return builder;
    }

    @Override
    public void setRequestConfiguration(RequestConfiguration requestConfiguration) {
        this.requestConfiguration = requestConfiguration;
        mOkClient = configClient(createOkClientBuilder(), requestConfiguration).build();
    }

    @Override
    public void setNetworkErrorEventListener(NetworkErrorEventListener networkErrorEventListener) {
        this.networkErrorEventListener = networkErrorEventListener;
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
    public String queue(final RequestElement requestElement, final int retryCounter) {
        final Request request = buildRestRequest(requestElement, BodyBuilder.Default);
        return queue(request, requestElement, retryCounter);
    }

    @Override
    public String queue(final RequestElement requestElement) {
        final Request request = buildRestRequest(requestElement, BodyBuilder.Default);
        return queue(request, requestElement, requestConfiguration.getMaxRetries());
    }

    private String queue(final Request request, final RequestElement action, final int retryCounter) {
        //log.d("Start queue");

        try {
            Call call = getOkClient(action.config()).newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) { //!! in case of request error on client side

                    if (call.isCanceled()) {
                        //log.w("onFailure: call " + call.request().tag() + " was canceled. not passing results");
                        return;
                    }
                    // handle failures: create response from exception
                    action.onComplete(new ExecutedRequest().error(e).success(false));
                    log.e("enqueued request finished with failure, results passed to callback");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (call.isCanceled()) {
                        //log.w("call " + call.request().tag() + " was canceled. not passing results");
                        return;
                    }

                    if (response.code() >= HttpURLConnection.HTTP_BAD_REQUEST && retryCounter > 0) {
                        log.d("enqueued request finished with failure, retryCounter = " + retryCounter + " response = " + response.message());
                        if (networkErrorEventListener != null) {
                            ErrorElement errorElement = ErrorElement.fromCode(response.code(), response.message());
                            if (response.request() != null && response.request().url() != null) {
                                errorElement.addMessage("url=" + response.request().url().toString());
                            }
                            networkErrorEventListener.onError(errorElement);
                        }

                        new Handler(Looper.getMainLooper()).postDelayed (() -> {
                            //log.v("queue delay = " + retryPolicy.getDelayMS(retryCounter));
                            queue(request, action, retryCounter - 1);
                        }, requestConfiguration.getRetryDelayMs(retryCounter));
                        return;
                    }

                    // pass parsed response to action completion block
                    ResponseElement responseElement = onGotResponse(response, action);
                    action.onComplete(responseElement);
                    //log.v("enqueued request finished with success = " + response.isSuccessful() + " , results passed to callback");
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
            log.d("call canceled:" + call.request().tag());

        }
        call = findCall(reqId, dispatcher.runningCalls());
        if (call != null) {
            call.cancel();
            log.d("call canceled:" + call.request().tag());
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
                return requestElement.getBody() != null ? RequestBody.create(requestElement.getBody().getBytes(), JSON_MediaType) : null;
            }
        };
    }

    private Request buildRestRequest(RequestElement request, BodyBuilder bodyBuilder) {

        String url = request.getUrl();

        log.d( "request url: " + url + "\nrequest body:\n" + request.getBody() + "\n");

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
