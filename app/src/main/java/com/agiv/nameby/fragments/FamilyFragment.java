package com.agiv.nameby.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.TextView;

import com.agiv.nameby.R;
import com.agiv.nameby.Settings;
import com.agiv.nameby.entities.Family;
import com.agiv.nameby.entities.Member;
import com.agiv.nameby.utils.EditableListViewAdapter;

import java.security.InvalidParameterException;
import java.util.List;

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
            family.removeSaveMember(member);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        family = Settings.getFamily();
        layout = inflater.inflate(R.layout.family_settings, container, false);
        memberList = (ListView) layout.findViewById(R.id.member_list);

        if (family != null && family.familyMembers != null){

            adapter = new EditableListViewAdapter(removeCallback, family.familyMembers, getContext());
            memberList.setAdapter(adapter);
            setFamilyNameBox();
            setAddMemberButton();
        }
        return layout;
    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        adapter.notifyDataSetChanged();
//    }

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
                            member.save();
                            family.addSaveMember(member);
                            adapter.notifyDataSetChanged();
                        }catch (InvalidParameterException e) {
                            showErrorAlert(e.getMessage());
                        }
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
                        .setTitle(R.string.change_family_name)
                        .setMessage(R.string.change_family_name_body)
                        .setCancelable(false)
                        .setView(input)
                        .setPositiveButton(R.string.mark_approve_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!family.setName(input.getText().toString())){
                                    showErrorAlert(R.string.error_set_name);
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



    private void showErrorAlert(String error){
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.error_dialog_title)
                .setMessage(error)
                .setCancelable(false)
                .setPositiveButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .show();
    }
    private void showErrorAlert(int error){
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.error_dialog_title)
                .setMessage(error)
                .setCancelable(false)
                .setPositiveButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .show();
    }






}
