package com.fz.compoundtext;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;


/**
 * 带有清除按钮的TextInputEditText
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2017/8/29 16:24
 */
public class ClickDrawableTextView extends AppCompatTextView implements IDrawableClickAble, CompoundDrawablesClickHelper.DrawableClickListener {

    private final CompoundDrawablesClickHelper drawablesClickHelper;
    private OnDrawableClickListener onDrawableClickListener;
    private boolean isVisible[] = new boolean[]{true, true, true, true};

    public ClickDrawableTextView(final Context context) {
        this(context, null);
    }

    public ClickDrawableTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClickDrawableTextView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Drawable[] drawables = getCompoundDrawablesRelative();
        //注意，如果使用此自定义类且控件inputType为textPassword，则应调用 setGravity(Gravity.END);将焦点放右边
        setTextDirection(View.TEXT_DIRECTION_LOCALE);
        drawablesClickHelper = new CompoundDrawablesClickHelper(this, drawables[0], drawables[1], drawables[2], drawables[3]);
        drawablesClickHelper.setDrawableClickListener(this);
    }

    public void setDrawableVisible(boolean isVisible) {
        this.isVisible = new boolean[]{isVisible, isVisible, isVisible, isVisible};
        drawablesClickHelper.setDrawables();
    }

    public void setStarDrawableVisible(boolean isVisible) {
        this.isVisible[0] = isVisible;
        drawablesClickHelper.setDrawables();
    }

    public void setTopDrawableVisible(boolean isVisible) {
        this.isVisible[1] = isVisible;
        drawablesClickHelper.setDrawables();
    }

    public void setEndDrawableVisible(boolean isVisible) {
        this.isVisible[2] = isVisible;
        drawablesClickHelper.setDrawables();
    }

    public void setBottomDrawableVisible(boolean isVisible) {
        this.isVisible[3] = isVisible;
        drawablesClickHelper.setDrawables();
    }

    public void setDrawableVisible(boolean[] isVisible) {
        this.isVisible = isVisible;
        drawablesClickHelper.setDrawables();
    }

    public void setDrawables() {
        drawablesClickHelper.setCompoundDrawables(getCompoundDrawablesRelative());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return drawablesClickHelper.onTouchEvent(event);
    }

    public void setOnDrawableClickListener(OnDrawableClickListener onDrawableClickListener) {
        this.onDrawableClickListener = onDrawableClickListener;
    }

    @Override
    public boolean callSuperOnTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean[] isVisible() {
        return isVisible;
    }

    @Override
    public boolean isVisible(int position) {
        return isVisible[position];
    }

    @Override
    public void onClick(DrawablePosition position) {
        if (onDrawableClickListener != null) {
            onDrawableClickListener.onDrawableClick(this, position);
        }
    }
}
