package csci4020.shawnbickel_judsonthomas.assignment3.thedrawingapp;

import android.graphics.Path;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created by sbickel20 on 3/19/17.
 */

// th Paths class is used to save each Path to a vector, return the path, or delete the path
public class Paths implements Serializable {
    private Vector<Path> imagePaths;

    public Paths(){
        imagePaths = new Vector<>();
    }

    public void setElement(Path path){
        imagePaths.add(path);
    }

    public Path getElment(int index){
        return imagePaths.get(index);
    }

    public void removeElement(Path currentPath){
        int index = imagePaths.indexOf(currentPath);
        imagePaths.remove(index);
    }
}
