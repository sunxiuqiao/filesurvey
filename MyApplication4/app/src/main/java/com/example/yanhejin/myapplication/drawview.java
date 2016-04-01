package com.example.yanhejin.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by licheetec on 2015/5/2.
 */
public class drawview extends View {

    private int view_width = 0;		//屏幕的宽度
    private int view_height = 0;	//屏幕的高度
    private float preX;				//起始点的x坐标
    private float preY;				//起始点的y坐标
    private Path path;				//路径
    Paint paint=null;		//画笔
    Bitmap cacheBitmap = null;		//定义一个内存中的图片，该图片将作为缓冲区
    Canvas cacheCanvas = null;
    public drawview(Context context,AttributeSet attrs) {
        super(context,attrs);
        view_width=context.getResources().getDisplayMetrics().widthPixels;
        view_height=context.getResources().getDisplayMetrics().heightPixels;
        cacheBitmap=Bitmap.createBitmap(view_width,view_height, Bitmap.Config.ARGB_8888);
        cacheCanvas=new Canvas();
        cacheCanvas.setBitmap(cacheBitmap);
        path=new Path();
        paint=new Paint(Paint.DITHER_FLAG);
        paint.setColor(Color.RED);//默认画笔为红色
        /*
        * 设置画笔颜色
        * */
        paint.setStyle(Paint.Style.STROKE);//设置填充方式为描边
        paint.setStrokeJoin(Paint.Join.ROUND);//设置画笔的图形样式
        paint.setStrokeCap(Paint.Cap.ROUND);//设置画笔转弯处的连接风格
        paint.setStrokeWidth(1);//设置默认画笔的宽度为1像素
        paint.setAntiAlias(true);//设置抗锯齿功能
        paint.setDither(true);//设置抖动效果
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0xffffff);//设置背景颜色
        Paint bmpPaint=new Paint();//采用默认画笔
        canvas.drawBitmap(cacheBitmap, 0, 0, bmpPaint);
        canvas.drawPath(path, paint);//保存绘制的画布状态，最后所有的信息保存在第一个创建的bitmap中
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();
        switch (event.getAction()){
            case  MotionEvent.ACTION_DOWN:
                path.moveTo(x,y);
                preX=x;
                preY=y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx=Math.abs(x-preX);
                float dy=Math.abs(y-preY);
                if (dx>=5||dy>=5){
                    path.quadTo(preX,preY,(x+preX)/2,(y+preY)/2);
                    preX=x;
                    preY=y;
                }
                break;
            case MotionEvent.ACTION_UP:
                cacheCanvas.drawPath(path,paint);
                path.reset();
                break;
        }
        invalidate();
        return true;
    }

    public void clear(){
        try {
            saveBitmap(String.valueOf(new PorterDuffXfermode(PorterDuff.Mode.CLEAR)));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void saveBitmap(String fileName) throws IOException {
        File file = new File("/sdcard/ArcGISSurvey/Image/" + fileName + ".png");	//创建文件对象
        file.createNewFile();	//创建一个新文件
        FileOutputStream fileOS = new FileOutputStream(file);	//创建一个文件输出流对象
        //将绘图内容压缩为png格式输出到输出流对象中,其中100代表品质
        cacheBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOS);
        fileOS.flush();		//将缓冲区中的数据全部写出到输出流中
        fileOS.close();		//关闭文件输出流对象
    }

}
