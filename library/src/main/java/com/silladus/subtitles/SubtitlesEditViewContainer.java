package com.silladus.subtitles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by silladus on 2020/3/17.
 * GitHub: https://github.com/silladus
 * Description:
 */
public class SubtitlesEditViewContainer extends FrameLayout {
    List<SubtitlesEditView> subtitlesEditViews = new ArrayList<>();

    public SubtitlesEditViewContainer(@NonNull Context context) {
        this(context, null);
    }

    public SubtitlesEditViewContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubtitlesEditViewContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addItemView(@NonNull String content, @ColorInt int color, String colorBbGgRr, SubtitlesEditView.OnContentClickListener onContentClickListener) {
        SubtitlesEditView subtitlesEditView = (SubtitlesEditView) inflate(getContext(), R.layout.layout_subtitles_edit, null);
        addView(subtitlesEditView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        subtitlesEditView.init();
        for (int i = 0; i < subtitlesEditViews.size(); i++) {
            subtitlesEditViews.get(i).showOptView(false);
        }
        subtitlesEditViews.add(subtitlesEditView);

        subtitlesEditView.setOnCloseListener((container, textView) -> {
            subtitlesEditViews.remove(container);
            removeView(container);
        });
        subtitlesEditView.setOnContentClickListener((container, textView) -> {
                    for (int i = 0; i < subtitlesEditViews.size(); i++) {
                        SubtitlesEditView v = subtitlesEditViews.get(i);
                        v.showOptView(v == container);
                    }
                    if (onContentClickListener != null) {
                        onContentClickListener.onClick(container, textView);
                    }
                }
        );
        subtitlesEditView.show(content, color, colorBbGgRr);
    }

    public void clearItemView() {
        subtitlesEditViews.clear();
        removeAllViews();
    }

    public List<SubtitlesEditView> getSubtitlesEditViews() {
        return subtitlesEditViews;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (event.getEventTime() - event.getDownTime() < 200/* && event.getX() - mOriginalX < 10 && event.getY() - mOriginalY < 10*/) {
                    for (int i = 0; i < subtitlesEditViews.size(); i++) {
                        subtitlesEditViews.get(i).showOptView(false);
                    }
                }
                break;
        }
        if (event.getY() < getHeight() - dp2px(getContext(), 46)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dpValue * scale);
    }
}
