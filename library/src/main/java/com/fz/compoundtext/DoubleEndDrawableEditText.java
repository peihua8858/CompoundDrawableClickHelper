package com.fz.compoundtext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.textfield.TextInputEditText;

/**
 * {@link #getCompoundDrawables()}[2]两个图标切换的EditText
 * {@link #mFirstDrawable} 默认图标
 * {@link #mSecondDrawable}根据条件切换的后的图标
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2020/5/10 12:49
 */
public class DoubleEndDrawableEditText extends TextInputEditText implements IDrawableClickAble, OnFocusChangeListener, TextWatcher, CompoundDrawablesClickHelper.DrawableClickListener {
    public final static int DRAWABLE_FIRST = 0x11;
    public final static int DRAWABLE_SECOND = 0x12;

    @IntDef({DRAWABLE_FIRST, DRAWABLE_SECOND})
    public @interface DrawableType {
    }

    protected Drawable mFirstDrawable;
    protected Drawable mSecondDrawable;
    @ColorInt
    public int colorInt;
    private boolean isChangedColorInt = false;
    protected CompoundDrawablesClickHelper drawablesClickHelper;
    protected OnDoubleDrawableClickListener onDoubleDrawableClickListener;
    @DrawableType
    private int showDrawableType;

    public DoubleEndDrawableEditText(Context context) {
        this(context, null);
    }

    public DoubleEndDrawableEditText(Context context, AttributeSet attrs) {
        //这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        super(context, attrs);
        init(context, attrs);
    }

    public DoubleEndDrawableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //后面输入的如是阿拉伯语，则显示在最左边，光标在最左边
        setTextDirection(View.TEXT_DIRECTION_LOCALE);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DoubleEndDrawableEditText);
        mFirstDrawable = a.getDrawable(R.styleable.DoubleEndDrawableEditText_ct_first_drawable);
        mSecondDrawable = a.getDrawable(R.styleable.DoubleEndDrawableEditText_ct_second_drawable);
        colorInt = a.getColor(R.styleable.DoubleEndDrawableEditText_ct_drawable_tint, -1);
        a.recycle();
        isChangedColorInt = true;
        drawablesClickHelper = new CompoundDrawablesClickHelper(this);
        setCompoundDrawables(context);
        //默认设置隐藏图标
        onChanged(false);
        //设置焦点改变的监听
        setOnFocusChangeListener(this);
        //设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
        drawablesClickHelper.setDrawableClickListener(this);
    }

    public void setColorInt(@ColorInt int colorInt) {
        this.colorInt = colorInt;
        isChangedColorInt = true;
    }

    public void setDrawableTint(Drawable drawable) {
        if (drawable != null && colorInt != -1) {
            DrawableCompat.setTint(drawable, colorInt);
        }
    }

    public void setCompoundDrawables(Context context) {
        Drawable[] drawables = getCompoundDrawablesRelative();
        if (mFirstDrawable != null) {
            drawables[2] = mFirstDrawable;
        }
        drawablesClickHelper.setCompoundDrawables(drawables);
    }

    public void setOnDoubleDrawableClickListener(OnDoubleDrawableClickListener onDoubleDrawableClickListener) {
        this.onDoubleDrawableClickListener = onDoubleDrawableClickListener;
    }

    public void setShowDrawableType(@DrawableType int showDrawableType) {
        this.showDrawableType = showDrawableType;
    }

    public int getShowDrawableType() {
        return showDrawableType;
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public final boolean onTouchEvent(MotionEvent event) {
        return drawablesClickHelper.onTouchEvent(event);
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public final void onFocusChange(View v, boolean hasFocus) {
        onChanged(hasFocus);
    }

    protected final boolean hasText() {
        Editable editable = getText();
        return editable != null && editable.length() > 0;
    }

    /**
     * 图标切换处理，默认有文本有焦点时切换到第二个，否则默认第一个
     *
     * @param hasFocus
     */
    protected void onChangedState(boolean hasFocus) {
        Drawable end = null;
        if (hasFocus && hasText()) {
            end = mSecondDrawable;
            setShowDrawableType(DRAWABLE_SECOND);
        } else {
            end = mFirstDrawable;
            setShowDrawableType(DRAWABLE_FIRST);
        }
        Drawable[] drawables = getCompoundDrawablesRelative();
        if (end != null) {
            drawables[2] = end;
        }
        drawablesClickHelper.setCompoundDrawables(drawables);
    }

    /**
     * 图标切换处理，默认有文本有焦点时切换到第二个，否则默认第一个
     *
     * @param hasFocus
     */
    private void onChanged(boolean hasFocus) {
        if (isChangedColorInt) {
            isChangedColorInt = false;
            setDrawableTint(mFirstDrawable);
            setDrawableTint(mSecondDrawable);
        }
        onChangedState(hasFocus);
    }


    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public final void onTextChanged(CharSequence s, int start, int count, int after) {
        if (isFocused()) {
            onChanged(s.length() > 0);
        }
    }

    @Override
    public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public final void afterTextChanged(Editable s) {
    }

    @Override
    public boolean[] isVisible() {
        return new boolean[]{true, true, true, true};
    }

    @Override
    public boolean isVisible(int position) {
        return true;
    }

    @Override
    public final boolean callSuperOnTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(DrawablePosition position) {
        if (onDoubleDrawableClickListener != null) {
            if (showDrawableType == DRAWABLE_FIRST) {
                onDoubleDrawableClickListener.onFirstDrawableClick(this, position);
            } else if (showDrawableType == DRAWABLE_SECOND) {
                onDoubleDrawableClickListener.onSecondDrawableClick(this, position);
            }
        }
    }

    public interface OnDoubleDrawableClickListener extends OnDrawableClickListener {

        /**
         * 第一个图标点击事件
         *
         * @param view
         * @param position
         * @author dingpeihua
         * @date 2020/5/10 13:20
         * @version 1.0
         */
        void onFirstDrawableClick(View view, DrawablePosition position);

        /**
         * 第二个图标点击事件
         *
         * @param view
         * @param position
         * @author dingpeihua
         * @date 2020/5/10 13:20
         * @version 1.0
         */
        default void onSecondDrawableClick(View view, DrawablePosition position) {
            onDrawableClick(view, position);
        }
    }
}