package com.agiv.nameby.Firebase;

import com.agiv.nameby.NameList;
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

    public static void initFamilyListener(final String familyId) {
        DatabaseReference ref = database.getReference("families/" + familyId);

        ValueEventListener familyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Family family = dataSnapshot.getValue(Family.class);
                family.setId(familyId);
                initFamilyMemberListener(family);
                Settings.setFamily(family);
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


    private static void initMemberListener(String memberId, final Family family) {
        DatabaseReference ref = database.getReference("users/" + memberId);
        ValueEventListener memberListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Member> t = new GenericTypeIndicator<Member>() {
                };
                Member member = dataSnapshot.getValue(t);
                family.addMember(member);
                System.out.println(family.familyMembers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ref.addValueEventListener(memberListener);
    }
}

