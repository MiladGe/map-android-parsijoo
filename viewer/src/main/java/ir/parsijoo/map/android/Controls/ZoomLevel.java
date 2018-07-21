package ir.parsijoo.map.android.Controls;

public enum ZoomLevel {

    WorldWide(1), Wide(4), Country_1(5), Country_2(6)
    ,Country_3(7),Country_4(8),Province_5(9),Province_6(10),Province_7(11)
    ,City_1(12),City_2(13),City_3(14),Road_1(15),Road_2(16),Alley_1(17),Alley_2(18),Alley_3(19);

    private int zoomLevel;

    ZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public int get() {
        return this.zoomLevel;
    }
}
