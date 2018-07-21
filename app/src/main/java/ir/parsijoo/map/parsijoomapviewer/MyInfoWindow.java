package ir.parsijoo.map.parsijoomapviewer;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.NetworkResponse;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import ir.parsijoo.map.android.CallBacks.LocationDetailCallBack;
import ir.parsijoo.map.android.Models.LocationDetail;
import ir.parsijoo.map.android.Viewer;

public class MyInfoWindow extends MarkerInfoWindow {


    private onDeleteClicked onDeleteClicked;
    private Viewer viewer;
    private Marker _marker;
    private int autoHide = -1;

    public MyInfoWindow(int layoutResId, MapView mapView) {
        super(layoutResId, mapView);
    }

    @Override
    public void open(Object object, GeoPoint position, int offsetX, int offsetY) {
        super.open(object, position, offsetX, offsetY);
        final TextView detail = mView.findViewById(R.id.addressDetailTv);
        Button delete = mView.findViewById(R.id.deleteMarker);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onDeleteClicked != null) {
                    onDeleteClicked.onDeleteClicked(_marker);
                    close();
                }
            }
        });

        viewer.getAddress(position.getLatitude(), position.getLongitude(), new LocationDetailCallBack() {
            @Override
            public void onResult(LocationDetail locationDetail) {
                detail.setText(locationDetail.getWays());
            }

            @Override
            public void onFail(NetworkResponse response) {

            }
        });

        if (autoHide!=-1){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    close();
                }
            },autoHide);
        }
    }


    @Override
    public void onOpen(Object item) {

    }

    public MyInfoWindow setViewer(Viewer viewer) {
        this.viewer = viewer;
        return this;
    }

    public MyInfoWindow setOnDeleteClicked(MyInfoWindow.onDeleteClicked onDeleteClicked) {
        this.onDeleteClicked = onDeleteClicked;
        return this;
    }

    public MyInfoWindow setMarker(Marker marker) {
        this._marker = marker;
        return this;
    }

    public MyInfoWindow setAutoHide(int timeInMili) {
        this.autoHide = timeInMili;
        return this;
    }


    public interface onDeleteClicked {
        void onDeleteClicked(Marker marker);
    }
}
