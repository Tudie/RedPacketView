package com.example.redpacketview;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BaseInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import java.util.Random;

import static com.example.redpacketview.RedPacketView.dpToPixel;
import static com.example.redpacketview.RedPacketView.getRandomFloat;
import static com.example.redpacketview.RedPacketView.getRandomInt;
import static com.example.redpacketview.RedPacketView.getWindowWidthAndHeight;

public class MainActivity extends AppCompatActivity {

    private int[] mSize;//保存屏幕尺寸

    private FrameLayout mFrameLayout;

    private int mTotalAmount;//保存抢到的红包总金额

    private int mTotalAccount = 10000;//红包的个数

    private int mCurrentAccount;//当前生成的个数，一旦达到总的红包个数，停止继续生成红包

    private static final int mInitY = 60;

    private int mDuration = 3000;//每个动画的默认时长

    private int mDelay = 300;//每次生成红包的默认间隔

    private TimeInterpolator[] mInterpolators;//保存不同的插值器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mSize = getWindowWidthAndHeight(this);//获取屏幕的宽和高
        initInterpolator();//设置好插值器数组，然后随机设置插值器，红包的动画就会速度不同的效果
        mFrameLayout = findViewById(R.id.rl_container);

        startAnimation();//开始动画
    }



    //循环生成红包，使用static，不持有Activity的引用
    private static Handler mHandler = new Handler();

    private void startAnimation() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mCurrentAccount > mTotalAccount) {
                    return;
                }else {
                    mCurrentAccount++;
                }

                final RedPacketView redPacketView = new RedPacketView(MainActivity.this);
                redPacketView.setAmount(getRandomFloat(1000));//每个红包的额度暂定为50，其实这个应该是后端传过来的数据，这里省略

                mFrameLayout.addView(redPacketView);//把红包添加进来
                redPacketView.setX(getInitialX());

                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(redPacketView, "translationY",
                        -dpToPixel(mInitY),mSize[1] + dpToPixel(mInitY));

                //随机设置插值器
                objectAnimator.setInterpolator(mInterpolators[getRandomInt(3)]);

                //设置动画时长，也可以设置成随机的
                objectAnimator.setDuration(mDuration);

                //给动画添加监听器，当动画结束时，主要做两件事1.判断红包是否被拆开，如拆开，增加抢到的红包金额
                //2.remove子view
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        if (redPacketView.isClicked()) {
                            mTotalAmount += redPacketView.getAmount();

                            Log.e("MainActivity", "抢到的红包总额  " + mTotalAmount);
                        }

                        mFrameLayout.removeView(redPacketView);//一旦动画结束，立即remove，让系统及时回收
                    }
                });

                objectAnimator.start();
                mHandler.postDelayed(this, mDelay);
            }
        }, mDelay);
    }

    //红包生成时的初始X坐标，这里设置为0-屏幕width-红包width
    private float getInitialX(){
        int max = (int)(mSize[0] - dpToPixel(50));
        Random random = new Random();
        float ranNum = random.nextInt(max);
        return ranNum;
    }

    private void initInterpolator () {
        //这里设置了属性动画的不同的插值器，第一个是线性Interpolator,匀速，第二个持续加速，第三个先加速再减速
        //如果对动画还有不了解的，推荐博客:http://hencoder.com/page/2/
        mInterpolators = new BaseInterpolator[] {new LinearInterpolator(), new AccelerateInterpolator(),
                new AccelerateDecelerateInterpolator()};
    }


}