/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.xyzreader.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

import com.example.xyzreader.R;


public class DrawInsetsFrameLayout extends FrameLayout {
    private Drawable mInsetBackground;
    private Drawable mTopInsetBackground;
    private Drawable mBottomInsetBackground;
    private Drawable mSideInsetBackground;

    private Rect mInsets;
    private Rect mTempRect = new Rect();
    private OnInsetsCallback mOnInsetsCallback;

    public DrawInsetsFrameLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public DrawInsetsFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DrawInsetsFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.DrawInsetsFrameLayout, defStyle, 0);
        assert a != null;

        mInsetBackground = a.getDrawable(R.styleable.DrawInsetsFrameLayout_insetBackground);

        a.recycle();
    }

    public void setInsetBackground(Drawable insetBackground) {
        if (mInsetBackground != null) {
            mInsetBackground.setCallback(null);
        }

        if (insetBackground != null) {
            insetBackground.setCallback(this);
        }

        mInsetBackground = insetBackground;
        postInvalidateOnAnimation();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requestApplyInsets();
        }
        if (mInsetBackground != null) {
            mInsetBackground.setCallback(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mInsetBackground != null) {
            mInsetBackground.setCallback(null);
        }
    }

    public void setOnInsetsCallback(OnInsetsCallback onInsetsCallback) {
        mOnInsetsCallback = onInsetsCallback;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        insets = super.onApplyWindowInsets(insets);
        mInsets = new Rect(
                insets.getSystemWindowInsetLeft(),
                insets.getSystemWindowInsetTop(),
                insets.getSystemWindowInsetRight(),
                insets.getSystemWindowInsetBottom());
        setWillNotDraw(false);
        postInvalidateOnAnimation();
        if (mOnInsetsCallback != null) {
            mOnInsetsCallback.onInsetsChanged(mInsets);
        }
        return insets;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int width = getWidth();
        int height = getHeight();

        if (mInsets != null) {
            // Top
            mTempRect.set(0, 0, width, mInsets.top);
            if (mInsetBackground != null) {
                mInsetBackground.setBounds(mTempRect);
                mInsetBackground.draw(canvas);
            }

            // Bottom
            mTempRect.set(0, height - mInsets.bottom, width, height);
            if (mInsetBackground != null) {
                mInsetBackground.setBounds(mTempRect);
                mInsetBackground.draw(canvas);
            }

            // Left
            mTempRect.set(0, mInsets.top, mInsets.left, height - mInsets.bottom);
            if (mInsetBackground != null) {
                mInsetBackground.setBounds(mTempRect);
                mInsetBackground.draw(canvas);
            }

            // Right
            mTempRect.set(width - mInsets.right, mInsets.top, width, height - mInsets.bottom);
            if (mInsetBackground != null) {
                mInsetBackground.setBounds(mTempRect);
                mInsetBackground.draw(canvas);
            }
        }
    }

    public static interface OnInsetsCallback {
        public void onInsetsChanged(Rect insets);
    }
}
