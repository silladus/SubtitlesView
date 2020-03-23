package com.silladus.subtitles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * Created by silladus on 2020/3/5.
 * GitHub: https://github.com/silladus
 * Description:
 */
public class SubtitlesEditView extends ConstraintLayout {

    float mOriginalX;
    float mOriginalY;
    float mOriginalRawX;
    float mOriginalRawY;

    private View closeView;
    private View pullView;
    private View bgView;
    private TextView tvContent;

    public SubtitlesEditView(Context context) {
        this(context, null);
    }

    public SubtitlesEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubtitlesEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 布局完成后调用，不然有bug, tvContent不能自动匹配宽度, 即使在onAttachedToWindow()中调用也不行
     */
    public void init() {
        this.closeView = findViewById(R.id.iv_close);
        this.pullView = findViewById(R.id.iv_pull);
        this.bgView = findViewById(R.id.v_bg);
        this.tvContent = findViewById(R.id.tv_content);

        closeView.setOnClickListener(v -> /*hide()*/{
            if (onCloseListener != null) {
                onCloseListener.onClick(this, tvContent);
            }
        });
        setPullIconView(pullView);
    }

    private OnContentClickListener onCloseListener;

    public void setOnCloseListener(OnContentClickListener onCloseListener) {
        this.onCloseListener = onCloseListener;
    }

    public interface OnContentClickListener {
        void onClick(SubtitlesEditView container, TextView textView);
    }

    OnContentClickListener onContentClickListener;

    public void setOnContentClickListener(OnContentClickListener onContentClickListener) {
        this.onContentClickListener = onContentClickListener;
    }

    public static boolean isInRect(float x, float y, Rect rect) {
        return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                changeOriginalTouchParams(event);
                break;
            case MotionEvent.ACTION_MOVE:
                updateViewPosition(event);
                break;
            case MotionEvent.ACTION_UP:
                Rect notCancelRect = new Rect();
                tvContent.getGlobalVisibleRect(notCancelRect);
                if (event.getEventTime() - event.getDownTime() < 200 && Math.abs(event.getRawX() - mOriginalRawX) < 10 && Math.abs(event.getRawY() - mOriginalRawY) < 10
                        && isInRect(event.getRawX(), event.getRawY(), notCancelRect)) {
                    if (onContentClickListener != null) {
                        onContentClickListener.onClick(this, tvContent);
                    }
                }
                break;
        }

        return true;
    }

    public void updateViewPosition(MotionEvent event) {
        float activeX = /*Math.max(0, */mOriginalX + event.getRawX() - mOriginalRawX/*)*/;
//        activeX = Math.min(activeX, getActiveWidth());
        setX(activeX);
        float activeY = /*Math.max(0, */mOriginalY + event.getRawY() - mOriginalRawY/*)*/;
//        activeY = Math.min(activeY, getActiveHeight());
        setY(activeY);
    }

    private void changeOriginalTouchParams(MotionEvent event) {
        mOriginalX = getX();
        mOriginalY = getY();
        mOriginalRawX = event.getRawX();
        mOriginalRawY = event.getRawY();
    }

    private String colorBbGgRr;

    public String getColorBbGgRr() {
        return colorBbGgRr;
    }

    public int getTextSize() {
        return (int) tvContent.getTextSize();
    }

    public String getContent() {
        return tvContent.getText().toString();
    }

    public void show(String content, @ColorInt int color, String colorBbGgRr) {
        this.colorBbGgRr = colorBbGgRr;
        ViewGroup.LayoutParams lp = tvContent.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        tvContent.setLayoutParams(lp);
        setVisibility(View.VISIBLE);
        tvContent.setText(content);
        tvContent.setTextColor(color);
//        tvContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                minContentViewWidth = tvContent.getWidth();
//                minContentViewHeight = tvContent.getHeight();
//                tvContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//            }
//        });
    }

    public void hide() {
        setVisibility(View.GONE);
    }

    private float oldRawX;
    private float oldRawY;

    private float oldSize;

    private float px, py;
    private float pw, ph;

    private float bgOX;
    private float bgOY;

    @SuppressLint("ClickableViewAccessibility")
    public void setPullIconView(@NonNull View v) {
        v.setOnTouchListener((v1, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    oldRawX = event.getRawX();
                    oldRawY = event.getRawY();
                    oldSize = tvContent.getTextSize();

                    px = getX();
                    py = getY();
                    pw = getWidth();
                    ph = getHeight();

                    defaultAngle = getRotation();

                    oldDist = spacing(oldRawX - getPivotX(), oldRawY - getY() - getHeight() * 0.5f);

                    int[] bgPosition = new int[2];
                    bgView.getLocationOnScreen(bgPosition);

                    int[] closePosition = new int[2];
                    closeView.getLocationOnScreen(closePosition);

                    int[] pullPosition = new int[2];
                    pullView.getLocationOnScreen(pullPosition);

                    float dx = Math.abs(closePosition[0] - pullPosition[0]) * 0.5f;
                    float dy = Math.abs(closePosition[1] - pullPosition[1]) * 0.5f;
                    if (bgPosition[0] < pullPosition[0]) {
                        bgOX = bgPosition[0] + dx;
                    } else {
                        bgOX = bgPosition[0] - dx;
                    }
                    if (bgPosition[1] < pullPosition[1]) {
                        bgOY = bgPosition[1] + dy;
                    } else {
                        bgOY = bgPosition[1] - dy;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:

                    // 旋转
                    setRotation(angleBetweenLines(oldRawX, oldRawY, event.getRawX(), event.getRawY(), bgOX, bgOY) + defaultAngle);

                    // 拉伸
                    setX(px + (pw - getWidth()) * 0.5f);
                    setY(py + (ph - getHeight()) * 0.5f);
                    float newDist = spacing(event.getRawX() - bgOX, event.getRawY() - bgOY);
                    scale = newDist / oldDist;
                    if (newDist > oldDist + 1) {
                        zoom(scale);
                        oldDist = newDist;
                    }
                    if (newDist < oldDist - 1) {
                        zoom(scale);
                        oldDist = newDist;
                    }
//                    float newSize = event.getRawX() - oldRawX + oldSize;
//                    tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);


                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        });
    }

    private void zoom(float f) {
        tvContent.setTextSize(oldSize *= f);
    }

    float scale;
    float oldDist;
    float defaultAngle = 0;

    /**
     * 计算两点之间的距离
     *
     * @return 两点之间的距离
     */
    private float spacing(float xs, float ys) {
        return (float) Math.sqrt(xs * xs + ys * ys);
    }

    /**
     * 计算刚开始触摸的两个点构成的直线和滑动过程中两个点构成直线的角度
     *
     * @param fX  初始点一号x坐标
     * @param fY  初始点一号y坐标
     * @param nfX 终点一号x坐标
     * @param nfY 终点一号y坐标
     * @return 构成的角度值
     */
    private float angleBetweenLines(float fX, float fY, float nfX, float nfY, float cfX, float cfY) {
        float angle1 = (float) Math.atan2((fY - cfY), (fX - cfX));
        float angle2 = (float) Math.atan2((nfY - cfY), (nfX - cfX));
        float angle = ((float) Math.toDegrees(angle2 - angle1)) % 360;
        return angle;
    }

    public void showOptView(boolean isShow) {
        setViewVisible(closeView, isShow);
        setViewVisible(pullView, isShow);
        setViewVisible(bgView, isShow);
    }

    public static boolean setViewVisible(View view, boolean isVisible) {
        if (view != null) {
            if (isVisible) {
                if (view.getVisibility() != View.VISIBLE) {
                    view.setVisibility(View.VISIBLE);
                }
            } else {
                if (view.getVisibility() != View.GONE) {
                    view.setVisibility(View.GONE);
                }
            }
        }
        return isVisible;
    }
}
