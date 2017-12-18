package media.socialapp.sildren.DataModels;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import media.socialapp.sildren.utilities.OnGroupChangedListener;

public class ChatGroup {

    private DatabaseReference groupRef;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private String userKey;
    private List<String> groupTitle = new ArrayList<>();
    private List<String> groupPhoto = new ArrayList<>();
    private List<String> groupName= new ArrayList<>();


    //TODO.삭제.4
    public List<String> groupkeys = new ArrayList<>();


    public ChatGroup() {
        user = auth.getCurrentUser();
        userKey = user.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        groupRef = database.getReference("chat_groups");
    }


    public void setOnGroupChangedListener(final OnGroupChangedListener listener) {
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupTitle = new ArrayList<>();
                groupName = new ArrayList<>();
                groupPhoto = new ArrayList<>();

//                HashMap x = new HashMap<>();

                Iterable<DataSnapshot> groups = dataSnapshot.getChildren();
                for (DataSnapshot e : groups) {
                    String title = (String) e.getKey();
                    String name = (String) e.child("name").getValue();
                    String url = (String) e.child("photo").getValue();
                    groupTitle.add(title);
                    groupName.add(name);
                    groupPhoto.add(url);
//                    groupPhoto.add();

                    //TODO.삭제.5
                    groupkeys.add(e.getKey());
                }

                if (listener != null) {
                    listener.onDataChanged(groupTitle);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void saveGroup(String title) {
        if(!groupTitle.contains(title))
            groupRef.push().setValue(title);
    }

    public String getGroupTitle(int position) {
        return groupTitle.get(position);
    }

    public String getGroupPhoto(int position) {
        return groupPhoto.get(position);
    }
    public String getGroupName(int position) {
        return groupName.get(position);
    }

    public int getSize() {
        return groupTitle.size();
    }
}