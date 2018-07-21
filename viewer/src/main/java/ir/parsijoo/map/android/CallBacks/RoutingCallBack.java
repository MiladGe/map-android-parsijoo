package ir.parsijoo.map.android.CallBacks;

import com.android.volley.NetworkResponse;

import org.osmdroid.views.overlay.Polyline;

import ir.parsijoo.map.android.Models.RoutingDetail;
import ir.parsijoo.map.android.Viewer;

public interface RoutingCallBack {

    void onSuccess(RoutingDetail routingDetail, Polyline polyline, Viewer viewer);

    void onFail(NetworkResponse networkResponse);
}
