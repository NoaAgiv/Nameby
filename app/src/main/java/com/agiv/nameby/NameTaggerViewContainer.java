package com.agiv.nameby;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Noa Agiv on 2/17/2017.
 */

public class NameTaggerViewContainer extends View{
    private TextView textView;
    private Name currentName = null;

    public static MediaPlayer loveSound;
    public static MediaPlayer unlikeSound;
    public static MediaPlayer matchSound;

    private ImageView loveButton;
    private ImageView disloveButton;


    public NameTaggerViewContainer(Context context, TextView textView, Activity activity) {
        super(context);
        loveSound = MediaPlayer.create(context, R.raw.c_tone);
        unlikeSound = MediaPlayer.create(context, R.raw.a_tone);
        matchSound = MediaPlayer.create(context, R.raw.pin_drop_match);

        this.textView = textView;
        setOnTouchListener(new OnSwipeTouchListener(context) {
            public void onSwipeRight() {
                swipeRight();
            }
            public void onSwipeLeft() {
                swipeLeft();
            }

        });


        loveButton = (ImageView) activity.findViewById(R.id.love_image);
        disloveButton = (ImageView) activity.findViewById(R.id.dislove_image);

        loveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeRight();
            }
        });

        disloveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeLeft();
            }
        });
    }

    public CharSequence getText(){
        return currentName.name;
    }

    public Name getCurrentName(){
        return currentName;
    }

    public final void setName(Name name){
        currentName = name;
        textView.setText(name.name);
    }

    public TextView getTextView() {
        return textView;
    }

    public void setOnTouchListener(View.OnTouchListener tl){
        textView.setOnTouchListener(tl);
    }

    public void swipeRight(){
        Log.d("swipe right", getCurrentName().name);
        boolean isMatch = NameTagger2.markNameLoved(getCurrentName());
        if (isMatch)
            matchSound.start();
        else
            loveSound.start();
//        emphesize_animation(loveButton);
    }

    public void swipeLeft(){
        unlikeSound.start();
        NameTagger2.markNameUnloved(getCurrentName());
//        emphesize_animation(disloveButton);
    }

    public void setVisibility(int visibility){
        textView.setVisibility(visibility);
        loveButton.setVisibility(visibility);
        disloveButton.setVisibility(visibility);
    }
}
