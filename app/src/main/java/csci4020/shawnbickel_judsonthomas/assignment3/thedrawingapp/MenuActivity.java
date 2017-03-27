package csci4020.shawnbickel_judsonthomas.assignment3.thedrawingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button create = (Button) findViewById(R.id.createImage);
        Button description = (Button) findViewById(R.id.description);
        Button about = (Button) findViewById(R.id.about);

        create.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent createImage = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(createImage);
            }
        });

        description.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent descr = new Intent(MenuActivity.this, DrawingFunctions.class);
                startActivity(descr);
            }
        });

    }
}
