package com.agiv.nameby.Firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.agiv.nameby.NameTagger;
import com.agiv.nameby.R;
import com.agiv.nameby.Settings;
import com.agiv.nameby.entities.Family;
import com.agiv.nameby.entities.Member;
import com.agiv.nameby.entities.Name;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
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

public class FirebaseDb {
    final static FirebaseDatabase database = FirebaseDatabase.getInstance();


    public static ValueEventListener initTagListener(final Member member) {
        final ValueEventListener tagsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nameId = dataSnapshot.getKey();
                String tag = (String) dataSnapshot.getValue();
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
                NameTagger.updateListsWithName(name);
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
                System.out.println("gender is " + family.gender);
                Settings.setGender(family.gender);
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
                if (member.id.equals(Settings.getMember().id)) {
                    member = Settings.getMember();
                }

                family.addMember(member);

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

    public static Task<Boolean> removeMemberFromFamilyTask(final String memberId){
        final TaskCompletionSource<Boolean> tcs = new TaskCompletionSource<>();

        final ValueEventListener membersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("listener called");
                GenericTypeIndicator<Member> t = new GenericTypeIndicator<Member>() {
                };
                Member member = dataSnapshot.getValue(t);
                System.out.println(member.getFamily());
                if (member!= null && member.getFamily()!=null) {
                    Log.i("family.addSaveMember", "removing member from his current family " + member.getFamily());
                    DatabaseReference oldFamilyRef = database.getReference("families/" + member.getFamily() + "/members/");
                    oldFamilyRef.child(member.id).removeValue();
                }

                tcs.setResult(true);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        System.out.println("something happened" + memberId);

        DatabaseReference ref = database.getReference("users/" + memberId);
        ref.addListenerForSingleValueEvent(membersListener);
        return tcs.getTask();
    }

    public enum MemberInitiationState{
        Unknown,
        Subscribed,
        SubscribedToFamily;
    }
    public static Task<MemberInitiationState> setMemberAndFamily(String email){
        final TaskCompletionSource<MemberInitiationState> tcs = new TaskCompletionSource<>();

        DatabaseReference ref = database.getReference("users/" + Member.generateId(email));

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Member> t = new GenericTypeIndicator<Member>() {
                };
                Member member = dataSnapshot.getValue(t);

                if (member != null) {
                    Settings.setMember(member);
                    if (member.getFamily()!=null) {
                        Settings.setFamilyId(member.family);
                        tcs.setResult(MemberInitiationState.SubscribedToFamily);
                    }
                    else
                        tcs.setResult(MemberInitiationState.Subscribed);

                }
                else {
                    tcs.setResult(MemberInitiationState.Unknown);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return tcs.getTask();
    }
}

