package ir.parsijoo.map.android.CallBacks;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import ir.parsijoo.map.android.Viewer;

public interface OnMapClickListener {

    void onMapClicked(GeoPoint p, Viewer mapViewer);
    void onLongPress(GeoPoint p, Viewer mapViewer);
}
