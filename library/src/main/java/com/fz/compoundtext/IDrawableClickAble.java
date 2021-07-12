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

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * Created by dingpeihua on 2017/8/29.
 */
public interface IDrawableClickAble {
    /**
     * 获取drawable数组
     *
     * @return Drawable[start, top, end, bottom]
     * @see {@link TextView#getCompoundDrawables()}
     */
    Drawable[] getCompoundDrawables();

    Drawable[] getCompoundDrawablesRelative();

    void setCompoundDrawablesRelative(@Nullable Drawable start, @Nullable Drawable top, @Nullable Drawable end, @Nullable Drawable bottom);

    /**
     * 设置控件图片
     *
     * @param start
     * @param top
     * @param end
     * @param bottom
     * @see {@link TextView#setCompoundDrawables(Drawable, Drawable, Drawable, Drawable)}
     */
    void setCompoundDrawables(@Nullable Drawable start, @Nullable Drawable top, @Nullable Drawable end, @Nullable Drawable bottom);

    /**
     * 获取控件宽度
     *
     * @return
     * @see {@link TextView#getWidth()}
     */
    int getWidth();

    /**
     * 获取控件高度
     *
     * @return
     * @see {@link TextView#getHeight()}
     */
    int getHeight();

    /**
     * 获取drawable与文字间距
     *
     * @return
     * @see {@link TextView#getCompoundDrawablePadding()}
     */
    int getCompoundDrawablePadding();

    /**
     * 获取需要显示或隐藏的值
     *
     * @return Visible[start, top, end, bottom]
     */
    boolean[] isVisible();

    /**
     * 调用父类的onTouchEvent方法
     *
     * @param event
     * @return
     */
    boolean callSuperOnTouchEvent(MotionEvent event);

    /**
     * 获取Resources
     *
     * @author dingpeihua
     * @version 1.0
     * @date 2018/2/27 18:49
     */
    Resources getResources();

    void setFocusable(boolean focusable);

    void setClickable(boolean clickable);
}
