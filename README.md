# map-android-parsijoo
<p dir="rtl">
نقشه پارسی جو در اپلیکیشن های اندروید
</p>


<p dir="rtl">
        کد زیر را در قسمت لایوت مربوط به اکتیویتی خود اضافه کنید:
</p>

<div class="highlight highlight-text-xml">
<pre>&lt;<span class="pl-ent">ir.parsijoo.map.android.Viewer</span>
    android:id="@+id/mapview"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:api_key="*********"  /&gt;</pre></div>


<p dir="rtl">
      به جای ********* می بایست کلید api مربوط به نقشه پارسی جو را وارد نمایید. به منظور دریافت کلید api به لینک زیر مراجعه نمایید :
</p>
<p>
        http://developers.parsijoo.ir/dashboard/
</p>

<p dir="rtl">
    به منظورآموزش استفاده از تمامی توابع api نقشه پارسی جو به لینک زیر مراجعه نمایید:
</p>
<p>
        http://developers.parsijoo.ir/service/map
</p>
<p dir="rtl">
        دسترسی های مورد نیاز
</p>

<div class="highlight highlight-text-xml"><pre>&lt;<span class="pl-ent">uses-permission</span> <span class="pl-e">android</span><span class="pl-e">:</span><span class="pl-e">name</span>=<span class="pl-s"><span class="pl-pds">"</span>android.permission.ACCESS_FINE_LOCATION<span class="pl-pds">"</span></span>/&gt;
&lt;<span class="pl-ent">uses-permission</span> <span class="pl-e">android</span><span class="pl-e">:</span><span class="pl-e">name</span>=<span class="pl-s"><span class="pl-pds">"</span>android.permission.INTERNET<span class="pl-pds">"</span></span> /&gt;
&lt;<span class="pl-ent">uses-permission</span> <span class="pl-e">android</span><span class="pl-e">:</span><span class="pl-e">name</span>=<span class="pl-s"><span class="pl-pds">"</span>android.permission.ACCESS_NETWORK_STATE<span class="pl-pds">"</span></span>  /&gt;
&lt;<span class="pl-ent">uses-permission</span> <span class="pl-e">android</span><span class="pl-e">:</span><span class="pl-e">name</span>=<span class="pl-s"><span class="pl-pds">"</span>android.permission.WRITE_EXTERNAL_STORAGE<span class="pl-pds">"</span></span> /&gt;</pre></div>
<p dir="rtl">
        برای اندروید 6 به بالا(API level 23)، می بایست مجوز ها به صورت داینامیک از کاربر دریافت گردد : <br/>

</p>
<p dir="rtl">
       تنظیمات اولیه نقشه :
</p>
<div class="highlight highlight-text-xml">
<pre><span class="pl-ent">
viewer.setStartPosition(new GeoPoint(31.89739, 54.35119), ZoomLevel.City_1);//ست کردن نقطه آغازی نقشه با زوم
viewer.setFirstLoadCallBack(this);//کالبکی برای متوجه شدن از کامل لود شدن نقشه ( برای شروع تغییر زوم ، مسیر یابی و ... )
viewer.setOnMapClickListener()//گرفتن کلیک های نقشه
viewer.enableRotateGesture();//فعال کردن قابلیت چرخش زاویه نقشه
viewer.animateToPosition(GeoPoint point);
</pre></div>
<p dir="rtl">
       رسم PolyLine , Polygon , Marker :
</p>
<div class="highlight highlight-text-xml">
<pre><span class="pl-ent">
viewer.drawPolygon(polygon,"tag");
viewer.drawPolyLine(polyline, "tag");
viewer.addMarker(marker);
</pre></div><p dir="rtl">
       حذف کردن PolyLine , Polygon , Marker :
</p>
<pre><span class="pl-ent">
 viewer.removeShape(viewer.findItemByTag(route1));//حذف کردن OverLay با استفاده از تگ آن
 viewer.removeShape(polygon);//حذف بااستفاده ازآبجکت OverLay
 viewer.removeShape(polyline);//حذف بااستفاده ازآبجکت OverLay
 viewer.removeAllShapes();//پاک کردن نقشه(به جز مارکرها)
 viewer.removeMarker(marker);//حذف مارکر
 viewer.removeAllMarkers()
 </pre></div><p dir="rtl">

<p dir="rtl">
       زوم کردن به یک BoundingBox :
</p>
<div class="highlight highlight-text-xml">
<pre><span class="pl-ent"> private void zoomToBound() {


        BoundingBox boundingBox = new BoundingBoxBuilder()
                .addPoint(new GeoPoint(31.91551, 54.32237))
                .addPoint(new GeoPoint(31.90240, 54.31009))
                .addPoint(new GeoPoint(31.89183, 54.32777))
                .addPoint(new GeoPoint(31.89627, 54.33721))
                .create();

        viewer.zoomToBoundingBox(boundingBox, true);

}</pre></div>
<p dir="rtl">
       جستجو در نقشه :
</p>
<div class="highlight highlight-text-xml">
<pre><span class="pl-ent"> private void searchPlace(String query, String city) {
        viewer.searchPlace(query, city, new SearchResultCallBack() {
            @Override
            public void onResult(ArrayList&lt;Place&gt; places) {

                if (places.size() > 0) {

                    Toast.makeText(MainActivity.this, "" + places.get(0).getAddress(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(NetworkResponse error) {

            }
        });
}</pre></div>

<p dir="rtl">
       گرفتن مشخصات یک نقطه (Reverse GeoCoding) :
</p>
<div class="highlight highlight-text-xml">
<pre><span class="pl-ent"> private void getAddress(@NonNull GeoPoint point) {
        viewer.getAddress(point.getLatitude(), point.getLongitude(), new LocationDetailCallBack() {
            @Override
            public void onResult(LocationDetail locationDetail) {

            }

            @Override
            public void onFail(NetworkResponse response) {

            }
        });
}</pre></div>
<p dir="rtl">
       مسیر یابی :
</p>
<div class="highlight highlight-text-xml">
<pre><span class="pl-ent">
ArrayList&lt;GeoPoint&gt; middle_points = new ArrayList&lt;&gt;();
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
});</pre></div>
