package com.kelsey.searchbox.webinfo;

import com.kelsey.searchbox.search.Item;

import java.util.List;

public class ListElement {

    public static void addElement(List<Item> vedioList, Item vedio){
        synchronized (vedioList){
            vedioList.add(vedio);
        }
    }

}
