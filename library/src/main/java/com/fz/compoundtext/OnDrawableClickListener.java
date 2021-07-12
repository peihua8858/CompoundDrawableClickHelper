package com.fz.compoundtext;

import android.view.View;
import android.widget.EditText;

/**
 * 图标点击事件监听器
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2020/5/10 13:53
 */
public interface OnDrawableClickListener {
    /**
     * 图标点击事件
     *
     * @param view
     * @param position
     * @author dingpeihua
     * @date 2020/5/10 13:20
     * @version 1.0
     */
    default void onDrawableClick(View view, DrawablePosition position) {
        if (view instanceof EditText) {
            EditText editText = (EditText) view;
            if (position == DrawablePosition.END) {
                editText.setError(null);
                editText.setText("");
            }
        }
    }
}
