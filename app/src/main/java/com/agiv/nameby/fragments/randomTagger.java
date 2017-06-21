package com.agiv.nameby.fragments;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agiv.nameby.NameTagger2;
import com.agiv.nameby.NameTaggerViewContainer;
import com.agiv.nameby.OnSwipeTouchListener;
import com.agiv.nameby.R;
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

    private ImageView loveButton;
    private ImageView disloveButton;


    public RandomTagger() {
    }

    public void setName(Name name){
        currentName = name;
        if (textView != null)
            textView.setText(name.name);
        System.out.println(currentName.name);
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

        loveSound = MediaPlayer.create(getContext(), R.raw.c_tone);
        unlikeSound = MediaPlayer.create(getContext(), R.raw.a_tone);
        matchSound = MediaPlayer.create(getContext(), R.raw.pin_drop_match);

        textView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeRight() {
                swipeRight();
            }
            public void onSwipeLeft() {
                swipeLeft();
            }

        });

        return layout;
    }

    public View getLayout(){
        return layout;
    }

    public void swipeRight(){
        Log.d("swipe right", currentName.name);
        boolean isMatch = NameTagger2.markNameLoved(currentName);
        if (isMatch)
            matchSound.start();
        else
            loveSound.start();
//        emphesize_animation(loveButton);
    }

    public void swipeLeft(){
        unlikeSound.start();
        NameTagger2.markNameUnloved(currentName);
//        emphesize_animation(disloveButton);
    }
}
