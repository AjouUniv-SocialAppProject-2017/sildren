package media.socialapp.sildren;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import media.socialapp.sildren.DataModels.ChatGroup;
import media.socialapp.sildren.utilities.OnGroupChangedListener;

public class ChatGroupActivity extends AppCompatActivity {
    private static final String TAG = "ChatGroupActivity";

    private ChatGroup chatGroup;
    private FirebaseUser fbUser;
    private List<String> groupNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group);
        FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "intiated");

        chatGroup = new ChatGroup();
        groupNames = new ArrayList<>();
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.chatList_recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new RecyclerView.Adapter<ChatGroupHolder>() {
            @Override
            public ChatGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.inflate(R.layout.item_chat_group, parent, false);
                return new ChatGroupHolder(view);
            }

            @Override
            public void onBindViewHolder(final ChatGroupHolder holder, int position) {
                String groupTitle = chatGroup.getGroupTitle(position);
                String photoUrl = chatGroup.getGroupPhoto(position);
                String groupName = chatGroup.getGroupName(position);
                //                String message = cmodel.getMessage(position);
//                String timestamp = cmodel.getTimestamp(position);

//                if (umodel.getUserId().equals(cmodel.getUserId(position))) {
//                    holder.setTextRight(user, message, timestamp);
//                } else {
//
//                }

                holder.setText(groupTitle,groupName, photoUrl);
//                String imageUrl = cmodel.getImageURL(position);
//                holder.setImage(imageUrl);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChatGroupActivity.this, ChatActivity.class);
                        intent.putExtra("chat_group", holder.getGroupName());
                        startActivity(intent);
                    }
                });
                
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.LL_chatgroup_activity);
                        Log.d(TAG, "logout_menu - clicked");
                        LayoutInflater inflater = (LayoutInflater)
                                getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.popup_chatout, null);
                        AppCompatButton chatoutBtn = (AppCompatButton) popupView.findViewById(R.id.chatout_btn);
                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true; // lets taps outside the popup also dismiss it
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                        chatoutBtn = (AppCompatButton) popupView.findViewById(R.id.chatout_btn);
                        chatoutBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG, "chatout_btn - clicked");
                                FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference groupRef = mFirebaseDatabase.getReference();
                                groupRef.child("chat_groups").child(holder.getGroupName()).removeValue();
                            }
                        });
                        popupView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                popupWindow.dismiss();
                                return true;
                            }
                        });
                        // show the popup window
                        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

                        return false;
                    }
                });

            }

            @Override
            public int getItemCount() {
                return chatGroup.getSize();
            }
        });

        chatGroup.setOnGroupChangedListener(new OnGroupChangedListener() {
            @Override
            public void onDataChanged(List<String> groups) {
                groupNames = groups;
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });

//        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener()
//        {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
//            {
//                Log.d(TAG,"onInterceptTouchEvent");
//                Intent intent = new Intent(ChatGroupActivity.this, ChatActivity.class);
//                intent.putExtra("chat_group", holder.getGroupName());
//                startActivity(intent);
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//                Log.d(TAG,"onTouchEvent");
//
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });


        chatGroup.setOnGroupChangedListener(new OnGroupChangedListener() {
            @Override
            public void onDataChanged(List<String> groups) {
                recyclerView.getAdapter().notifyDataSetChanged();
                int count = recyclerView.getAdapter().getItemCount();
                recyclerView.scrollToPosition(count - 1);
            }
        });


    }

    public void onChatGroupRemove() {

    }

    class ChatGroupHolder extends RecyclerView.ViewHolder {

        private TextView nameView;
        private TextView titleView;
        private ImageView imageView;
        private TextView timestampView;
        private LinearLayout layout;


        public ChatGroupHolder(View itemView) {
            super(itemView);

            titleView = (TextView) itemView.findViewById(R.id.chatgroup_title_textview);
            nameView = (TextView) itemView.findViewById(R.id.chatgroup_name);
            imageView = (ImageView) itemView.findViewById(R.id.chat_group_profile);
//            timestampView = (TextView) itemView.findViewById(R.id.chat_timestamp_view);
//            layout = itemView.findViewById(R.id.);
        }

        public void setText(String title, String name, String photoUrl) {
//            layout.setGravity(Gravity.LEFT);
            final ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(photoUrl, imageView);
            titleView.setText(title);
            nameView.setText(name);
//            textView.setBackground(getBaseContext().getResources().getDrawable(R.drawable.bg_msg_from));
//            timestampView.setText(timestamp);
        }

        public String getGroupName() {
            return titleView.getText().toString();
        }
    }


//        public void setTextRight(String user, String text, String timestamp) {
//            layout.setGravity(Gravity.RIGHT);
//
//            userView.setText(user);
//            textView.setText(text);
//            textView.setBackground(getBaseContext().getResources().getDrawable(R.drawable.bg_msg_to));
//            timestampView.setText(timestamp);
//        }

    // 공통성과 가변성의 분리 - 변하는 것과 변하지 않는 것은 분리되어야 한다.
//            public void setImage(String imageUrl) {
//                int visibility = imageUrl.isEmpty() ? View.GONE : View.VISIBLE;
//                imageView.setVisibility(visibility);
//
//                Glide.with(ChatActivity.this)
//                        .load(imageUrl)
//                        .into(imageView);
//            }

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
