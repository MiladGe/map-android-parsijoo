package ir.parsijoo.map.android.Util;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class WktParsers {

    public static ArrayList<GeoPoint> lineStringToGeoPoint(String wkt){
        String par = wkt.replace("LINESTRING ", "");
        par = par.replace("(", "");
        par = par.replace(")", "");
//                par = par.replace("\"","");
        String[] locs = par.split(", ");

        ArrayList<GeoPoint> geoPoints = new ArrayList<>();

        for (String loc : locs) {
            String[] point = loc.split(" ");
            try {

                geoPoints.add(new GeoPoint(Double.parseDouble(point[1]), Double.parseDouble(point[0])));
            } catch (Exception ignore) {
            }
        }
        return geoPoints;
    }
}
