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

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;

import java.util.Locale;

/**
 * CompoundDrawable 点击事件辅助类
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2020/5/10 11:50
 */
public final class CompoundDrawablesClickHelper {
    private final IDrawableClickAble iDrawableClickAble;
    /**
     * 各个方向的图片资源
     **/
    private Drawable mStartDrawable;
    private Drawable mTopDrawable;
    private Drawable mEndDrawable;
    private Drawable mBottomDrawable;

    /**
     * 各个方向的图片资源是否被touch
     **/
    private boolean mIsStartTouched;
    private boolean mIsTopTouched;
    private boolean mIsEndTouched;
    private boolean mIsBottomTouched;

    /**
     * Drawable可响应的点击区域x方向允许的误差，表示图片x方向的此范围内的点击都被接受
     **/
    private int mLazyX = 0;
    /**
     * Drawable可响应的点击区域y方向允许的误差，表示图片y方向的此范围内的点击都被接受
     **/
    private int mLazyY = 0;
    /**
     * 图片点击的监听器
     **/
    private DrawableClickListener mDrawableClickListener;

    public CompoundDrawablesClickHelper(@NonNull IDrawableClickAble iDrawableClickAble,
                                        Drawable mStartDrawable, Drawable mTopDrawable,
                                        Drawable mEndDrawable, Drawable mBottomDrawable) {
        this.iDrawableClickAble = iDrawableClickAble;
        setCompoundDrawables(mStartDrawable, mTopDrawable, mEndDrawable, mBottomDrawable);
    }

    public CompoundDrawablesClickHelper(@NonNull IDrawableClickAble iDrawableClickAble) {
        this.iDrawableClickAble = iDrawableClickAble;
    }

    public CompoundDrawablesClickHelper(@NonNull IDrawableClickAble iDrawableClickAble, @NonNull Drawable[] drawables) {
        this.iDrawableClickAble = iDrawableClickAble;
        setCompoundDrawables(drawables);
    }

    public void setCompoundDrawables(@NonNull Drawable[] drawables) {
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    public void setCompoundDrawables(@Nullable Drawable start, @Nullable Drawable top,
                                     @Nullable Drawable end, @Nullable Drawable bottom) {
        iDrawableClickAble.setClickable(true);
        iDrawableClickAble.setFocusable(true);
        this.mStartDrawable = wrap(start);
        setBounds(this.mStartDrawable);
        this.mTopDrawable = wrap(top);
        setBounds(this.mTopDrawable);
        this.mEndDrawable = wrap(end);
        setBounds(this.mEndDrawable);
        this.mBottomDrawable = wrap(bottom);
        setBounds(this.mBottomDrawable);
        setDrawables();
    }

    public void setDrawables() {
        if (iDrawableClickAble == null) {
            throw new NullPointerException("Must be implements interface IDrawableClickAble");
        }
        final Drawable[] compoundDrawables = iDrawableClickAble.getCompoundDrawablesRelative();
        boolean[] isVisible = iDrawableClickAble.isVisible();
        if (compoundDrawables == null || compoundDrawables.length != 4) {
            throw new RuntimeException("compoundDrawables.length != 4");
        }
        if (isVisible == null || isVisible.length != 4) {
            throw new RuntimeException("isVisible.length != 4");
        }
        isVisible[0] = isVisible[0] && (compoundDrawables[0] != null || mStartDrawable != null);
        isVisible[1] = isVisible[1] && (compoundDrawables[1] != null || mTopDrawable != null);
        isVisible[2] = isVisible[2] && (compoundDrawables[2] != null || mEndDrawable != null);
        isVisible[3] = isVisible[3] && (compoundDrawables[3] != null || mBottomDrawable != null);
        this.iDrawableClickAble.setCompoundDrawablesRelative(isVisible[0] ? this.mStartDrawable == null ? compoundDrawables[0] : mStartDrawable : null,
                isVisible[1] ? this.mTopDrawable == null ? compoundDrawables[1] : mTopDrawable : null,
                isVisible[2] ? this.mEndDrawable == null ? compoundDrawables[2] : mEndDrawable : null,
                isVisible[3] ? this.mBottomDrawable == null ? compoundDrawables[3] : mBottomDrawable : null);
    }

    private Drawable wrap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        return DrawableCompat.wrap(drawable);
    }

    private void setBounds(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        drawable.setBounds(0, 0, drawable.getIntrinsicHeight(), drawable.getIntrinsicHeight());
    }

    public boolean onTouchEvent(MotionEvent event) {
        // 在event为actionDown时标记用户点击是否在相应的图片范围内
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            resetTouchStatus();
            if (mDrawableClickListener != null) {
                if (isRtl()) {
                    mIsStartTouched = touchEndDrawable(event);
                    mIsEndTouched = touchStartDrawable(event);
                } else {
                    mIsStartTouched = touchStartDrawable(event);
                    mIsEndTouched = touchEndDrawable(event);
                }
                mIsTopTouched = touchTopDrawable(event);
                mIsBottomTouched = touchBottomDrawable(event);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            /**
             * 按照左上右下的顺序响应第一个点击范围内的Drawable
             */
            if (mDrawableClickListener != null) {
                if (mIsStartTouched) {
                    mDrawableClickListener.onClick(DrawablePosition.START);
                } else if (mIsTopTouched) {
                    mDrawableClickListener.onClick(DrawablePosition.TOP);
                } else if (mIsEndTouched) {
                    mDrawableClickListener.onClick(DrawablePosition.END);
                } else if (mIsBottomTouched) {
                    mDrawableClickListener.onClick(DrawablePosition.BOTTOM);
                }
            }
        }
        return iDrawableClickAble.callSuperOnTouchEvent(event);
    }

    boolean isRtl() {
        Resources resources = iDrawableClickAble != null ? iDrawableClickAble.getResources() : null;
        Configuration configuration = resources != null ? resources.getConfiguration() : null;
        int layoutDirection = configuration != null ? configuration.getLayoutDirection() :
                TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault());
        return layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    @Override
    protected void finalize() throws Throwable {
        mEndDrawable = null;
        mBottomDrawable = null;
        mStartDrawable = null;
        mTopDrawable = null;
        super.finalize();
    }

    public Drawable getStartDrawable() {
        return mStartDrawable;
    }

    public Drawable getTopDrawable() {
        return mTopDrawable;
    }

    public Drawable getEndDrawable() {
        return mEndDrawable;
    }

    public Drawable getBottomDrawable() {
        return mBottomDrawable;
    }

    /**
     * 重置各个图片touch的状态
     */
    private void resetTouchStatus() {
        mIsStartTouched = false;
        mIsTopTouched = false;
        mIsEndTouched = false;
        mIsBottomTouched = false;
    }

    /**
     * touch左边的Drawable
     *
     * @param event
     * @return 是否在touch范围内
     */
    private boolean touchStartDrawable(MotionEvent event) {
        Drawable drawable = isRtl() ? mEndDrawable : mStartDrawable;
        if (drawable == null) {
            return false;
        }

        // 计算图片点击可响应的范围，计算方法见http://trinea.iteye.com/blog/1562388
        int drawHeight = drawable.getIntrinsicHeight();
        int drawWidth = drawable.getIntrinsicWidth();
        int topBottomDis = (mTopDrawable == null ? 0 : mTopDrawable.getIntrinsicHeight())
                - (mBottomDrawable == null ? 0 : mBottomDrawable.getIntrinsicHeight());
        double imageCenterY = 0.5 * (iDrawableClickAble.getHeight() + topBottomDis);
        Rect imageBounds = new Rect(iDrawableClickAble.getCompoundDrawablePadding() - mLazyX,
                (int) (imageCenterY - 0.5 * drawHeight - mLazyY), iDrawableClickAble.getCompoundDrawablePadding()
                + drawWidth + mLazyX,
                (int) (imageCenterY + 0.5 * drawHeight + mLazyY));
        return imageBounds.contains((int) event.getX(), (int) event.getY());
    }

    /**
     * touch上边的Drawable
     *
     * @param event
     * @return 是否在touch范围内
     */
    private boolean touchTopDrawable(MotionEvent event) {
        if (mTopDrawable == null) {
            return false;
        }

        int drawHeight = mTopDrawable.getIntrinsicHeight();
        int drawWidth = mTopDrawable.getIntrinsicWidth();
        int leftRightDis = (mStartDrawable == null ? 0 : mStartDrawable.getIntrinsicWidth())
                - (mEndDrawable == null ? 0 : mEndDrawable.getIntrinsicWidth());
        double imageCenterX = 0.5 * (iDrawableClickAble.getWidth() + leftRightDis);
        Rect imageBounds = new Rect((int) (imageCenterX - 0.5 * drawWidth - mLazyX), iDrawableClickAble.getCompoundDrawablePadding()
                - mLazyY,
                (int) (imageCenterX + 0.5 * drawWidth + mLazyX), iDrawableClickAble.getCompoundDrawablePadding()
                + drawHeight + mLazyY);
        return imageBounds.contains((int) event.getX(), (int) event.getY());
    }

    /**
     * touch右边的Drawable
     *
     * @param event
     * @return 是否在touch范围内
     */
    private boolean touchEndDrawable(MotionEvent event) {
        Drawable drawable = isRtl() ? mStartDrawable : mEndDrawable;
        if (drawable == null) {
            return false;
        }

        int drawHeight = drawable.getIntrinsicHeight();
        int drawWidth = drawable.getIntrinsicWidth();
        int topBottomDis = (mTopDrawable == null ? 0 : mTopDrawable.getIntrinsicHeight())
                - (mBottomDrawable == null ? 0 : mBottomDrawable.getIntrinsicHeight());
        double imageCenterY = 0.5 * (iDrawableClickAble.getHeight() + topBottomDis);
        Rect imageBounds = new Rect(iDrawableClickAble.getWidth() - iDrawableClickAble.getCompoundDrawablePadding() - drawWidth - mLazyX,
                (int) (imageCenterY - 0.5 * drawHeight - mLazyY),
                iDrawableClickAble.getWidth() - iDrawableClickAble.getCompoundDrawablePadding() + mLazyX,
                (int) (imageCenterY + 0.5 * drawHeight + mLazyY));
        return imageBounds.contains((int) event.getX(), (int) event.getY());
    }

    /**
     * touch下边的Drawable
     *
     * @param event
     * @return 是否在touch范围内
     */
    private boolean touchBottomDrawable(MotionEvent event) {
        if (mBottomDrawable == null) {
            return false;
        }

        int drawHeight = mBottomDrawable.getIntrinsicHeight();
        int drawWidth = mBottomDrawable.getIntrinsicWidth();
        int leftRightDis = (mStartDrawable == null ? 0 : mStartDrawable.getIntrinsicWidth())
                - (mEndDrawable == null ? 0 : mEndDrawable.getIntrinsicWidth());
        double imageCenterX = 0.5 * (iDrawableClickAble.getWidth() + leftRightDis);
        Rect imageBounds = new Rect((int) (imageCenterX - 0.5 * drawWidth - mLazyX), iDrawableClickAble.getHeight()
                - iDrawableClickAble.getCompoundDrawablePadding()
                - drawHeight - mLazyY,
                (int) (imageCenterX + 0.5 * drawWidth + mLazyX), iDrawableClickAble.getHeight()
                - iDrawableClickAble.getCompoundDrawablePadding()
                + mLazyY);
        return imageBounds.contains((int) event.getX(), (int) event.getY());
    }

    /**
     * 得到Drawable可响应的点击区域x方向允许的误差
     *
     * @return the lazyX
     */
    public int getLazyX() {
        return mLazyX;
    }

    /**
     * 设置Drawable可响应的点击区域x方向允许的误差
     *
     * @param lazyX
     */
    public void setLazyX(int lazyX) {
        this.mLazyX = lazyX;
    }

    /**
     * 得到Drawable可响应的点击区域y方向允许的误差
     *
     * @return the lazyY
     */
    public int getLazyY() {
        return mLazyY;
    }

    /**
     * 设置Drawable可响应的点击区域y方向允许的误差
     *
     * @param lazyY
     */
    public void setLazyY(int lazyY) {
        this.mLazyY = lazyY;
    }

    /**
     * 设置Drawable可响应的点击区域x和y方向允许的误差
     *
     * @param lazyX
     * @param lazyY
     */
    public void setLazy(int lazyX, int lazyY) {
        this.mLazyX = lazyX;
        this.mLazyY = lazyY;
    }

    /**
     * 设置图片点击的listener
     *
     * @param listener
     */
    public void setDrawableClickListener(DrawableClickListener listener) {
        this.mDrawableClickListener = listener;
    }


    public interface DrawableClickListener {

        /**
         * 点击相应位置的响应函数
         *
         * @param position
         */
        void onClick(DrawablePosition position);
    }
}
