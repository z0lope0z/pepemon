package com.lopefied.pepemon.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.lopefied.pepemon.db.model.Photo;

/**
 * 
 * @author Lope Chupijay Emano
 * 
 */
public class PepemonUtils {

    public static List<Photo> combineDTOList(List<Photo> oldList,
            List<Photo> newList) {
        Set<Photo> set = new HashSet<Photo>();
        List<Photo> updatedList = new ArrayList<Photo>();
        newList.addAll(oldList);
        for (Iterator<Photo> iter = newList.iterator(); iter.hasNext();) {
            Photo element = iter.next();
            if (set.add((Photo) element)) {
                System.out.println("adding : " + element.getPhotoID());
                updatedList.add((Photo) element);
            } else {
                System.out.println("found duplicate : " + element.getPhotoID());
            }
        }
        return updatedList;
    }
}
