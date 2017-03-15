package csci4020.shawnbickel_judsonthomas.assignment3.thedrawingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by sbickel20 on 3/15/17.
 */

public class UserDrawingEngine extends View {
    private Path userTouch;
    private Paint imageStyles;
    private Canvas drawImage;


    public UserDrawingEngine(Context context) {
        super(context);
        userDrawingSetup();
    }

    public UserDrawingEngine(Context context, AttributeSet attrs) {
        super(context, attrs);
        userDrawingSetup();
    }

    public UserDrawingEngine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        userDrawingSetup();
    }

    private void userDrawingSetup() {
        userTouch = new Path();
        imageStyles = new Paint();
        imageStyles.setAntiAlias(true);
        drawImage = new Canvas();

    }

    @Override
    protected void onDraw(Canvas canvas) {

    }

    // onTouchEvent listener responds to user touches on the display
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // the two position variables retrieve x and y coordinates of the user press location
        float xPosition = event.getX();
        float yPosition = event.getY();

        /* when the users first touch the screen, the path to be drawn starts at the
                 location the users first touch */
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            userTouch.moveTo(xPosition, yPosition);

         /* when the users draw the image on the screen, the path follows their direction */
        }else if (event.getAction() == MotionEvent.ACTION_MOVE){
            userTouch.lineTo(xPosition, yPosition);

         /* when the users lift their finger from the screen, the path is drawn and then reset */
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            drawImage.drawPath(userTouch, imageStyles);
            userTouch.reset();
        }else{
            return false;
        }
          // call onDraw()
          invalidate();
          return true;

    }
}
