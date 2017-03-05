package com.mnn.nnn.clockviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

/**
 * Created by mnn on 2016/8/14.
 */

public class ClockView extends View {

    /**
     * 外圆半径
     */
    private float mRadius; //外圆半径
    /**
     * 边距
     */
    private float mPadding; //边距
    /**
     * 文字大小
     */
    private float mTextSize; //文字大小
    /**
     * 时针宽度
     */
    private float mHourPointWidth; //时针宽度
    /**
     * 分针宽度
     */
    private float mMinutePointWidth; //分针宽度
    /**
     * 秒针宽度
     */
    private float mSecondPointWidth; //秒针宽度
    /**
     * 指针圆角
     */
    private int mPointRadius; // 指针圆角
    /**
     * 指针末尾的长度
     */
    private float mPointEndLength; //指针末尾的长度

    /**
     * 长线的颜色
     */
    private int mColorLong; //长线的颜色
    /**
     * 短线的颜色
     */
    private int mColorShort; //短线的颜色
    /**
     * 时针的颜色
     */
    private int mHourPointColor; //时针的颜色
    /**
     * 分针的颜色
     */
    private int mMinutePointColor; //分针的颜色
    /**
     * 秒针的颜色
     */
    private int mSecondPointColor; //秒针的颜色

    /**
     * 画笔
     */
    private Paint mPaint; //画笔

    public ClockView(Context context) {
        super(context);
    }

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        obtainStyledAttrs(attrs); //获取自定义的属性
        init(); //初始化画笔
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 获取表盘圆的半径值与尾部长度值
     * 获取值应该在测量完成之后,所以在onSizeChange里面获取
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = (Math.min(w, h) - mPadding) / 2;
        mPointEndLength = mRadius / 6; //尾部指针默认为半径的六分之一
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setTextSize(40);

        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);

        //绘制外圆背景
        paintCircle(canvas);
        //绘制刻度
        paintScale(canvas);
        //绘制指针
        paintPointer(canvas);

        canvas.restore();//恢复 之前的中点在中心

        //刷新
        postInvalidateDelayed(1000);


    }

    /**
     * 绘制指针
     *
     * @param canvas
     */
    private void paintPointer(Canvas canvas) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY); //时
        int minute = calendar.get(Calendar.MINUTE); //分
        int second = calendar.get(Calendar.SECOND); //秒
        int angleHour = (hour % 12) * 360 / 12; //时针转过的角度
        int angleMinute = minute * 360 / 60; //分针转过的角度
        int angleSecond = second * 360 / 60; //秒针转过的角度


        //绘制时针
        canvas.save();
        canvas.rotate(angleHour); //旋转到时针的角度
        RectF rectFHour = new RectF(-mHourPointWidth / 2, -mRadius * 1 / 2, mHourPointWidth / 2, mPointEndLength);
        mPaint.setColor(mHourPointColor); //设置指针颜色
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mHourPointWidth); //设置边界宽度
        canvas.drawRoundRect(rectFHour, mPointRadius, mPointRadius, mPaint); //绘制时针
        canvas.restore();
        //绘制分针
        canvas.save();
        canvas.rotate(angleMinute);
        RectF rectFMinute = new RectF(-mMinutePointWidth / 2, -mRadius * 3.5f / 5, mMinutePointWidth / 2, mPointEndLength);
        mPaint.setColor(mMinutePointColor);
        mPaint.setStrokeWidth(mMinutePointWidth);
        canvas.drawRoundRect(rectFMinute, mPointRadius, mPointRadius, mPaint);
        canvas.restore();
        //绘制秒针
        canvas.save();
        canvas.rotate(angleSecond);
        RectF rectFSecond = new RectF(-mSecondPointWidth / 2, -mRadius + mPadding, mSecondPointWidth / 2, mPointEndLength);
        mPaint.setColor(mSecondPointColor);
        mPaint.setStrokeWidth(mSecondPointWidth);
        canvas.drawRoundRect(rectFSecond, mPointRadius, mPointRadius, mPaint);
        canvas.restore();
        //绘制中心小圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mSecondPointColor);
        canvas.drawCircle(0, 0, mSecondPointWidth * 4, mPaint);

    }

    /**
     * 绘制刻度
     *
     * @param canvas
     */

    private void paintScale(Canvas canvas) {
        mPaint.setStrokeWidth(DensityUtil.dp2px(getContext(), 1));

        int lineWidth = 0;


        for (int i = 0; i < 60; i++) {
            if (i % 5 == 0) {
                mPaint.setStrokeWidth(DensityUtil.dp2px(getContext(), 1.5f));
                mPaint.setColor(mColorLong);
                lineWidth = 40;
                //由于只有整点才有文字,所以文字的绘制就放到 整点 绘制的if里面.
                mPaint.setTextSize(mTextSize);
                String text = ((i / 5) == 0 ? 12 : (i / 5)) + "";
                Rect textBound = new Rect();
                mPaint.getTextBounds(text, 0, text.length(), textBound);

                mPaint.setColor(Color.BLACK);
                canvas.save();
                canvas.translate(0, -mRadius + DptoPx(15) + lineWidth + (textBound.bottom - textBound.top));
                canvas.rotate(-6 * i);
                canvas.drawText(text, -(textBound.right - textBound.left) / 2, textBound.bottom, mPaint);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.restore();

            } else {
                mPaint.setStrokeWidth(DensityUtil.dp2px(getContext(), 1));
                mPaint.setColor(mColorShort);
                lineWidth = 30;
            }

            canvas.drawLine(0, -mRadius + mPadding, 0, -mRadius + mPadding + lineWidth, mPaint);//-半径是因为在负半轴
            canvas.rotate(6);
        }
    }

    /**
     * 绘制外圆背景
     */
    private void paintCircle(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(0, 0, mRadius, mPaint);

    }

    //画笔初始化
    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    private void obtainStyledAttrs(AttributeSet attrs) {
        TypedArray array = null;
        try {
            array = getContext().obtainStyledAttributes(attrs, R.styleable.WatchBoard);
            mPadding = array.getDimension(R.styleable.WatchBoard_wb_padding, DptoPx(10));
            mTextSize = array.getDimension(R.styleable.WatchBoard_wb_text_size, SptoPx(16));
            mHourPointWidth = array.getDimension(R.styleable.WatchBoard_wb_hour_pointer_width, DptoPx(4));
            mMinutePointWidth = array.getDimension(R.styleable.WatchBoard_wb_minute_pointer_width, DptoPx(3));
            mSecondPointWidth = array.getDimension(R.styleable.WatchBoard_wb_second_pointer_width, DptoPx(2));
            mPointRadius = (int) array.getDimension(R.styleable.WatchBoard_wb_pointer_corner_radius, DptoPx(10));
            mPointEndLength = array.getDimension(R.styleable.WatchBoard_wb_pointer_end_length, DptoPx(10));

            mColorLong = array.getColor(R.styleable.WatchBoard_wb_scale_long_color, Color.argb(225, 0, 0, 0));
            mColorShort = array.getColor(R.styleable.WatchBoard_wb_scale_short_color, Color.argb(125, 0, 0, 0));
            mMinutePointColor = array.getColor(R.styleable.WatchBoard_wb_minute_pointer_color, Color.BLACK);
            mSecondPointColor = array.getColor(R.styleable.WatchBoard_wb_second_pointer_color, Color.RED);
            mHourPointColor = array.getColor(R.styleable.WatchBoard_wb_hour_pointer_color, Color.BLACK);
        } catch (Exception e) {
            //一旦出现错误全部使用默认值
            mPadding = DptoPx(10);
            mTextSize = SptoPx(16);
            mHourPointWidth = DptoPx(4);
            mMinutePointWidth = DptoPx(3);
            mSecondPointWidth = DptoPx(2);
            mPointRadius = (int) DptoPx(10);
            mPointEndLength = DptoPx(10);

            mColorLong = Color.argb(225, 0, 0, 0);
            mColorShort = Color.argb(125, 0, 0, 0);
            mMinutePointColor = Color.BLACK;
            mHourPointColor = Color.BLACK;

            mSecondPointColor = Color.RED;
        } finally {
            if (array != null) {
                array.recycle();
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 1000; //设定一个最小值


        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        if (widthMode == MeasureSpec.AT_MOST
                || widthMode == MeasureSpec.UNSPECIFIED
                || heightMeasureSpec == MeasureSpec.AT_MOST
                || heightMeasureSpec == MeasureSpec.UNSPECIFIED) {
            try {
                throw new NoDetermineSizeException("宽度高度至少有一个确定的值,不能同时为wrap_content");
            } catch (NoDetermineSizeException e) {
                e.printStackTrace();
            }
        } else { //至少有一个为确定值,要获取其中的最小值
            if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
                width = Math.min(widthSize, heightSize);
            } else {
                if (widthMode == MeasureSpec.EXACTLY) {
                    width = Math.min(widthSize, width);
                }
                if (heightMode == MeasureSpec.EXACTLY) {
                    width = Math.min(heightSize, width);
                }
            }
        }

        setMeasuredDimension(width, width);

    }

    //Dp转px
    private float DptoPx(int value) {

        return DensityUtil.dp2px(getContext(), value);
    }

    //sp转px
    private float SptoPx(int value) {
        return DensityUtil.sp2px(getContext(), value);
    }


    /**
     * 当宽高均为wrap_content的时候抛出异常
     */
    class NoDetermineSizeException extends Exception {
        public NoDetermineSizeException(String message) {
            super(message);
        }
    }

}
