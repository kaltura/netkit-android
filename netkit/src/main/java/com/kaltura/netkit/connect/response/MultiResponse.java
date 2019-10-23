package com.kaltura.netkit.connect.response;


import com.kaltura.netkit.utils.NKLog;

import java.util.ArrayList;

/**
 * @hide
 */

public class MultiResponse extends ArrayList<BaseResult> {

    private static final NKLog log = NKLog.get("MultiResponse");

    public MultiResponse(){
        super();
    }

    public MultiResponse(BaseResult response){
        super();
        add(response);
    }

    public boolean failedOn(int i) {
        try {
            return get(i).error != null;
        } catch (IndexOutOfBoundsException e) {
            log.e("failedOn: index " + i + " out of range");
            return true;
        }
    }

    public <T extends BaseResult> T getAt(String indexStr) {
        int index = 0;
        try {
            index = Integer.valueOf(indexStr) - 1;

        } catch (NumberFormatException e) {
            e.printStackTrace();
            index = -1;
        }

        return getAt(index);
    }

    public <T extends BaseResult> T getAt(int i){
        return i >= 0 && size() > i ? (T) get(i) : null;
    }

}
