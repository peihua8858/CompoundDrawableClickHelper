package com.fz.compoundtext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;


/**
 * 带有清除按钮的TextInputEditText
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2017/8/29 16:24
 */
public class ClearTextInputEditText extends TextInputEditText implements IDrawableClickAble, TextWatcher, CompoundDrawablesClickHelper.DrawableClickListener {

    private CompoundDrawablesClickHelper drawablesClickHelper;
    private boolean isVisible[] = new boolean[]{true, true, true, true};

    public ClearTextInputEditText(final Context context) {
        super(context);
        init(context, null);
    }

    public ClearTextInputEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ClearTextInputEditText(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        Drawable drawable = null;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClearTextInputEditText);
        if (a.hasValue(R.styleable.ClearTextInputEditText_ct_cancel)) {
            drawable = a.getDrawable(R.styleable.ClearTextInputEditText_ct_cancel);
        }
        a.recycle();
        Drawable[] drawables = getCompoundDrawablesRelative();
        //注意，如果使用此自定义类且控件inputType为textPassword，则应调用 setGravity(Gravity.END);将焦点放右边
        setTextDirection(View.TEXT_DIRECTION_LOCALE);
        drawablesClickHelper = new CompoundDrawablesClickHelper(this, drawables[0],
                drawables[1], drawable == null ? drawables[2] : drawable, drawables[3]);
        drawablesClickHelper.setDrawableClickListener(this);
        addTextChangedListener(this);
    }

    @Override
    protected void onFocusChanged(boolean hasFocus, int direction, Rect previouslyFocusedRect) {
        if (hasFocus) {
            setDrawableVisible(getText().length() > 0);
        } else {
            setDrawableVisible(false);
        }
        super.onFocusChanged(hasFocus, direction, previouslyFocusedRect);
    }

    @Override
    public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        if (isFocused()) {
            setDrawableVisible(s.length() > 0);
        }
    }

    public void setDrawableVisible(boolean isVisible) {
        this.isVisible = new boolean[]{isVisible, isVisible, isVisible, isVisible};
        drawablesClickHelper.setDrawables();
    }

    public void setDrawableVisible(boolean[] isVisible) {
        this.isVisible = isVisible;
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
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return drawablesClickHelper.onTouchEvent(event);
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
        if (position == DrawablePosition.END) {
            setError(null);
            setText("");
        }
    }
}
