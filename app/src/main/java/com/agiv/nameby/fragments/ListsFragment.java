package com.agiv.nameby.fragments;


import android.content.Context;
import android.media.Image;
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
import com.agiv.nameby.utils.ImageArrayAdapater;
import com.agiv.nameby.utils.ImageItem;
import com.agiv.nameby.utils.ImageItems;

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
    ImageArrayAdapater itemSpinnerAdapter;
    private NameAdditionFragment nameAdditionFragment;

    public void setNames(NameList names, Context context, NameAdditionFragment nameAdditionFragment){
        this.names = names;
//        if (adapter == null) {
            adapter = new SearchableAdapter(context, getActivity(), names, itemSpinnerAdapter);
//        }
        if (nameList != null) {
            nameList.setAdapter(adapter);
        }
        this.nameAdditionFragment = nameAdditionFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.name_lists, container, false);
        nameList = (ListView) layout.findViewById(R.id.loved_names);
        if (names != null){
            ImageItems tagImages = new ImageItems();
            for (Member.NameTag tag : Member.NameTag.values()){
                tagImages.add(new ImageItem(tag.imageResId, tag));
            }
            itemSpinnerAdapter = new ImageArrayAdapater(getContext(), tagImages);
            adapter = new SearchableAdapter(getContext(), getActivity(), names, itemSpinnerAdapter);
            nameList.setAdapter(adapter);

            searchTextBox = (EditText) layout.findViewById(R.id.searchBox);
            setSearchTextWatcher();

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
                ft.replace(R.id.content_frame, nameAdditionFragment);
                ft.commit();
            }
        });

    }

    public void notifyChange(){
        if (adapter!=null) {
            adapter.notifyDataSetChanged();
        }
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
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, items);
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
