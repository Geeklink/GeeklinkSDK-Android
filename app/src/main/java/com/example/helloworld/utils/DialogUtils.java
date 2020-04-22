package com.example.helloworld.utils;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.example.helloworld.R;
import com.example.helloworld.adapter.CommonAdapter;
import com.example.helloworld.adapter.holder.ViewHolder;
import com.example.helloworld.impl.OnItemClickListener;
import com.example.helloworld.view.CustomItemDialog;
import java.util.List;


public class DialogUtils {

    public static void showItemDialog(AppCompatActivity context , List<String> dialogItem, OnItemClickListener listener){
        CustomItemDialog.Builder builder = new CustomItemDialog.Builder();
        CustomItemDialog dialog = builder.create(context, new CommonAdapter<String>(context, R.layout.list_item,dialogItem) {

            @Override
            public void convert(ViewHolder holder, String item, int postion) {
                holder.setText(R.id.item_name,item);
            }
        }, listener);
        dialog.show();
    }

}
