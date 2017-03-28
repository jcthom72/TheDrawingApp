package csci4020.shawnbickel_judsonthomas.assignment3.thedrawingapp;

import android.content.Context;
import android.graphics.Bitmap;
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
    public enum PreviewType{RECTANGLE, OVAL, FREELINE, STRAIGHTLINE, TEXT, ERASER}
    private final Map<PreviewType, DrawableObject> previewCache = new HashMap<PreviewType, DrawableObject>();
    private final int MAX_REDO_HISTORY_SIZE = 10;
    private Paint backgroundPaint; //describes paint style for background
    private Paint foregroundPaint; //describes paint style for drawable objects in foreground
    private DrawableObject previewObject;
    private LinkedList<DrawableObject> objectsToDraw;
    private LinkedList<DrawableObject> redoHistoryObjects;
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
        protected Path path;

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

    private class Eraser extends FreeLine{
        float eraserSize;

        public Eraser(){
            super(backgroundPaint);
            this.eraserSize = backgroundPaint.getStrokeWidth();
        }

        public Eraser(Path path, float eraserSize){
            super(path, backgroundPaint);
            this.eraserSize = backgroundPaint.getStrokeWidth();
        }

        @Override
        public void drawMe(Canvas canvas) {
            paint.setStrokeWidth(eraserSize);
            super.drawMe(canvas);
        }

        public void setEraserSize(float eraserSize){
            this.eraserSize = eraserSize;
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            /*important here that cloned erasers do not allocate a new background paint object;
            * instead, their constructors always use the same current background paint object, ensuring that all
            * erasers are always "drawn" with the correct, most current background color.*/
            return new Eraser(new Path(path), backgroundPaint.getStrokeWidth());
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

    private class Text extends DrawableObject{
        private float startX, startY;
        private String text;

        public Text(Paint paint){
            super(paint);
            //paint.setTextSize(60f);
            //paint.setStyle(Paint.Style.FILL_AND_STROKE);
        }

        public Text(String text, float startX, float startY, Paint paint){
            super(paint);
            this.startX = startX;
            this.startY = startY;
            this.text = text;
        }

        public void setText(String text){
            this.text = text;
        }

        @Override
        public void drawMe(Canvas canvas) {
            canvas.drawText(text, startX, startY, paint);
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
            if(isMorphing){
                /*for the special case of a Text DrawableObject, moving the text
                * changes its start position*/
                startX = xPos;
                startY = yPos;
            }
        }

        @Override
        public void end(float xPos, float yPos) {
            if(isMorphing){
                move(xPos, yPos);
                isMorphing = false;
            }
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return new Text(new String(text), startX, startY, new Paint(paint));
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

        /*for our erasers; safe to do because background will always only
        * be drawn using drawColor, which does not care about stroke width / style*/
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(10);

        //initialize our preview cache of all the different objects that can be created
        previewCache.put(PreviewType.RECTANGLE, new Rectangle(foregroundPaint));
        previewCache.put(PreviewType.OVAL, new Oval(foregroundPaint));
        previewCache.put(PreviewType.FREELINE, new FreeLine(foregroundPaint));
        previewCache.put(PreviewType.STRAIGHTLINE, new StraightLine(foregroundPaint));
        previewCache.put(PreviewType.TEXT, new Text(foregroundPaint));
        previewCache.put(PreviewType.ERASER, new Eraser());

        previewObject = previewCache.get(PreviewType.FREELINE);
        isPreviewing = false;

        objectsToDraw = new LinkedList<DrawableObject>();
        redoHistoryObjects = new LinkedList<DrawableObject>();
    }

    public void undoLastDraw(){
        if(redoHistoryObjects.size() < MAX_REDO_HISTORY_SIZE) {
            if (!objectsToDraw.isEmpty()) {
                redoHistoryObjects.push(objectsToDraw.removeLast());
                invalidate();
            }
        }
    }

    public void redoLastDraw(){
        if(redoHistoryObjects.size() > 0){
            objectsToDraw.addLast(redoHistoryObjects.pop());
            invalidate();
        }
    }

    /*creates a bitmap and draws the current drawings to it*/
    public Bitmap exportDrawingToBitmap(){
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        //draw the background
        canvas.drawPaint(backgroundPaint);

        //draw all of the objects
        for(DrawableObject objectToDraw : objectsToDraw){
            objectToDraw.drawMe(canvas);
        }

        //note, if we are "previewing", the preview object is not drawn

        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw the background
        canvas.drawColor(backgroundPaint.getColor());

        //draw all of the previous objects
        for(DrawableObject objectToDraw : objectsToDraw){
            objectToDraw.drawMe(canvas);
        }

        //draw the "preview" object if we are currently previewing
        if (isPreviewing) {
            previewObject.drawMe(canvas);
        }
    }

    /*resets the view; removing all drawing calls*/
    public void reset(){
        objectsToDraw.clear();
        redoHistoryObjects.clear();
        backgroundPaint.setColor(Color.WHITE);
        isPreviewing = false;
        invalidate();
    }

    public void setPreviewPaintColor(int color){
        /*if you attempt to change the preview color while you're in eraser mode the program
        * will change the foreground color which will affect the color of the next drawable object you select,
        * but will not change the eraser's color*/
        if(previewObject == previewCache.get(PreviewType.ERASER)){
            foregroundPaint.setColor(color);
            return;
        }

        previewObject.paint.setColor(color);

        /*if the user changes the foreground color during a preview, invalidate the screen
        * to reflect the updated color; otherwise, there is no need to invalidate*/
        if(isPreviewing){
            invalidate();
        }
    }

    /*used to set the preview paint style (e.g. STROKE, STROKE_AND_FILL, etc.)*/
    public void setPreviewPaintStyle(Paint.Style style){
        previewObject.paint.setStyle(style);

        if(isPreviewing) {
            invalidate();
        }
    }

    /*if previewing text, sets the text size; otherwise, sets the stroke size*/
    public void setPreviewPaintSize(float size){
        if(previewObject == previewCache.get(PreviewType.TEXT)){
            previewObject.paint.setTextSize(size);
        }

        else if(previewObject == previewCache.get(PreviewType.ERASER)){
            ((Eraser) previewObject).setEraserSize(size);
        }

        previewObject.paint.setStrokeWidth(size);

        if(isPreviewing){
            invalidate();
        }
    }

    public void setBackgroundPaintColor(int color){
        backgroundPaint.setColor(color);
        invalidate();
    }

    public void setCurrentObjectToDraw(PreviewType previewType){
        previewObject = previewCache.get(previewType);
    }

    /*if the current preview object is a "Text" object, then this will set
    * its text. Otherwise, it does nothing.*/
    public void setTextToDraw(String textToDraw){
        if(previewObject == previewCache.get(PreviewType.TEXT)){
            ((Text) previewObject).setText(textToDraw);
        }

        if(isPreviewing){
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    // onTouchEvent listener responds to user touches on the display
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // the two position variables retrieve x and y coordinates of the user press location
        float x = event.getX();
        float y = event.getY();

        /* when the users first touch the screen*/
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            isPreviewing = true;
            previewObject.start(x, y);

            /*Note: for action down we do not need to issue an invalidate call;
            * there is no reason to redraw the screen yet until the user moves the endpoint*/
        }

        /* when the user drags their finger across the screen */
        else if (event.getAction() == MotionEvent.ACTION_MOVE){
            if(isPreviewing) {
                previewObject.move(x, y);

                //invalidate our screen, queueing a new onDraw() call
                invalidate();
            }
        }

        /* when the user releases the screen */
        else if(event.getAction() == MotionEvent.ACTION_UP){
            if(isPreviewing) {
                isPreviewing = false;
                previewObject.end(x, y);
                //add preview object to container
                try {
                    objectsToDraw.addLast((DrawableObject) previewObject.clone());
                    redoHistoryObjects.clear(); /*clear our redo history; if the user has undone draw operations
                and performs a new draw operation, the undone draw operations should be forgotten*/
                } catch (CloneNotSupportedException e) {
                /*fatal error occurred; we cannot clone our object. Apologize profusely to the user...*/
                }

                //invalidate our screen, queueing a new onDraw() call
                invalidate();
            }
        }

        //touch event not handled
        else{
            return false;
        }

        return true;

    }
}
