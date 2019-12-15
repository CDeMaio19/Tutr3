// code by Tihomir RAdeff
package com.example.tutr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class CanvasView extends View {

    private Paint paint;
    private Path path;
    private float mX,mY;
    private Bitmap bitmap;
    public int width,height;
    private Canvas canvas;
    Context context;
    private static final float TOLERANCE = 5;
    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        paint = new Paint();
        path = new Path();

        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }
    private void startTouch(float x, float y)
    {
        path.moveTo(x,y);
        mX = x;
        mY = y;
    }

    private void moveTouch(float x, float y)
    {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if(dx >= TOLERANCE || dy >= TOLERANCE)
        {
            path.quadTo(mX,mY,(x + mX) /2, (y +mY)/2);
        }
        mX = x;
        mY = y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path,paint);
    }
    private void upTouch()
    {
        path.lineTo(mX,mY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case(MotionEvent.ACTION_DOWN):
                startTouch(x,y);
                invalidate();
                break;
            case(MotionEvent.ACTION_MOVE):
                moveTouch(x,y);
                invalidate();
                break;
            case(MotionEvent.ACTION_UP):
                upTouch();
                invalidate();
                break;
        }
        return true;
    }

    public void ClearCanvas()
    {
        path.reset();
        invalidate();
    }

    public Bitmap getBitmapFromView(View v) {
        v.layout(v.getLeft(),v.getTop(),v.getRight(),v.getBottom());
        v.draw(canvas);
        return bitmap;
    }
}
