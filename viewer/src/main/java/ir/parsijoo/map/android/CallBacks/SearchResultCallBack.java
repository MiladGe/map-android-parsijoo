package ir.parsijoo.map.android.CallBacks;

import com.android.volley.NetworkResponse;

import java.util.ArrayList;

import ir.parsijoo.map.android.Models.Place;

public interface SearchResultCallBack {

    void onResult(ArrayList<Place> places);
    void onFail(NetworkResponse error);
}
