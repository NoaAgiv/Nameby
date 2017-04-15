package com.agiv.nameby;

import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by Noa Agiv on 3/12/2017.
 */

public class NameGenerator {
    private static final Random rgenerator = new Random();
    NameList untaggedNames;
    NameList untaggedPartnerPositiveNames;
    NamePreferences pref;
    private static Name END_OF_LIST = new Name("Congrats! You tagged all the names!","f", 0);

    public NameGenerator(NameList names, NameList PartnerPositiveNames, NamePreferences pref) {
        this.untaggedNames = names;
        this.untaggedPartnerPositiveNames = names;
        this.pref = pref;
    }

    public Name getNextUntaggedName() {
        if (untaggedNames.isEmpty())
            return END_OF_LIST;
        else{
            if (getFromPartnerLovedNamesRandomChoice())
                return getRandomFromPartnerLovedNames();
            else
                return getRandomFromUntaggedPopularityBias();
        }
    }

    private Name getRandomFromUntaggedPopularityBias(){
        Collections.sort(untaggedNames, new Comparator<Name>() {
            @Override
            public int compare(Name name1, Name name) {
                return name1.popularity - name.popularity;
            }
        });
        return untaggedNames.get(rgenerator.nextInt(Math.min(10,untaggedNames.size())));
    }

    private Name getRandomFromPartnerLovedNames(){
        return untaggedPartnerPositiveNames.get(rgenerator.nextInt(untaggedPartnerPositiveNames.size()));
    }

    private boolean getFromPartnerLovedNamesRandomChoice(){
        if (untaggedPartnerPositiveNames.isEmpty()) return false;
        int rnd = rgenerator.nextInt(100);
        if (rnd < 20)
            return false;
        else
            return true;
    }

}
