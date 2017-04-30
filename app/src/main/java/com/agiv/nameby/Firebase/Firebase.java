package com.agiv.nameby.Firebase;

import android.util.Log;

import com.agiv.nameby.NameList;
import com.agiv.nameby.NameTagger2;
import com.agiv.nameby.Settings;
import com.agiv.nameby.entities.Family;
import com.agiv.nameby.entities.Member;
import com.agiv.nameby.entities.Name;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Noa Agiv on 4/16/2017.
 */

public class Firebase {
    final static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static ValueEventListener initTagListener(final Member member) {
        final ValueEventListener tagsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nameId = dataSnapshot.getKey();
                String tag = (String) dataSnapshot.getValue();
                NameTagger2.initTag(member, nameId, tag);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        return tagsListener;
    }

    public static void initNameTagListeners(){
        DatabaseReference namesRef = database.getReference("names");

        final ChildEventListener namesListener = new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                Name name = dataSnapshot.getValue(Name.class);
                name.setId(id);
                NameTagger2.initName(name);
                for (Member m : Settings.getFamily().familyMembers) {
                    DatabaseReference userTagsRef = database.getReference("users/" + m.id + "/tags");
                    userTagsRef.child(id).addListenerForSingleValueEvent(initTagListener(m));
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        namesRef.addChildEventListener(namesListener);


    }

    public static void initFamilyListener(final String familyId) {
        DatabaseReference ref = database.getReference("families/" + familyId);

        ValueEventListener familyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Family family = dataSnapshot.getValue(Family.class);
                family.setId(familyId);
                Settings.setFamily(family);
                initFamilyMemberListener(family);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        ref.addValueEventListener(familyListener);
    }


    private static void initFamilyMemberListener(final Family family) {
        final ChildEventListener familyMembersListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                initMemberListener(id, family);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        DatabaseReference ref = database.getReference("families/" + family.id + "/members");
        ref.addChildEventListener(familyMembersListener);
    }


    private static void initMemberListener(final String memberId, final Family family) {
        DatabaseReference ref = database.getReference("users/" + memberId);
        ValueEventListener memberListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Member> t = new GenericTypeIndicator<Member>() {
                };
                Member member = dataSnapshot.getValue(t);
                member.setId(memberId);
                Member existingMember = family.getMember(memberId);
                if (existingMember != null) {
                    existingMember.updateDetails(member);
                    return;
                }
                family.addMember(member);

                if (memberId.equals(Settings.getMemberId())){
                    Settings.setMember(member);
                }
                DatabaseReference userTagsRef = database.getReference("users/" + member.id + "/tags");
                for (String nameId : NameTagger2.getNameIds()) {
                    userTagsRef.child(nameId).addListenerForSingleValueEvent(initTagListener(member));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ref.addValueEventListener(memberListener);
    }
}

