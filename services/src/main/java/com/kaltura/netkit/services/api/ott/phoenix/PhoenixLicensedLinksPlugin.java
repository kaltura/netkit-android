package com.kaltura.netkit.services.api.ott.phoenix;

import com.google.gson.GsonBuilder;
import com.kaltura.netkit.services.api.ott.phoenix.model.AssetResult;
import com.kaltura.netkit.services.api.ott.phoenix.model.KalturaLicensedUrl;
import com.kaltura.netkit.services.api.ott.phoenix.model.OttResultAdapter;
import com.kaltura.netkit.services.api.ott.phoenix.services.LicensedUrlService;
import com.kaltura.netkit.utils.SessionProvider;
import com.kaltura.netkit.connect.executor.APIOkRequestsExecutor;
import com.kaltura.netkit.utils.Accessories;
import com.kaltura.netkit.utils.ErrorElement;
import com.kaltura.netkit.utils.OnCompletion;
import com.kaltura.netkit.utils.OnRequestCompletion;
import com.kaltura.netkit.connect.response.PrimitiveResult;
import com.kaltura.netkit.connect.request.RequestBuilder;
import com.kaltura.netkit.connect.executor.RequestQueue;
import com.kaltura.netkit.connect.response.ResponseElement;
import com.kaltura.netkit.connect.response.ResultElement;


/**
 * Created by tehilarozin on 22/11/2016.
 */

public class PhoenixLicensedLinksPlugin {

    private SessionProvider sessionProvider;
    private RequestQueue requestsExecutor = APIOkRequestsExecutor.getSingleton();

    private void fetchShiftedLiveLicenseLinks(final String assetId, String programId, final String streamType, final long startDate, final OnCompletion<ResultElement<String>> completion) {
        //catchup/startOver/trickPlay
        sessionProvider.getSessionToken(new OnCompletion<PrimitiveResult>() {
            @Override
            public void onComplete(PrimitiveResult response) {
                if (response != null && response.error == null) {
                    RequestBuilder requestBuilder = LicensedUrlService.getForShiftedLive(sessionProvider.baseUrl(), response.getResult(), assetId, streamType, startDate);
                    execute(requestBuilder, completion);
                }
            }
        });
    }

    private void fetchMediaLicenseLinks(final String assetId, final String fileId, final String fileUrl, final OnCompletion<ResultElement<String>> completion) {
        // vod/channel

        sessionProvider.getSessionToken(new OnCompletion<PrimitiveResult>() {
            @Override
            public void onComplete(PrimitiveResult response) {
                if (response != null && response.error == null){
                    PhoenixRequestBuilder requestBuilder = LicensedUrlService.getForMedia(sessionProvider.baseUrl(), response.getResult(), assetId, fileId, fileUrl);
                    execute(requestBuilder, completion);
                }
            }
        });
    }

    private void fetchRecordingLicenseLinks(final String assetId, final String fileFormat, final OnCompletion<ResultElement<String>> completion) {
        // recording
        sessionProvider.getSessionToken(new OnCompletion<PrimitiveResult>() {
            @Override
            public void onComplete(PrimitiveResult response) {
                if (response != null && response.error == null) {
                    PhoenixRequestBuilder requestBuilder = LicensedUrlService.getForRecording(sessionProvider.baseUrl(), response.getResult(), assetId, fileFormat);
                    execute(requestBuilder, completion);
                }
            }
        });
    }

    private void execute(RequestBuilder requestBuilder, final OnCompletion<ResultElement<String>> completion) {
        requestBuilder.completion(new OnRequestCompletion() {
            @Override
            public void onComplete(ResponseElement response) {
                String licensedLink = null;
                ErrorElement error = null;
                //TODO check success and call loadCompletion with results
                if(response.isSuccess()){
                    KalturaLicensedUrl licensedUrl = parseLicensedUrl(response.getResponse());
                    if(licensedUrl.error == null) {
                        //-> we have valid licensed link
                        licensedLink = licensedUrl.getLicensedUrl();// in case mainUrl is null will return the alternative url
                    }

                    if(licensedLink == null) { // error response from the server
                        error = ErrorElement.LoadError.message("failed fetching licensed url, media can't be played");
                    }

                } else { // request execution failure
                    //-> report error with ErrorElement
                    error = response.getError() != null ? response.getError() : ErrorElement.LoadError.message("failed fetching licensed url, media can't be played");
                }

                if(completion != null){
                    completion.onComplete(Accessories.buildResult(licensedLink, error));
                }
            }
        });

        requestsExecutor.queue(requestBuilder.build());
    }

    private KalturaLicensedUrl parseLicensedUrl(String response){
        return new GsonBuilder().registerTypeAdapter(AssetResult.class, new OttResultAdapter()).create().fromJson(response, KalturaLicensedUrl.class);
    }



}
