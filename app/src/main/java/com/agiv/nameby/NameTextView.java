package com.agiv.nameby;

import android.view.View;
import android.widget.TextView;

/**
 * Created by Noa Agiv on 2/17/2017.
 */

public class NameTextView {
    TextView textView;
    Name currentName = null;

    public NameTextView(TextView textView) {
        this.textView = textView;
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

    public void setOnTouchListener(View.OnTouchListener tl){
        textView.setOnTouchListener(tl);
    }


}
