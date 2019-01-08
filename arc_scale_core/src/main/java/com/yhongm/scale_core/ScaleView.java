package com.yhongm.scale_core;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by yuhongmiao on 2017/4/24.
 */

public class ScaleView extends View {
    private int mWidth;
    private int mHeight;
    private int mCenterX;
    private int mCenterY;
    private int radius;
    private int padding = 10;
    private float currentAngle = 0;
    private double initAngle;
    private Path mArcPath;
    private Paint mArcPaint;// 弧线画笔
    private Paint mScaleLinePaint;//刻度线画笔
    private TextPaint mScaleTextPaint;//刻度值画笔
    private TextPaint mSelectedTextPaint;//选中刻度线画笔
    private Paint mCurrentSelectValuePaint;//当前值画笔

    private Paint mIndicatorPaint;//指针画笔
    private Paint mMaskPaint;//画遮罩层画笔
    private SelectScaleListener selectScaleListener;//选择器监听
    private String mScaleUnit = "单位";//刻度单位
    private float mEvenyScaleValue = 1;//每滑动多少度,值得增加
    private int mScaleNumber = 30;//刻度数量
    private int mScaleSpace = 1;//刻度间距
    private int mScaleMin = 200;//刻度最小值
    private int mDrawLineSpace = 1;//画线间距
    private int mDrawTextSpace = 5;//画刻度值的间隔

    private int mArcLineColor = Color.RED;//弧线颜色
    private int mScaleLineColor = Color.BLUE;//刻度线颜色
    private int mIndicatorColor = Color.GREEN;
    private int mScaleTextColor = Color.BLACK;//刻度文字颜色
    private int mSelectTextColor = Color.BLACK;//选中刻度文字颜色

    private int mScaleMaxLength = 100;//刻度尺的长度

    private int eachScalePix = 15;//每个刻度值的像素
    private int mSlidingMoveX = 0;//滑动的差值
    private int totalX = 0;//滑动总距离
    private int mDownX;
    private boolean isArc = false;//是否为弧形刻度
    private int currentValue = 0;// 当前刻度值


    public ScaleView(Context context) {
        this(context, null);

    }

    public ScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        initAttr(context, attrs);
        initPath();
        initPaint();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleView);
        int shape = typedArray.getInt(R.styleable.ScaleView_shape, 0);
        if (shape == 0) {
            isArc = true;
        } else {
            isArc = false;
        }
        mScaleUnit = typedArray.getString(R.styleable.ScaleView_scaleUnit);
        if (TextUtils.isEmpty(mScaleUnit)) {
            mScaleUnit = "";
        }
        mEvenyScaleValue = typedArray.getFloat(R.styleable.ScaleView_everyScaleValue, 1);
        mScaleNumber = typedArray.getInt(R.styleable.ScaleView_scaleNum, 30);
        mScaleSpace = typedArray.getInt(R.styleable.ScaleView_scaleSpace, 1);
        mScaleMin = typedArray.getInt(R.styleable.ScaleView_scaleMin, 30);
        mDrawLineSpace = typedArray.getInt(R.styleable.ScaleView_drawLineSpace, 1);
        mDrawTextSpace = typedArray.getInt(R.styleable.ScaleView_drawTextSpace, 5);

        mArcLineColor = typedArray.getColor(R.styleable.ScaleView_arcLineColor, Color.RED);
        mScaleLineColor = typedArray.getColor(R.styleable.ScaleView_scaleLineColor, Color.RED);
        mIndicatorColor = typedArray.getColor(R.styleable.ScaleView_indicatorColor, Color.GREEN);
        mScaleTextColor = typedArray.getColor(R.styleable.ScaleView_scaleTextColor, Color.BLACK);
        mSelectTextColor = typedArray.getColor(R.styleable.ScaleView_selectTextColor, Color.BLACK);

        mScaleLineColor = typedArray.getColor(R.styleable.ScaleView_scaleLineColor, Color.parseColor("#666666"));

        mScaleTextColor = typedArray.getColor(R.styleable.ScaleView_scaleTextColor, Color.parseColor("#666666"));

        mIndicatorColor = typedArray.getColor(R.styleable.ScaleView_indicatorColor, Color.parseColor("#ff9933"));
        mScaleMaxLength = typedArray.getInt(R.styleable.ScaleView_scaleMaxLength, 100);

    }

    private void initPaint() {
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setColor(mArcLineColor);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(18);

        mScaleLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScaleLinePaint.setColor(mScaleLineColor);
        mScaleLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mScaleLinePaint.setStyle(Paint.Style.STROKE);

        mScaleTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mScaleTextPaint.setColor(mScaleTextColor);
        mScaleTextPaint.setTypeface(Typeface.SANS_SERIF);
        mScaleTextPaint.setTextSize(20);


        mSelectedTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mSelectedTextPaint.setTypeface(Typeface.SERIF);
        mSelectedTextPaint.setColor(mSelectTextColor);
        mSelectedTextPaint.setTextSize(50);

        mIndicatorPaint = new Paint();
        mIndicatorPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mIndicatorPaint.setStrokeCap(Paint.Cap.ROUND);
        mIndicatorPaint.setColor(mIndicatorColor);
        mIndicatorPaint.setStrokeWidth(18);

        mMaskPaint = new Paint();
        mMaskPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mScaleLinePaint = new Paint();
        mScaleLinePaint.setAntiAlias(true);
        mScaleLinePaint.setStyle(Paint.Style.STROKE);
        mScaleLinePaint.setStrokeWidth(2);
        mScaleLinePaint.setColor(mScaleLineColor);


        mCurrentSelectValuePaint = new Paint();
        mCurrentSelectValuePaint.setAntiAlias(true);
        mCurrentSelectValuePaint.setTextSize(30);
        mCurrentSelectValuePaint.setColor(mSelectTextColor);
        mCurrentSelectValuePaint.setTextAlign(Paint.Align.CENTER);

        mIndicatorPaint = new Paint();
        mIndicatorPaint.setAntiAlias(true);
        mIndicatorPaint.setColor(mIndicatorColor);


    }

    private void initPath() {
        mArcPath = new Path();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        if (isArc) {

            mCenterX = mWidth / 2;

            mCenterY = 0;

            radius = Math.min(mWidth / 2 - padding, mHeight / 2 - padding);
        } else {

            mCenterY = getMeasuredHeight() / 2;
            mCenterX = getMeasuredWidth() / 2;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isArc) {
            //绘制弧形刻度尺选择器
            drawArc(canvas);
            drawScale(canvas);
            drawSelectedScale(canvas);
            drawIndicator(canvas);
            drawMask(canvas);
        } else {
            //绘制直尺刻度尺选择器
            drawCurrentScale(canvas);

            drawNum(canvas);

            drawMask(canvas);
        }
    }

    /**
     * 画上层遮罩
     *
     * @param canvas
     */
    private void drawMask(Canvas canvas) {
        if (isArc) {
            //画左侧遮罩
            LinearGradient mLeftLinearGradient = new LinearGradient(0, 0, mCenterX - 100, 150, Color.WHITE, Color.TRANSPARENT, Shader.TileMode.CLAMP);
            mMaskPaint.setShader(mLeftLinearGradient);
            canvas.drawPath(mArcPath, mMaskPaint);
            //画右侧遮罩
            LinearGradient rightLinearGradient = new LinearGradient(mWidth, 0, mCenterX + 100, 150, Color.WHITE, Color.TRANSPARENT, Shader.TileMode.CLAMP);
            mMaskPaint.setShader(rightLinearGradient);
            canvas.drawPath(mArcPath, mMaskPaint);

            canvas.drawPath(mArcPath, mArcPaint);
        } else {

            mMaskPaint.setStrokeWidth(mHeight);
            LinearGradient mLeftLinearGradient = new LinearGradient(0, mCenterY, 100, mCenterY, Color.parseColor("#01000000"), Color.parseColor("#11000000"), Shader.TileMode.CLAMP);
            mMaskPaint.setShader(mLeftLinearGradient);
            canvas.drawLine(0, mCenterY, mCenterX, mCenterY, mMaskPaint);
            LinearGradient mRightLinearGradient = new LinearGradient(mWidth - 100, mCenterY, mWidth, mCenterY, Color.parseColor("#11000000"), Color.parseColor("#01000000"), Shader.TileMode.CLAMP);
            mMaskPaint.setShader(mRightLinearGradient);
            canvas.drawLine(mCenterX, mCenterY, mWidth, mCenterY, mMaskPaint);

        }
    }

    /**
     * 画指示器
     *
     * @param canvas
     */
    private void drawIndicator(Canvas canvas) {
        PathMeasure mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mArcPath, false);
        float[] tan = new float[2];
        float[] pos = new float[2];
        mPathMeasure.getPosTan(mPathMeasure.getLength() * (0.5f), pos, tan);
        canvas.save();
        double angle = calcArcAngle(Math.atan2(tan[1], tan[0])) + 90;
        canvas.rotate((float) angle, pos[0], pos[1]);
        //画直线
        canvas.drawLine(pos[0], pos[1], pos[0] + 80, pos[1], mIndicatorPaint);
        Path linePath = new Path();
        //画箭头
        linePath.moveTo(pos[0] + 80, pos[1] - 20);
        linePath.lineTo(pos[0] + 80 + 20, pos[1]);
        linePath.lineTo(pos[0] + 80, pos[1] + 20);
        canvas.drawPath(linePath, mIndicatorPaint);
        canvas.restore();
    }

    /**
     * 画选中的刻度值
     *
     * @param canvas
     */
    private void drawSelectedScale(Canvas canvas) {
        int selectedScale = Math.round(currentAngle + mScaleMin + (mScaleNumber / 2) * mScaleSpace);
        if (selectScaleListener != null) {
            selectScaleListener.selectScale(selectedScale);
        }
        String selectedScaleText = selectedScale + mScaleUnit;
        float selectedScaleTextLength = mSelectedTextPaint.measureText(selectedScaleText, 0, selectedScaleText.length());
        canvas.drawText(selectedScaleText, mCenterX - selectedScaleTextLength / 2, mCenterY + 100, mSelectedTextPaint);
    }

    /**
     * 画刻度线和刻度
     *
     * @param canvas
     */
    private void drawScale(Canvas canvas) {
        PathMeasure mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mArcPath, false);
        float[] pos = new float[2];
        float[] tan = new float[2];
        for (int i = 1; i <= mScaleNumber; i++) {
            float percentage = i / (float) mScaleNumber;//所占百分比
            mPathMeasure.getPosTan(mPathMeasure.getLength() * percentage, pos, tan);
            double atan2 = Math.atan2(tan[1], tan[0]);
            double angle = calcArcAngle(atan2) + 90;//刻度线垂直切线，所以旋转90°

            int scale = Math.round(currentAngle + mScaleMin + i * mScaleSpace);

            if (scale >= mScaleMin && scale % mDrawLineSpace == 0) {
                float startX = pos[0];
                float startY = pos[1];
                float endX = 0;
                float endY = pos[1];
                if (scale % mDrawTextSpace == 0) {
                    endX = pos[0] + 80;
                    mScaleLinePaint.setStrokeWidth(15);
                    mScaleLinePaint.setColor(mScaleLineColor);
                    if (currentAngle >= (-(mScaleNumber / 2) * mScaleSpace)) {
                        canvas.save();
                        canvas.rotate((float) (angle + 90), pos[0], pos[1]);
                        String mScaleText = scale + mScaleUnit;
                        float scaleTextLength = mScaleTextPaint.measureText(mScaleText, 0, mScaleText.length());
                        canvas.drawText(mScaleText, pos[0] - scaleTextLength / 2, pos[1] - 130, mScaleTextPaint);
                        canvas.restore();
                    }
                } else if (scale % mDrawLineSpace == 0) {
                    mScaleLinePaint.setColor(mScaleTextColor);
                    mScaleLinePaint.setStrokeWidth(10);
                    endX = pos[0] + 50;
                }

                canvas.save();
                canvas.rotate((float) angle, pos[0], pos[1]);
                canvas.drawLine(startX, startY, endX, endY, mScaleLinePaint);
                canvas.restore();

            }
        }
    }

    /**
     * 画半圆
     *
     * @param canvas
     */
    private void drawArc(Canvas canvas) {
        int top = -radius;
        int bottom = radius;
        int left = mCenterX - radius;
        int right = mCenterX + radius;
        mArcPath.reset();

        mArcPath.addArc(new RectF(left, top, right, bottom), 0, 180);

        canvas.drawPath(mArcPath, mArcPaint);
    }

    /**
     * 绘画数字
     *
     * @param canvas
     */
    private void drawNum(Canvas canvas) {
        mScaleTextPaint.setStrokeWidth(2);
        mScaleTextPaint.setColor(mScaleTextColor);
        mScaleTextPaint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < mWidth; i++) {
            int top = mCenterY + 10;
            if ((-totalX + i) % (eachScalePix * mDrawTextSpace) == 0) {
                top = top + 30;
                if ((-totalX + i) >= 0 && (-totalX + i) <= mScaleMaxLength * eachScalePix)
                    canvas.drawText((-totalX + i) / eachScalePix + mScaleMin + "", i, top + 20, mScaleTextPaint);

            }
            if ((-totalX + i) % eachScalePix == 0) {
                if ((-totalX + i) >= 0 && (-totalX + i) <= mScaleMaxLength * eachScalePix) {
                    canvas.drawLine(i, mCenterY, i, top, mScaleLinePaint);
                }

            }

        }
    }

    /**
     * 绘画刻度
     *
     * @param canvas
     */
    private void drawCurrentScale(Canvas canvas) {
        currentValue = (-totalX + mCenterX) / eachScalePix + mScaleMin;
        if (selectScaleListener != null) {
            selectScaleListener.selectScale(currentValue);
        }
        RectF roundRectF = new RectF();
        roundRectF.left = mCenterX - 3;
        roundRectF.right = mCenterX + 3;
        roundRectF.top = mCenterY;
        roundRectF.bottom = mCenterY + 50;
        canvas.drawRoundRect(roundRectF, 6, 6, mIndicatorPaint);
        String currentScaleText = currentValue + "";

        canvas.drawText(currentScaleText + mScaleUnit, mCenterX, mCenterY - 10, mCurrentSelectValuePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isArc) {
                    float mDownX = event.getX();

                    float mDownY = event.getY();

                    initAngle = computeAngle(mDownX, mDownY);
                    if (mDownX > mCenterX) {
                        initAngle = 180 - initAngle;
                    }
                } else {
                    mDownX = (int) event.getX();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isArc) {
                    float mTouchX = event.getX();
                    float mTouchY = event.getY();
                    if (isTouch(mTouchX, mTouchY)) {
                        double moveAngle = computeAngle(mTouchX, mTouchY);
                        if (mTouchX > mCenterX) {
                            moveAngle = 180 - moveAngle;
                        }
                        double tempAngle = moveAngle - initAngle;
                        long addValue = Math.round(tempAngle * mEvenyScaleValue);
                        currentAngle += addValue;
                        if (currentAngle >= -(mScaleNumber / 2) * mScaleSpace) {
                            invalidate();

                        } else {
                            currentAngle = currentAngle - addValue;

                        }
                        initAngle = moveAngle;
                        return true;
                    }
                } else {
                    mSlidingMoveX = (int) (event.getX() - mDownX);//滑动距离
                    totalX = totalX + mSlidingMoveX;
//                     if (mSlidingMoveX < 0) {
//                         //向左滑动,刻度值增大
//                         if (-totalX + mCenterX > mScaleMaxLength * eachScalePix) {
//                             //向左滑动如果刻度值大于最大值，则不能滑动了
//                             totalX = totalX - mSlidingMoveX;
//                             return true;
//                         } else {
//                             invalidate();
//                         }
//                     } else {
//                         //向右滑动，刻度值减小
// //                    向右滑动刻度值小于最小值则不能滑动了
//                         if (totalX - mCenterX > 0) {
//                             totalX = totalX - mSlidingMoveX;
//                             return true;
//                         } else {
//                             invalidate();

//                         }
//                     }
                    // fix g19980115建议解决滑动边界问题  see issue https://github.com/yhongm/ScaleView/issues/1
                    //向左滑动,刻度值增大
                    if (mSlidingMoveX < 0) {
         
                          if (-totalX + mCenterX > mScaleMaxLength * eachScalePix) {
                           //向左滑动如果刻度值大于最大值，则不能滑动了
                               totalX = -mScaleMaxLength * eachScalePix + mCenterX;
                               invalidate();
                               return true;
                          } else {
                               invalidate();
                    
                    } else {
                           //向右滑动，刻度值减小
                           //向右滑动刻度值小于最小值则不能滑动了
                           if (totalX - mCenterX > 0) {
                               totalX = mCenterX;
                               invalidate();
                               return true;
                           } else {
                              invalidate();
                           }
                   }
                    mDownX = (int) event.getX();

                }

                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 计算角度
     *
     * @param touchX
     * @param touchY
     * @retuen 旋转角度
     */
    private double computeAngle(float touchX, float touchY) {
        double atan2 = Math.atan2(touchY, touchX);
        double taperedEdge = Math.sqrt(Math.pow(touchX - mCenterX, 2) + Math.pow(touchY - mCenterY, 2));//计算斜边
        double sin = (touchY - mCenterY) / taperedEdge;
        double asin = Math.asin(sin);
        double calcArcAngle = calcArcAngle(asin);
        return calcArcAngle;
    }

    private double calcArcAngle(double arc) {
        double angle = arc * 180.0 / Math.PI;
        return angle;
    }

    /**
     * 是否在触摸范围内
     *
     * @param touchX 触摸点x坐标
     * @param touchY 触摸点y坐标
     */
    private boolean isTouch(float touchX, float touchY) {
        float x = touchX - mCenterX;
        float y = touchY - mCenterY;
        return Math.pow(x, 2) + Math.pow(y, 2) < Math.pow(radius, 2);
    }

    /**
     * 选择器监听
     */
    public interface SelectScaleListener {
        void selectScale(int value);
    }

    public void setSelectScaleListener(SelectScaleListener listener) {
        this.selectScaleListener = listener;
    }

    /**
     * 设置刻度单位
     *
     * @param scaleUnit
     */
    public void setScaleUnit(String scaleUnit) {
        this.mScaleUnit = scaleUnit;
    }

    /**
     * 设置每度代表的刻度值
     *
     * @param everyScaleValue
     */
    public void setEvenyScaleValue(float everyScaleValue) {
        this.mEvenyScaleValue = everyScaleValue;
    }

    /**
     * 设置刻度线数量
     *
     * @param scaleNum
     */
    public void setScaleNum(int scaleNum) {
        this.mScaleNumber = scaleNum;
    }

    /**
     * 刻度最小值
     *
     * @param mScaleMin
     */
    public void setScaleMin(int mScaleMin) {
        this.mScaleMin = mScaleMin;
    }


    /**
     * 画线间距
     *
     * @param drawLineSpace
     */
    public void setDrawLineSpace(int drawLineSpace) {
        this.mScaleNumber = drawLineSpace;
        invalidate();
    }

    /**
     * 画刻度值的间隔
     *
     * @param drawTextSpace
     */
    public void setDrawTextSpace(int drawTextSpace) {
        this.mScaleNumber = drawTextSpace;
        invalidate();
    }

    /**
     * 设置弧线颜色
     */
    private void setArcLineColor(int color) {
        this.mArcLineColor = color;
        invalidate();
    }

    /**
     * 设置刻度线颜色
     */
    private void setScaleLineColor(int color) {
        this.mScaleLineColor = color;
        invalidate();
    }

    /**
     * 设置指示器颜色
     */
    private void setIndicatorColor(int color) {
        this.mIndicatorColor = color;
        invalidate();
    }

    /**
     * 设置刻度值颜色
     */
    private void setScaleTextColor(int color) {
        this.mScaleTextColor = color;
        invalidate();
    }

    /**
     * 设置选中颜色
     */
    private void setSelectTextColor(int color) {
        this.mSelectTextColor = color;
        invalidate();
    }

    /**
     * 设置刻度尺的长度
     *
     * @param mScaleMaxLength 刻度尺的长度
     */
    public void setmScaleMaxLength(int mScaleMaxLength) {
        this.mScaleMaxLength = mScaleMaxLength;
        invalidate();
    }

    public void setShape2Arc(boolean isArc) {
        this.isArc = isArc;
        invalidate();
    }
}
