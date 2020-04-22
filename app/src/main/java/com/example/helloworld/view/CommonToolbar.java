package com.example.helloworld.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.helloworld.R;
import com.example.helloworld.utils.DensityUtil;

/**
 * Created by Yao Ming on 2018/1/19.
 */

public class CommonToolbar extends Toolbar implements View.OnClickListener,View.OnTouchListener {


    private View view;
//    private TextView mTxtLeftTitle;
    private TextView tilte;
    private TextView rightTv;
    private TextView edBtn;
    private LinearLayout rightBtn;
    private ImageView backIconImgV;

    private String editBtnStr;
    private int editBtnTextColor;
    private int colorNumber;
    private int rightTextColor;
    private String rightTextStr;
    private String tilteStr;
    private Drawable rightTextbg;
    private float rightTextSize;
    private float titleTextSize;
    private boolean rightTextIsvisible = true;
    private Drawable rightImgbg;
    private boolean rightImgIsvisible;
    private boolean leftVisible;
    private boolean editVisible;
    private Context context;

    private ImageView rightImg;


    LeftListener leftListener;
    RightListener rightListener;
    EditListener editListener;



    public interface LeftListener {
        void leftClick();
    }

    public interface RightListener {
        void rightClick();
    }
    public interface  EditListener{
        void editClick();
    }

    public CommonToolbar(Context context) {
        super(context);
    }

    public CommonToolbar(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        view = inflate(context, R.layout.title_layout, null);
//        mTxtLeftTitle = view.findViewById(R.id.left_tv);
        tilte = view.findViewById(R.id.title);
        rightTv = view.findViewById(R.id.right_tv);
        rightImg = view.findViewById(R.id.right_icon);
        edBtn = view.findViewById(R.id.btn_edit);
        rightBtn = view.findViewById(R.id.rightRL);
        backIconImgV = view.findViewById(R.id.back);

        TypedArray tArray = context.obtainStyledAttributes(attrs, R.styleable.titlebar);
        if(tArray != null){
            rightTextColor = tArray.getColor(R.styleable.titlebar_viewbar_right_TextColor,Color.WHITE);
            rightTextStr = tArray.getString(R.styleable.titlebar_viewbar_right_Text);
            rightTextIsvisible = tArray.getBoolean(R.styleable.titlebar_viewbar_right_isvisible, true);
            rightTextSize = tArray.getDimension(R.styleable.titlebar_viewbar_right_TextSize, ViewGroup.LayoutParams.WRAP_CONTENT);
            rightImgbg = tArray.getDrawable(R.styleable.titlebar_right_ImgBackground);
            rightImgIsvisible = tArray.getBoolean(R.styleable.titlebar_right_Imgisvisible, false);
            leftVisible = tArray.getBoolean(R.styleable.titlebar_viewbar_left_isvisible, true);
            editBtnStr = tArray.getString(R.styleable.titlebar_edit_button_Text);
            editVisible = tArray.getBoolean(R.styleable.titlebar_edit_button, false);
            editBtnTextColor = tArray.getColor(R.styleable.titlebar_edit_button_TextColor,Color.WHITE);
            titleTextSize = tArray.getDimension(R.styleable.titlebar_viewbar_title_TextSize, ViewGroup.LayoutParams.WRAP_CONTENT);
            tilteStr = tArray.getString(R.styleable.titlebar_viewbar_title);
            tilte.setText(tilteStr);
            tilte.setTextSize(titleTextSize);

            rightTv.setText(rightTextStr);
            rightTv.setTextColor(rightTextColor);
            rightTv.setVisibility(rightTextIsvisible?VISIBLE : GONE);
//            if(rightTextStr.length()>4){
//                rightTv.setTextSize(11);
//            }

            if (rightImgIsvisible) {
                rightImg.setVisibility(VISIBLE);
                rightImg.setImageDrawable(rightImgbg);
            } else {
                rightImg.setVisibility(GONE);
            }
            if(!leftVisible){
                view.findViewById(R.id.backRY).setVisibility(View.GONE);
            }
            if(editVisible){
                edBtn.setVisibility(View.VISIBLE);
                edBtn.setOnClickListener(this);
                edBtn.setOnTouchListener(this);
                if(!TextUtils.isEmpty(editBtnStr)){
                    edBtn.setText(editBtnStr);
                }
                edBtn.setTextColor(editBtnTextColor);
            }
        }

        setContentInsetsRelative(0,0);
        addView(view);

        view.findViewById(R.id.rightRL).setOnTouchListener(this);
        view.findViewById(R.id.backRY).setOnClickListener(this);
        view.findViewById(R.id.rightRL).setOnClickListener(this);
    }

    public CommonToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backRY:
                if (leftListener != null) {
                    leftListener.leftClick();
                } else {
                    ((Activity)getContext()).finish();
                }
                break;

            case R.id.rightRL:
                if ((rightTv.getVisibility() == VISIBLE && !TextUtils.isEmpty(rightTv.getText()))
                        || rightImg.getVisibility() == VISIBLE) {
                    if (rightListener != null) {
                        rightListener.rightClick();
                    }
                }
                break;
            case R.id.btn_edit:
                if(editListener != null){
                    editListener.editClick();
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(view.getId() == R.id.right_tv){
                    rightTv.setTextColor(Color.WHITE);
                }else if(view.getId() == R.id.btn_edit){
                    edBtn.setTextColor(Color.WHITE);
                }
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                if(view.getId() == R.id.right_tv){
                    rightTv.setTextColor(rightTextColor);
                }else if(view.getId() == R.id.btn_edit){
                    edBtn.setTextColor(editBtnTextColor);
                }

                break;
        }
        return false;
    }


    public void setLeftClick(LeftListener l) {
        leftListener = l;
    }

    public void setRightClick(RightListener l) {
        rightListener = l;
    }
    public void setEditListener(EditListener listener){
        this.editListener = listener;
    }

//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        mTxtLeftTitle = findViewById(R.id.txt_left_title);
//        mTxtMiddleTitle = findViewById(R.id.txt_main_title);
//        mTxtRightTitle = findViewById(R.id.txt_right_title);
//    }

    //设置中间title的内容
    public void setMainTitle(String text) {
        this.setTitle(" ");
        tilte.setVisibility(View.VISIBLE);
        tilte.setText(text);
    }

    public void setRightImg(int img){
        rightImgbg = getContext().getResources().getDrawable(img);
        rightImg.setImageDrawable(rightImgbg);
        rightImgIsvisible = true;
        rightImg.setVisibility(VISIBLE);
    }

    public void setEditBtnVisible(boolean isVisisble){
        if(isVisisble){
            edBtn.setVisibility(VISIBLE);
        }else{
            edBtn.setVisibility(GONE);
        }
    }

    public void setRightImgVisible(boolean isVisisble){
        if(isVisisble){
            rightImg.setVisibility(VISIBLE);
        }else{
            rightImg.setVisibility(GONE);
        }
    }
    public ImageView getRightImageView(){
        return rightImg;
    }
    public void setMainTitle(int text) {
        this.setTitle(" ");
        tilte.setVisibility(View.VISIBLE);
        tilte.setText(getContext().getResources().getString(text));
    }

    //设置中间title的内容文字的颜色
    public void setMainTitleColor(int color) {
        tilte.setTextColor(color);
    }

//    //设置title左边文字
//    public void setLeftTitleText(String text) {
//        mTxtLeftTitle.setVisibility(View.VISIBLE);
//        mTxtLeftTitle.setText(text);
//    }

    public void setEditText(int resId){
        edBtn.setText(resId);
    }

    public void setEditText(String text){
        edBtn.setText(text);
    }
    public void setRightText(String text){
        rightTv.setText(text);
    }

    public void setRightTextVisible(boolean visible){
        if(visible){
            rightTv.setVisibility(VISIBLE);
        }else{
            rightTv.setVisibility(GONE);
        }
    }

    public void setRightTextColor(int colorId){
        rightTv.setTextColor(getResources().getColor(colorId));
    }

    public void setRightTextClickable(boolean isClickable){
        rightTv.setClickable(isClickable);
    }




    public void setBtnRight(int icon) {
        Drawable img = context.getResources().getDrawable(icon);
        int height = DensityUtil.dip2px(context, 30);
        int width = img.getIntrinsicWidth() * height / img.getIntrinsicHeight();
        img.setBounds(0, 0, width, height);
        rightImg.setVisibility(View.VISIBLE);
        rightImg.setBackgroundResource(icon);
    }

    public View getEditBtn(){
        return  edBtn;
    }
    public View getRightBtn(){
        return  rightBtn;
    }
    public ImageView getBackIconView(){
        return backIconImgV;
    }
}
