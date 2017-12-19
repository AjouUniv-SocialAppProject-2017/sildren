package media.socialapp.sildren;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private RelativeLayout mRelativeLayout;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private ImageView logoutMenu;
    private Button logoutBtn;

    private GoogleSignInClient mGoogleSignInClient;

    public static Bundle mBundle;
    private GoogleApiClient mGoogleApiClient;

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
        logoutMenu = (ImageView) findViewById(R.id.logout_menu);
//        logoutBtn = (Button) findViewById(R.id.logout_btn);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        setupFirebaseAuth();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        initImageLoader();
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(new Bundle());
        openFragmentByContent(homeFragment, null);
        logoutMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.logout_menu:
                        CoordinatorLayout mainLayout = (CoordinatorLayout) findViewById(R.id.main_coord_layout);
                        Log.d(TAG, "logout_menu - clicked");
                        LayoutInflater inflater = (LayoutInflater)
                                getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.popup_logout, null);
                        AppCompatButton logoutBtn = (AppCompatButton) popupView.findViewById(R.id.logout_btn);
                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true; // lets taps outside the popup also dismiss it
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                        logoutBtn = (AppCompatButton) popupView.findViewById(R.id.logout_btn);
                        logoutBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG, "logout_btn - clicked");
                                signOut();
                            }
                        });
                        // show the popup window
                        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
                        dimBehind(popupWindow);
                        // dismiss the popup window when touched


                        popupView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                popupWindow.dismiss();
                                return true;
                            }
                        });
                        break;
//                    case R.id.logout_btn:
//                        Log.d(TAG, "logout_btn - clicked");
////                        mAuth.signOut();
//                          revokeAccess();
//                        signOut();
//                        Intent intent = new Intent(getApplicationContext(),InitActivity.class);
//                        startActivity(intent);
//                        mAuth.signOut();
//                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
//
//                        if(mAuth.getCurrentUser()!=null) {
//                            Log.d(TAG,"mAuth.getCurrentUser() - not null");
//
//                        }
                }
            }
        });

//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(v.getId() == R.id.logout_btn){
//                    Log.d(TAG, "logout_btn - clicked");
//                    revokeAccess();
//                }
//
//
//            }
//        });
    }
//
//    public void onLogoutBtnClick(){
//        Log.d(TAG,"logoutBTn CLICK");
//        revokeAccess();
//    }

    private void signOut() {
        // Firebase sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getApplicationContext(), InitActivity.class);
                        startActivity(intent);
                    }
                });
        mAuth.signOut();

        // Google sign out
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient);




    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getApplicationContext(), InitActivity.class);
                        startActivity(intent);
                    }
                });
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
                    openFragmentByContent(homeFragment, null);
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

                    intent = new Intent(getApplicationContext(), PathMapActivity.class);
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


    private void openFragmentByContent(final Fragment fragment, String string) {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.content, fragment);
        transaction.addToBackStack(string);
        transaction.commit();
    }

    private void openFragmentByContainer(Fragment fragment, String string) {
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

        openFragmentByContainer(fragment, "HomeFragment");
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

        Log.d(TAG, "Bundling args :" + photo);
        Log.d(TAG, "Bundled args :" + args);

        openFragmentByContainer(fragment, "HomeFragment");
//        transaction = getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.container, fragment);
//        transaction.commit();

    }

    public void onParticipatesClicked(Photo photo) {
        String url = photo.getImage_path();
        String title = photo.getTitle();
        String name = photo.getName();
//        String userID = mAuth.getCurrentUser().getUid();

//        myRef.child(mContext.getString(R.string.dbname_users))
//                .child(userID)
//                .setValue(title);
        myRef.child("chat_groups").child(title).child("photo").setValue(url);
        myRef.child("chat_groups").child(title).child("name").setValue(name);
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
        if (mFrameLayout.getVisibility() == View.VISIBLE) {
            showLayout();
        }
    }

    public static void dimBehind(PopupWindow popupWindow) {
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;
        wm.updateViewLayout(container, p);
    }


}
