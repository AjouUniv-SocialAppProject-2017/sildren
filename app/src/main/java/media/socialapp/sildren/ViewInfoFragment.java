package media.socialapp.sildren;

import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import media.socialapp.sildren.DataModels.Comment;
import media.socialapp.sildren.DataModels.MarkerItem;
import media.socialapp.sildren.DataModels.Photo;
import media.socialapp.sildren.utilities.FirebaseMethods;
import media.socialapp.sildren.utilities.MainfeedListAdapter;

public class ViewInfoFragment extends Fragment {

    private static final String TAG = "ViewInfoFragment";

    private ArrayList<Photo> mPhotos;
    private ArrayList<Photo> mPaginatedPhotos;
    private ArrayList<String> mFollowing;
    private ListView mListView;
    private MainfeedListAdapter mAdapter;
    private int mResults;

    private FirebaseMethods mFirebaseMethods;

    private ImageView mImageView;
//    private EditText mCaption;
    private EditText mTitle;
    private EditText mDate;
    private EditText mStartTime;
    private EditText mEndTime;
    private EditText mLocation;
    private EditText mRecruit;
    private EditText mContent;


    private Photo selectedPhoto;

    public ViewInfoFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_info, container, false);
        mListView = (ListView) view.findViewById(R.id.listView);



        mFollowing = new ArrayList<>();
        mPhotos = new ArrayList<>();

        mFirebaseMethods = new FirebaseMethods(getContext());

        mImageView = (ImageView) view.findViewById(R.id.imageShare);
//        mCaption = (EditText) view.findViewById(R.id.caption);
        mTitle = (EditText) view.findViewById(R.id.field_title);
        mLocation = (EditText) view.findViewById(R.id.field_location);
        mDate = (EditText) view.findViewById(R.id.field_date);
        mStartTime = (EditText) view.findViewById(R.id.field_start_time);
        mEndTime = (EditText) view.findViewById(R.id.field_end_time);
        mRecruit = (EditText) view.findViewById(R.id.field_recruit);
        mContent = (EditText) view.findViewById(R.id.field_content);

        if(MainActivity.mBundle != null) {
            selectedPhoto = MainActivity.mBundle.getParcelable("PHOTO");
            Log.d(TAG, "selectedPhoto : " + selectedPhoto);
            showInfo();
        }

        getFollowing();

        return view;
    }

    private void showInfo() {
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(selectedPhoto.getImage_path(),mImageView);
//        mCaption.setText(selectedPhoto.getCaption());
        mTitle.setText(selectedPhoto.getTitle());
        mLocation.setText(selectedPhoto.getLocation());
        mDate.setText(selectedPhoto.getDate());
        mStartTime.setText(selectedPhoto.getStartTime());
        mEndTime.setText(selectedPhoto.getEndTime());
        mRecruit.setText(selectedPhoto.getRecruit());
        mContent.setText(selectedPhoto.getContent());
    }

    private void getFollowing() {
        Log.d(TAG, "getFollowing: searching for following");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.child(getString(R.string.field_user_id)).getValue());

                    mFollowing.add(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());
                }
                mFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                //get the photos
                getPhotos();








            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPhotos() {
        Log.d(TAG, "getPhotos: getting photos");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for (int i = 0; i < mFollowing.size(); i++) {
            final int count = i;
            Query query = reference
                    .child(getString(R.string.dbname_user_photos))
                    .child(mFollowing.get(i))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                        Photo photo = new Photo();

                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();


//                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        if (objectMap.get(getString(R.string.field_tags)) != null)
                            photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        ArrayList<Comment> comments = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()) {
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);
                        }

                        photo.setComments(comments);
                        mPhotos.add(photo);
                    }
                    if (count >= mFollowing.size() - 1) {
                        //display our photos
                        displayPhotos();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void displayPhotos() {
        mPaginatedPhotos = new ArrayList<>();
        if (mPhotos != null) {
            try {
                Collections.sort(mPhotos, new Comparator<Photo>() {
                    @Override
                    public int compare(Photo o1, Photo o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });

                int iterations = mPhotos.size();

                if (iterations > 10) {
                    iterations = 10;
                }


                mResults = 10;
                for (int i = 0; i < iterations; i++) {
                    mPaginatedPhotos.add(mPhotos.get(i));
                }

                mAdapter = new MainfeedListAdapter(getActivity(), R.layout.layout_mainfeed_listitem, mPaginatedPhotos);
                mListView.setAdapter(mAdapter);

            } catch (NullPointerException e) {
                Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage());
            }
        }
    }



}

