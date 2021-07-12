/*
 * Copyright (C) Globalegrow E-Commerce Co. , Ltd. 2007-2018.
 * All rights reserved.
 * This software is the confidential and proprietary information
 * of Globalegrow E-Commerce Co. , Ltd. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement
 * you entered into with Globalegrow.
 */

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
    private boolean isVisible;

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
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
        super.onFocusChanged(hasFocus, direction, previouslyFocusedRect);
    }

    @Override
    public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        if (isFocused()) {
            setClearIconVisible(s.length() > 0);
        }
    }

    private void setClearIconVisible(final boolean visible) {
        isVisible = visible;
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
        return new boolean[]{true, true, isVisible, true};
    }

    @Override
    public void onClick(DrawablePosition position) {
        if (position == DrawablePosition.END) {
            setError(null);
            setText("");
        }
    }
}
