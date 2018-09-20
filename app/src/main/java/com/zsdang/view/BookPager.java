package com.zsdang.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class BookPager extends View {

    private Paint mCurrentPagePaint;
    private Paint mBackPagePaint;
    private Paint mNextPagePaint;

    private Path mCurrentPagePath;

    private Bitmap mBitmap;

    private Canvas mCanvas;




    private Paint pointPaint;//绘制各标识点的画笔
    private Paint bgPaint;//背景画笔
    private MyPoint a,f,g,e,h,c,j,b,k,d,i;

    private int defaultWidth;//默认宽度
    private int defaultHeight;//默认高度
    private int viewWidth;
    private int viewHeight;

    private Paint pathAPaint;//绘制A区域画笔
    private Path pathA;
    private Paint pathBPaint;//绘制A区域画笔
    private Path pathB;
    private Paint pathCPaint;//绘制A区域画笔
    private Path pathC;
    private Bitmap bitmap;//缓存bitmap
    private Canvas bitmapCanvas;



    public BookPager(Context context) {
        this(context, null);
    }

    public BookPager(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BookPager(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BookPager(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        defaultWidth = 600;
        defaultHeight = 1000;

        viewWidth = defaultWidth;
        viewHeight = defaultHeight;

        a = new MyPoint();
        f = new MyPoint();
        g = new MyPoint();
        e = new MyPoint();
        h = new MyPoint();
        c = new MyPoint();
        j = new MyPoint();
        b = new MyPoint();
        k = new MyPoint();
        d = new MyPoint();
        i = new MyPoint();
        calcPointsXY(a,f);

        pointPaint = new Paint();
        pointPaint.setColor(Color.RED);
        pointPaint.setTextSize(25);

        bgPaint = new Paint();
        bgPaint.setColor(Color.GREEN);


        pathAPaint = new Paint();
        pathAPaint.setColor(Color.BLUE);
        pathAPaint.setAntiAlias(true);//设置抗锯齿

        pathA = new Path();

        pathBPaint = new Paint();
        pathBPaint.setColor(Color.RED);
        pathBPaint.setAntiAlias(true);//设置抗锯齿
        pathBPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));

        pathB = new Path();


        pathCPaint = new Paint();
        pathCPaint.setColor(Color.YELLOW);
        pathCPaint.setAntiAlias(true);//设置抗锯齿
        pathCPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));

        pathC = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        bitmap = Bitmap.createBitmap((int) viewWidth, (int) viewHeight, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        if(a.x==-1 && a.y==-1){
            bitmapCanvas.drawPath(getPathDefault(),pathAPaint);
        }else {
            if(f.x==viewWidth && f.y==0){
                bitmapCanvas.drawPath(getPathAFromTopRight(),pathAPaint);
            }else if(f.x==viewWidth && f.y==viewHeight){
                bitmapCanvas.drawPath(getPathAFromLowerRight(),pathAPaint);
            }

            bitmapCanvas.drawPath(getPathC(),pathCPaint);
            bitmapCanvas.drawPath(getPathB(),pathBPaint);
        }
        canvas.drawBitmap(bitmap,0,0,null);

    }
    private Path getPathAFromTopRight(){
        pathA.reset();
        pathA.lineTo(c.x,c.y);//移动到c点
        pathA.quadTo(e.x,e.y,b.x,b.y);//从c到b画贝塞尔曲线，控制点为e
        pathA.lineTo(a.x,a.y);//移动到a点
        pathA.lineTo(k.x,k.y);//移动到k点
        pathA.quadTo(h.x,h.y,j.x,j.y);//从k到j画贝塞尔曲线，控制点为h
        pathA.lineTo(viewWidth,viewHeight);//移动到右下角
        pathA.lineTo(0, viewHeight);//移动到左下角
        pathA.close();
        return pathA;
    }


    private String style;
    public static final String STYLE_LEFT = "STYLE_LEFT";//点击左边区域
    public static final String STYLE_RIGHT = "STYLE_RIGHT";//点击右边区域
    public static final String STYLE_MIDDLE = "STYLE_MIDDLE";//点击中间区域
    public static final String STYLE_TOP_RIGHT = "STYLE_TOP_RIGHT";//f点在右上角
    public static final String STYLE_LOWER_RIGHT = "STYLE_LOWER_RIGHT";//f点在右下角


    /**
     * 设置触摸点
     * @param x
     * @param y
     * @param style
     */
    public void setTouchPoint(float x, float y, String style){
        MyPoint touchPoint = new MyPoint();
        a.x = x;
        a.y = y;
        this.style = style;
        switch (style){
            case STYLE_TOP_RIGHT:
                f.x = viewWidth;
                f.y = 0;
                calcPointsXY(a,f);
                touchPoint = new MyPoint(x,y);
                if(calcPointCX(touchPoint,f)<0){//如果c点x坐标小于0则重新测量a点坐标
                    calcPointAByTouchPoint();
                    calcPointsXY(a,f);
                }
                postInvalidate();
                break;
            case STYLE_LEFT:
            case STYLE_RIGHT:
                a.y = viewHeight-1;
                f.x = viewWidth;
                f.y = viewHeight;
                calcPointsXY(a,f);
                postInvalidate();
                break;
            case STYLE_LOWER_RIGHT:
                f.x = viewWidth;
                f.y = viewHeight;
                calcPointsXY(a,f);
                touchPoint = new MyPoint(x,y);
                if(calcPointCX(touchPoint,f)<0){//如果c点x坐标小于0则重新测量a点坐标
                    calcPointAByTouchPoint();
                    calcPointsXY(a,f);
                }
                postInvalidate();
                break;
            default:
                break;
        }
    }

    private void calcPointAByTouchPoint(){
        float w0 = viewWidth - c.x;

        float w1 = Math.abs(f.x - a.x);
        float w2 = viewWidth * w1 / w0;
        a.x = Math.abs(f.x - w2);

        float h1 = Math.abs(f.y - a.y);
        float h2 = w2 * h1 / w1;
        a.y = Math.abs(f.y - h2);
    }

    private float calcPointCX(MyPoint a, MyPoint f){
        MyPoint g,e;
        g = new MyPoint();
        e = new MyPoint();
        g.x = (a.x + f.x) / 2;
        g.y = (a.y + f.y) / 2;

        e.x = g.x - (f.y - g.y) * (f.y - g.y) / (f.x - g.x);
        e.y = f.y;

        return e.x - (f.x - e.x) / 2;
    }

    /**
     * 回到默认状态
     */
    public void setDefaultPath(){
        a.x = -1;
        a.y = -1;
        postInvalidate();
    }

    /**
     * 绘制默认的界面
     * @return
     */
    private Path getPathDefault(){
        pathA.reset();
        pathA.lineTo(0, viewHeight);
        pathA.lineTo(viewWidth,viewHeight);
        pathA.lineTo(viewWidth,0);
        pathA.close();
        return pathA;
    }

    public float getViewWidth(){
        return viewWidth;
    }

    public float getViewHeight(){
        return viewHeight;
    }

    /**
     * 计算各点坐标
     * @param a
     * @param f
     */
    private void calcPointsXY(MyPoint a, MyPoint f){
        g.x = (a.x + f.x) / 2;
        g.y = (a.y + f.y) / 2;

        e.x = g.x - (f.y - g.y) * (f.y - g.y) / (f.x - g.x);
        e.y = f.y;

        h.x = f.x;
        h.y = g.y - (f.x - g.x) * (f.x - g.x) / (f.y - g.y);

        c.x = e.x - (f.x - e.x) / 2;
        c.y = f.y;

        j.x = f.x;
        j.y = h.y - (f.y - h.y) / 2;

        b = getIntersectionPoint(a,e,c,j);
        k = getIntersectionPoint(a,h,c,j);

        d.x = (c.x + 2 * e.x + b.x) / 4;
        d.y = (2 * e.y + c.y + b.y) / 4;

        i.x = (j.x + 2 * h.x + k.x) / 4;
        i.y = (2 * h.y + j.y + k.y) / 4;
    }

    /**
     * 计算两线段相交点坐标
     * @param lineOne_My_pointOne
     * @param lineOne_My_pointTwo
     * @param lineTwo_My_pointOne
     * @param lineTwo_My_pointTwo
     * @return 返回该点
     */
    private MyPoint getIntersectionPoint(MyPoint lineOne_My_pointOne, MyPoint lineOne_My_pointTwo, MyPoint lineTwo_My_pointOne, MyPoint lineTwo_My_pointTwo){
        float x1,y1,x2,y2,x3,y3,x4,y4;
        x1 = lineOne_My_pointOne.x;
        y1 = lineOne_My_pointOne.y;
        x2 = lineOne_My_pointTwo.x;
        y2 = lineOne_My_pointTwo.y;
        x3 = lineTwo_My_pointOne.x;
        y3 = lineTwo_My_pointOne.y;
        x4 = lineTwo_My_pointTwo.x;
        y4 = lineTwo_My_pointTwo.y;

        float pointX =((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1))
                / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
        float pointY =((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1) * (y3 - y4))
                / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));

        return  new MyPoint(pointX,pointY);
    }

    /**
     * 获取f点在右下角的pathA
     * @return
     */
    private Path getPathAFromLowerRight(){
        pathA.reset();
        pathA.lineTo(0, viewHeight);//移动到左下角
        pathA.lineTo(c.x,c.y);//移动到c点
        pathA.quadTo(e.x,e.y,b.x,b.y);//从c到b画贝塞尔曲线，控制点为e
        pathA.lineTo(a.x,a.y);//移动到a点
        pathA.lineTo(k.x,k.y);//移动到k点
        pathA.quadTo(h.x,h.y,j.x,j.y);//从k到j画贝塞尔曲线，控制点为h
        pathA.lineTo(viewWidth,0);//移动到右上角
        pathA.close();//闭合区域
        EditText et;
        return pathA;

    }

    /**
     * 绘制区域B
     * @return
     */
    private Path getPathB(){
        pathB.reset();
        pathB.lineTo(0, viewHeight);//移动到左下角
        pathB.lineTo(viewWidth,viewHeight);//移动到右下角
        pathB.lineTo(viewWidth,0);//移动到右上角
        pathB.close();//闭合区域
        return pathB;
    }

    /**
     * 绘制区域C
     * @return
     */
    private Path getPathC(){
        pathC.reset();
        pathC.moveTo(i.x,i.y);//移动到i点
        pathC.lineTo(d.x,d.y);//移动到d点
        pathC.lineTo(b.x,b.y);//移动到b点
        pathC.lineTo(a.x,a.y);//移动到a点
        pathC.lineTo(k.x,k.y);//移动到k点
        pathC.close();//闭合区域
        return pathC;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = measureSize(defaultHeight, heightMeasureSpec);
        int width = measureSize(defaultWidth, widthMeasureSpec);
        setMeasuredDimension(width, height);

        viewWidth = width;
        viewHeight = height;
        a.x = -1;
        a.y = -1;
    }

    private int measureSize(int defaultSize,int measureSpec) {
        int result = defaultSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("suby1", "onTouchEvent");
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                float width = getViewWidth();
                float height = getViewHeight();
                if(x<=width/3){//左
                    style = STYLE_LEFT;
//                            Toast.makeText(PageActivity.this,"点击了左部",Toast.LENGTH_SHORT).show();
                    setTouchPoint(x,y,style);

                }else if(x>width/3 && y<=height/3){//上
                    style = STYLE_TOP_RIGHT;
//                            Toast.makeText(PageActivity.this,"点击了上部",Toast.LENGTH_SHORT).show();
                    setTouchPoint(x,y,style);

                }else if(x>width*2/3 && y>height/3 && y<=height*2/3){//右
                    style = STYLE_RIGHT;
//                            Toast.makeText(PageActivity.this,"点击了右部",Toast.LENGTH_SHORT).show();
                    setTouchPoint(x,y,style);

                }else if(x>width/3 && y>height*2/3){//下
                    style = STYLE_LOWER_RIGHT;
//                            Toast.makeText(PageActivity.this,"点击了下部",Toast.LENGTH_SHORT).show();
                    setTouchPoint(x,y,style);

                }else if(x>width/3 && x<width*2/3 && y>height/3 && y<height*2/3){//中
                    style = STYLE_MIDDLE;
//                            Toast.makeText(PageActivity.this,"点击了中部",Toast.LENGTH_SHORT).show();
//                            bookPageView.setTouchPoint(x,y,bookPageView.STYLE_MIDDLE);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                setTouchPoint(event.getX(),event.getY(),style);
                break;
            case MotionEvent.ACTION_UP:
                setDefaultPath();
                break;
        }
        return true;
    }

    public class MyPoint {
        public float x,y;
        public MyPoint(){}
        public MyPoint(float x, float y){
            this.x = x;
            this.y = y;
        }
    }
}
