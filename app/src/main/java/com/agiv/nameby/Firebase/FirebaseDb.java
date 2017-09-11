package com.agiv.nameby.Firebase;

import com.agiv.nameby.NameTagger;
import com.agiv.nameby.Settings;
import com.agiv.nameby.entities.Family;
import com.agiv.nameby.entities.Member;
import com.agiv.nameby.entities.Name;
import com.agiv.nameby.fragments.FamilyFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by Noa Agiv on 4/16/2017.
 */

public class FirebaseDb {
    final static FirebaseDatabase database = FirebaseDatabase.getInstance();


    public static ValueEventListener initTagListener(final Member member) {
        final ValueEventListener tagsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nameId = dataSnapshot.getKey();
                String tag = (String) dataSnapshot.getValue();
                System.out.println("member " + member + ", tag " + tag);
                NameTagger.initTag(member, nameId, tag);
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
                NameTagger.initName(name);
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
//                familyFragment.setFamily(family);

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
                Member existingMember = family.getMember(memberId);
                if (existingMember != null) {
                    existingMember.updateDetails(member);
                    return;
                }
                System.out.println("??? what " + Settings.getMember() + " " + member);
                if (member.id.equals(Settings.getMember().id)) {
                    member = Settings.getMember();
                }

                family.addMember(member);

//                System.out.println("wawawiwas   " + Settings.getMemberId() + "!=" + memberId);
//                if (memberId.equals(Settings.getMemberId())){
//                    System.out.println("wawawiwas" + Settings.getMemberId());
//                    Settings.setMember(member);
//                }
                DatabaseReference userTagsRef = database.getReference("users/" + member.id + "/tags");
                for (String nameId : NameTagger.getNameIds()) {
                    userTagsRef.child(nameId).addListenerForSingleValueEvent(initTagListener(member));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ref.addValueEventListener(memberListener);
    }

    public static void setMemberAndFamily(String email){
        System.out.println("users/" + Member.generateId(email));
        DatabaseReference ref = database.getReference("users/" + Member.generateId(email));

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Member> t = new GenericTypeIndicator<Member>() {
                };
                System.out.println(dataSnapshot.getValue());
                Member member = dataSnapshot.getValue(t);
                System.out.println(member);
                Settings.setMember(member);
                Settings.setFamilyId(member.family);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

