package com.kaltura.netkit.services;

import com.kaltura.netkit.utils.NKLog;

import org.junit.After;

import java.util.concurrent.CountDownLatch;


/**
 * @hide
 */

public class BaseTest {

    private static final NKLog log = NKLog.get("BaseTest");

    protected CountDownLatch testWaitCount;
    Object syncObject = new Object();

    public BaseTest() { }

    protected void resume() {
        if (testWaitCount != null) {
            synchronized (syncObject) {
                testWaitCount.countDown();
                log.d("count down reduced to " + testWaitCount.getCount());
            }
        }
    }

    protected synchronized void resume(int delay) {
        try {
            Thread.sleep(delay);
            resume();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void wait(int count) {
        synchronized (syncObject) {
            testWaitCount = new CountDownLatch(count);
        }
        try {
            testWaitCount.await(/*10000, TimeUnit.MILLISECONDS*/);
            log.d("count down set for " + count);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @After
    public void tearDown() {
        /*resume();*/
    }


    public interface TestBlock<T>{
        void execute(T data) throws AssertionError;
    }
}
