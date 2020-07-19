package com.example.zavrnirad;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

class colorData extends View {
    Paint paint=new Paint();
    Path mPath=new Path();
    Canvas canvasView;

    private Paint mSemiBlackPaint;
    public colorData(Context context) {
        super(context);
        setWillNotDraw(false);
        init(null,0);
        initPaints();
    }
    public void init(AttributeSet attributeSet,int defStyle)
    {
        initPaints();
    }

    @Override
    public void onDraw(Canvas  canvas) {
        super.onDraw(canvas);

        canvasView=canvas;

        mPath.reset();

        mPath.addRect(getWidth()-((float)(getWidth()*0.90)),getHeight()-((float)(getHeight()*0.85)), getWidth()-((float)(getWidth()*0.10)),getHeight()-((float)(getHeight()*0.15)),Path.Direction.CW);
        mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);

        paint.setColor(Color.rgb(250,203,190));
        paint.setStrokeWidth(8);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawPath(getPath(getHeight(),getWidth()),paint);

        canvas.drawPath(mPath, mSemiBlackPaint);
        canvas.clipPath(mPath);
        canvas.drawColor(Color.parseColor("#A6000000"));
    }
    private void initPaints() {
        mSemiBlackPaint = new Paint();
        mSemiBlackPaint.setColor(Color.TRANSPARENT);
        mSemiBlackPaint.setStrokeWidth(10);
    }

    private Path getPath(int h, int w)
    {
        Path path=new Path();
        float left,right,top,bottom;

        left=(float) w-((float)(w*0.85));//85
        right=(float)w-((float)(w*0.15));
        top=(float) h-((float)(h*0.65));//70
        bottom=(float) h-((float)(h*0.55));
        path.moveTo(left, top + 25);
        path.lineTo(left,top);
        path.lineTo(left + 25,top);

        path.moveTo(right-25, top);
        path.lineTo(right,top);
        path.lineTo(right,top+ 25);

        path.moveTo(left, bottom-25);
        path.lineTo(left,bottom);
        path.lineTo(left + 25,bottom);

        path.moveTo(right-25, bottom);
        path.lineTo(right,bottom);
        path.lineTo(right,bottom-25);


        return path;
    }
    public colorData(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }
    public Rect getRect()
    {
        Rect rect=new Rect();
        int left,right,top,bottom;
        int w=getWidth();
        int h=getHeight();
        left= w-((int)(w*0.90));
        right=w-((int)(w*0.05));
        top=h-((int)(h*0.70));
        bottom= h-((int)(h*0.50));

        rect.set(left,top,right,bottom);
        Log.v("aaefef",""+w+"  "+h+" "+rect);
        return  rect;
    }

}
