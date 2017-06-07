package com.kaltura.netkit.utils;

import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Created by tehilarozin on 04/12/2016.
 */

public abstract class BaseCallableLoader<T> implements Callable<T> {

    protected final String loadId = this.toString() + ":" + System.currentTimeMillis();

    protected CountDownLatch waitCompletion;

    protected String TAG;
    protected final Object syncObject = new Object();
    protected boolean isCanceled = false;


    protected BaseCallableLoader(String tag) {
        this.TAG = tag;
    }

    abstract protected T load() throws InterruptedException;

    abstract protected void cancel();

    protected void notifyCompletion() {
        if (waitCompletion != null) {
            synchronized (syncObject) {
                Log.v(TAG, loadId + ": notifyCompletion: countDown =  " + waitCompletion.getCount());
                waitCompletion.countDown();
            }
        }
    }

    protected void waitCompletion(int countDownLatch) throws InterruptedException {
        if(waitCompletion != null && waitCompletion.getCount() == countDownLatch){
            return;
        }

        synchronized (syncObject) {
            Log.i(TAG, loadId + ": waitCompletion: set new counDown " + (waitCompletion != null ? "already has counter " + waitCompletion.getCount() : ""));
            waitCompletion = new CountDownLatch(countDownLatch);
        }
        waitCompletion.await();
        waitCompletion = null;
    }

    protected void waitCompletion() throws InterruptedException {
        waitCompletion(1);
    }

    @Override
    public T call() {
        if (isCanceled()) { // needed in case cancel done before callable started
            Log.i(TAG, loadId + ": Loader call canceled");
            return null;
        }

        Log.i(TAG, loadId + ": Loader call started ");

        try {
            return load();
        } catch (InterruptedException e) {
            interrupted();
        }
        return null;
    }

    protected boolean isCanceled() {
        return isCanceled;// Thread.currentThread().isInterrupted();
    }

    protected void interrupted() {
        Log.i(TAG, loadId + ": loader operation interrupted ");
        cancel();
    }


}
