package media.socialapp.sildren;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import media.socialapp.sildren.DataModels.MarkerItem;
import media.socialapp.sildren.DataModels.Route;
import media.socialapp.sildren.utilities.DirectionFinder;
import media.socialapp.sildren.utilities.DirectionFinderListener;
import media.socialapp.sildren.utilities.OnMarkerSetListener;
import media.socialapp.sildren.utilities.Permissions;

public class PathMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, DirectionFinderListener {
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private static final String TAG = "MapsActivity";
    private static final String EXTRA = "NextActivity";
    private static final float DEFAULT_ZOOM = 15;

    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1000;
    private int PLACE_CODE = 2;
    private final int START_PLACE_CODE = 10;
    private final int END_PLACE_CODE = 20;

    ArrayList<Marker> markers = new ArrayList<Marker>();
    static final int POLYGON_POINTS = 5;
    Polygon shape;

    public static MarkerItem markerItem = new MarkerItem();
    private List<MarkerItem> markerItems;
    private DatabaseReference markerRef;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private OnMarkerSetListener onMarkerSetListener;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();


    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    private EditText startEdit;
    private EditText endEdit;
    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition;
    private LatLng mDefaultLocation = new LatLng(37.285812, 127.045423);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServicesAvailable()) {
//            Toast.makeText(this, "Perfect!!!", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_path_map);
            initMap();
        } else {
            // No Google Maps Layout
        }

    }

    private void initMap() {
//        com.google.android.gms.maps.MapFragment mapFragment = (com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        markerRef = database.getReference("markers");

    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cant connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        startEdit = (EditText) findViewById(R.id.start_edit);
        endEdit = (EditText) findViewById(R.id.end_edit);
        Button search = (Button) findViewById(R.id.path_button);


        startEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
//                            placeAutoComplete();
                            PLACE_CODE = START_PLACE_CODE;
                        }
                    }, 1);
                }
            }
        });

        endEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
//                            placeAutoComplete();
                            PLACE_CODE = END_PLACE_CODE;
                        }
                    }, 1);
                }
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();

            }
        });

        if (mGoogleMap != null) {

            setMarkers("Local");


            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    PathMapActivity.this.setMarker("Local", latLng.latitude, latLng.longitude);
                }
            });


            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                    Geocoder gc = new Geocoder(PathMapActivity.this, Locale.KOREAN);
                    LatLng ll = marker.getPosition();
                    double lat = ll.latitude;
                    double lng = ll.longitude;
                    List<Address> list = null;
                    try {
                        list = gc.getFromLocation(lat, lng, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Address add = list.get(0);
                    marker.setTitle(add.getLocality());
                    marker.showInfoWindow();

                }
            });

            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Log.d(TAG, "onMarkerClick");
                    double mLat = marker.getPosition().latitude;
                    double mLng = marker.getPosition().longitude;
//                    markerItem = new MarkerItem();
//                    markerItem.setMarker(marker);
//                    Log.d(TAG, "extra - " + getIntent().getStringExtra(EXTRA));
//                    if (EXTRA.equals(getIntent().getStringExtra("Calling"))) {
//                        finish();
//                    }
                    getDeviceLocation();
                    double mLat2 = mLastKnownLocation.getLatitude();
                    double mLng2 = mLastKnownLocation.getLongitude();
                    Geocoder gc = new Geocoder(PathMapActivity.this, Locale.KOREAN);

                    List<Address> list = null;
                    try {
                        list = gc.getFromLocation(mLat, mLng, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address add = list.get(0);

                    List<Address> list2 = null;
                    try {
                        list2 = gc.getFromLocation(mLat2, mLng2, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address add2 = list2.get(0);

//                    try {
//                        new DirectionFinder(this, add2.getAddressLine(0), add.getAddressLine(0)).execute();
//
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
                    requestDirection(add2.getAddressLine(0), add.getAddressLine(0));

                    return false;
                }
            });


            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window, null);

                    TextView tvLocality = (TextView) v.findViewById(R.id.tv_locality);
                    TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
                    TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
                    TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);
                    //ImageView imageView = (ImageView) v.findViewById(R.id.info_image);

                    LatLng ll = marker.getPosition();
                    tvLocality.setText(marker.getTitle());

                    Geocoder gc = new Geocoder(PathMapActivity.this, Locale.KOREAN);
                    double lat = ll.latitude;
                    double lng = ll.longitude;
                    List<Address> list = null;
                    try {
                        list = gc.getFromLocation(lat, lng, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Address add = list.get(0);
                    tvLat.setText(marker.getSnippet());
//                    tvLat.setText("Latitude: " + ll.latitude);
//                    tvLng.setText("Longitude: " + ll.longitude);
//                    tvLat.setVisibility(View.GONE);
//                    tvLng.setVisibility(View.GONE);
                    tvSnippet.setText(add.getAddressLine(0));

                    return v;
                }
            });
        }


        goToLocationZoom(37.285812, 127.045423, 15);


        if (checkPermissionsArray(Permissions.PERMISSIONS)) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();


    }

    private void placeAutoComplete() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(PathMapActivity.this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    public void onPlaceSelected(Place place) {

        if (PLACE_CODE == START_PLACE_CODE) {
            startEdit.setText(place.getName());
        }

        if (PLACE_CODE == END_PLACE_CODE) {
            endEdit.setText(place.getName());

        }

    }


    private void goToLocation(double lat, double lng) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        mGoogleMap.moveCamera(update);
    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);


    }

    public void geoLocate(View view) throws IOException {

        EditText et = (EditText) findViewById(R.id.editText);
        String location = et.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1);
        Address address = list.get(0);
        String locality = address.getLocality();

        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

        double lat = address.getLatitude();
        double lng = address.getLongitude();
        goToLocationZoom(lat, lng, 15);

        setMarker(locality, lat, lng);

    }

    public void setMarkers(final String locality) {
        Log.d(TAG, "setMarkers init");
        if (markers.size() == POLYGON_POINTS) {
            removeEverything();
        }


        if (markers.size() == POLYGON_POINTS) {
            drawPolygon();
        }
        markerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                markerItems = new ArrayList<MarkerItem>();

                Iterable<DataSnapshot> groups = dataSnapshot.getChildren();
                for (DataSnapshot e : groups) {
                    String title = (String) e.child("title").getValue();
                    Log.d(TAG, "title - " + title);
                    String name = (String) e.child("name").getValue();
                    Log.d(TAG, "name - " + name);
                    double lat = (double) e.child("lat").getValue();
                    Log.d(TAG, "lat - " + lat);
                    double lng = (double) e.child("lon").getValue();
                    Log.d(TAG, "lng - " + title);

                    MarkerOptions options = new MarkerOptions()
                            .title(title)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                            .position(new LatLng(lat, lng))
                            .snippet(name);
                    markers.add(mGoogleMap.addMarker(options));
                    Log.d(TAG, "markers size - " + markers.size());
                }
                if (markers.size() == POLYGON_POINTS) {
                    drawPolygon();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


//    Circle circle;

//    Marker marker1;
//    Marker marker2;
//    Polyline line;


    private void setMarker(String locality, double lat, double lng) {
//        if(marker != null){
//            removeEverything();
//        }

        if (markers.size() == POLYGON_POINTS) {
            removeEverything();
        }

        MarkerOptions options = new MarkerOptions()
                .title(locality)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                .position(new LatLng(lat, lng))
                .snippet("I am Here");

        markers.add(mGoogleMap.addMarker(options));


        if (markers.size() == POLYGON_POINTS) {
            drawPolygon();
        }

//        if(marker1 == null) {
//            marker1 = mGoogleMap.addMarker(options);
//        } else if(marker2 == null) {
//            marker2 = mGoogleMap.addMarker(options);
//            drawLine();
//        } else {
//            removeEverything();
//            marker1 = mGoogleMap.addMarker(options);
//        }

//        circle = drawCircle(new LatLng(lat, lng));

    }

    private void drawPolygon() {
        PolygonOptions options = new PolygonOptions()
                .fillColor(0x330000FF)
                .strokeWidth(3)
                .strokeColor(Color.RED);

        for (int i = 0; i < POLYGON_POINTS; i++) {
            options.add(markers.get(i).getPosition());
        }
        shape = mGoogleMap.addPolygon(options);

    }

    private void removeEverything() {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
        shape.remove();
        shape = null;

    }

    private void getDeviceLocation() {
    /*
     * Before getting the device location, you must check location
     * permission, as described earlier in the tutorial. Then:
     * Get the best and most recent location of the device, which may be
     * null in rare cases when a location is not available.
     */

        if (checkPermissionsArray(Permissions.PERMISSIONS)) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            mGoogleMap.setMyLocationEnabled(true);
        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }


        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), 15));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }
//    private void drawLine() {
//
//        PolylineOptions options = new PolylineOptions()
//                .add(marker1.getPosition())
//                .add(marker2.getPosition())
//                .color(Color.BLUE)
//                .width(3);
//
//        line = mGoogleMap.addPolyline(options);
//    }

//    private Circle drawCircle(LatLng latLng) {
//
//        CircleOptions options = new CircleOptions()
//                .center(latLng)
//                .radius(1000)
//                .fillColor(0x33FF0000)
//                .strokeColor(Color.BLUE)
//                .strokeWidth(3);
//
//        return mGoogleMap.addCircle(options);
//    }

//    private void removeEverything(){
//        marker1.remove();
//        marker1 = null;
//        marker2.remove();
//        marker2 = null;
//        line.remove();
////        circle.remove();
////        circle = null;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapTypeNone:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    LocationRequest mLocationRequest;

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(this, "Cant get current location", Toast.LENGTH_LONG).show();
        } else {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            mGoogleMap.animateCamera(update);
        }
    }

    public void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                PathMapActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    public boolean checkPermissionsArray(String[] permissions) {
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for (int i = 0; i < permissions.length; i++) {
            String check = permissions[i];
            if (!checkPermissions(check)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkPermissions(String permission) {
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(PathMapActivity.this, permission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        } else {
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }

    public void sendRequest() {
//        Uri uri = Uri.parse("https://maps.googleapis.com/maps/api/directions/json?origin=Brooklyn&destination=Queens&mode=transit&key=AIzaSyBjll9Cro-BivWa4MEgfTVQbaDUINajF1Q");
//        Intent it = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(it);

        String origin = startEdit.getText().toString();
        String destination = endEdit.getText().toString();

        if (origin.isEmpty()) {
            return;
        }
        if (destination.isEmpty()) {
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public void requestDirection(String add1, String add2) {
        try {
            new DirectionFinder(this, add1, add2).execute();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.duration_textView)).setText(route.duration.text);
            ((TextView) findViewById(R.id.distance_textView)).setText(route.distance.text);

            originMarkers.add(mGoogleMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mGoogleMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(R.color.colorAccent).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mGoogleMap.addPolyline(polylineOptions));
        }

    }

}


