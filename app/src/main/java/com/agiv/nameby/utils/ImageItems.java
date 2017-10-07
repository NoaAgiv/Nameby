package com.agiv.nameby.utils;

import java.util.ArrayList;
import java.util.List;

/**
* Created by Noa Agiv on 10/7/2017.
*/

public class ImageItems extends ArrayList<ImageItem> {
    public List<Integer> getImageSources(){
        List<Integer> imageSources = new ArrayList<>();
        for (ImageItem img : this){
            imageSources.add(img.source);
        }
        return imageSources;
    }
}

