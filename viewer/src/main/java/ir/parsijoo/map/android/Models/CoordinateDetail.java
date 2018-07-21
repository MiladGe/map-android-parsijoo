package ir.parsijoo.map.android.Models;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

public class CoordinateDetail {

    private GeoPoint topLeft;
    private GeoPoint topRight;
    private GeoPoint bottomRight;
    private GeoPoint bottomLeft;
    private GeoPoint center;

    public GeoPoint getTopLeft() {
        return topLeft;
    }

    public CoordinateDetail setTopLeft(IGeoPoint topLeft) {
        this.topLeft = new GeoPoint(topLeft.getLatitude(), topLeft.getLongitude());
        return this;
    }

    public GeoPoint getTopRight() {
        return topRight;
    }

    public CoordinateDetail setTopRight(IGeoPoint topRight) {
        this.topRight = new GeoPoint(topRight.getLatitude(), topRight.getLongitude());
        return this;

    }

    public GeoPoint getBottomRight() {
        return bottomRight;
    }

    public CoordinateDetail setBottomRight(IGeoPoint bottomRight) {
        this.bottomRight = new GeoPoint(bottomRight.getLatitude(), bottomRight.getLongitude());
        return this;

    }

    public GeoPoint getBottomLeft() {
        return bottomLeft;
    }

    public CoordinateDetail setBottomLeft(IGeoPoint bottomLeft) {
        this.bottomLeft = new GeoPoint(bottomLeft.getLatitude(), bottomLeft.getLongitude());
        return this;
    }

    public GeoPoint getCenter() {
        return center;
    }

    public CoordinateDetail setCenter(IGeoPoint center) {
        this.center = new GeoPoint(center.getLatitude(), center.getLongitude());
        return this;
    }
}
