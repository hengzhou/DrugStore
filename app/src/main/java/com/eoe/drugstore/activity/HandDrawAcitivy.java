package com.eoe.drugstore.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HandDrawAcitivy extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        init(R.layout.activity_hand_draw_acitivy);
        setContentView(new DrawView(this, null));
    }

    class DrawView extends View {
        float preX;
        float preY;
        private Path path;
        public Paint paint = null;
        final int VIEW_WIDTH = 320;
        final int VIEW_HEIGHT = 480;

        //定义一个内存中的图片，该图片将作为缓冲区
        Bitmap cacheBitmap = null;
        //定义cacheBitmap上的Canvas对象
        Canvas cacheCanvas = null;

        public DrawView(Context context, AttributeSet attrs) {
            super(context, attrs);
            //创建一个与该View相同大小的缓存区
            cacheBitmap = Bitmap.createBitmap(VIEW_WIDTH, VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
            cacheCanvas = new Canvas();
            path = new Path();
            //设置cacheCanvas将会绘制到内存中cacheBitmap上
            cacheCanvas.setBitmap(cacheBitmap);
            //设置画笔的颜色
            paint = new Paint(Paint.DITHER_FLAG);
            paint.setColor(Color.RED);
            //设置画布的风格
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            //反锯齿
            paint.setAntiAlias(true);
            paint.setDither(true);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            //获取拖动事件的发生位置
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(x, y);
                    preX = x;
                    preY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    path.quadTo(preX, preY, x, y);
                    preX = x;
                    preY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    cacheCanvas.drawPath(path, paint);
                    paint.reset();
                    break;
            }
            invalidate();
            return true;
            //返回ture表明处理方法已经处理该事件
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint bmpPaint = new Paint();
            //将cacheBitmap绘制到该View组件上
            canvas.drawBitmap(cacheBitmap, 0, 0, bmpPaint);
            //沿着path绘制
            canvas.drawPath(path, paint);
        }
    }
}