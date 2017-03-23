package csci4020.shawnbickel_judsonthomas.assignment3.thedrawingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by sbickel20 on 3/15/17.
 */

public class UserDrawingEngine extends View {
    public enum PreviewType{RECTANGLE, OVAL, FREELINE, STRAIGHTLINE}
    private final Map<PreviewType, DrawableObject> previewCache = new HashMap<PreviewType, DrawableObject>();
    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private DrawableObject previewObject;
    private LinkedList<DrawableObject> objectsToDraw;
    private boolean isPreviewing;

    /*our DrawableObjects*/

    /*DrawableObject uses drawMe to provide a consistent interface for objects
    in our drawing engine to be drawn onto a canvas*/
    private abstract class DrawableObject implements Cloneable{
        protected Paint paint;
        protected boolean isMorphing;

        protected DrawableObject(Paint paint){
            this.paint = paint;
            isMorphing = false;
        }

        /*draws the object onto the given canvas*/
        abstract public void drawMe(Canvas canvas);

        abstract public void start(float xPos, float yPos);

        abstract public void move(float xPos, float yPos);

        abstract public void end(float xPos, float yPos);

        /*force our derived classes to implement a publicly exposed
        version of the clone method*/
        @Override
        abstract public Object clone() throws CloneNotSupportedException;
    }

    private class Rectangle extends DrawableObject{
        private RectF rect;

        public Rectangle(Paint paint){
            super(paint);
            rect = new RectF();
        }

        public Rectangle(RectF rect, Paint paint){
            super(paint);
            this.rect = rect;
        }

        @Override
        public void drawMe(Canvas canvas) {
            canvas.drawRect(rect, paint);
        }

        @Override
        public void start(float xPos, float yPos) {
            if(!isMorphing) {
                rect.left = xPos;
                rect.top = yPos;
                isMorphing = true;
            }
        }

        @Override
        public void move(float xPos, float yPos) {
            if(isMorphing) {
                rect.right = xPos;
                rect.bottom = yPos;
            }
        }

        @Override
        public void end(float xPos, float yPos) {
            if(isMorphing) {
                move(xPos, yPos);
                isMorphing = false;
            }
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return new Rectangle(new RectF(rect), new Paint(paint));
        }
    }

    private class Oval extends DrawableObject{
        private RectF rect;

        public Oval(Paint paint){
            super(paint);
            rect = new RectF();
        }

        public Oval(RectF rect, Paint paint){
            super(paint);
            this.rect = rect;
        }

        @Override
        public void drawMe(Canvas canvas) {
            canvas.drawOval(rect, paint);
        }

        @Override
        public void start(float xPos, float yPos) {
            if(!isMorphing) {
                rect.left = xPos;
                rect.top = yPos;
                isMorphing = true;
            }
        }

        @Override
        public void move(float xPos, float yPos) {
            if(isMorphing) {
                rect.right = xPos;
                rect.bottom = yPos;
            }
        }

        @Override
        public void end(float xPos, float yPos) {
            move(xPos, yPos);
            isMorphing = false;
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return new Oval(new RectF(rect), new Paint(paint));
        }
    }

    private class FreeLine extends DrawableObject{
        private Path path;

        public FreeLine(Paint paint){
            super(paint);
            path = new Path();
        }

        public FreeLine(Path path, Paint paint){
            super(paint);
            this.path = path;
        }

        @Override
        public void drawMe(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public void start(float xPos, float yPos) {
            if(!isMorphing) {
                path.rewind();
                path.moveTo(xPos, yPos);
                isMorphing = true;
            }
        }

        @Override
        public void move(float xPos, float yPos) {
            if(isMorphing) {
                path.lineTo(xPos, yPos);
            }
        }

        @Override
        public void end(float xPos, float yPos) {
            move(xPos, yPos);
            //using path.close() here makes a connected, closed path
            isMorphing = false;
        }

        @Override
        /*though clone is protected, we override it as a public method
        * to expose it to outside code*/
        public Object clone() throws CloneNotSupportedException {
            return new FreeLine(new Path(path), new Paint(paint));
        }
    }

    private class StraightLine extends DrawableObject{
        private float startX, startY;
        private float endX, endY;

        public StraightLine(Paint paint){
            super(paint);
        }

        public StraightLine(float startX, float startY, float endX, float endY, Paint paint){
            super(paint);
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }

        @Override
        public void drawMe(Canvas canvas) {
            canvas.drawLine(startX, startY, endX, endY, paint);
        }

        @Override
        public void start(float xPos, float yPos) {
            if(!isMorphing) {
                startX = xPos;
                startY = yPos;
                isMorphing = true;
            }
        }

        @Override
        public void move(float xPos, float yPos) {
            if(isMorphing) {
                endX = xPos;
                endY = yPos;
            }
        }

        @Override
        public void end(float xPos, float yPos) {
            if(isMorphing) {
                move(xPos, yPos);
                isMorphing = false;
            }
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return new StraightLine(startX, startY, endX, endY, new Paint(paint));
        }
    }


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

        foregroundPaint = new Paint();
        foregroundPaint.setColor(Color.BLACK);
        foregroundPaint.setAntiAlias(true);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(10);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        //initialize our preview cache of all the different objects that can be created
        previewCache.put(PreviewType.RECTANGLE, new Rectangle(foregroundPaint));
        previewCache.put(PreviewType.OVAL, new Oval(foregroundPaint));
        previewCache.put(PreviewType.FREELINE, new FreeLine(foregroundPaint));
        previewCache.put(PreviewType.STRAIGHTLINE, new StraightLine(foregroundPaint));

        previewObject = previewCache.get(PreviewType.FREELINE);
        isPreviewing = false;



        objectsToDraw = new LinkedList<DrawableObject>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw the background
        canvas.drawPaint(backgroundPaint);


        //draw all of the previous objects
        for(DrawableObject objectToDraw : objectsToDraw){
            objectToDraw.drawMe(canvas);
        }

        //draw the "preview" object if we are currently previewing
        if (isPreviewing) {
            previewObject.drawMe(canvas);
        }

    }

    public void setForegroundPaintColor(int color){
        foregroundPaint.setColor(color);
        invalidate();
    }

    public void setForegroundPaintStrokeWidth(float width){
        foregroundPaint.setStrokeWidth(width);
    }

    public void setBackgroundPaintColor(int color){
        backgroundPaint.setColor(color);
        invalidate();
    }

    public void setCurrentObjectToDraw(PreviewType previewType){
        previewObject = previewCache.get(previewType);
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

        /* when the users first touch the screen*/
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            isPreviewing = true;
            previewObject.start(xPosition, yPosition);

            /*Note: for action down we do not need to issue an invalidate call;
            * there is no reason to redraw the screen yet until the user moves the endpoint*/
        }

        /* when the user drags their finger across the screen */
        else if (event.getAction() == MotionEvent.ACTION_MOVE){
            previewObject.move(xPosition, yPosition);

            //invalidate our screen, queueing a new onDraw() call
            invalidate();
        }

        /* when the user releases the screen */
        else if(event.getAction() == MotionEvent.ACTION_UP){
            isPreviewing = false;
            previewObject.end(xPosition, yPosition);
            //add preview object to container
            try{
                objectsToDraw.addLast((DrawableObject) previewObject.clone());
            } catch(CloneNotSupportedException e){
                /*fatal error occurred; we cannot clone our object. Apologize profusely to the user...*/
            }

            //invalidate our screen, queueing a new onDraw() call
            invalidate();
        }

        //touch event not handled
        else{
            return false;
        }

        return true;

    }
}
