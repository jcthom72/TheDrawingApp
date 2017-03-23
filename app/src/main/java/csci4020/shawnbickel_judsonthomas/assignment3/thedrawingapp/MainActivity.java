package csci4020.shawnbickel_judsonthomas.assignment3.thedrawingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

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
    private Image userImage;    // each new image is an object stored in a vector

    private ImageView backgroundColor;
    private ImageView lineColor;
    private Spinner freeStyle;
    private ImageView circle;
    private ImageView eraser;
    private ImageView gallery;
    private ImageView newImage;
    private ImageView rectangle;
    private ImageView VerticalLine;
    private ImageView text;
    private ImageView undo;
    private ImageView redo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        colorData = new ColorData();
        colorList = new ArrayList<String>();
        drawingEngine = (UserDrawingEngine) findViewById(R.id.drawingLayout);
        userImage = new Image();

        // ImageViews are linked to the choices the user has on screen
        backgroundColor = (ImageView) findViewById(R.id.background_color);
        lineColor = (ImageView) findViewById(R.id.lineColor);
        freeStyle = (Spinner) findViewById(R.id.freeStyle);
        circle = (ImageView) findViewById(R.id.circle);
        eraser = (ImageView) findViewById(R.id.erase);
        gallery = (ImageView) findViewById(R.id.saveToFile);
        newImage = (ImageView) findViewById(R.id.newImage);
        rectangle = (ImageView) findViewById(R.id.rectangle);
        VerticalLine = (ImageView) findViewById(R.id.vertical_line);
        text = (ImageView) findViewById(R.id.text);
        undo = (ImageView) findViewById(R.id.undo);
        redo = (ImageView) findViewById(R.id.redo);

        // ArrayAdapter populates the spinner with the contents of a string array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.line_widths, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        freeStyle.setAdapter(adapter);
        freeStyle.setPrompt("Choose a Line Width");
        freeStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int width = Integer.parseInt(freeStyle.getSelectedItem().toString());
                drawingEngine.setForegroundPaintStrokeWidth((float) width);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // concise format to hold ImageViews
        ImageView[] userOptionImageViews = {backgroundColor, lineColor, circle,
                        eraser, gallery, newImage, rectangle, VerticalLine, text, undo, redo};

        // when an image is clicked, the loop finds the View and sets the listener
        for (ImageView image: userOptionImageViews){
            image.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        int v = view.getId();

        /* if one of the color wheels is chosen, the changeColor method will change the
          respective color */
        if (v == R.id.background_color || v == R.id.lineColor){
            retrieveColorData(); // retrieves colors from ArrayList
            changeColor(v);
        }

        /* when the new page button is clicked, the image is saved to a vector in the Image class
           and the page is reset by the newPage() method */
        else if (v == R.id.newImage){
            userImage.addImage(drawingEngine);
            saveImageToFile(userImage);
            newPage();

        }

        // saves the image to a file
        else if(v == R.id.saveToFile){
            saveImageToFile(userImage);
        }

        // deletes image from vector
        else if (v == R.id.erase){
            deleteImagefromVector(drawingEngine);
        }

        // draws a circle on the screen
        else if (v == R.id.circle){
            drawingEngine.setCurrentObjectToDraw(UserDrawingEngine.PreviewType.OVAL);
        }

        // draws a rectangle on the screen
        else if (v == R.id.rectangle){
            drawingEngine.setCurrentObjectToDraw(UserDrawingEngine.PreviewType.RECTANGLE);
        }

        // gives the user the ability to draw any image with a choice of line thickness
        else if (v == R.id.freeStyle){
            drawingEngine.setCurrentObjectToDraw(UserDrawingEngine.PreviewType.FREELINE);
        }

        // allows the user to place a straight line on the screen
        else if(v == R.id.vertical_line){
            drawingEngine.setCurrentObjectToDraw(UserDrawingEngine.PreviewType.STRAIGHTLINE);
        }

        // allows the user to draw text at a specified point
        else if (v == R.id.text){

        }

        else if (v == R.id.undo){

        }

        else if (v == R.id.redo){

        }


    }
    /* this method creates a new colorPicker object to ensure that colorPicker has the correct
        parent View; colorList retrieves the colors from the ColorData class; then the ArrayList is
         populated by the colors retrieved from the ColorData class */
    public void retrieveColorData(){
        colorPicker = new ColorPicker(MainActivity.this);
        colorList = colorData.getColorData();
        colorPicker.setColors(colorList); // library method
    }

    // this method actually changes the colors on the screen
    public void changeColor(final int id){
        colorPicker.show(); // library method to display ArrayList of colors
        // implements library listener
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            // onChooseColor is a method in the library OnChooseColorListener interface
            public void onChooseColor(int position, int color) {
                if (id == R.id.background_color){
                    drawingEngine.setBackgroundPaintColor(color);
                }else if (id == R.id.lineColor){
                    drawingEngine.setForegroundPaintColor(color);
                }
            }
            @Override
            public void onCancel() {

            }
        });
    }

    public void newPage(){
        //drawingEngine.clearImage();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveImageToFile(userImage);
    }

    public void saveImageToFile(Image image){

    }

    public void deleteImagefromVector(UserDrawingEngine image){
        userImage.deleteImage(image);
    }

}

