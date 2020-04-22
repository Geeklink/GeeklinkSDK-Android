package com.example.helloworld.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.helloworld.R;

public class SelectorImageView extends AppCompatImageView {

    private final int norResId;
    private ColorMatrix brightnessMatrix;

    public SelectorImageView(Context context) {
        this(context,null,0);
    }

    public SelectorImageView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public SelectorImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectorImageView);
        norResId = typedArray.getResourceId(R.styleable.SelectorImageView_norBackGround, 0);
        typedArray.recycle();
        if (norResId != 0) {
            //改变图片颜色值 只有Src有用
            setImageResource(norResId);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()){
            case  MotionEvent.ACTION_DOWN:
//                changeLight(0.8f);
                setAlpha(0.6f);
                break;
            case MotionEvent.ACTION_UP:
//                changeLight(1.0f);
                setAlpha(1.0f);
                break;
        }

        return true;
    }

    private void changeLight(float brightness) {
        brightnessMatrix = new ColorMatrix();
        brightnessMatrix.setScale(brightness, brightness, brightness, 1f);
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(brightnessMatrix);
        this.setColorFilter(cf);
    }
}
