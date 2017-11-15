package com.agiv.nameby.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.agiv.nameby.Firebase.FirebaseDb;
import com.agiv.nameby.R;
import com.agiv.nameby.Settings;
import com.agiv.nameby.entities.Family;
import com.agiv.nameby.entities.Member;
import com.agiv.nameby.utils.EditableListViewAdapter;
import com.agiv.nameby.utils.ErrorHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Set;

import static com.agiv.nameby.utils.EditableListViewAdapter.RemoveItemCallback;

/**
 * Created by Noa Agiv on 6/13/2017.
 */

public class FamilyFragment extends Fragment {

    private View layout;
    private Family family;
    private EditableListViewAdapter adapter;
    private ListView memberList;

    RemoveItemCallback removeCallback = new RemoveItemCallback() {
        @Override
        public void remove(Object obj){
            Member member = (Member) obj;
            if (Settings.getMember().id.equals(member.id)){
                Toast.makeText(getActivity(), getString(R.string.cannot_delete_your_member), Toast.LENGTH_LONG).show();
                return;
            }
            family.removeSaveMember(member);
        }
    };

    ErrorHandler.DismissAction dismissSetNamesErrorAction = new ErrorHandler.DismissAction() {
        @Override
        public void run(){
            showSetNamesDialog();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        family = Settings.getFamily();
        layout = inflater.inflate(R.layout.family_settings, container, false);
        memberList = (ListView) layout.findViewById(R.id.member_list);
        if (family != null && family.familyMembers != null){
            init(getContext());
        }
        return layout;
    }


    private void init(Context context){
        if (Settings.getMember().name.equals(getResources().getString(R.string.default_member_name))){
            showSetNamesDialog();
        }
        adapter = new EditableListViewAdapter(removeCallback, family.familyMembers, context);
        memberList.setAdapter(adapter);
        setFamilyNameBox();
        setAddMemberButton();
        setGenderRadioButtons();

    }

    private void showSetNamesDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.settings);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameBox = new EditText(getContext());
        nameBox.setHint(R.string.default_member_name);
        layout.addView(nameBox);

        final EditText familyNameBox = new EditText(getContext());
        familyNameBox.setHint(R.string.family_name);
        layout.addView(familyNameBox);

        final TextView familyNameView = (TextView) layout.findViewById(R.id.family_name);

        dialog.setView(layout)
                .setPositiveButton(R.string.mark_approve_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameBox.getText().toString();
                        String familyName = familyNameBox.getText().toString();
                        if(!Settings.getMember().setName(name)){
                            ErrorHandler.showErrorAlert(R.string.error_set_name, getContext(), dismissSetNamesErrorAction);
                            return;
                        }
                        Settings.getMember().save();

                        Settings.getMember().setName(name);
                        adapter.notifyDataSetChanged();


                        if (!family.setName(familyName)){
                            ErrorHandler.showErrorAlert(R.string.error_set_name, getContext(), dismissSetNamesErrorAction);
                            return;
                        }
                        setFamilyNameBox();

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void setGenderRadioButtons(){

        final RadioGroup genderRadio = (RadioGroup) layout.findViewById(R.id.gender_radio);

        int selectedGenderID = Settings.getGender().equals(Settings.Gender.FEMALE)?
                R.id.radio_female : R.id.radio_male;

        genderRadio.check(selectedGenderID);
        genderRadio.callOnClick();

        genderRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int selectedId) {
                RadioButton radioButton = (RadioButton) layout.findViewById(selectedId);
                Settings.Gender selectedGender = Settings.Gender.fromText(radioButton.getText().toString(), getResources());
                Settings.setGender(selectedGender);
            }
        });
    }

    public void resetFamily(){
        family = Settings.getFamily();
    }

    public void addMember(final Member member){
        Task<Boolean> tryGetMember = FirebaseDb.removeMemberFromFamilyTask(member.id);

        tryGetMember.addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task task) {
                member.setFamily(family.id);
                member.save();
                family.addSaveMember(member);
                adapter.notifyDataSetChanged();

            }
        });

    }

    public void setAddMemberButton(){
        final FloatingActionButton bt = (FloatingActionButton) layout.findViewById(R.id.add_family_member);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.add_family_member_title);

                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText nameBox = new EditText(getContext());
                nameBox.setHint(R.string.name);
                layout.addView(nameBox);

                final EditText emailBox = new EditText(getContext());
                emailBox.setHint(R.string.email);
                layout.addView(emailBox);

                dialog.setView(layout)
                .setPositiveButton(R.string.mark_approve_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameBox.getText().toString();
                        String email = emailBox.getText().toString();
                        Member member = null;
                        try {
                            member = new Member(name, email);
                        }catch (InvalidParameterException e) {
                            ErrorHandler.showErrorAlert(e.getMessage(), getContext());
                            return;
                        }
                        addMember(member);
                        dialog.dismiss();
                    }
                })
                        .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();;
            }
        });
    }

    public void setFamilyNameBox(){
        final TextView familyName = (TextView) layout.findViewById(R.id.family_name);
        familyName.setText(getResources().getString(R.string.family) + " " + family.name);
        familyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(getContext());
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.family_name)
                        .setMessage(R.string.change_family_name_body)
                        .setCancelable(false)
                        .setView(input)
                        .setPositiveButton(R.string.mark_approve_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!family.setName(input.getText().toString())){
                                    ErrorHandler.showErrorAlert(R.string.error_set_name, getContext());
                                    return;
                                }
                                familyName.setText(getResources().getString(R.string.family) + " " + family.name);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }});
    }

}
