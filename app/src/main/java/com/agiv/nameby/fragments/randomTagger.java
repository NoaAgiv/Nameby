package com.agiv.nameby.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agiv.nameby.R;

/**
 * Created by Noa Agiv on 5/2/2017.
 */

public class RandomTagger extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.random_tagger, container, false);
    }
}
