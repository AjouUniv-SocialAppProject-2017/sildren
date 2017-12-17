package media.socialapp.sildren;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
                String groupName = chatGroup.getGroup(position);
//                String message = cmodel.getMessage(position);
//                String timestamp = cmodel.getTimestamp(position);

//                if (umodel.getUserId().equals(cmodel.getUserId(position))) {
//                    holder.setTextRight(user, message, timestamp);
//                } else {
//
//                }

                holder.setText(groupName);
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

    class ChatGroupHolder extends RecyclerView.ViewHolder {

        private TextView userView;
        private TextView titleView;
        private ImageView imageView;
        private TextView timestampView;
        private LinearLayout layout;


        public ChatGroupHolder(View itemView) {
            super(itemView);

            titleView = (TextView) itemView.findViewById(R.id.chatgroup_name_textview);
//            textView = (TextView) itemView.findViewById(R.id.chat_text_view);
//            imageView = (ImageView) itemView.findViewById(R.id.chat_image_view);
//            timestampView = (TextView) itemView.findViewById(R.id.chat_timestamp_view);
//            layout = itemView.findViewById(R.id.);
        }

        public void setText(String title) {
//            layout.setGravity(Gravity.LEFT);

            titleView.setText(title);
//            textView.setText(text);
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

}
