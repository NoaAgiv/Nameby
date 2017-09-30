package com.agiv.nameby.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.agiv.nameby.NameList;
import com.agiv.nameby.R;
import com.agiv.nameby.SearchableAdapter;
import com.agiv.nameby.entities.Member;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Noa Agiv on 6/13/2017.
 */

public class ListsFragment extends Fragment {
    private View layout = null;
    public NameList names = null;
    private ListView nameList;
    private SearchableAdapter adapter;
    private EditText searchTextBox;
    private Spinner tagFilterSpinner;
    private List<String> tagOptions;
    private String selectedTag = "all";

    public void setNames(NameList names, Context context){
        this.names = names;
        if (adapter == null) {
            adapter = new SearchableAdapter(context, names);
        }
        if (nameList != null) {
            nameList.setAdapter(adapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.name_lists, container, false);
        nameList = (ListView) layout.findViewById(R.id.loved_names);
        if (names != null){
            adapter = new SearchableAdapter(getContext(), names);
            searchTextBox = (EditText) layout.findViewById(R.id.searchBox);
            setSearchTextWatcher();
            nameList.setAdapter(adapter);
            setTagSpinner();
            setAddButton();
        }
        return layout;
    }

    private void setAddButton() {
        FloatingActionButton fb = (FloatingActionButton) layout.findViewById(R.id.add_button);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, new NameAdditionFragment());
                ft.commit();
            }
        });

    }

    public void notifyChange(){
        adapter.notifyDataSetChanged();
    }


    public void setTagSpinner(){
        tagFilterSpinner = (Spinner) layout.findViewById(R.id.filter_by_tag);
        tagOptions = new ArrayList<String>(){{
            add(getResources().getString(R.string.all));
            add(getResources().getString(R.string.matches));
        }};

        for (Member.NameTag tag : Member.NameTag.values()){
            tagOptions.add(getResources().getString(tag.displayName));
        }
        String[] items = new String[tagOptions.size()];
        items = tagOptions.toArray(items);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        tagFilterSpinner.setAdapter(spinnerAdapter);
        tagFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.getFilter().filter("tag." + ((TextView) view).getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        setSpinnerSelectedTag(selectedTag);

    }

    public void setSearchTextWatcher(){
        searchTextBox.addTextChangedListener(new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
                  adapter.getFilter().filter("name." + s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {

                }

        @Override
        public void afterTextChanged(Editable s) {
                }
                });
    }

    public void filterByTag(String tag){
        selectedTag = tag;
        if (tagFilterSpinner != null){
            setSpinnerSelectedTag(tag);
        }

    }

    public void setSpinnerSelectedTag(String tag){
        int pos = 0;
        for (int i=0; i<tagOptions.size(); i++){
            if (tagOptions.get(i).equals(tag))
                pos = i;
        }
        tagFilterSpinner.setSelection(pos, true);
    }





}
