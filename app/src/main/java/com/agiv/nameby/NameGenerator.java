package com.agiv.nameby;

import com.agiv.nameby.entities.Name;

import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by Noa Agiv on 3/12/2017.
 */

public class NameGenerator {
    private static final Random rgenerator = new Random();
    NameList names;
    NamePreferences pref;
    private static Name END_OF_LIST = new Name("Congrats! You tagged all the names!","f", 0);
    private boolean allTagged = true;

    public NameGenerator(NameList names, NamePreferences pref) {
        System.out.println("nameS! " + names);
        this.names = names;
        this.pref = pref;
    }

    public boolean allNamesTagged(){
        return allTagged;
    }

    public Name getNextUntaggedName() {
        if (names.isEmpty()) {
            System.out.println(names);
            allTagged = true;
            return END_OF_LIST;
        }
        else{
            allTagged = false;

            NameList familyLoved = filteredNames(
                    NameList.fullyInitiatedFilter,
                    NameList.untaggedFilter,
                    NameList.someFamilyMembersLovedFilter);
            System.out.println("partner list " + familyLoved);
            if (getFromPartnerLovedNamesRandomChoice(familyLoved))
                return getRandomFromPartnerLovedNames(familyLoved);

            else {
                NameList untagged = filteredNames(
                        NameList.fullyInitiatedFilter,
                        NameList.untaggedFilter
                        );

                if (untagged.isEmpty()) {
                    allTagged = true;
                    return END_OF_LIST;
                }
                System.out.println("untagged list " + untagged);
                return getRandomFromUntaggedPopularityBias(untagged);
            }
        }
    }

    private Name getRandomFromUntaggedPopularityBias(NameList untagged){

        Collections.sort(untagged, new Comparator<Name>() {
            @Override
            public int compare(Name name1, Name name) {
                return name1.popularity - name.popularity;
            }
        });
        System.out.println("and now?" + untagged.size());
        return untagged.get(rgenerator.nextInt(Math.min(10, untagged.size())));
    }

    private Name getRandomFromPartnerLovedNames(NameList familyLoved){
        return familyLoved.get(rgenerator.nextInt(familyLoved.size()));
    }

    private boolean getFromPartnerLovedNamesRandomChoice(NameList familyLoved){
        if (familyLoved.isEmpty()) return false;
        int rnd = rgenerator.nextInt(100);
        return (rnd >= 20);
    }

    private NameList filteredNames(UniqueList.ListCondition<Name> ... conds){
        NameList filtered = new NameList();
        filtered.conditionalAddAll(names, conds);
        return filtered;
    }
}
