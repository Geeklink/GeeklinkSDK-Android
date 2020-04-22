package com.example.helloworld.impl;

import android.app.Dialog;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Yao Ming on 2018/1/17.
 */

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private Dialog dialog;


    public RecyclerItemClickListener(final Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {


            public boolean onSingleTapUp(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null) {
                    if (mListener != null) {
                        mListener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
                    }
                } else {
                    mListener.onOutSideClick();
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null) {
                    mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
            }


        });
    }



    public RecyclerItemClickListener(final Context context, final RecyclerView recyclerView, OnItemClickListener listener, final Dialog dialog) {
        mListener = listener;
        this.dialog = dialog;

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {


            public boolean onSingleTapUp(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null) {
                    mListener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
                    if(dialog != null){
                        dialog.dismiss();
                    }
                }
                return true;
            }

        });
    }


    public RecyclerItemClickListener(final Context context, final RecyclerView recyclerView, OnItemClickListener listener, final Dialog dialog, final boolean autoDissmiss) {
        mListener = listener;
        this.dialog = dialog;

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {


            public boolean onSingleTapUp(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null) {
                    mListener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
                    if(dialog != null && autoDissmiss){
                        dialog.dismiss();
                    }
                }
                return false;
            }

        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
