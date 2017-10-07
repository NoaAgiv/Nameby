package com.agiv.nameby.fragments;


import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.agiv.nameby.NameGenerator;
import com.agiv.nameby.NameTagger;
import com.agiv.nameby.OnSwipeTouchListener;
import com.agiv.nameby.R;
import com.agiv.nameby.Settings;
import com.agiv.nameby.entities.Name;

/**
 * Created by Noa Agiv on 5/2/2017.
 */

public class RandomTagger extends Fragment {
    private View layout;
    private Name currentName;
    private TextView textView = null;

    public static MediaPlayer loveSound;
    public static MediaPlayer unlikeSound;
    public static MediaPlayer matchSound;
    public static MediaPlayer maybeSound;

    private ImageView loveButton;
    private ImageView disloveButton;
    private ImageView maybeButton;


    public RandomTagger() {
    }

    public void setName(Name name){
        currentName = name;
        if (textView != null)
            textView.setText(name.name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.random_tagger, container, false);
        textView = (TextView) layout.findViewById(R.id.untagged_names_view);
        if (currentName != null)
            setName(currentName);
        loveButton = (ImageView) layout.findViewById(R.id.love_image);
        disloveButton = (ImageView) layout.findViewById(R.id.dislove_image);
        maybeButton = (ImageView) layout.findViewById(R.id.maybe_image);

        loveSound = MediaPlayer.create(getContext(), R.raw.c_tone);
        unlikeSound = MediaPlayer.create(getContext(), R.raw.a_tone);
        matchSound = MediaPlayer.create(getContext(), R.raw.pin_drop_match);
        maybeSound = MediaPlayer.create(getContext(), R.raw.beep_love);

        textView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeRight() {
                tagLoved();
            }
            public void onSwipeLeft() {
                tagUnloved();
            }
            public void onSwipeBottom() {
                tagMaybe();
            }
        });

        loveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentName.equals(NameGenerator.END_OF_LIST))
                    tagLoved();
            }
        });

        disloveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentName.equals(NameGenerator.END_OF_LIST))
                    tagUnloved();
            }
        });

        maybeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentName.equals(NameGenerator.END_OF_LIST))
                    tagMaybe();
            }
        });

        return layout;
    }

    public View getLayout(){
        return layout;
    }

    public void tagLoved(){
        Log.d("swipe right", currentName.name);
        String name = currentName.name; // markNameLoved calls setName, so I need to save the name here
        boolean isMatch = NameTagger.markNameLoved(currentName);
        if (isMatch) {
            matchSound.start();
            Toast.makeText(getActivity(), getString(R.string.match_massage) + " " + name,
                    Toast.LENGTH_LONG).show();
            Settings.getFamily().sendNotification(getString(R.string.nameby_notif_title), getString(R.string.match_notif_body));
        }
        else
            loveSound.start();
//        emphesize_animation(loveButton);

    }

    public void tagUnloved(){
        unlikeSound.start();
        NameTagger.markNameUnloved(currentName);
//        emphesize_animation(disloveButton);
    }

    public void tagMaybe(){
        maybeSound.start();
        NameTagger.markNameMaybe(currentName);
//        emphesize_animation(disloveButton);
    }
}
