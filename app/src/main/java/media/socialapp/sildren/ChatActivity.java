package media.socialapp.sildren;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Random;

import media.socialapp.sildren.DataModels.ChatData;
import media.socialapp.sildren.utilities.ChatAdapter;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 1001;
    private static final String TAG = "ChatActivity";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;

    private ListView mListView;
    private EditText mEdtMessage;
    private AppCompatButton sendBtn;

    private ChatAdapter mAdapter;
    private String userName;
    private String chatGroupName;

    int enableTextColor;
    int disableTextColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sendBtn = (AppCompatButton) findViewById(R.id.btn_send);
        Log.d(TAG,"sendBtn" + sendBtn);
        initViews();
        initFirebaseDatabase();
        initFirebaseAuth();
        initValues();
    }

    private void initViews() {
        chatGroupName = getIntent().getStringExtra("chat_group");
        mListView = (ListView) findViewById(R.id.list_message);
        mAdapter = new ChatAdapter(this, 0);
        mListView.setAdapter(mAdapter);

        mEdtMessage = (EditText) findViewById(R.id.edit_message);
        TextView appBarTitle = (TextView) findViewById(R.id.appbar_title);
        appBarTitle.setText(chatGroupName);
        findViewById(R.id.btn_send).setOnClickListener(this);

        enableTextColor = R.color.colorEnabled;
        disableTextColor = R.color.colorDisabled;

        sendBtn.setEnabled(false);
        sendBtn.setTextColor(disableTextColor);
        mEdtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    sendBtn.setEnabled(true);
                    sendBtn.setTextColor(enableTextColor);
                } else {
                    sendBtn.setEnabled(false);
                    sendBtn.setTextColor(disableTextColor);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void initFirebaseDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("message").child(chatGroupName);
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatData chatData = dataSnapshot.getValue(ChatData.class);
                chatData.firebaseKey = dataSnapshot.getKey();
                mAdapter.add(chatData);
                mListView.smoothScrollToPosition(mAdapter.getCount());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String firebaseKey = dataSnapshot.getKey();
                int count = mAdapter.getCount();
                for (int i = 0; i < count; i++) {
                    if (mAdapter.getItem(i).firebaseKey.equals(firebaseKey)) {
                        mAdapter.remove(mAdapter.getItem(i));
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    private void initFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateProfile();
            }
        };
    }

    private void initValues() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            userName = "Guest" + new Random().nextInt(5000);
        } else {
            userName = user.getDisplayName();
        }
    }

    private void updateProfile() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            // 비 로그인 상태 (메시지를 전송할 수 없다.)
            mAdapter.setEmail(null);
            mAdapter.notifyDataSetChanged();

        } else {
            // 로그인 상태
            findViewById(R.id.btn_send).setVisibility(View.VISIBLE);

            userName = user.getDisplayName();
            String email = user.getEmail();
            StringBuilder profile = new StringBuilder();
            profile.append(userName).append("\n").append(user.getEmail());
            mAdapter.setEmail(email);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        ;
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseReference.removeEventListener(mChildEventListener);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(ChatActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                updateProfile();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                Log.d(TAG, "chat send btn clicked");
                String message = mEdtMessage.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    mEdtMessage.setText("");
                    ChatData chatData = new ChatData();
                    chatData.userName = userName;
                    chatData.message = message;
                    chatData.time = System.currentTimeMillis();
                    chatData.userEmail = mAuth.getCurrentUser().getEmail();
                    if (mAuth.getCurrentUser().getPhotoUrl() != null)
                        chatData.userPhotoUrl = mAuth.getCurrentUser().getPhotoUrl().toString();
                    mDatabaseReference.push().setValue(chatData);

                }
                break;
            case R.id.btn_google_signin:
                break;

        }
    }
}

