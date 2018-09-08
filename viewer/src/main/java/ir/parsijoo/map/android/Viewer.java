package ir.parsijoo.map.android;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleInvalidationHandler;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.parsijoo.map.android.Builder.MarkerBuilder;
import ir.parsijoo.map.android.CallBacks.LocationDetailCallBack;
import ir.parsijoo.map.android.CallBacks.OnMapClickListener;
import ir.parsijoo.map.android.CallBacks.RoutingCallBack;
import ir.parsijoo.map.android.CallBacks.SearchResultCallBack;
import ir.parsijoo.map.android.Controls.ZoomLevel;
import ir.parsijoo.map.android.Models.CoordinateDetail;
import ir.parsijoo.map.android.Models.LocationDetail;
import ir.parsijoo.map.android.Models.MyLocationHolder;
import ir.parsijoo.map.android.Models.Place;
import ir.parsijoo.map.android.Models.RoutingDetail;
import ir.parsijoo.map.android.Util.ShapeUtil;
import ir.parsijoo.map.android.Util.WktParsers;
import ir.parsijoo.map.viewer.R;

/**
 * Created by mhkyazd on 12/30/2017.
 */

public class Viewer extends RelativeLayout {

    private Context context;
    private MapView mapView;
    private TilesOverlay hibridTileOverlay = null;
    public MapTileProviderBasic mapTileProviderBasicParsijoo;
    private String api_key;
    private CallBackAddress callBackAddress;
    private CallBackSearch callBackSearch;
    private CallBackDirection callBackDirection;
    private OnMapClickListener onMapClickListener;
    private RotationGestureOverlay mRotationGestureOverlay;
    private FolderOverlay shapesOverLay;
    private FolderOverlay markerOverLay;
    private FolderOverlay myLocationOverLay;
    private HashMap<String, Overlay> allOverLays;
    public ItemizedIconOverlay<OverlayItem> itemizedIconOverlay;
    private MyLocationHolder myLocationHolder;
    private ImageView myLocationIv;
    private LocationCallback locationCallback = null;
    private static boolean mustShowMyLocationBt = false;
    private LinearLayout myLocationBtParent;

    public Viewer(Context context) {
        super(context);
        this.context = context;
        initializer(context, null, 0);
    }

    public Viewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializer(context, attrs, 0);
    }

    public Viewer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initializer(context, attrs, defStyleAttr);
    }

    private void initializer(Context context, AttributeSet attrs, int defStyleAttr) {

        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        allOverLays = new HashMap<>();

        View view = inflate(context, R.layout.viewer, this);
        myLocationIv = view.findViewById(R.id.myLocation);
        myLocationBtParent = view.findViewById(R.id.mylocationBtParent);
        myLocationIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myLocationHolder != null && myLocationHolder.getLastPosition() != null) {
                    animateToPosition(myLocationHolder.getLastPosition());

                    if (mapView.getZoomLevelDouble() < ZoomLevel.Province_5.get())
                        setZoom(ZoomLevel.City_3);
                    drawMyLocationOverLay();
                }
            }
        });
        mapView = view.findViewById(R.id.mapviewosm);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomViewer, defStyleAttr, 0);
        this.api_key = a.getString(R.styleable.CustomViewer_api_key);
        mapView.setTilesScaledToDpi(true);
        Configuration.getInstance().setMapViewHardwareAccelerated(true);
        Configuration.getInstance().setUserAgentValue(context.getPackageName());
        setConfigZoom(false);
        mapView.getController().setCenter(new GeoPoint(31.8974, 54.3569));
        mapView.getController().setZoom(4);
        mapView.setMaxZoomLevel(20.0);
        mapView.setMinZoomLevel(3.0);
        setConfigMultiTouch(true);
        parsijooTileProvider();
        myLocationHolder = new MyLocationHolder(ContextCompat.getDrawable(context, R.drawable.my_location));
        myLocationOverLay = new FolderOverlay();
        mapView.getOverlays().add(myLocationOverLay);

        a.recycle();

    }

    private void drawMyLocationOverLay() {

        List<Overlay> overlays = myLocationOverLay.getItems();

        for (int i = overlays.size() - 1; i >= 0; i--)
            myLocationOverLay.remove(overlays.get(i));

        Marker marker = MarkerBuilder.Create(getSelf(), myLocationHolder.getLastPosition());
        marker.setIcon(myLocationHolder.getIcon());
        Polygon circle = ShapeUtil.createCirlce(myLocationHolder.getLastPosition(), myLocationHolder.getAccuracy());
        circle.setStrokeColor(R.color.myLocationBorder);
        circle.setStrokeWidth(2);
        circle.setFillColor(ColorUtils.setAlphaComponent(ResourcesCompat.getColor(getResources(), R.color.myLocationBorder, null), 60));
        myLocationOverLay.add(circle);
        myLocationOverLay.add(marker);
        mapView.invalidate();
    }

    public MapView getMapView() {
        return mapView;
    }

    public void animateToPosition(GeoPoint point) {
        mapView.getController().animateTo(point);

    }

    /**
     * در اینجا با ست کردن یک کالبک می توانی زمانی که نقشه برای بار اول لود می شود را بفهیم
     * برای انجام کارهایی که نیاز است ابتدا نقشه لود شده باشد استفاده می شود
     * مثل zoomToBoundingBox
     *
     * @param loadCallBack کالبک برای اینکه بفهمیم چه زمانی نقشه لود شده است
     */
    public void setFirstLoadCallBack(MapView.OnFirstLayoutListener loadCallBack) {
        mapView.addOnFirstLayoutListener(loadCallBack);
    }


    public void zoomToBoundingBox(final BoundingBox box, final boolean animate) {
        if (mapView != null) {


            if (animate)
                animateToPosition(new GeoPoint(box.getCenterLatitude(), box.getCenterLongitude()));
            mapView.zoomToBoundingBox(box, false);
        }

    }

    public void setCenter(GeoPoint center, boolean isAnimate) {

        mapView.getController().setCenter(center);
        if (isAnimate) animateToPosition(center);
    }

    public void setOnMapClickListener(final OnMapClickListener onMapClickListener_) {
        this.onMapClickListener = onMapClickListener_;


        setClickListenerToAllOverlays();
    }

    private void setClickListenerToAllOverlays() {


        if (shapesOverLay == null) {
            shapesOverLay = new FolderOverlay();
            mapView.getOverlayManager().add(shapesOverLay);
        }

        getMapView().getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {

                if (onMapClickListener != null) onMapClickListener.onMapClicked(p, getSelf());
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                if (onMapClickListener != null) onMapClickListener.onLongPress(p, getSelf());
                return true;
            }
        }));


    }

    public void setConfigZoom(Boolean bool) {
        this.mapView.setBuiltInZoomControls(bool);
    }

    public void setConfigMultiTouch(Boolean bool) {
        this.mapView.setMultiTouchControls(bool);
    }

    public void parsijooHibridTileOverllay() {
        final MapTileProviderBasic tileProvider = new MapTileProviderBasic(context);
        ITileSource tileSource = new XYTileSource("Hibrid", 3, 17, 256, "",
                new String[]{
                        context.getString(R.string.base_tile_url_a) + "GetHibrid?imageID=p_",
                        context.getString(R.string.base_tile_url_b) + "GetHibrid?imageID=p_",
                        context.getString(R.string.base_tile_url_c) + "GetHibrid?imageID=p_",
                        context.getString(R.string.base_tile_url_d) + "GetHibrid?imageID=p_",
                        context.getString(R.string.base_tile_url_e) + "GetHibrid?imageID=p_",
                }) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return getBaseUrl() + "&x=" + MapTileIndex.getX(pMapTileIndex) + "&y=" + MapTileIndex.getY(pMapTileIndex) + "&z=" + MapTileIndex.getZoom(pMapTileIndex);
            }
        };
        tileProvider.setTileSource(tileSource);
        hibridTileOverlay = new TilesOverlay(tileProvider, context);
        hibridTileOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
        mapView.getOverlays().add(hibridTileOverlay);
        mapView.invalidate();
    }

    public void parsijooTileProvider() {

        int newScale = 256;
        mapTileProviderBasicParsijoo = new MapTileProviderBasic(context);
        mapTileProviderBasicParsijoo.setTileRequestCompleteHandler(new SimpleInvalidationHandler(mapView));
        Configuration.getInstance().getAdditionalHttpRequestProperties().put("api-key", api_key);

        OnlineTileSourceBase parsijoo = new OnlineTileSourceBase("Road", 3, 19, newScale, "", new String[]{
                "http://developers.parsijoo.ir/web-service/v1/map/?type=tile"
        }) {

            @Override
            public String getTileURLString(long pMapTileIndex) {

                return getBaseUrl() + "&x=" + MapTileIndex.getX(pMapTileIndex) + "&y=" + MapTileIndex.getY(pMapTileIndex) + "&z=" + MapTileIndex.getZoom(pMapTileIndex);
            }
        };
        mapTileProviderBasicParsijoo.setTileSource(parsijoo);
        mapView.setTileProvider(mapTileProviderBasicParsijoo);
    }

    public void addMarker(@NonNull Marker marker) {
        addMarker(marker, null);

    }

    public void addMarker(@NonNull GeoPoint point, String tag) {
        addMarker(point.getLatitude(), point.getLongitude(), tag);

    }

    public void addMarker(@NonNull GeoPoint point) {
        addMarker(point, null);

    }

    public void addMarker(@NonNull List<GeoPoint> points) {

        for (GeoPoint point : points)
            addMarker(point.getLatitude(), point.getLongitude());

    }

    public void addMarker(@NonNull Marker marker, String tag) {

        if (markerOverLay == null) {
            markerOverLay = new FolderOverlay();
            mapView.getOverlays().add(markerOverLay);
        }
        markerOverLay.add(marker);
        if (mapView != null)
            mapView.invalidate();

        if (tag != null)
            allOverLays.put(tag, marker);
    }

    public Marker addMarker(double y, double x) {
        return addMarker(y, x, null);
    }

    public Marker addMarker(double y, double x, String tag) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(y, x));
        marker.setAnchor(Marker.ANCHOR_CENTER, 1.0f);

        if (markerOverLay == null) {
            markerOverLay = new FolderOverlay();
            mapView.getOverlays().add(markerOverLay);
        }
        markerOverLay.add(marker);
        if (mapView != null)
            mapView.invalidate();

        if (tag != null)
            allOverLays.put(tag, marker);
        return marker;
    }

    public void removeMarker(Marker marker) {

        if (markerOverLay != null && mapView != null) {
            markerOverLay.remove(marker);
            mapView.invalidate();
        }
    }

    public void removeAllMarkers() {

        if (markerOverLay != null && mapView != null) {

            List<Overlay> overlayList = markerOverLay.getItems();
            for (int i = overlayList.size() - 1; i >= 0; i--) {
                markerOverLay.remove(overlayList.get(i));
            }
            mapView.invalidate();
        }

    }

    public void drawPolyLine(Polyline polyline, String tag) {
        if (polyline == null)
            return;

        if (shapesOverLay == null) {
            shapesOverLay = new FolderOverlay();
            mapView.getOverlayManager().add(shapesOverLay);
        }
        shapesOverLay.add(polyline);
        mapView.invalidate();

        if (tag != null)
            allOverLays.put(tag, polyline);

    }

    public void drawPolyLine(Polyline polyline) {
        drawPolyLine(polyline, null);
    }

    public void drawPolygon(Polygon polygon, String tag) {
        if (polygon == null)
            return;

        if (shapesOverLay == null) {
            shapesOverLay = new FolderOverlay();
            mapView.getOverlayManager().add(shapesOverLay);
        }
        shapesOverLay.add(polygon);
        mapView.invalidate();

        if (tag != null) {
            allOverLays.put(tag, polygon);
        }
    }

    public void drawPolygon(Polygon polygon) {
        drawPolygon(polygon, null);
    }

    public void removeAllShapes() {

        if (shapesOverLay != null && mapView != null) {
            mapView.invalidate();

            List<Overlay> overlayList = shapesOverLay.getItems();

            for (int i = overlayList.size() - 1; i >= 0; i--) {
                shapesOverLay.remove(overlayList.get(i));
            }

            mapView.invalidate();
        }
    }

    public void removeShape(Polyline polyline) {
        if (shapesOverLay != null && mapView != null) {
            shapesOverLay.remove(polyline);
            mapView.invalidate();
        }
    }

    public void removeShape(Overlay overlay) {
        if (shapesOverLay != null && mapView != null) {
            shapesOverLay.remove(overlay);
            mapView.invalidate();
        }
    }

    public void removeShape(Polygon polygon) {
        if (shapesOverLay != null && mapView != null) {
            shapesOverLay.remove(polygon);
            mapView.invalidate();
        }
    }

    public void setZoom(ZoomLevel zoomLevel) {
        mapView.getController().setZoom(zoomLevel.get());
    }

    public void setStartPosition(GeoPoint p, ZoomLevel zoomLevel) {
        setCenter(p, true);
        setZoom(zoomLevel);
    }


    public boolean showMyLocationButton(boolean isShow, final LocationCallback callback) {


        if (isShow) {

            mustShowMyLocationBt = true;
            if (ActivityCompat.checkSelfPermission(mapView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mapView.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            myLocationIv.setVisibility(VISIBLE);
            myLocationBtParent.setVisibility(VISIBLE);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    myLocationHolder.setLastPosition(new GeoPoint(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()));
                    myLocationHolder.setAccuracy(locationResult.getLastLocation().getAccuracy());
                    if (myLocationHolder.getTrackCount() == -1) {
                        drawMyLocationOverLay();
                        animateToPosition(new GeoPoint(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()));
                    } else if (myLocationHolder.getTrackCount() > 0) {
                        myLocationHolder.setTrackCount(myLocationHolder.getTrackCount() - 1);
                        drawMyLocationOverLay();
                        animateToPosition(new GeoPoint(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()));

                    }
                    if (callback != null) {
                        callback.onLocationResult(locationResult);
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    super.onLocationAvailability(locationAvailability);
                    if (callback != null) {
                        callback.onLocationAvailability(locationAvailability);
                    }
                }
            };
            final LocationRequest locationRequest = new LocationRequest();
            locationRequest.setFastestInterval(1000)
                    .setInterval(3000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setSmallestDisplacement(1);


            LocationServices.getFusedLocationProviderClient(mapView.getContext())
                    .requestLocationUpdates(locationRequest, locationCallback, null);

            return true;
        } else {

            myLocationBtParent.setVisibility(GONE);
            mustShowMyLocationBt = false;
            myLocationIv.setVisibility(GONE);
            removeLocationUpdateCallBack();
            return true;
        }


    }

    public boolean showMyLocationButton(boolean isShow) {
        return showMyLocationButton(isShow, null);

    }

    public void showCurrentLocation(boolean animate, boolean mustUpdate) {

        myLocationHolder.setTrackCount(mustUpdate ? -1 : 1);
        myLocationHolder.setMustAnimate(animate);
        showMyLocationButton(true);
    }

    public void removeLocationUpdateCallBack() {
        if (locationCallback != null) {
            LocationServices.getFusedLocationProviderClient(mapView.getContext()).removeLocationUpdates(locationCallback);
            locationCallback = null;
        }

    }

    public void enableRotateGesture() {

        mRotationGestureOverlay = new RotationGestureOverlay(mapView);
        mRotationGestureOverlay.setEnabled(true);
        mapView.setMultiTouchControls(true);
        mapView.getOverlays().add(mRotationGestureOverlay);
    }

    public void disableRotateGesture() {

        mapView.getOverlays().remove(mRotationGestureOverlay);
        mRotationGestureOverlay.setEnabled(true);
        mRotationGestureOverlay = null;
        mapView.setMultiTouchControls(false);
    }

    public void setOrientation(float degree) {
        mapView.setMapOrientation(degree);
    }

    public Overlay findItemByTag(String tag) {
        return allOverLays.get(tag);
    }

    private void addPointLayer() {
        ArrayList<OverlayItem> overlayArray = new ArrayList<OverlayItem>();
        itemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(context, overlayArray, null);
        mapView.getOverlays().add(itemizedIconOverlay);
    }

    @Deprecated
    public void getAddress(final double y, final double x, final CallBackAddress callBackAddress) {
        this.callBackAddress = callBackAddress;
        String url = "http://developers.parsijoo.ir/web-service/v1/map/?type=address&x=" + x + "&y=" + y;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonParser parser = new JsonParser();
                        JsonObject rootObj = parser.parse(response).getAsJsonObject();
                        JsonObject resultObj = rootObj.getAsJsonObject("result");
                        resultObj = resultObj.getAsJsonObject();
                        HashMap<String, String> result = new HashMap<>();
                        result.put("state", resultObj.get("state").getAsString());
                        result.put("county", resultObj.get("county").getAsString());
                        result.put("city", resultObj.get("city").getAsString());
                        result.put("region", resultObj.get("region").getAsString());
                        result.put("zone", resultObj.get("zone").getAsString());
                        result.put("district", resultObj.get("district").getAsString());
                        result.put("village", resultObj.get("village").getAsString());
                        result.put("other", resultObj.get("other").getAsString());
                        result.put("ways", resultObj.get("ways").getAsString());
                        callBackAddress.onResponse(result);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null || error.networkResponse == null) {
                    return;
                }
                callBackAddress.onError(error.networkResponse);
                String body;
                //get status code here
                final String statusCode = String.valueOf(error.networkResponse.statusCode);
                //get response body and parse with appropriate encoding
                try {
                    body = new String(error.networkResponse.data, "UTF-8");
                    System.out.println("sysosout " + body);
                } catch (UnsupportedEncodingException e) {
                    // exception
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x", String.valueOf(x));
                params.put("y", String.valueOf(y));
                System.out.println("sysosout " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("api-key", api_key);
                return params;
            }

            ;
        };
        // Add the request to the RequestQueue.

        queue.add(stringRequest);
    }

    public void getAddress(final double y, final double x, final LocationDetailCallBack callBack) {
        String url = "http://developers.parsijoo.ir/web-service/v1/map/?type=address&x=" + x + "&y=" + y;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JsonParser parser = new JsonParser();
                            JsonObject rootObj = parser.parse(response).getAsJsonObject();
                            JsonObject resultObj = rootObj.getAsJsonObject("result");
                            resultObj = resultObj.getAsJsonObject();
                            LocationDetail detail = new LocationDetail();
                            detail.setState(resultObj.get("state").getAsString());
                            detail.setCounty(resultObj.get("county").getAsString());
                            detail.setCity(resultObj.get("city").getAsString());
                            detail.setRegion(resultObj.get("region").getAsString());
                            detail.setZone(resultObj.get("zone").getAsString());
                            detail.setDistrict(resultObj.get("district").getAsString());
                            detail.setVillage(resultObj.get("village").getAsString());
                            detail.setOther(resultObj.get("other").getAsString());
                            detail.setWays(resultObj.get("ways").getAsString());
                            if (callBack != null)
                                callBack.onResult(detail);
                        } catch (Exception ignored) {
                            return;
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null || error.networkResponse == null) {
                    return;
                }
                if (callBack != null)
                    callBack.onFail(error.networkResponse);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x", String.valueOf(x));
                params.put("y", String.valueOf(y));
                System.out.println("sysosout " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("api-key", api_key);
                return params;
            }

            ;
        };
        // Add the request to the RequestQueue.

        queue.add(stringRequest);
    }


    public static interface CallBackAddress {
        public void onResponse(HashMap<String, String> resultAddress);

        public void onError(NetworkResponse networkResponse);
    }

    @Deprecated
    public void getSearch(String q, final CallBackSearch callBackSearch) {
        doSearch(q, null, 0, 0, callBackSearch);
    }

    @Deprecated
    public void getSearch(String q, String city, int page, final CallBackSearch callBackSearch) {
        doSearch(q, city, page, 0, callBackSearch);
    }

    @Deprecated
    public void getSearch(String q, String city, int page, int nrpp, final CallBackSearch callBackSearch) {
        doSearch(q, city, page, nrpp, callBackSearch);
    }

    private void doSearch(String q, String city, int page, int nrpp, final CallBackSearch callBackSearch) {
        this.callBackSearch = callBackSearch;
        String query = "";
        try {
            query = URLEncoder.encode(q, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "http://developers.parsijoo.ir/web-service/v1/map/?type=search&q=" + query;
        url = (city != null) ? url + "&city=" + city : url;
        url = (page > 0) ? url + "&page=" + page : url;
        url = (nrpp > 0) ? url + "&nrpp=" + nrpp : url;
        System.out.println("sysosout url " + url);
        RequestQueue queue = Volley.newRequestQueue(context);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonParser parser = new JsonParser();
                        JsonObject rootObj = parser.parse(response).getAsJsonObject();
                        JsonObject resultObj = rootObj.getAsJsonObject("result");
                        int resultNumber = resultObj.get("resultNumber").getAsInt();
                        JsonArray itemsJsonArray = resultObj.getAsJsonArray("items");
                        ArrayList<HashMap<String, String>> itemsArray = new ArrayList<HashMap<String, String>>();
                        for (int i = itemsJsonArray.size() - 1; i >= 0; i--) {
                            JsonElement itemJsonElement = itemsJsonArray.get(i);
                            JsonObject itemJsonObject = itemJsonElement.getAsJsonObject();
                            HashMap<String, String> result = new HashMap<>();
                            result.put("title", itemJsonObject.get("title").getAsString());
                            result.put("longitude", itemJsonObject.get("longitude").getAsString());
                            result.put("latitude", itemJsonObject.get("latitude").getAsString());
                            result.put("zoom", itemJsonObject.get("zoom").getAsString());
                            result.put("type", itemJsonObject.get("type").getAsString());
                            result.put("name", itemJsonObject.get("name").getAsString());
                            result.put("address", itemJsonObject.get("address").getAsString());
                            itemsArray.add(result);
                        }
                        if (callBackSearch != null)
                            callBackSearch.onResponse(resultNumber, itemsArray);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null || error.networkResponse == null) {
                    return;
                }
                if (callBackSearch != null)
                    callBackSearch.onError(error.networkResponse);
                String body;
                final String statusCode = String.valueOf(error.networkResponse.statusCode);
                try {
                    body = new String(error.networkResponse.data, "UTF-8");
                    System.out.println("sysosout " + body);
                } catch (UnsupportedEncodingException e) {
                    // exception
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("api-key", api_key);
                return params;
            }

            ;
        };
        queue.add(stringRequest);
    }

    public void searchPlace(String q, String city, int page, int nrpp, SearchResultCallBack callBack) {
        doSearch(q, city, page, nrpp, callBack);
    }

    public void searchPlace(String q, String city, int page, SearchResultCallBack callBack) {
        doSearch(q, city, page, 0, callBack);
    }

    public void searchPlace(String q, String city, SearchResultCallBack callBack) {
        doSearch(q, city, 0, 0, callBack);
    }

    public void searchPlace(String q, SearchResultCallBack callBack) {
        doSearch(q, null, 0, 0, callBack);
    }

    private void doSearch(String q, String city, int page, int nrpp, final SearchResultCallBack callBack) {

        String query = "";
        try {
            query = URLEncoder.encode(q, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "http://developers.parsijoo.ir/web-service/v1/map/?type=search&q=" + query;
        url = (city != null) ? url + "&city=" + city : url;
        url = (page > 0) ? url + "&page=" + page : url;
        url = (nrpp > 0) ? url + "&nrpp=" + nrpp : url;
        System.out.println("sysosout url " + url);
        RequestQueue queue = Volley.newRequestQueue(context);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonParser parser = new JsonParser();
                        JsonObject rootObj = parser.parse(response).getAsJsonObject();
                        JsonObject resultObj = rootObj.getAsJsonObject("result");
                        JsonArray itemsJsonArray = resultObj.getAsJsonArray("items");
                        ArrayList<Place> places = new ArrayList<>();
                        for (int i = itemsJsonArray.size() - 1; i >= 0; i--) {
                            JsonElement itemJsonElement = itemsJsonArray.get(i);
                            JsonObject itemJsonObject = itemJsonElement.getAsJsonObject();
                            Place place = new Place();
                            place.setTitle(itemJsonObject.get("title").getAsString());
                            place.setLongtitude(itemJsonObject.get("longitude").getAsDouble());
                            place.setLatitude(itemJsonObject.get("latitude").getAsDouble());
                            place.setZoom(itemJsonObject.get("zoom").getAsInt());
                            place.setType(itemJsonObject.get("type").getAsString());
                            place.setName(itemJsonObject.get("name").getAsString());
                            place.setAddress(itemJsonObject.get("address").getAsString());
                            places.add(place);
                        }
                        if (callBack != null)
                            callBack.onResult(places);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null || error.networkResponse == null) {
                    return;
                }
                if (callBack != null)
                    callBack.onFail(error.networkResponse);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("api-key", api_key);
                return params;
            }

            ;
        };
        queue.add(stringRequest);
    }

    public static interface CallBackSearch {

        public void onResponse(int resultNumber, ArrayList<HashMap<String, String>> arrayListItems);

        public void onError(NetworkResponse networkResponse);
    }

    public void getRoute(@NonNull GeoPoint startPoint, @NonNull GeoPoint endPoint, ArrayList<GeoPoint> middle_points, final RoutingCallBack callBack) {

        String p;
        Map params = new HashMap<>();
        params.put("lat1", startPoint.getLatitude() + "");
        params.put("lon1", startPoint.getLongitude() + "");
        params.put("lat2", endPoint.getLatitude() + "");
        params.put("lon2", endPoint.getLongitude() + "");
        List<Map> temp_middle_points_array = new ArrayList<>();
        if (middle_points != null)
            for (int i = 0; i < middle_points.size(); i++) {
                Map temp_middle_points = new HashMap<>();
                temp_middle_points.put("lat", middle_points.get(i).getLatitude() + "");
                temp_middle_points.put("lon", middle_points.get(i).getLongitude() + "");
                temp_middle_points_array.add(temp_middle_points);
            }
        params.put("points", temp_middle_points_array);
        Gson gson = new Gson();
        p = gson.toJson(params);
        try {
            p = URLEncoder.encode(p, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "http://developers.parsijoo.ir/web-service/v1/map/?type=direction&p=" + p;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String wkt = "";
                        float totalDistance = (float) 0.0;
                        int totalTime = 0;
                        ArrayList<HashMap<String, String>> instructionList = new ArrayList<>();
                        JsonParser parser = new JsonParser();
                        JsonObject rootObj = parser.parse(response).getAsJsonObject();
                        if (rootObj.getAsJsonArray("result").size() > 0) {
                            JsonObject resultObj = (JsonObject) rootObj.getAsJsonArray("result").get(0);
                            wkt = resultObj.get("wkt").getAsString();
                            totalDistance = resultObj.get("totalDistance").getAsFloat();
                            totalTime = resultObj.get("totalTime").getAsInt();
                            JsonArray instructionListArray = (JsonArray) resultObj.getAsJsonArray("instructionList");
                            for (int i = 0; i < instructionListArray.size(); i++) {
                                HashMap<String, String> instruction = new HashMap<>();
                                instruction.put("name", instructionListArray.get(i).getAsJsonObject().get("name").getAsString());
                                instruction.put("distance", instructionListArray.get(i).getAsJsonObject().get("distance").getAsString());
                                instruction.put("time", instructionListArray.get(i).getAsJsonObject().get("time").getAsString());
                                instruction.put("text", instructionListArray.get(i).getAsJsonObject().get("text").getAsString());
                                instructionList.add(instruction);
                            }
                        }


                        if (callBack != null) {
                            RoutingDetail detail = new RoutingDetail()
                                    .setDistanceInMeter(totalDistance)
                                    .setTimeInMilis(totalTime)
                                    .setInstructionList(instructionList)
                                    .setPoints(WktParsers.lineStringToGeoPoint(wkt));
                            callBack.onSuccess(detail, ShapeUtil.createPolyLine(detail.getPoints()), getSelf());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null || error.networkResponse == null) {
                    return;
                }
                if (callBack != null)
                    callBack.onFail(error.networkResponse);
                String body;
                final String statusCode = String.valueOf(error.networkResponse.statusCode);
                try {
                    body = new String(error.networkResponse.data, "UTF-8");
                    System.out.println("sysosout " + body);
                } catch (UnsupportedEncodingException e) {
                    // exception
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("api-key", api_key);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void getRoute(GeoPoint startPoint, GeoPoint endPoint, final RoutingCallBack callBack) {

        getRoute(startPoint, endPoint, null, callBack);
    }


    @Deprecated
    public void getDirection(double lat1, double lon1, double lat2, double lon2, List<HashMap<String, Double>> middle_points, final CallBackDirection callBackDirection) {

        this.callBackDirection = callBackDirection;
        String p;
        Map params = new HashMap<>();
        params.put("lat1", lat1 + "");
        params.put("lon1", lon1 + "");
        params.put("lat2", lat2 + "");
        params.put("lon2", lon2 + "");
        List<Map> temp_middle_points_array = new ArrayList<>();
        if (middle_points != null)
            for (int i = 0; i < middle_points.size(); i++) {
                Map temp_middle_points = new HashMap<>();
                temp_middle_points.put("lat", middle_points.get(i).get("lat") + "");
                temp_middle_points.put("lon", middle_points.get(i).get("lon") + "");
                temp_middle_points_array.add(temp_middle_points);
            }
        params.put("points", temp_middle_points_array);
        Gson gson = new Gson();
        p = gson.toJson(params);
        try {
            p = URLEncoder.encode(p, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "http://developers.parsijoo.ir/web-service/v1/map/?type=direction&p=" + p;
        System.out.println("sysosout url " + url);
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String wkt = "";
                        float totalDistance = (float) 0.0;
                        int totalTime = 0;
                        ArrayList<HashMap<String, String>> instructionList = new ArrayList<>();
                        JsonParser parser = new JsonParser();
                        JsonObject rootObj = parser.parse(response).getAsJsonObject();
                        if (rootObj.getAsJsonArray("result").size() > 0) {
                            JsonObject resultObj = (JsonObject) rootObj.getAsJsonArray("result").get(0);
                            wkt = resultObj.get("wkt").getAsString();
                            totalDistance = resultObj.get("totalDistance").getAsFloat();
                            totalTime = resultObj.get("totalTime").getAsInt();
                            JsonArray instructionListArray = (JsonArray) resultObj.getAsJsonArray("instructionList");
                            for (int i = 0; i < instructionListArray.size(); i++) {
                                HashMap<String, String> instruction = new HashMap<>();
                                instruction.put("name", instructionListArray.get(i).getAsJsonObject().get("name").getAsString());
                                instruction.put("distance", instructionListArray.get(i).getAsJsonObject().get("distance").getAsString());
                                instruction.put("time", instructionListArray.get(i).getAsJsonObject().get("time").getAsString());
                                instruction.put("text", instructionListArray.get(i).getAsJsonObject().get("text").getAsString());
                                instructionList.add(instruction);
                            }
                        }
                        callBackDirection.onResponse(wkt, totalDistance, totalTime, instructionList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null || error.networkResponse == null) {
                    return;
                }
                callBackDirection.onError(error.networkResponse);
                String body;
                final String statusCode = String.valueOf(error.networkResponse.statusCode);
                try {
                    body = new String(error.networkResponse.data, "UTF-8");
                    System.out.println("sysosout " + body);
                } catch (UnsupportedEncodingException e) {
                    // exception
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("api-key", api_key);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public CoordinateDetail getVisibleCorners() {

        int height = getSelf().getHeight();
        int width = getSelf().getWidth();
        IGeoPoint topLeft = getSelf().getMapView().getProjection().fromPixels(0, 0);
        IGeoPoint topRight = getSelf().getMapView().getProjection().fromPixels(width, 0);
        IGeoPoint bottomRight = getSelf().getMapView().getProjection().fromPixels(width, height);
        IGeoPoint bottomLeft = getSelf().getMapView().getProjection().fromPixels(0, height);

        return new CoordinateDetail()
                .setTopRight(topRight)
                .setTopLeft(topLeft)
                .setBottomLeft(bottomLeft)
                .setBottomRight(bottomRight)
                .setCenter(mapView.getProjection().getCurrentCenter());
    }

    public static interface CallBackDirection {
        public void onResponse(String wkt, float totalDistance, int totalTime, ArrayList<HashMap<String, String>> instructionList);

        public void onError(NetworkResponse networkResponse);
    }

    public Viewer getSelf() {
        return this;

    }
}
