package media.socialapp.sildren;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import media.socialapp.sildren.DataModels.MarkerItem;
import media.socialapp.sildren.utilities.FirebaseMethods;
import media.socialapp.sildren.utilities.OnMarkerSetListener;
import media.socialapp.sildren.utilities.UniversalImageLoader;

public class NextActivity extends AppCompatActivity implements OnMarkerSetListener {

    private static final String TAG = "NextActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    private EditText mCaption;
    private EditText mTitle;
    private EditText mDate;
    private EditText mStartTime;
    private EditText mEndTime;
    private EditText mLocation;
    private EditText mRecruit;
    private EditText mContent;
    private ImageButton locationBtn;

    private String mAppend = "file:/";
    private int imageCount = 0;
    private String imgUrl;
    private Bitmap bitmap;
    private Intent intent;
    private Calendar calendar;
    private MarkerItem markerItem;
    private double latitude;
    private double longitude;
    private String str = "";
    private List<Address> addressList;
    private OnMarkerSetListener onMarkerSetListener;
    private double mLat;
    private double mLng;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mFirebaseMethods = new FirebaseMethods(NextActivity.this);
        mCaption = (EditText) findViewById(R.id.caption);
        mTitle = (EditText) findViewById(R.id.field_title);
        mLocation = (EditText) findViewById(R.id.field_location);
        mDate = (EditText) findViewById(R.id.field_date);
        mStartTime = (EditText) findViewById(R.id.field_start_time);
        mEndTime = (EditText) findViewById(R.id.field_end_time);
        mRecruit = (EditText) findViewById(R.id.field_recruit);
        mContent = (EditText) findViewById(R.id.field_content);
        locationBtn = (ImageButton) findViewById(R.id.info_location_button);

        calendar = Calendar.getInstance();


        setupFirebaseAuth();

        ImageView backArrow = (ImageView) findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the activity");
                finish();
            }
        });


        TextView share = (TextView) findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");
                //upload the image to firebase
                Toast.makeText(NextActivity.this, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();
                String caption = mCaption.getText().toString();
                String title = mTitle.getText().toString();
                String location = mLocation.getText().toString();
                String date = mDate.getText().toString();
                String startTime = mStartTime.getText().toString();
                String endTime = mEndTime.getText().toString();
                String recruit = mRecruit.getText().toString();
                String content = mContent.getText().toString();
                String name = mAuth.getCurrentUser().getDisplayName();

                MarkerItem markerItem = new MarkerItem(title, name, latitude, longitude);
                myRef.child("markers").child(title).setValue(markerItem);
                if (intent.hasExtra(getString(R.string.selected_image))) {
                    imgUrl = intent.getStringExtra(getString(R.string.selected_image));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, title,
                            location, date, startTime, endTime, recruit, content, imageCount, imgUrl, null);
                } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
                    bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, title,
                            location, date, startTime, endTime, recruit, content, imageCount, null, bitmap);
                }
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel();
            }

        };

        final TimePickerDialog.OnTimeSetListener startTime = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateStartTimeLabel();
            }
        };

        final TimePickerDialog.OnTimeSetListener endTime = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateEndTimeLabel();
            }
        };

        mDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: mDate");
                new DatePickerDialog(NextActivity.this, DatePickerDialog.THEME_HOLO_LIGHT, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mStartTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: mStartTime");
                new TimePickerDialog(NextActivity.this, TimePickerDialog.THEME_HOLO_LIGHT, startTime, calendar
                        .get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)
                        , true).show();
            }
        });

        mEndTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: mEndTime");
                new TimePickerDialog(NextActivity.this, TimePickerDialog.THEME_HOLO_LIGHT, endTime, calendar
                        .get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)
                        , true).show();
            }
        });
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("Calling", TAG);
                startActivity(intent);
            }
        });
        setImage();
    }

    private void updateDateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mDate.setText(sdf.format(calendar.getTime()));
    }

    private void updateStartTimeLabel() {
        String myFormat = "HH:mm"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mStartTime.setText(sdf.format(calendar.getTime()));
    }

    private void updateEndTimeLabel() {
        String myFormat = "HH:mm"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mEndTime.setText(sdf.format(calendar.getTime()));
    }

    private void setImage() {
        intent = getIntent();
        ImageView image = (ImageView) findViewById(R.id.imageShare);

        if (intent.hasExtra(getString(R.string.selected_image))) {
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: got new image url: " + imgUrl);
            UniversalImageLoader.setImage(imgUrl, image, null, mAppend);
        } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
            bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setImage: got new bitmap");
            image.setImageBitmap(bitmap);
        }
    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: image count: " + imageCount);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count: " + imageCount);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop()");
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");


        if (MapsActivity.markerItem.getMarker() != null) {
            Marker marker = MapsActivity.markerItem.getMarker();
            Log.d(TAG, "marker - " + marker);
            latitude = marker.getPosition().latitude;
            longitude = marker.getPosition().longitude;
//                    LatLng latLng = new LatLng(latitude, longitude);
            Geocoder geocoder = new Geocoder(getApplicationContext());
            try {
                addressList = geocoder.getFromLocation(latitude, longitude, 1);
                str = addressList.get(0).getLocality() + ",";
                str += addressList.get(0).getAddressLine(0);
                mLocation.setText(str);
                Log.d(TAG, "setLocation() str - " + str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        setLocation();
        super.onResume();

    }

    public void setLocation() {
        Log.d(TAG, "setLocation()");
        markerItem = new MarkerItem();

        markerItem.setOnMarkerSetListener(new OnMarkerSetListener() {
            @Override
            public void onMarkerSet(Marker marker) {
                Log.d(TAG, "setLocation() marker -" + marker);
                if (marker != null) {
//                    latitude = marker.getPosition().latitude;
//                    longitude = marker.getPosition().longitude;
////                    LatLng latLng = new LatLng(latitude, longitude);
//                    Geocoder geocoder = new Geocoder(getApplicationContext());
//                    try {
//                        addressList = geocoder.getFromLocation(latitude, longitude, 1);
//                        str = addressList.get(0).getLocality()+",";
//                        str+=addressList.get(0).getAddressLine(0);
//                        mLocation.setText(str);
//                        Log.d(TAG,"setLocation() str - " + str);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        });
    }

    @Override
    public void onMarkerSet(Marker marker) {
        Log.d(TAG, "onMarkerSet");
        setLocation();
    }
}
