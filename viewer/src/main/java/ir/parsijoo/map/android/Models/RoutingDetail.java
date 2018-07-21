package ir.parsijoo.map.android.Models;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

public class RoutingDetail {

    private float distanceInMeter;
    private float timeInMilis;
    private ArrayList<HashMap<String, String>> instructionList;
    private ArrayList<GeoPoint> points;

    public float getDistanceInMeter() {
        return distanceInMeter;
    }

    public RoutingDetail setDistanceInMeter(float distanceInMeter) {
        this.distanceInMeter = distanceInMeter;
        return this;
    }

    public float getTimeInMilis() {
        return timeInMilis;
    }

    public RoutingDetail setTimeInMilis(float timeInMilis) {
        this.timeInMilis = timeInMilis;
        return this;
    }

    public ArrayList<HashMap<String, String>> getInstructionList() {
        return instructionList;
    }

    public RoutingDetail setInstructionList(ArrayList<HashMap<String, String>> instructionList) {
        this.instructionList = instructionList;
        return this;
    }

    public ArrayList<GeoPoint> getPoints() {
        return points;
    }

    public RoutingDetail setPoints(ArrayList<GeoPoint> points) {
        this.points = points;
        return this;
    }
}
