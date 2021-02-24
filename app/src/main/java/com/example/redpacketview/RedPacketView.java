package com.example.redpacketview;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.Random;

public class RedPacketView extends View {

    private float amount;//每个红包的金额

    private boolean isClicked;//判断红包是否被点击过，点击之后再点不会触发效果

    private Bitmap redPacketBitmap;//红包对应的bitmap

    private Paint paint;

    private int width = 50;
    private int height= 50;

    public RedPacketView(Context context) {
        this(context, null);
    }

    public RedPacketView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RedPacketView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置默认的为拆开的红包图片
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.hongbao);
        redPacketBitmap = Bitmap.createScaledBitmap(bitmap, (int) dpToPixel(width), (int) dpToPixel(height), false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //这里直接设置了大小，如果想更精确一些，也可以设置为屏幕宽度和高度的百分比,这样适配会更好一些
        setMeasuredDimension((int) dpToPixel(width), (int) dpToPixel(height));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(redPacketBitmap, 0, 0, paint);
    }

    //设置红包被拆开的图片
    private void setRedPacketBitmap() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.hongbao1);
        redPacketBitmap = Bitmap.createScaledBitmap(bitmap, (int)dpToPixel(width), (int)dpToPixel(height), false);
        invalidate();
//        this.setVisibility(INVISIBLE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN :

                //获取点击事件,如果未被拆开，点击之后设置拆开的图片
                if (!isClicked()) {
                    setClicked(true);
                    setRedPacketBitmap();
                }

                break;
        }

        return true;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    //dp转px
    public static float dpToPixel(float dp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return dp * displayMetrics.density;
    }

    //获取屏幕尺寸
    public static int[] getWindowWidthAndHeight(Context context) {
        WindowManager windowManager = ((Activity)context).getWindowManager();
        return new int[] {windowManager.getDefaultDisplay().getWidth(),
                windowManager.getDefaultDisplay().getHeight() };
    }


    public static float getRandomFloat(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    public static int getRandomInt(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }
}
