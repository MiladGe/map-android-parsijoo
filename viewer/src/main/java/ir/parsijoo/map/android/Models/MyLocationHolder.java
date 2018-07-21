package ir.parsijoo.map.android.Models;

import android.graphics.drawable.Drawable;

import org.osmdroid.util.GeoPoint;

public class MyLocationHolder {

    private GeoPoint lastPosition;
    private Drawable icon;
    private float accuracy;
    private int trackCount;
    private boolean mustAnimate;

    MyLocationHolder() {

    }

    public MyLocationHolder(Drawable icon) {
        this.icon = icon;
    }

    public GeoPoint getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(GeoPoint lastPosition) {
        this.lastPosition = lastPosition;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public boolean isMustAnimate() {
        return mustAnimate;
    }

    public void setMustAnimate(boolean mustAnimate) {
        this.mustAnimate = mustAnimate;
    }
}
