package media.socialapp.sildren.DataModels;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import media.socialapp.sildren.utilities.OnGroupChangedListener;

public class ChatGroup {

    private DatabaseReference groupRef;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private String userKey;
    private List<String> groupNames = new ArrayList<>();
    private List<String> groupPhoto = new ArrayList<>();


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
                groupNames = new ArrayList<>();
                groupPhoto = new ArrayList<>();
//                HashMap x = new HashMap<>();

                Iterable<DataSnapshot> groups = dataSnapshot.getChildren();
                for (DataSnapshot e : groups) {
                    String name = (String) e.getKey();
                    String url = (String) e.getValue();
                    groupNames.add(name);
                    groupPhoto.add(url);
//                    groupPhoto.add();

                    //TODO.삭제.5
                    groupkeys.add(e.getKey());
                }

                if (listener != null) {
                    listener.onDataChanged(groupNames);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void saveGroup(String title) {
        if(!groupNames.contains(title))
            groupRef.push().setValue(title);
    }

    public String getGroup(int position) {
        return groupNames.get(position);
    }

    public String getGroupPhoto(int position) {
        return groupPhoto.get(position);
    }

    public int getSize() {
        return groupNames.size();
    }
}