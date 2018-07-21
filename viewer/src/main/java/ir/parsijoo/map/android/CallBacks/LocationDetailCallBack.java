package ir.parsijoo.map.android.CallBacks;

import com.android.volley.NetworkResponse;

import ir.parsijoo.map.android.Models.LocationDetail;

public interface LocationDetailCallBack {

    void onResult(LocationDetail locationDetail);
    void onFail(NetworkResponse response);
}
