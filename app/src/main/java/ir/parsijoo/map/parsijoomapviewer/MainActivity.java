package ir.parsijoo.map.parsijoomapviewer;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.ColorUtils;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkResponse;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import ir.parsijoo.map.android.Builder.MarkerBuilder;
import ir.parsijoo.map.android.CallBacks.LocationDetailCallBack;
import ir.parsijoo.map.android.CallBacks.OnMapClickListener;
import ir.parsijoo.map.android.CallBacks.RoutingCallBack;
import ir.parsijoo.map.android.CallBacks.SearchResultCallBack;
import ir.parsijoo.map.android.Controls.ZoomLevel;
import ir.parsijoo.map.android.Models.CoordinateDetail;
import ir.parsijoo.map.android.Models.LocationDetail;
import ir.parsijoo.map.android.Models.Place;
import ir.parsijoo.map.android.Models.RoutingDetail;
import ir.parsijoo.map.android.Util.BoundingBoxBuilder;
import ir.parsijoo.map.android.Util.ShapeUtil;
import ir.parsijoo.map.android.Viewer;

public class MainActivity extends RuntimePermissionsActivity implements MapView.OnFirstLayoutListener {

    public Viewer viewer;
    private String TAG = "ParsijooTag";

    private String route1 = "r1", route2 = "r2";
    private int ACCESS_REQUEST_CODE = 20000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setListeners();
        viewer = findViewById(R.id.mapview);

        viewer.setStartPosition(new GeoPoint(31.89739, 54.35119), ZoomLevel.City_1);
//        viewer.enableRotateGesture();
        viewer.setFirstLoadCallBack(this);
//        viewer.showCurrentLocation(true, true);
//        boolean locationPermission = viewer.showMyLocationButton(true);
//        if (!locationPermission) {
//            //کاربر دسترسی لوکیشن را نداده پس دکمه هم نمایش داده نمی شود
//            Toast.makeText(this, "لطفا دسترسی لوکیشن را تایید نمایید", Toast.LENGTH_SHORT).show();
//        }
        viewer.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClicked(GeoPoint p, Viewer mapViewer) {
                Marker marker = MarkerBuilder.Create(viewer, p);

                MyInfoWindow myInfoWindow = new MyInfoWindow(R.layout.info_window, viewer.getMapView())
                        .setViewer(viewer)
                        .setMarker(marker)
                        .setAutoHide(2500)
                        .setOnDeleteClicked(new MyInfoWindow.onDeleteClicked() {
                            @Override
                            public void onDeleteClicked(Marker marker) {
                                viewer.removeMarker(marker);
                            }
                        });

                marker.setInfoWindow(myInfoWindow);
                marker.setAnchor(Marker.ANCHOR_CENTER, 1.0f);
                marker.setIcon(getResources().getDrawable(R.drawable.marker_blue));
//                mapViewer.addMarker(p.getLatitude(), p.getLongitude());
                mapViewer.addMarker(marker);
                mapViewer.animateToPosition(p);


            }

            @Override
            public void onLongPress(GeoPoint p, Viewer mapViewer) {

                Toast.makeText(getApplicationContext(), "LongClick", Toast.LENGTH_SHORT).show();
            }
        });

        getPermission();

    }

    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            MainActivity.super.requestAppPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_REQUEST_CODE);
        }
    }

    private void zoomToBound() {


        BoundingBox boundingBox = new BoundingBoxBuilder()
                .addPoint(new GeoPoint(31.91551, 54.32237))
                .addPoint(new GeoPoint(31.90240, 54.31009))
                .addPoint(new GeoPoint(31.89183, 54.32777))
                .addPoint(new GeoPoint(31.89627, 54.33721))
                .create();

        viewer.zoomToBoundingBox(boundingBox, true);

    }

    private void drawPolygon() {

        Polygon polygon = new Polygon();

        List<GeoPoint> points = new ArrayList<>();
        points.add(new GeoPoint(31.81462, 54.35277));
        points.add(new GeoPoint(31.81983, 54.36024));
        points.add(new GeoPoint(31.81086, 54.36522));
        points.add(new GeoPoint(31.81236, 54.35505));
        polygon.setPoints(points);
        //without theme
        int strokeColor = ResourcesCompat.getColor(getResources(), R.color.purpleBorder, null);
        int fillColor = ColorUtils.setAlphaComponent(ResourcesCompat.getColor(getResources(), R.color.purpleFill, null), 40);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);

        viewer.drawPolygon(polygon);
    }

    private void getAddress(@NonNull GeoPoint point) {
        viewer.getAddress(point.getLatitude(), point.getLongitude(), new LocationDetailCallBack() {
            @Override
            public void onResult(LocationDetail locationDetail) {

            }

            @Override
            public void onFail(NetworkResponse response) {

            }
        });
    }

    private void searchPlace(String query, String city) {
        viewer.searchPlace(query, city, new SearchResultCallBack() {
            @Override
            public void onResult(ArrayList<Place> places) {

                if (places.size() > 0) {

                    Toast.makeText(MainActivity.this, "" + places.get(0).getAddress(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(NetworkResponse error) {

            }
        });
    }

    private void getRoute_withMiddle() {
        ArrayList<GeoPoint> middle_points = new ArrayList<>();
        middle_points.add(new GeoPoint(31.88812, 54.32759));
        viewer.getRoute(new GeoPoint(31.870725879312808, 54.397498339843594),
                new GeoPoint(31.936595766279076, 54.32059404296901),
                middle_points,
                new RoutingCallBack() {
                    @Override
                    public void onSuccess(RoutingDetail routingDetail, Polyline polyline, Viewer viewer) {
                        if (polyline != null) {
                            viewer.drawPolyLine(polyline, route1);
                        }
                    }

                    @Override
                    public void onFail(NetworkResponse networkResponse) {

                    }
                });

    }

    private void getRoute_noMiddle() {
        viewer.getRoute(new GeoPoint(32.00491, 54.21610)
                , new GeoPoint(31.85975, 54.31165)
                , new RoutingCallBack() {
                    @Override
                    public void onSuccess(RoutingDetail routingDetail, Polyline polyline, Viewer viewer) {
                        if (polyline != null) {
                            int color = Color.parseColor("#ff0000");
                            polyline.setColor(color);
                            polyline.setWidth(15);
                            viewer.drawPolyLine(polyline, route2);
                        }
                    }

                    @Override
                    public void onFail(NetworkResponse networkResponse) {

                    }
                });
    }

    private void drawCircle() {


        Polygon polygon = ShapeUtil.createCirlce(new GeoPoint(31.91478, 54.37008), 1500);
        int strokeColor = ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null);
        int fillColor = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null);
        fillColor = ColorUtils.setAlphaComponent(fillColor, 40);
        polygon.setFillColor(fillColor);
        polygon.setStrokeColor(strokeColor);
        viewer.drawPolygon(polygon);
    }

    private void setListeners() {

        findViewById(R.id.deleteTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewer != null) {
                    viewer.removeShape(viewer.findItemByTag(route1));
                    viewer.removeShape(viewer.findItemByTag(route2));
                }
            }
        });
        findViewById(R.id.deleteAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewer != null) {
                    viewer.removeAllShapes();
                }
            }
        });
//        findViewById(R.id.remove_R1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (viewer != null && route1 != null)
//                    viewer.removeShape(route1);
//            }
//        });
//
//        findViewById(R.id.remove_R2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (viewer != null && route2 != null)
//                    viewer.removeShape(route2);
//            }
//        });
//
//        findViewById(R.id.remove_markers).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (viewer != null)
//                    viewer.removeAllMarkers();
//            }
//        });
    }

    @Override
    public void onFirstLayout(View v, int left, int top, int right, int bottom) {

        searchPlace("بلوار مطهری", "یزد");
        getRoute_withMiddle();
        getRoute_noMiddle();
        getAddress(new GeoPoint(31.885, 54.32750));
        drawPolygon();
        zoomToBound();
        drawCircle();

        CoordinateDetail currentVisible = viewer.getVisibleCorners();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewer.removeLocationUpdateCallBack();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    @Override
    public void onPermissionsDeny(int requestCode) {

        Toast.makeText(this, "برای کارکرد برنامه لطفا دسترسی کارت حافظه را تایید نمایید", Toast.LENGTH_SHORT).show();
//        getPermission();
    }

}
