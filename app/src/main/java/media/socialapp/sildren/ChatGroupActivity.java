//package media.socialapp.sildren;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class ChatGroupActivity extends AppCompatActivity {
//
//    String chatGroupName;
//    private FirebaseUser fbUser;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat_group);
//        FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//
//        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.chatList_recyclerView);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
//
//        recyclerView.setAdapter(new RecyclerView.Adapter<ChatGroupHolder>() {
//            @Override
//            public ChatGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
//                View view = layoutInflater.inflate(R.layout.item_chat, parent, false);
//                return new ChatGroupHolder(view);
//            }
//
//            @Override
//            public void onBindViewHolder(ChatGroupHolder holder, int position) {
//                String user = chatGroupName.getUserId(position);
//                String message = cmodel.getMessage(position);
//                String timestamp = cmodel.getTimestamp(position);
//
//                if (umodel.getUserId().equals(cmodel.getUserId(position))) {
//                    holder.setTextRight(user, message, timestamp);
//                } else {
//                    holder.setText(user, message, timestamp);
//                }
//
//
//
//                String imageUrl = cmodel.getImageURL(position);
//                holder.setImage(imageUrl);
//
//            }
//
//            @Override
//            public int getItemCount() {
//                return cmodel.getMessageCount();
//            }
//        });
//
//        cmodel.setOnDataChangedListener(new OnDataChangedListener() {
//            @Override
//            public void onDataChanged() {
//                recyclerView.getAdapter().notifyDataSetChanged();
//                int count = recyclerView.getAdapter().getItemCount();
//                recyclerView.scrollToPosition(count - 1);
//            }
//        });
//
//
//    }
//
//    class ChatGroupHolder extends RecyclerView.ViewHolder {
//
//        private TextView userView;
//        private TextView textView;
//        private ImageView imageView;
//        private TextView timestampView;
//        private LinearLayout layout;
//
//
//        public ChatGroupHolder(View itemView) {
//            super(itemView);
//
//            userView = itemView.findViewById(R.id.chat_user_id);
//            textView = (TextView) itemView.findViewById(R.id.chat_text_view);
//            imageView = (ImageView) itemView.findViewById(R.id.chat_image_view);
//            timestampView = (TextView) itemView.findViewById(R.id.chat_timestamp_view);
//            layout = itemView.findViewById(R.id.chat_item_layout);
//        }
//
//        public void setText(String user, String text, String timestamp) {
//            layout.setGravity(Gravity.LEFT);
//
//            userView.setText(user);
//            textView.setText(text);
//            textView.setBackground(getBaseContext().getResources().getDrawable(R.drawable.bg_msg_from));
//            timestampView.setText(timestamp);
//        }
//
//        public void setTextRight(String user, String text, String timestamp) {
//            layout.setGravity(Gravity.RIGHT);
//
//            userView.setText(user);
//            textView.setText(text);
//            textView.setBackground(getBaseContext().getResources().getDrawable(R.drawable.bg_msg_to));
//            timestampView.setText(timestamp);
//        }
//
//        // 공통성과 가변성의 분리 - 변하는 것과 변하지 않는 것은 분리되어야 한다.
////            public void setImage(String imageUrl) {
////                int visibility = imageUrl.isEmpty() ? View.GONE : View.VISIBLE;
////                imageView.setVisibility(visibility);
////
////                Glide.with(ChatActivity.this)
////                        .load(imageUrl)
////                        .into(imageView);
////            }
//    }
//
//}
