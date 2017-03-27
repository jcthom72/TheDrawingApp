package csci4020.shawnbickel_judsonthomas.assignment3.thedrawingapp;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import petrov.kristiyan.colorpicker.ColorPicker;
/* this is a ColorPicker library; here is the URL:
    https://github.com/kristiyanP/colorpicker?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=3121 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // colorData is used to access the data that contains the colors the user will choose
    private ColorData colorData;
    private UserDrawingEngine drawingEngine;
    private ArrayList<String> colorList; // colorList will contain the colors for the user to choose
    private ColorPicker colorPicker; // colorPicker is used to access the library methods

    private ImageView backgroundColor;
    private ImageView lineColor;
    private Spinner lineWidth;
    private ImageView circle;
    private ImageView eraser;
    private ImageView gallery;
    private ImageView newImage;
    private ImageView rectangle;
    private ImageView VerticalLine;
    private ImageView text;
    private ImageView undo;
    private ImageView redo;
    private ImageView freeLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        colorData = new ColorData();
        colorList = new ArrayList<String>();
        drawingEngine = (UserDrawingEngine) findViewById(R.id.drawingLayout);

        // ImageViews are linked to the choices the user has on screen
        backgroundColor = (ImageView) findViewById(R.id.background_color);
        lineColor = (ImageView) findViewById(R.id.lineColor);
        lineWidth = (Spinner) findViewById(R.id.lineWidth);
        circle = (ImageView) findViewById(R.id.circle);
        eraser = (ImageView) findViewById(R.id.erase);
        gallery = (ImageView) findViewById(R.id.saveToFile);
        newImage = (ImageView) findViewById(R.id.newImage);
        rectangle = (ImageView) findViewById(R.id.rectangle);
        VerticalLine = (ImageView) findViewById(R.id.vertical_line);
        text = (ImageView) findViewById(R.id.text);
        undo = (ImageView) findViewById(R.id.undo);
        redo = (ImageView) findViewById(R.id.redo);
        freeLine = (ImageView) findViewById(R.id.freeLine);

        // ArrayAdapter populates the spinner with the contents of a string array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.line_widths, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lineWidth.setAdapter(adapter);
        lineWidth.setPrompt("Choose a Line Width");
        lineWidth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int width = Integer.parseInt(lineWidth.getSelectedItem().toString());
                drawingEngine.setPreviewPaintStrokeWidth((float) width);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // concise format to hold ImageViews
        ImageView[] userOptionImageViews = {backgroundColor, lineColor, circle,
                        eraser, gallery, newImage, rectangle, VerticalLine, text, undo, redo, freeLine};

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


        else if (v == R.id.newImage){
            drawingEngine.reset();
        }

        // saves the image to a file
        else if(v == R.id.saveToFile){
            final View dialogView = (LayoutInflater.from(this)).inflate(R.layout.dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setView(dialogView);

            builder.setTitle("Enter the title of the image to save");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id){
                    Bitmap bitmap = drawingEngine.exportDrawingToBitmap();
                    String title = ((EditText) dialogView.findViewById(R.id.userText)).getText().toString().trim();
                    if(saveImageToGallery(bitmap, title)){
                        //successfully saved
                        Toast.makeText(getApplicationContext(), "\"" + title + "\" successfully saved to the gallery!", Toast.LENGTH_LONG).show();
                    }

                    else{
                        //failure
                        Toast.makeText(getApplicationContext(), "Error: \"" + title + "\" could not be saved to the gallery.", Toast.LENGTH_LONG).show();
                    }
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){
                    dialog.cancel();
                }
            });

            builder.show();
        }

        // switches to eraser mode (which is a freeline with background color)
        else if (v == R.id.erase){
            drawingEngine.setCurrentObjectToDraw(UserDrawingEngine.PreviewType.ERASER);
        }

        // draws a circle on the screen
        else if (v == R.id.circle){
            drawingEngine.setCurrentObjectToDraw(UserDrawingEngine.PreviewType.OVAL);
        }

        // draws a rectangle on the screen
        else if (v == R.id.rectangle){
            drawingEngine.setCurrentObjectToDraw(UserDrawingEngine.PreviewType.RECTANGLE);
        }

        else if (v == R.id.freeLine){
            drawingEngine.setCurrentObjectToDraw(UserDrawingEngine.PreviewType.FREELINE);
        }

        // handled by spinner
        /*else if (v == R.id.lineWidth){
        }*/

        // allows the user to place a straight line on the screen
        else if(v == R.id.vertical_line){
            drawingEngine.setCurrentObjectToDraw(UserDrawingEngine.PreviewType.STRAIGHTLINE);
        }

        // allows the user to draw text at a specified point
        else if (v == R.id.text){
            final View dialogView = (LayoutInflater.from(this)).inflate(R.layout.dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setView(dialogView);

            builder.setTitle("Enter Text to Place on the screen");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id){
                            drawingEngine.setCurrentObjectToDraw(UserDrawingEngine.PreviewType.TEXT);
                            String textToDraw = ((EditText) dialogView.findViewById(R.id.userText)).getText().toString().trim();
                            drawingEngine.setTextToDraw(textToDraw);
                        }
                    });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            dialog.cancel();
                        }
                    });

            builder.show();

        }

        else if (v == R.id.undo){
            drawingEngine.undoLastDraw();
        }

        else if (v == R.id.redo){
            drawingEngine.redoLastDraw();
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
        setColorPickerTitle(id); // set color picker title
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
                colorPicker.dismissDialog();
            }
        });
    }

    //saves "bitmapToSave" to the gallery. returns true if successful; false otherwise.
    public boolean saveImageToGallery(Bitmap bitmapToSave, String imageTitle){
        /*return(MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmapToSave,
                imageTitle,
                "Image drawn using Image Collector!") != null);*/

        File fileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
        if(!fileDir.exists()) {
            if(!fileDir.mkdirs()){
                return false;
            }
        }

        File file = new File(fileDir + "/" + imageTitle + ".png");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            return bitmapToSave.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (FileNotFoundException e) {
            return false;
        }
    }

    // sets the title of the color picker depending on the user's choice
    public void setColorPickerTitle(int id){
        if (id == R.id.background_color){
            colorPicker.setTitle("Pick a Background Color"); // library method setTitle()
        }else if (id == R.id.lineColor){
            colorPicker.setTitle("Pick the Line Color");
        }
    }
}

