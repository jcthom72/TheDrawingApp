package csci4020.shawnbickel_judsonthomas.assignment3.thedrawingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private Paths userPath;
    private boolean circle = false;
    private boolean rect = false;
    private boolean clear = false;

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


    /* in this method the path that the user draws on the screen is saved to a vector in the
         Paths class; the graphics object are initialized */
    private void userDrawingSetup() {
        userTouch = new Path();
        userPath = new Paths();
        userPath.setElement(userTouch);
        imageStyles = new Paint();
        imageStyles.setAntiAlias(true);
        imageStyles.setStyle(Paint.Style.STROKE);
        drawImage = new Canvas();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(userTouch, imageStyles);

        if (circle){
            int x = getWidth();
            int y = getHeight();
            int radius = 200;
            canvas.drawCircle(x/2, y/2, radius, imageStyles);
        }

        if (rect){
            canvas.drawRect(50, 50, 200, 200, imageStyles);
        }

        if (clear){
            canvas.drawColor(Color.WHITE);
            clear = false;
            rect = false;
            circle = false;
        }
    }

    public void setPaintColor(int color){
        imageStyles.setColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    // onTouchEvent listener responds to user touches on the display
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // the two position variables retrieve x and y coordinates of the user press location
        float xPosition = event.getX();
        float yPosition = event.getY();

        /* when the users first touch the screen, the path to be drawn starts at the location
             the users first touch */
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            userTouch.moveTo(xPosition, yPosition);

         // when the users draw the image on the screen, the path follows their direction
        }else if (event.getAction() == MotionEvent.ACTION_MOVE){
            userTouch.lineTo(xPosition, yPosition);

         // when the users lift their finger from the screen, the path is drawn and then reset
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            drawImage.drawPath(userTouch, imageStyles);
        }else{
            return false;
        }
          // call onDraw()
          invalidate();
          return true;

    }

    public void drawCircle(){
        circle = true;
        invalidate();
    }

    public void drawRectangle(){
        rect = true;
        invalidate();
    }

    // clear the current path from the user's screen
    public void clearImage(){
        userTouch.reset();
        clear = true;
        invalidate();
    }
}
