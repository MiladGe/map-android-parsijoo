package ir.parsijoo.map.android.Util;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class BoundingBoxBuilder {

    private ArrayList<GeoPoint> points;


    public BoundingBoxBuilder addPoint(GeoPoint point) {
        if (points == null)
            points = new ArrayList<>();

        points.add(point);
        return this;
    }

    public BoundingBox create() {
        return BoundingBox.fromGeoPoints(points);
    }
}
