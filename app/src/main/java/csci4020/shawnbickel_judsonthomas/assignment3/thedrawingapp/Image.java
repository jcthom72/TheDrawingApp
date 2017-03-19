package csci4020.shawnbickel_judsonthomas.assignment3.thedrawingapp;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created by sbickel20 on 3/19/17.
 */

/* The Image class is used to save each image to a vector, return the image, and delete an image */
public class Image implements Serializable {
    private Vector<UserDrawingEngine> images;

    public Image(){
        images = new Vector<UserDrawingEngine>();
    }

    public void addImage(UserDrawingEngine img){
        images.add(img);
    }

    public UserDrawingEngine returnImage(UserDrawingEngine image){
        int index = images.indexOf(image);
        return images.elementAt(index);
    }

    public void deleteImage(UserDrawingEngine image){
        if (images.contains(image)){
            int index = images.indexOf(image);
            images.remove(index);
        }
    }

}
