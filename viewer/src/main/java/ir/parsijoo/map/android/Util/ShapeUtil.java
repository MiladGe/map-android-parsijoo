package ir.parsijoo.map.android.Util;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

public class ShapeUtil {

    public static Polyline createPolyLine(ArrayList<GeoPoint> points) {

        if (points.size() > 0) {
            Polyline line = new Polyline();   //see note below!
            line.setPoints(points);
            return line;
        } else return null;
    }

    public static Polygon createPolgone(ArrayList<GeoPoint> points) {

        if (points.size() > 0) {
            Polygon polygon = new Polygon();   //see note below!
            polygon.setPoints(points);
            return polygon;
        } else return null;
    }

    public static Polygon createCirlce(GeoPoint center, float radius) {
        Polygon oPolygon = new Polygon();
        ArrayList<GeoPoint> circlePoints = new ArrayList<GeoPoint>();
        for (float f = 0; f < 360; f += 1) {
            circlePoints.add(new GeoPoint(center.getLatitude(), center.getLongitude()).destinationPoint(radius, f));
        }
        oPolygon.setPoints(circlePoints);
        return oPolygon;
    }
}
