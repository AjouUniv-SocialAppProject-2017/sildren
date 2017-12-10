package media.socialapp.sildren;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import media.socialapp.sildren.utilities.SectionsPagerAdapter;

public class PostActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 5;
    private static final int REQ_CODE_SELECT_IMAGE = 100;
    private static final String TAG = "PostActivity";
    private ViewPager mViewPager;

    private Button galleryBtn;
    private Button cameraBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Log.d(TAG, "onCreate");
        setupViewPager();
    }

    private void setupViewPager() {
        Log.d(TAG, "SetupViewPager executed.");

        final SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new PhotoFragment());

        cameraBtn = (Button) findViewById(R.id.cameraButton);
        galleryBtn = (Button) findViewById(R.id.galleryButton);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mViewPager.setAdapter(adapter);



        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });
//        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsBottom);
//        tabLayout.setupWithViewPager(mViewPager);
//
//        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
//        tabLayout.getTabAt(1).setText(getString(R.string.photo));
//
//
//
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                if (tab.getText() == "GALLERY") {
//                    Intent intent = new Intent(Intent.ACTION_PICK);
//                    intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
//                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
//                } else {
//                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
//                }
//
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE) {
            Log.d(TAG, "onActivityResult: done taking a photo.");
            Log.d(TAG, "onActivityResult: attempting to navigate to final share screen.");

            Bitmap bitmap = null;
            try {
                bitmap = (Bitmap) data.getExtras().get("data");
            } catch (NullPointerException e) {
                Log.d(TAG, " data.getExtras().get('data' is null");
            }

            try {
                Log.d(TAG, "onActivityResult: received new bitmap from camera: " + bitmap);
                Intent intent = new Intent(getApplicationContext(), NextActivity.class);
                intent.putExtra(getString(R.string.selected_bitmap), bitmap);
                startActivity(intent);
            } catch (NullPointerException e) {
                Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
            }

        }
    }

}
