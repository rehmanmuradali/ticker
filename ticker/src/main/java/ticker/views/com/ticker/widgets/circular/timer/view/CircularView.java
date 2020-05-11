package ticker.views.com.ticker.widgets.circular.timer.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.LinearInterpolator;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ticker.views.com.ticker.R;
import ticker.views.com.ticker.widgets.circular.timer.callbacks.CircularViewCallback;

/**
 * Created by Rehman Murad Ali on 9/14/2018.
 */
public class CircularView extends AppCompatTextView {


    // Default Properties
    private static final int DEFAULT_CIRCLE_STROKE_COLOR = 0xEEEEEE;
    private static final int DEFAULT_ARC_STROKE_COLOR = 0xFFFFFF;
    private static final int DEFAULT_CIRCLE_RADIUS_IN_DP = 70;
    private static final int DEFAULT_CIRCLE_STROKE_WIDTH = 20;
    private static final int DEFAULT_ARC_MARGIN = 5;
    private static final int START_ARC_ANGLE = 180;
    private static final int SWEEP_ARC_ANGLE = 80;
    private static final int CIRCLE_MINIMUM_DEGREE = 0;
    private static final int CIRCLE_MAX_DEGREES = 360;
    private static final int ONE_SEC_IN_MILLIS = 1000;


    //Paint Objects
    private Paint paintOuterCircle;
    private Paint paintInnerArc;

    //Outer Circle Properties
    private float circleRadiusInPX;
    private float circleStrokeWidthInPx;
    private float rotationDegrees;
    private int colorCircleStroke;

    //Inner Arc Properties
    private int colorArcStroke;
    private float arcMargin = -1;
    private RectF arcRectContainer;

    //Timer Value in Seconds Or Custom Text
    private long counterInSeconds = OptionsBuilder.INFINITE;
    private String customText;

    //Rotation Animator Properties
    private ValueAnimator rotationAnimator;

    //Center Point of Screen Properties
    private float yLocation;
    private float xLocation;

    //Gradient Color Properties
    private int[] colorSequence;
    private float[] colorStartingPointSequence;

    // Show Text or not
    private boolean shouldDisplayTimer = true;

    //Timer Callback
    private CircularViewCallback circularViewCallback;

    //Timer Handler,Runnable
    private Handler handler;
    private Runnable runnable;
    private boolean isPause = false;
    private Lock lock = new ReentrantLock();

    public CircularView(Context context) {
        this(context, null);
    }

    public CircularView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircularView);
            try {
                circleRadiusInPX = dpToPx(typedArray.getInt(R.styleable.CircularView_m_circle_radius, DEFAULT_CIRCLE_RADIUS_IN_DP));
                circleStrokeWidthInPx = dpToPx(typedArray.getInt(R.styleable.CircularView_m_cicle_stroke_width, DEFAULT_CIRCLE_STROKE_WIDTH));
                colorCircleStroke = typedArray.getColor(R.styleable.CircularView_m_circle_stroke_color, DEFAULT_CIRCLE_STROKE_COLOR);
                colorArcStroke = typedArray.getColor(R.styleable.CircularView_m_arc_stroke_color, DEFAULT_ARC_STROKE_COLOR);
                arcMargin = dpToPx(typedArray.getInt(R.styleable.CircularView_m_arc_margin, DEFAULT_ARC_MARGIN));

            } finally {

                typedArray.recycle();
            }

        }
        init();
    }


    /**
     * Initialize Helper Objects
     */
    private void init() {

        //Outer Circle Paint Object initialization
        paintOuterCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintOuterCircle.setColor(colorCircleStroke);
        paintOuterCircle.setStrokeWidth(circleStrokeWidthInPx);
        paintOuterCircle.setStyle(Paint.Style.STROKE);

        //Inner Arc Paint Object Initialization
        paintInnerArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintInnerArc.setColor(colorArcStroke);
        paintInnerArc.setStyle(Paint.Style.STROKE);
        paintInnerArc.setStrokeWidth(circleStrokeWidthInPx - arcMargin);
        paintInnerArc.setStrokeCap(Paint.Cap.ROUND);


        //Rotation Animator Object Initialization
        rotationAnimator = ValueAnimator.ofFloat(CIRCLE_MINIMUM_DEGREE, CIRCLE_MAX_DEGREES);
        rotationAnimator.setDuration(ONE_SEC_IN_MILLIS);
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimator.setInterpolator(new LinearInterpolator());
        rotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                rotationDegrees = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        //Gradient Color Sequence Initialization
        colorSequence = new int[]
                {
                        colorCircleStroke,
                        colorCircleStroke,
                        colorCircleStroke,
                        colorArcStroke,
                        colorArcStroke
                };

        //Gradient Color Starting Point Sequence Initialization
        colorStartingPointSequence = new float[]
                {
                        0.0f,
                        0.25f,
                        0.50f,
                        0.60f,
                        1f
                };

        //Initialize Arc Object
        arcRectContainer = new RectF();


        setTextViewProperties();
        setUpCountdownHandler();
    }

    /**
     * Setup Count Down Timer
     */
    private void setUpCountdownHandler() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {


                setTextOfTextViewWithSeconds(counterInSeconds);

                if (OptionsBuilder.INFINITE != counterInSeconds) counterInSeconds--;

                if (isCounterInRunningState()) {

                    handler.postDelayed(this, ONE_SEC_IN_MILLIS);

                } else if (circularViewCallback != null && !isPause) {

                    lock.lock();

                    circularViewCallback.onTimerFinish();
                    circularViewCallback = null;
                    stopTimer();

                    lock.unlock();


                }
            }
        };
    }

    private boolean isCounterInRunningState() {
        return counterInSeconds > -1 || counterInSeconds == OptionsBuilder.INFINITE;
    }

    private void setTextOfTextViewWithSeconds(long value) {
        String numberString;
       
        if (value / 60 > 0) {
            numberString = String.valueOf((value / 60) + 1);
           
        } else {
            numberString = String.valueOf(value);
         
        }

        //To resize the text size according to length
        SpannableString spannableString = new SpannableString(numberString);
        spannableString.setSpan(new RelativeSizeSpan(getTextProportion(numberString)), 0, numberString.length(), 0);

        if (shouldDisplayTimer) {

            if (customText != null) {
                setText(addLineSpacingIfNeeded(customText));
            } else {

                setText(spannableString);
            }

        }

    }

    private String addLineSpacingIfNeeded(String customText) {


        if ((circleRadiusInPX * 1.5) < getPaint().measureText(customText))
        {
            String[] array = customText.split(" ");
            StringBuilder result = new StringBuilder("");
            for (String anArray : array) {
                result.append(anArray).append("\n");
            }
            customText = result.toString().trim();
        }
        return customText;
    }


    /**
     * Setup Text View Properties
     */
    private void setTextViewProperties() {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, circleRadiusInPX / 5);
        setGravity(Gravity.CENTER);
    }


    /**
     * Start Animator and Handler so that they are in sync
     */
    public void startTimer() {
        //Check if countdown is already in running state or not
        if (isCounterInRunningState() && !rotationAnimator.isRunning()) {

            handler.post(runnable);
            rotationAnimator.start();
        }
    }

    /**
     * Stop Timer before it finishes
     */
    public void stopTimer() {
        handler.removeCallbacks(runnable);
        rotationAnimator.cancel();
        if (circularViewCallback != null && isCounterInRunningState()) {
            counterInSeconds = -1;
            circularViewCallback.onTimerCancelled();
        }
        circularViewCallback = null;
    }


    public boolean pauseTimer() {
        if (isCounterInRunningState() && rotationAnimator.isRunning()) {
            lock.lock();
            handler.removeCallbacks(runnable);
            rotationAnimator.pause();
            isPause = true;
            return true;
        }
        return false;
    }

    public void resumeTimer() {
        if (isCounterInRunningState() && rotationAnimator.isRunning() && isPause) {
            lock.unlock();
            handler.post(runnable);
            rotationAnimator.resume();
            isPause = false;
        }
    }


    /**
     * Set the size of Text with respect to Number of digits, to fit it in circle.
     *
     * @param numberString The String contains Number in seconcds
     * @return Proportion Size of Number to normal text size.
     */
    private float getTextProportion(@NonNull String numberString) {
        int len = numberString.length();
        if (len < 4)
            return 4f;
        else if (len < 5)
            return 3f;
        else
            return 2f;
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = measureWidth();
        int measuredHeight = measureHeight();


        yLocation = measuredHeight / 2;
        xLocation = measuredWidth / 2;


        paintInnerArc.setShader(new SweepGradient(xLocation, yLocation, colorSequence, colorStartingPointSequence));

        setMeasuredDimension(measuredWidth, measuredHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Set Inner Arc Position
        arcRectContainer.set(xLocation - circleRadiusInPX, yLocation - circleRadiusInPX, xLocation + circleRadiusInPX, yLocation + circleRadiusInPX);

        canvas.save();

        //Rotate and Draw Circle and Inner Arc
        canvas.rotate(rotationDegrees, xLocation, yLocation);
        canvas.drawCircle(xLocation, yLocation, circleRadiusInPX, paintOuterCircle);
        canvas.drawArc(arcRectContainer, START_ARC_ANGLE, SWEEP_ARC_ANGLE, false, paintInnerArc);
        canvas.restore();

    }

    /**
     * Set the height with respect to Circle Radius
     *
     * @return height
     */
    private int measureHeight() {

        return (int) (circleRadiusInPX * 2 + circleStrokeWidthInPx + getPaddingTop() + getPaddingBottom());
    }

    /**
     * Set the width with respect to Circle Radius
     *
     * @return width
     */
    private int measureWidth() {
        return (int) (circleRadiusInPX * 2 + circleStrokeWidthInPx + getPaddingRight() + getPaddingLeft());
    }

    /**
     * Convert Dp to Pixels
     *
     * @param value Value in Dp
     * @return Value in Pixels
     */
    private float dpToPx(final float value) {
        return value * getContext().getResources().getDisplayMetrics().density;
    }


    public void setOptions(OptionsBuilder optionBuilder) {
        this.counterInSeconds = optionBuilder.counterInSeconds;
        this.circularViewCallback = optionBuilder.circularViewCallback;
        this.shouldDisplayTimer = optionBuilder.shouldDisplayTimer;
        this.customText = optionBuilder.customText;
        setTextOfTextViewWithSeconds(counterInSeconds);
    }


    public static class OptionsBuilder {

        public static final int INFINITE = -23021996;
        private CircularViewCallback circularViewCallback = null;
        private boolean shouldDisplayTimer = true;
        private String customText = null;
        private long counterInSeconds = INFINITE;


        public OptionsBuilder setCircularViewCallback(CircularViewCallback circularViewCallback) {

            this.circularViewCallback = circularViewCallback;
            return this;
        }


        public OptionsBuilder shouldDisplayText(boolean shouldDisplayText) {

            this.shouldDisplayTimer = shouldDisplayText;
            return this;
        }

        public OptionsBuilder setCustomText(String text) {

            this.customText = text;
            return this;
        }

        public OptionsBuilder setCounterInSeconds(long counterInSeconds) {
            this.counterInSeconds = counterInSeconds;
            return this;
        }


    }


}
