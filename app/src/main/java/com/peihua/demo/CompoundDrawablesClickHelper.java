package com.peihua.demo;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;

import java.util.Locale;

/**
 * CompoundDrawable 点击事件辅助类
 * Created by dingpeihua on 2017/8/29.
 */
public final class CompoundDrawablesClickHelper {
    private IDrawableClickAble iDrawableClickAble;
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
        this.mStartDrawable = wrap(mStartDrawable);
        this.mTopDrawable = wrap(mTopDrawable);
        this.mEndDrawable = wrap(mEndDrawable);
        this.mBottomDrawable = wrap(mBottomDrawable);
        setBounds(this.mStartDrawable);
        setBounds(this.mTopDrawable);
        setBounds(this.mEndDrawable);
        setBounds(this.mBottomDrawable);
        setDrawables();
    }

    public void setDrawables() {
        if (iDrawableClickAble == null) {
            throw new NullPointerException("Must be implements interface IDrawableClickAble");
        }
        final Drawable[] compoundDrawables = iDrawableClickAble.getCompoundDrawables();
        boolean[] isVisible = iDrawableClickAble.isVisible();
        if (compoundDrawables == null || compoundDrawables.length != 4) {
            throw new RuntimeException("compoundDrawables.length != 4");
        }
        if (isVisible == null || isVisible.length != 4) {
            throw new RuntimeException("isVisible.length != 4");
        }
        if (isRtl()) {
            this.iDrawableClickAble.setCompoundDrawables(isVisible[2] ? this.mEndDrawable == null ? compoundDrawables[2] : mEndDrawable : null,
                    isVisible[1] ? this.mTopDrawable == null ? compoundDrawables[1] : mTopDrawable : null,
                    isVisible[0] ? this.mStartDrawable == null ? compoundDrawables[0] : mStartDrawable : null,
                    isVisible[3] ? this.mBottomDrawable == null ? compoundDrawables[3] : mBottomDrawable : null);
        } else {
            this.iDrawableClickAble.setCompoundDrawables(isVisible[0] ? this.mStartDrawable == null ? compoundDrawables[0] : mStartDrawable : null,
                    isVisible[1] ? this.mTopDrawable == null ? compoundDrawables[1] : mTopDrawable : null,
                    isVisible[2] ? this.mEndDrawable == null ? compoundDrawables[2] : mEndDrawable : null,
                    isVisible[3] ? this.mBottomDrawable == null ? compoundDrawables[3] : mBottomDrawable : null);
        }
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
                    mDrawableClickListener.onClick(DrawableClickListener.DrawablePosition.START);
                } else if (mIsTopTouched) {
                    mDrawableClickListener.onClick(DrawableClickListener.DrawablePosition.TOP);
                } else if (mIsEndTouched) {
                    mDrawableClickListener.onClick(DrawableClickListener.DrawablePosition.END);
                } else if (mIsBottomTouched) {
                    mDrawableClickListener.onClick(DrawableClickListener.DrawablePosition.BOTTOM);
                }
            }
        }
        return iDrawableClickAble.callSuperOnTouchEvent(event);
    }

    static boolean isRtl() {
        return TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL;
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

    /**
     * 图片点击的监听器
     *
     * @author Trinea 2012-5-3 下午11:45:41
     */
    public interface DrawableClickListener {

        /**
         * 图片的位置
         */
        enum DrawablePosition {
            /**
             * 图片在TextView的左部
             **/
            START,
            /**
             * 图片在TextView的上部
             **/
            TOP,
            /**
             * 图片在TextView的右部
             **/
            END,
            /**
             * 图片在TextView的底部
             **/
            BOTTOM,
        }


        /**
         * 点击相应位置的响应函数
         *
         * @param position
         */
        void onClick(DrawablePosition position);
    }
}
