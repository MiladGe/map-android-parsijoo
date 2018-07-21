package ir.parsijoo.map.android.Builder;

import android.content.Context;
import android.support.annotation.NonNull;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import ir.parsijoo.map.android.Viewer;

public class MarkerBuilder extends Marker {


    public MarkerBuilder(MapView mapView) {
        super(mapView);
    }

    public MarkerBuilder(MapView mapView, Context resourceProxy) {
        super(mapView, resourceProxy);
    }

    public static Marker Create(@NonNull Viewer viewer) {
        return new Marker(viewer.getMapView());
    }

    public static Marker Create(Viewer viewer, GeoPoint p) {

        Marker marker = new Marker(viewer.getMapView());
        marker.setPosition(p);
        return marker;
    }

    public static Marker Create(Viewer viewer, IGeoPoint p) {

        Marker marker = new Marker(viewer.getMapView());
        marker.setPosition(new GeoPoint(p.getLatitude(), p.getLongitude()));
        return marker;
    }
}
