package csci4020.shawnbickel_judsonthomas.assignment3.thedrawingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import petrov.kristiyan.colorpicker.ColorPicker;
/* this is a ColorPicker library; here is the URL:
    https://github.com/kristiyanP/colorpicker?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=3121 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // colorData is used to access the data that contains the colors the user will choose
    private ColorData colorData;
    private UserDrawingEngine drawingEngine;
    private ArrayList<String> colorList; // colorList will contain the colors for the user to choose
    private ColorPicker colorPicker; // colorPicker is used to access the library methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        colorData = new ColorData();
        colorList = new ArrayList<String>();
        drawingEngine = (UserDrawingEngine) findViewById(R.id.drawingLayout);


        // ImageViews are linked to the choices the user has on screen
        ImageView backgroundColor = (ImageView) findViewById(R.id.background_color);
        ImageView lineChoice = (ImageView) findViewById(R.id.lineThickness);
        ImageView brush = (ImageView) findViewById(R.id.drawingBrush);
        ImageView circle = (ImageView) findViewById(R.id.circle);
        ImageView lineColor = (ImageView) findViewById(R.id.lineColor);
        ImageView eraser = (ImageView) findViewById(R.id.erase);
        ImageView gallery = (ImageView) findViewById(R.id.saveToGallery);
        ImageView newImage = (ImageView) findViewById(R.id.newImage);
        ImageView rectangle = (ImageView) findViewById(R.id.rectangle);
        ImageView roundedRectangle = (ImageView) findViewById(R.id.roundedRect);
        ImageView text = (ImageView) findViewById(R.id.text);

        // concise format to hold ImageViews
        ImageView[] userOptionImageViews = {backgroundColor, lineChoice, brush, circle, lineColor,
                        eraser, gallery, newImage, rectangle, roundedRectangle, text};

        // when an image is clicked, the loop finds the View and sets the listener
        for (ImageView image: userOptionImageViews){
            image.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        retrieveColorData(); // retrieves colors from ArrayList
        if (view.getId() == R.id.lineColor){
            changeColor(R.id.lineColor);
        }

        else if (view.getId() == R.id.background_color){
            changeColor(R.id.background_color);
        }

        else if (view.getId() == R.id.drawingBrush){

        }
    }
    /* this method creates a new colorPicker object to ensure that colorParent has the correct
        parent View; colorList retrieves the colors from the ColorData class; then the ArrayList
        is set as the color to show the user */
    public void retrieveColorData(){
        colorPicker = new ColorPicker(MainActivity.this);
        colorList = colorData.getColorData();
        colorPicker.setColors(colorList);
    }

    // this method actually changes the colors on the screen
    public void changeColor(final int id){
        colorPicker.show();
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position, int color) {
                if (id == R.id.background_color){
                    findViewById(R.id.drawingLayout).setBackgroundColor(color);
                }else if (id == R.id.lineColor){
                    drawingEngine.setPaintColor(color);
                }
            }

            @Override
            public void onCancel() {

            }
        });
    }
}

