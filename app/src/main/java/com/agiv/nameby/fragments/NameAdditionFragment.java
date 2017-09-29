package com.agiv.nameby.fragments;


import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.agiv.nameby.NameList;
import com.agiv.nameby.NameTagger;
import com.agiv.nameby.R;
import com.agiv.nameby.SearchableAdapter;
import com.agiv.nameby.Settings;
import com.agiv.nameby.entities.Member;
import com.agiv.nameby.entities.Name;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Noa Agiv on 6/13/2017.
 */

public class NameAdditionFragment extends Fragment {
    private View layout = null;
    private Spinner tagChoiceSpinner;
    private List<String> tagOptions;
    private String selectedTag;
    private String selectedGender;
    private NameList names;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.name_addition, container, false);

        setTagSpinner();
        setGenderRadioButtons();
        setAddButton();

        return layout;
    }

    public void setNames(NameList names){
        this.names = names;
    }

    private void setAddButton() {
        Button addButton = (Button) layout.findViewById(R.id.add_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = (TextView) layout.findViewById(R.id.name_input);
                String nameStr = textView.getText().toString();
                Name name = new Name(nameStr, selectedGender);

                if (names.contains(name)){
                    names.get(nameStr).increasePopularity(1);
                }
                else{
                    names.add(name);
                    name.save();
                }
                Settings.getMember().tagName(names.get(nameStr), textToTag(selectedTag));
                NameTagger.saveNameTag(name, Settings.getMember());
                textView.setText("");
                Toast.makeText(getActivity(), getString(R.string.name_was_added) + " " + nameStr,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    private String textToGender(String text){
        return text.equals(getResources().getString(R.string.choose_female))?
                "f" :
                "m";

    }

    private Member.NameTag textToTag(String text){
        return text.equals(getResources().getString(R.string.loved))?
                    Member.NameTag.loved:
                text.equals(getResources().getString(R.string.unloved))?
                        Member.NameTag.unloved:
                        text.equals(getResources().getString(R.string.maybe))?
                                Member.NameTag.maybe:
                                Member.NameTag.untagged;




    }
    public void setGenderRadioButtons(){

        final RadioGroup genderRadio = (RadioGroup) layout.findViewById(R.id.gender_radio);

        int selectedGenderID = Settings.getGender().equals(Settings.Gender.FEMALE)?
                R.id.radio_female : R.id.radio_male;

        genderRadio.check(selectedGenderID);

        RadioButton radioButton = (RadioButton) layout.findViewById(selectedGenderID);
        selectedGender = textToGender(radioButton.getText().toString());

        genderRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = genderRadio.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) layout.findViewById(selectedId);
                selectedGender = textToGender(radioButton.getText().toString());
            }
        });
    }

    public void setTagSpinner(){
        tagChoiceSpinner = (Spinner) layout.findViewById(R.id.tag_choices);
        selectedTag = getResources().getString(R.string.loved);
        tagOptions = new ArrayList<String>();

        for (Member.NameTag tag : Member.NameTag.values()){
            tagOptions.add(getResources().getString(tag.displayName));
        }
        String[] items = new String[tagOptions.size()];
        items = tagOptions.toArray(items);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        tagChoiceSpinner.setAdapter(spinnerAdapter);
        tagChoiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedTag = ((TextView) view).getText().toString();
                setSpinnerSelectedTag(selectedTag);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        setSpinnerSelectedTag(selectedTag);
    }

    public void setSpinnerSelectedTag(String tag){
        int pos = 0;
        for (int i=0; i<tagOptions.size(); i++){
            if (tagOptions.get(i).equals(tag))
                pos = i;
        }
        tagChoiceSpinner.setSelection(pos, true);
    }
}
