package com.agiv.nameby;



import android.support.design.widget.TabLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

/**
 * Created by Noa Agiv on 3/11/2017.
 */

public class NameTagger4 {



    public static void initData(Object context, Object activity, TabLayout.Tab matchTab) throws IOException {
//        this.context = context;
//        this.activity = activity;
//        matchTab = matchTab;
        initLists();

//        initViewAdapters();
//        initUntaggedArea();


//        setListAdapters();
//        int unseenMatchesCount = GroupSettings.getCurrentUserUnseenMatches();
//        setMatchTabCount(unseenMatchesCount);
//        loveSound = MediaPlayer.create(context, R.raw.c_tone);
//        unlikeSound = MediaPlayer.create(context, R.raw.a_tone);
//        matchSound = MediaPlayer.create(context, R.raw.pin_drop_match);

    }


    private static void initLists(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference namesRef = database.getReference("names");

        final ValueEventListener TagsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                setUserNameTag(GroupSettings.getCurrentUser(), dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        ValueEventListener namesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<Name>> t = new GenericTypeIndicator<List<Name>>() {};
                List<Name> names = dataSnapshot.getValue(t);


                DatabaseReference userTagsRef = database.getReference("users/"+ GroupSettings.getCurrentUser() + "/tags");
                for (Name name : names) {
                    userTagsRef.child(Integer.toString(name.id)).addListenerForSingleValueEvent(TagsListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        namesRef.addListenerForSingleValueEvent(namesListener);
    }



}
