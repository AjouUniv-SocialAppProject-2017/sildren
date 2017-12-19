package media.socialapp.sildren.DataModels;

import com.google.android.gms.maps.model.Marker;

import media.socialapp.sildren.utilities.OnMarkerSetListener;

public class MarkerItem {


    private double lat;
    private double lon;
    private String title;
    private String name;
    private Marker marker;
    private OnMarkerSetListener onMarkerSetListener;

    public MarkerItem() {
    }

    public MarkerItem(String title, String name, double lat, double lon) {
        this.title = title;
        this.name = name;
        this.lat = lat;
        this.lon = lon;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }


    public void setOnMarkerSetListener(OnMarkerSetListener listener) {
        this.onMarkerSetListener = listener;
    }

    public OnMarkerSetListener getOnMarkerSetListener() {
        return onMarkerSetListener;
    }

    public void fetchMarker(){
        if(onMarkerSetListener != null){
            onMarkerSetListener.onMarkerSet(marker);
        }
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
