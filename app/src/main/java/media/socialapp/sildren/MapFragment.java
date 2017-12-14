package media.socialapp.sildren;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

/**
 * Created by ryu on 2017. 11. 13..
 */


public class MapFragment extends Fragment {
    private static final String TAG = "MapFragment";
    private NMapContext mMapContext;
    private static final String CLIENT_ID = "WASU1Lpw1zXoC3ROcGWo";// 애플리케이션 클라이언트 아이디 값

    private NMapView.OnMapStateChangeListener onMapStateChangeListener;
    private NMapView.OnMapViewTouchEventListener onMapViewTouchEventListener;
    private NMapController mMapController;
    private NMapResourceProvider mMapResourceProvider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapContext =  new NMapContext(super.getActivity());
        mMapResourceProvider = new NMapResourceProvider(getContext()) {
            @Override
            protected int findResourceIdForMarker(int i, boolean b) {
                return 0;
            }

            @Override
            protected Drawable getDrawableForMarker(int i, boolean b, NMapOverlayItem nMapOverlayItem) {
                return null;
            }

            @Override
            public Drawable getCalloutBackground(NMapOverlayItem nMapOverlayItem) {
                return null;
            }

            @Override
            public String getCalloutRightButtonText(NMapOverlayItem nMapOverlayItem) {
                return null;
            }

            @Override
            public Drawable[] getCalloutRightButton(NMapOverlayItem nMapOverlayItem) {
                return new Drawable[0];
            }

            @Override
            public Drawable[] getCalloutRightAccessory(NMapOverlayItem nMapOverlayItem) {
                return new Drawable[0];
            }

            @Override
            public int[] getCalloutTextColors(NMapOverlayItem nMapOverlayItem) {
                return new int[0];
            }

            @Override
            public Drawable[] getLocationDot() {
                return new Drawable[0];
            }

            @Override
            public Drawable getDirectionArrow() {
                return null;
            }

            @Override
            public int getParentLayoutIdForOverlappedListView() {
                return 0;
            }

            @Override
            public int getOverlappedListViewId() {
                return 0;
            }

            @Override
            public int getLayoutIdForOverlappedListView() {
                return 0;
            }

            @Override
            public void setOverlappedListViewLayout(ListView listView, int i, int i1, int i2) {

            }

            @Override
            public int getListItemLayoutIdForOverlappedListView() {
                return 0;
            }

            @Override
            public int getListItemTextViewId() {
                return 0;
            }

            @Override
            public int getListItemTailTextViewId() {
                return 0;
            }

            @Override
            public int getListItemImageViewId() {
                return 0;
            }

            @Override
            public int getListItemDividerId() {
                return 0;
            }

            @Override
            public void setOverlappedItemResource(NMapPOIitem nMapPOIitem, ImageView imageView) {

            }
        };
        mMapContext.onCreate();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NMapView mMapView = (NMapView)getView().findViewById(R.id.mapView);

        onMapStateChangeListener = new NMapView.OnMapStateChangeListener() {
            @Override
            public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
                ;
            }

            @Override
            public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {

            }

            @Override
            public void onMapCenterChangeFine(NMapView nMapView) {

            }

            @Override
            public void onZoomLevelChange(NMapView nMapView, int i) {

            }

            @Override
            public void onAnimationStateChange(NMapView nMapView, int i, int i1) {

            }
        };

        onMapViewTouchEventListener = new NMapView.OnMapViewTouchEventListener() {
            @Override
            public void onLongPress(NMapView nMapView, MotionEvent motionEvent) {

            }

            @Override
            public void onLongPressCanceled(NMapView nMapView) {

            }

            @Override
            public void onTouchDown(NMapView nMapView, MotionEvent motionEvent) {

            }

            @Override
            public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {

            }

            @Override
            public void onScroll(NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1) {

            }

            @Override
            public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent) {

            }
        };


        mMapView.setClientId(CLIENT_ID);// 클라이언트 아이디 설정
        mMapContext.setupMapView(mMapView);


        mMapView.setClickable(true);

        mMapView.setOnMapStateChangeListener(onMapStateChangeListener);
        mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
        mMapView.setBuiltInAppControl(true);
        mMapController = mMapView.getMapController();
    }
    @Override
    public void onStart(){
        super.onStart();
        mMapContext.onStart();
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapContext.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mMapContext.onPause();
    }
    @Override
    public void onStop() {
        mMapContext.onStop();
        super.onStop();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onDestroy() {
        mMapContext.onDestroy();
        super.onDestroy();
    }
}