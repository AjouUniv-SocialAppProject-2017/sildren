package media.socialapp.sildren;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nostra13.universalimageloader.core.ImageLoader;

import media.socialapp.sildren.DataModels.Photo;
import media.socialapp.sildren.utilities.MainfeedListAdapter;
import media.socialapp.sildren.utilities.UniversalImageLoader;

public class MainActivity extends AppCompatActivity implements
        MainfeedListAdapter.OnLoadMoreItemsListener {

    private static String TAG = "MainActivity";


    private Context mContext = MainActivity.this;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    public static Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Log.d(TAG, "onCreate: starting.");
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mFrameLayout = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);

        setupFirebaseAuth();

        initImageLoader();
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(new Bundle());
        openFragmentByContent(homeFragment,null);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    HomeFragment homeFragment = new HomeFragment();
                    homeFragment.setArguments(new Bundle());
                    openFragmentByContent(homeFragment,null);
                    return true;
                case R.id.navigation_camera:
                    intent = new Intent(getApplicationContext(), PostActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_dashboard:
//                    intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent = new Intent(getApplicationContext(), ChatGroupActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_map:
//                    MapFragment mapFragment = new MapFragment();
//                    mapFragment.setArguments(new Bundle());
//                    openFragmentByContent(mapFragment,null);

                    intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }

    };


    @Override
    public void onLoadMoreItems() {
        Log.d(TAG, "onLoadMoreItems: displaying more photos");
        HomeFragment fragment = (HomeFragment) getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + mViewPager.getCurrentItem());
        if (fragment != null) {
            fragment.displayMorePhotos();
        }
    }


    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }


    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if (user == null) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                checkCurrentUser(user);

                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }


    private void openFragmentByContent(final Fragment fragment ,String string ) {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.content, fragment);
        transaction.addToBackStack(string);
        transaction.commit();
    }

    private void openFragmentByContainer( Fragment fragment,String string ) {
        fragmentManager = getSupportFragmentManager();
        fragment.setArguments(fragment.getArguments());
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, fragment);
        transaction.addToBackStack(string);
        transaction.commit();
        mBundle = fragment.getArguments();

        Log.d(TAG, "openFragmentByContainer : " + fragment.getArguments());
        hideLayout();
    }

    public void onCommentThreadSelected(Photo photo, String callingActivity) {
        Log.d(TAG, "onCommentThreadSelected: selected a coemment thread");

        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putString(getString(R.string.main_activity), getString(R.string.main_activity));
        fragment.setArguments(args);

        openFragmentByContainer(fragment, "HomeFragment" );
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.container, fragment);
//        transaction.addToBackStack(getString(R.string.view_comments_fragment));
//        transaction.commit();

    }

    public void onInfoSelected(Photo photo, String callingActivity) {
        Log.d(TAG, "onCommentThreadSelected: selected a coemment thread");

        ViewInfoFragment fragment = new ViewInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
//        args.putString("HomeFragment", "HomeFragment");
        fragment.setArguments(args);

        Log.d(TAG,"Bundling args :" + photo);
        Log.d(TAG,"Bundled args :" + args);

        openFragmentByContainer(fragment, "HomeFragment");
//        transaction = getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.container, fragment);
//        transaction.commit();

    }

    public void showLayout() {
        Log.d(TAG, "hideLayout: showing layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }

    public void hideLayout() {
        Log.d(TAG, "hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mFrameLayout.getVisibility() == View.VISIBLE){
            showLayout();
        }
    }





}
