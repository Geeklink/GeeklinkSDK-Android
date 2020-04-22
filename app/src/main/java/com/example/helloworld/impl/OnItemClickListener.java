package com.example.helloworld.impl;

import android.view.View;

public interface OnItemClickListener {

    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);

    void onOutSideClick();

    void onSectionItemClick(View view, int position, int section);
}