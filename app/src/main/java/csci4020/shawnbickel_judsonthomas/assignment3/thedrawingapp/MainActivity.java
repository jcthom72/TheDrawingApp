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

    private ColorData colorData;
    private UserDrawingEngine drawingEngine;
    private ArrayList<String> colorList;
    private ColorPicker colorPicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        colorData = new ColorData();
        colorList = new ArrayList<String>();
        drawingEngine = (UserDrawingEngine) findViewById(R.id.drawingLayout);
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

        ImageView[] userOptionImageViews = {backgroundColor, lineChoice, brush, circle, lineColor,
                        eraser, gallery, newImage, rectangle, roundedRectangle, text};

        for (ImageView image: userOptionImageViews){
            image.setOnClickListener(this);
        }



    }

    @Override
    public void onClick(View view) {
        retrieveColorData();
        if (view.getId() == R.id.lineColor){

        }

        else if (view.getId() == R.id.background_color){

            colorPicker.show();
            colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                @Override
                public void onChooseColor(int position, int color) {
                    findViewById(R.id.drawingLayout).setBackgroundColor(color);

                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    public void retrieveColorData(){
        colorPicker = new ColorPicker(MainActivity.this);
        colorList = colorData.getColorData();
        colorPicker.setColors(colorList);
    }
}

