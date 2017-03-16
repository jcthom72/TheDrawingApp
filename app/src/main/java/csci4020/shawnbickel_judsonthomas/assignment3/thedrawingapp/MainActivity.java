package csci4020.shawnbickel_judsonthomas.assignment3.thedrawingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import petrov.kristiyan.colorpicker.ColorPicker;

/* this is a ColorPicker library; here is the URL:
    https://github.com/kristiyanP/colorpicker?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=3121 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageButton chooseColor = (ImageButton) findViewById(R.id.colorPicker);
        chooseColor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ColorPicker colorPicker = new ColorPicker(MainActivity.this);
                colorPicker.show();
            }
        });


    }
}
