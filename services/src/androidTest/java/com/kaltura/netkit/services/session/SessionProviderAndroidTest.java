package com.kaltura.netkit.services.session;

import androidx.test.runner.AndroidJUnit4;

import com.kaltura.netkit.connect.executor.APIOkRequestsExecutor;
import com.kaltura.netkit.connect.response.BaseResult;
import com.kaltura.netkit.connect.response.PrimitiveResult;
import com.kaltura.netkit.connect.response.ResponseElement;
import com.kaltura.netkit.services.BaseTest;
import com.kaltura.netkit.services.api.ott.phoenix.PhoenixParser;
import com.kaltura.netkit.services.api.ovp.OvpConfigs;
import com.kaltura.netkit.services.api.ovp.services.BaseEntryService;
import com.kaltura.netkit.services.api.ott.phoenix.session.OttSessionProvider;
import com.kaltura.netkit.services.api.ovp.session.OvpSessionProvider;
import com.kaltura.netkit.utils.ErrorElement;
import com.kaltura.netkit.utils.NKLog;
import com.kaltura.netkit.utils.OnCompletion;
import com.kaltura.netkit.utils.OnRequestCompletion;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static com.kaltura.netkit.services.MockParams.NonDRMEntryId;
import static com.kaltura.netkit.services.MockParams.OvpBaseUrl;
import static com.kaltura.netkit.services.MockParams.OvpLoginId;
import static com.kaltura.netkit.services.MockParams.OvpPartnerId;
import static com.kaltura.netkit.services.MockParams.OvpPassword;
import static com.kaltura.netkit.services.MockParams.PnxBaseUrl;
import static com.kaltura.netkit.services.MockParams.PnxPartnerId;
import static com.kaltura.netkit.services.MockParams.PnxPassword;
import static com.kaltura.netkit.services.MockParams.PnxUsername;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by tehilarozin on 28/11/2016.
 */

@RunWith(AndroidJUnit4.class)
public class SessionProviderAndroidTest extends BaseTest {

    private static final NKLog log = NKLog.get("SessionProviderAndroidTest");

    int singleTestWaitCount;

    @Test
    public void testOttSessionProviderBaseFlow() {
        final OttSessionProvider ottSessionProvider = new OttSessionProvider(PnxBaseUrl, PnxPartnerId);

        singleTestWaitCount = 1;
        ottSessionProvider.startSession(PnxUsername, PnxPassword, null, new OnCompletion<PrimitiveResult>() {
            @Override
            public void onComplete(PrimitiveResult response) {
                if (response.error != null) {
                    log.e("testOttSessionProvider", "failed to establish a session: " + response.error.getMessage());
                    singleTestWaitCount--;
                    resume();
                } else {
                    //singleTestWaitCount++;
                    ottSessionProvider.getSessionToken(new OnCompletion<PrimitiveResult>() {
                        @Override
                        public void onComplete(PrimitiveResult response) {
                            assertNotNull(response.getResult());
                            assertFalse(response.getResult().equals(""));
                            singleTestWaitCount--;
                            resume();
                            //todo: change to another BE call
                           /* new PhoenixMediaProvider()
                                    .setSessionProvider(ottSessionProvider)
                                    .setAssetId(MediaId)
                                    .setAssetType(APIDefines.KalturaAssetType.Media)
                                    .setFormats(FormatHD)
                                    .load(new OnMediaLoadCompletion() {
                                        @Override
                                        public void onComplete(ResultElement<PKMediaEntry> response) {
                                            if (response != null && response.isSuccess()) {
                                                log.i("testOttSessionProvider", "we have mediaEntry");
                                                assertTrue(response.getResponse().getId().equals(MediaId));
                                                assertTrue(response.getResponse().getSources().size() == 1);
                                                assertTrue(response.getResponse().getMediaType().equals(PKMediaEntry.MediaEntryType.Unknown));
                                            }
                                            resume();
                                        }
                                    });*/
                        }
                    });
                }
            }
        });

        wait(singleTestWaitCount);
    }

    @Test
    public void testOttSessionProviderRecovery() {
        final OttSessionProvider ottSessionProvider = new OttSessionProvider(PnxBaseUrl, PnxPartnerId);
        String sessionData;
        ottSessionProvider.startSession(PnxUsername, PnxPassword, null, new OnCompletion<PrimitiveResult>() {
            @Override
            public void onComplete(PrimitiveResult response) {
                if (response.error != null) {
                    log.e("testOttSessionProvider", "failed to establish a session: " + response.error.getMessage());
                    resume();
                } else {
                    ottSessionProvider.getSessionToken(new OnCompletion<PrimitiveResult>() {
                        @Override
                        public void onComplete(PrimitiveResult response) {
                            assertNotNull(response.getResult());
                            assertFalse(response.getResult().equals(""));
                        }
                    });
                    String encryptedSession = ottSessionProvider.encryptSession();
                    OttSessionProvider newSession = new OttSessionProvider(PnxBaseUrl, PnxPartnerId);
                    newSession.recoverSession(encryptedSession, new OnCompletion<PrimitiveResult>() {
                        @Override
                        public void onComplete(PrimitiveResult response) {

                            assertNotNull(response.getResult());
                            assertNull(response.error);
                            resume();

                        }
                    });

                }
            }
        });
        wait(1);
    }


    @Test
    public void testOttAnonymousSession() {
        final OttSessionProvider ottSessionProvider = new OttSessionProvider(PnxBaseUrl, PnxPartnerId);
        singleTestWaitCount = 1;
        ottSessionProvider.startAnonymousSession(null, new OnCompletion<PrimitiveResult>() {
            @Override
            public void onComplete(PrimitiveResult response) {
                if (response.error != null) {
                    log.e("testAnonymousSession", "failed to establish anonymous session: " + response.error.getMessage());
                    fail("Anonymous session creation failed: "+response.error.getMessage());
                    singleTestWaitCount--;
                } else {
                    ottSessionProvider.getSessionToken(new OnCompletion<PrimitiveResult>() {
                        @Override
                        public void onComplete(PrimitiveResult response) {
                            assertNotNull(response.getResult());
                            assertFalse(response.getResult().equals(""));

                            log.e("testAnonymousSession", "get ks = " + response.getResult());
                            ottSessionProvider.endSession(new OnCompletion<BaseResult>() {
                                @Override
                                public void onComplete(BaseResult response) {
                                    assertTrue(response.error == null);
                                    resume();
                                    singleTestWaitCount--;
                                    //todo: change to another BE call
                                    /*new PhoenixMediaProvider()
                                            .setSessionProvider(ottSessionProvider)
                                            .setAssetId(MediaId)
                                            .setAssetType(APIDefines.KalturaAssetType.Media)
                                            .setFormats(FormatSD)
                                            .load(new OnMediaLoadCompletion() {
                                                @Override
                                                public void onComplete(ResultElement<PKMediaEntry> response) {
                                                    assertTrue(response != null && response.isSuccess());
                                                    log.i("testOttSessionProvider", "we have mediaEntry");
                                                    assertTrue(response.getResponse().getId().equals(MediaId));
                                                    assertTrue(response.getResponse().getSources().size() == 1);
                                                    assertTrue(response.getResponse().getMediaType().equals(PKMediaEntry.MediaEntryType.Unknown));

                                                    resume();
                                                }
                                            });*/
                                }
                            });
                        }
                    });
                }
            }
        });

        wait(singleTestWaitCount);
    }

    @Test
    public void testOttEndSession() {
        final OttSessionProvider ottSessionProvider = new OttSessionProvider(PnxBaseUrl, PnxPartnerId);

        singleTestWaitCount = 1;
        ottSessionProvider.startSession(PnxUsername, PnxPassword, null, new OnCompletion<PrimitiveResult>() {
            @Override
            public void onComplete(PrimitiveResult response) {
                if (response.error != null) {
                    log.e("testAnonymousSession", "failed to establish anonymous session: " + response.error.getMessage());

                    if (response.error == ErrorElement.SessionError) {
                        fail("should have logged ");
                    }
                    singleTestWaitCount--;
                } else {
                    ottSessionProvider.getSessionToken(new OnCompletion<PrimitiveResult>() {
                        @Override
                        public void onComplete(PrimitiveResult response) {
                            assertNotNull(response.getResult());
                            assertFalse(response.getResult().equals(""));

                            log.e("testAnonymousSession", "get ks = " + response.getResult());
                            ottSessionProvider.endSession(new OnCompletion<BaseResult>() {
                                @Override
                                public void onComplete(BaseResult response) {
                                    assertTrue(response.error == null);
                                    resume();
                                    singleTestWaitCount--;
                                    //todo: change to another BE call
                                    /*new PhoenixMediaProvider()
                                            .setSessionProvider(ottSessionProvider)
                                            .setAssetId(MediaId)
                                            .setAssetType(APIDefines.KalturaAssetType.Media)
                                            .setFormats(FormatHD)
                                            .load(new OnMediaLoadCompletion() {
                                                @Override
                                                public void onComplete(ResultElement<PKMediaEntry> response) {
                                                    assertTrue(response != null && response.getError() != null);
                                                    assertTrue(response.getError().equals(ErrorElement.SessionError));
                                                    resume();
                                                }
                                            });*/
                                }
                            });
                        }
                    });
                }
            }
        });

        wait(singleTestWaitCount);
    }

    @Test
    public void testOttSwitchUser(){

    }




    @Test
    public void testOvpSessionProviderBaseFlow() {
        ovpSessionProvider = new OvpSessionProvider(OvpBaseUrl);
        singleTestWaitCount = 1;
        ovpSessionProvider.startSession(OvpLoginId, OvpPassword, OvpPartnerId, new OnCompletion<PrimitiveResult>() {
            @Override
            public void onComplete(PrimitiveResult response) {
                if (response.error != null) {
                    fail("failed to establish a session: " + response.error.getMessage());
                    singleTestWaitCount--;
                } else {
                    ovpSessionProvider.getSessionToken(new OnCompletion<PrimitiveResult>() {
                        @Override
                        public void onComplete(PrimitiveResult response) {
                            assertNotNull(response.getResult());
                            assertFalse(response.getResult().equals(""));
                            resume();
                            singleTestWaitCount--;
                            //todo: change to another BE call
                            /*new KalturaOvpMediaProvider()
                                    .setSessionProvider(ovpSessionProvider)
                                    .setEntryId(NonDRMEntryId)
                                    .load(new OnMediaLoadCompletion() {
                                        @Override
                                        public void onComplete(ResultElement<PKMediaEntry> response) {
                                            if (response != null && response.isSuccess()) {
                                                log.i("testOvpSessionProvider", "we have mediaEntry");
                                                assertTrue(response.getResponse().getId().equals(NonDRMEntryId));
                                                // Assert.assertTrue(response.getResponse().getSources().size() == 1);
                                            }
                                            resume();
                                        }
                                    });*/
                        }
                    });
                }
            }
        });

        wait(singleTestWaitCount);
    }


    @Test
    public void testOvpAnonymousSession() {
        final OvpSessionProvider ovpSessionProvider = new OvpSessionProvider(OvpBaseUrl);

        ovpSessionProvider.startAnonymousSession(OvpPartnerId, new OnCompletion<PrimitiveResult>() {
            @Override
            public void onComplete(PrimitiveResult response) {
                if (response.error != null) {
                    log.e("testAnonymousSession", "failed to establish anonymous session: " + response.error.getMessage());
                    fail("failed to create anonymous session: "+response.error.getMessage());

                } else {
                    ovpSessionProvider.getSessionToken(new OnCompletion<PrimitiveResult>() {
                        @Override
                        public void onComplete(PrimitiveResult response) {
                            assertNotNull(response.getResult());
                            assertFalse(response.getResult().equals(""));

                            log.e("testAnonymousSession", "get ks = " + response.getResult());
                            //todo: change to another BE call
                            /*new KalturaOvpMediaProvider()
                                    .setSessionProvider(ovpSessionProvider)
                                    .setEntryId(NonDRMEntryId)
                                    .load(new OnMediaLoadCompletion() {
                                        @Override
                                        public void onComplete(ResultElement<PKMediaEntry> response) {
                                            if (response != null && response.isSuccess()) {
                                                log.i("testOvpSessionProvider", "we have mediaEntry");
                                                assertTrue(response.getResponse().getId().equals(NonDRMEntryId));
                                            }
                                            resume();
                                        }
                                    });*/
                        }
                    });
                }
            }
        });

        wait(1);
    }


    @Test
    public void testOvpSPError() {
        final OvpSessionProvider ovpSessionProvider = new OvpSessionProvider(OvpBaseUrl + "invalid/");
        ovpSessionProvider.startSession(OvpLoginId, OvpPassword, OvpPartnerId, new OnCompletion<PrimitiveResult>() {
            @Override
            public void onComplete(PrimitiveResult response) {
                if (response.error != null) {
                    assertTrue(response.error.equals(ErrorElement.SessionError));
                    log.i("testOvpSPError", response.error.getCode() + ", " + response.error.getMessage());

                    ovpSessionProvider.setBaseUrl("http://www.kaltura.whatever/api_v3/");
                    ovpSessionProvider.startSession(OvpLoginId, OvpPassword, OvpPartnerId, new OnCompletion<PrimitiveResult>() {
                        @Override
                        public void onComplete(PrimitiveResult response) {
                            assertNotNull(response.error);
                            assertTrue(response.error.equals(ErrorElement.ConnectionError));
                            resume();
                        }
                    });
                }

            }
        });

        wait(1);
    }


    OvpSessionProvider ovpSessionProvider;

    @Test
    public void testOvpSP() {

        testOvpSessionProviderBaseFlow();

        testOvpUserEndSession();

    }


    String testKs;

    @Test
    public void testOvpUserEndSession() {
        final AtomicReference<AssertionError> failure = new AtomicReference<>();

        ovpSessionProvider = new OvpSessionProvider(OvpBaseUrl);
        ovpSessionProvider.startSession(OvpLoginId, OvpPassword, OvpPartnerId, new OnCompletion<PrimitiveResult>() {
            @Override
            public void onComplete(PrimitiveResult response) {
                if (response.error == null) {
                    ovpSessionProvider.getSessionToken(new OnCompletion<PrimitiveResult>() {
                        @Override
                        public void onComplete(PrimitiveResult response) {
                            assertNotNull(response.getResult());
                            testKs = response.getResult();

                            ovpSessionProvider.endSession(new OnCompletion<BaseResult>() {
                                @Override
                                public void onComplete(BaseResult response) {
                                    if (response.error == null) {
                                        APIOkRequestsExecutor.getSingleton().queue(BaseEntryService.list(ovpSessionProvider.baseUrl() + OvpConfigs.ApiPrefix,
                                                testKs, NonDRMEntryId)
                                                .completion(new OnRequestCompletion() {
                                                    @Override
                                                    public void onComplete(ResponseElement response) {
                                                        try {
                                                            assertNotNull(response.getResponse());
                                                            BaseResult parsedResponse = PhoenixParser.parse(response.getResponse());
                                                            assertNotNull(parsedResponse);
                                                            assertNotNull(parsedResponse.error);
                                                            assertTrue(parsedResponse.error.getCode().toLowerCase().contains("invalid_ks"));

                                                            //todo: change to another BE call
                                                            /*new KalturaOvpMediaProvider()
                                                                    .setSessionProvider(ovpSessionProvider)
                                                                    .setEntryId(NonDRMEntryId)
                                                                    .load(new OnMediaLoadCompletion() {
                                                                        @Override
                                                                        public void onComplete(ResultElement<PKMediaEntry> response) {
                                                                            //after ending session, it can't be renewed, start session should be called.
                                                                            try {
                                                                                assertNotNull(response.getError());
                                                                                assertTrue(response.getError().equals(ErrorElement.SessionError));
                                                                            } catch (AssertionError e) {
                                                                                failure.set(e);
                                                                                fail("failed assert error on entry loading on expired ks: " + e.getMessage());
                                                                            } finally {
                                                                                resume();
                                                                            }
                                                                        }
                                                                    });*/
                                                        } catch (AssertionError error) {
                                                            failure.set(error);
                                                            fail("failed assert error on expired ks: " + error.getMessage());
                                                        }
                                                    }
                                                }).build());

                                    } else {
                                        log.i("got an error: " + response.error.getMessage());
                                        fail("failed to end session");
                                        resume();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    fail("failed to establish session");
                }
            }
        });
        wait(1);
    }

    //TODO add failure test, add test for expiration - check renewal of session
}
