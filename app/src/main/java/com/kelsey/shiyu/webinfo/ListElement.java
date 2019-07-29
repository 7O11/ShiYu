package com.kelsey.shiyu.webinfo;

import com.kelsey.shiyu.search.Item;

import java.util.List;

public class ListElement {

    public static void addElement(List<Item> vedioList, Item vedio){
        synchronized (vedioList){
            vedioList.add(vedio);
        }
    }

}
