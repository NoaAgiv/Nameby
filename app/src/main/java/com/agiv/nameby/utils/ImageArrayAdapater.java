package com.agiv.nameby.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Noa Agiv on 10/3/2017.
 */


public class ImageArrayAdapater extends ArrayAdapter<Integer> {
        private ImageItems images;
        private List<Integer> imageSources = new ArrayList<>();


        public ImageArrayAdapater(Context context, ImageItems images) {
            super(context, android.R.layout.simple_spinner_item, images.getImageSources());
            this.images = images;
        }

        public int getImagePosition(int image){
            for (int i=0 ; i < images.size(); i++ ){
                int img = images.get(i).source;
                if (img == image)
                    return i;
            }
            return -1;
        }

        public Object get(int position){
            return images.get(position).item;
        }
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getImageForPosition(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getImageForPosition(position);
        }

        private View getImageForPosition(int position) {
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundResource(images.get(position).source);
            imageView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return imageView;
        }
}
