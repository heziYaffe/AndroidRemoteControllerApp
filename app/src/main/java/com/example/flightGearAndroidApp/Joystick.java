package com.example.flightGearAndroidApp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Joystick extends View {
    public static Connector connector;
    private Paint paint = new Paint();

    private static final int INNER_CIRCLE_RADIUS = 100;
    private static final int MIDDLE_CIRCLE_RADIUS = 220;
    private static final int OUTER_CIRCLE_RADIUS = 300;


    private float xOuterVal;
    private float yOuterVal;
    private float xInnerVal;
    private float yInnerVal;
    private String outerCircleColor = "#FFFFFF";
    private String middleCircleColor = "#F2EFE4";
    private String innerCircleColor = "#4A4A88";


    //public Connector connector;
    public Joystick(Context context) {
        super(context);
        init(null, 0, context);
    }

    public Joystick(Context context, AttributeSet attr) {
        super(context, attr);
        init(attr, 0, context);
    }

    public Joystick(Context context, AttributeSet attr, int defSytle) {
        super(context, attr, defSytle);
        init(attr, defSytle,context);
    }

    /**
     * in case that joystick colors doesn't set in the xml file (some of
     * the joystick colors or all of them) set the defualt colors.
     * otherwise set the colors specified in the xml file.
     */
    private void initJoystickColors(TypedArray a) {
        String surfaceColor = a.getString(R.styleable.Joystick_surfaceColor);
        String hatColor = a.getString(R.styleable.Joystick_hatColor);
        String middleHatColor = a.getString(R.styleable.Joystick_middleHatColor);

        if (surfaceColor != null) {
            this.outerCircleColor = surfaceColor;
        }
        if (hatColor != null) {
            this.innerCircleColor = hatColor;
        }
        if (middleHatColor != null) {
            this.middleCircleColor = middleHatColor;
        }
    }

    public void init(AttributeSet attr, int defStyle, Context context) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attr,
                R.styleable.Joystick, 0, 0);
        try {
            //get the text and colors specified using the names in attrs.xml
            xOuterVal = a.getFloat(R.styleable.Joystick_x, 0);
            yOuterVal = a.getFloat(R.styleable.Joystick_y, 0);//0 is default
            xInnerVal = xOuterVal;
            yInnerVal = yOuterVal;
            initJoystickColors(a);
        } finally {
            a.recycle();
        }

    }
    private int getDesiredWidth() {
        return OUTER_CIRCLE_RADIUS * 2 + 20;//20 for padding
    }
    private int getDesiredHeight() {
        return OUTER_CIRCLE_RADIUS * 2 + 20;//20 for padding
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Get the width measurement
        int widthSize = View.resolveSize(getDesiredWidth(), widthMeasureSpec);

        //Get the height measurement
        int heightSize = View.resolveSize(getDesiredHeight(), heightMeasureSpec);

        //store the measurements
        setMeasuredDimension(widthSize, heightSize);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw joystick surface
        paint.setColor(Color.parseColor(outerCircleColor));
        canvas.drawCircle(xOuterVal,yOuterVal, OUTER_CIRCLE_RADIUS, paint);
        //draw joystick's middle hat
        paint.setColor(Color.parseColor(middleCircleColor));
        canvas.drawCircle(xOuterVal,yOuterVal, MIDDLE_CIRCLE_RADIUS, paint);
        //draw joystick's inner hat
        paint.setColor(Color.parseColor(innerCircleColor));
        canvas.drawCircle(xInnerVal,yInnerVal, INNER_CIRCLE_RADIUS, paint);
    }

    private float distance(double p1X, double p1Y, double p2X, double p2Y) {
        return (float) Math.sqrt(Math.pow(p1X - p2X, 2) + Math.pow(p1Y - p2Y, 2));
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        float ratio;
        //check if the touch event occurred on the joystick
        float move = distance(motionEvent.getX(), motionEvent.getY(), xOuterVal, yOuterVal);
        float aileron = 0;
        float elevator = 0;
        if(motionEvent.getAction() != motionEvent.ACTION_UP) {
            if (move < OUTER_CIRCLE_RADIUS) {
                xInnerVal = x;
                yInnerVal = y;
                aileron = (motionEvent.getX() - xOuterVal)/ OUTER_CIRCLE_RADIUS;
                elevator = -1 * (motionEvent.getY() - yOuterVal) / OUTER_CIRCLE_RADIUS;
                //invoke drawOn
                invalidate();
            } else {
                ratio = OUTER_CIRCLE_RADIUS / move;
                xInnerVal = xOuterVal + (motionEvent.getX() - xOuterVal) * ratio;
                yInnerVal = yOuterVal + (motionEvent.getY() - yOuterVal) * ratio;
                aileron = (xInnerVal - xOuterVal)/ OUTER_CIRCLE_RADIUS;
                elevator = -1 * (yInnerVal - yOuterVal)/ OUTER_CIRCLE_RADIUS;
                //invoke drawOn
                invalidate();
            }
        } else { //if leave the steering wheel
            xInnerVal = xOuterVal;
            yInnerVal = yOuterVal;
            //invoke drawOn
            invalidate();
        }
        connector.onChange(aileron,elevator);

        return true;
    }

    @FunctionalInterface
    interface Connector
    {
        void onChange(float x, float y);
    }

}
