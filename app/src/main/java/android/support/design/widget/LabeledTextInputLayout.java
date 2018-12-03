package android.support.design.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LabeledTextInputLayout extends TextInputLayout {
    public LabeledTextInputLayout(Context context) {
        super(context);
    }

    public LabeledTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LabeledTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    void updateLabelState(boolean animate, boolean force) {
        final boolean isEnabled = isEnabled();
        final boolean hasText = mEditText != null;

        boolean isFocused = false;

        try {
            Method expandHint = TextInputLayout.class.getDeclaredMethod("arrayContains", int[].class, int.class);
            expandHint.setAccessible(true);
            isFocused = (boolean) expandHint.invoke(this, getDrawableState(), android.R.attr.state_focused);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        final boolean isErrorShowing = !TextUtils.isEmpty(getError());

        ColorStateList mDefaultTextColor = getReflect("mDefaultTextColor");
        boolean mHintExpanded = getReflectBoolean("mHintExpanded");
        boolean mCounterOverflowed = getReflectBoolean("mCounterOverflowed");
        TextView mCounterView = getReflect("mCounterView");
        ColorStateList mFocusedTextColor = getReflect("mFocusedTextColor");

        if (mDefaultTextColor != null) {
            mCollapsingTextHelper.setExpandedTextColor(mDefaultTextColor);
        }

        if (isEnabled && mCounterOverflowed && mCounterView != null) {
            mCollapsingTextHelper.setCollapsedTextColor(mCounterView.getTextColors());
        } else if (isEnabled && isFocused && mFocusedTextColor != null) {
            mCollapsingTextHelper.setCollapsedTextColor(mFocusedTextColor);
        } else if (mDefaultTextColor != null) {
            mCollapsingTextHelper.setCollapsedTextColor(mDefaultTextColor);
        }

        if (hasText || (isEnabled() && (isFocused || isErrorShowing))) {
            // We should be showing the label so do so if it isn't already
            if (force || mHintExpanded) {
                try {
                    Method expandHint = TextInputLayout.class.getDeclaredMethod("collapseHint", boolean.class);
                    expandHint.setAccessible(true);
                    expandHint.invoke(this, animate);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // We should not be showing the label so hide it
            if (force || !mHintExpanded) {
                try {
                    Method expandHint = TextInputLayout.class.getDeclaredMethod("expandHint", boolean.class);
                    expandHint.setAccessible(true);
                    expandHint.invoke(this, animate);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getReflect(String fieldName) {
        try {
            Field declaredField = TextInputLayout.class.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            return (T) declaredField.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean getReflectBoolean(String fieldName) {
        try {
            Field declaredField = TextInputLayout.class.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            return declaredField.getBoolean(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
}
