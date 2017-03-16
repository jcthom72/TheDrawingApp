package csci4020.shawnbickel_judsonthomas.assignment3.thedrawingapp;

import java.util.ArrayList;

/**
 * Created by sbickel20 on 3/16/17.
 */

public class ColorData {
    private ArrayList<String> colorL;

    public ColorData(){
        colorL = new ArrayList<String>();
        colorL.add("#ffffff");
        colorL.add("#000000");
        colorL.add("#2564ac");
        colorL.add("#5caeb6");
        colorL.add("#f3b43b");
        colorL.add("#da404c");
        colorL.add("#be8aad");
        colorL.add("#5e89cb");
        colorL.add("#5dba27");
        colorL.add("#e6703d");
        colorL.add("#f54e4a");
        colorL.add("#8b50f7");
        colorL.add("#57c1ec");
        colorL.add("#8dc25a");
        colorL.add("#c1987f");
        colorL.add("#cc7878");
        colorL.add("#9087b8");
        colorL.add("#BD33A4");
        colorL.add("#78866B");
        colorL.add("#DFFF00");
    }


    public ArrayList<String> getColorData(){
        return colorL;
    }
}
