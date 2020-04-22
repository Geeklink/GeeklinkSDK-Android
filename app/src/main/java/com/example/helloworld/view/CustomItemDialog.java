package com.example.helloworld.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;
import com.example.helloworld.impl.OnItemClickListener;
import com.example.helloworld.impl.RecyclerItemClickListener;

/**
 * Created by Yao Ming on 2018/1/20.
 */

public class CustomItemDialog extends Dialog {

    public CustomItemDialog(@NonNull Context context) {
        super(context);
    }

    public CustomItemDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CustomItemDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {
        CustomItemDialog dialog;

        public CustomItemDialog create(final AppCompatActivity context, RecyclerView.Adapter adapter,
                                       OnItemClickListener itemClickListener){
            CardView dialogView = (CardView) LayoutInflater.from(context).inflate(R.layout.list_dialog_layout, null);
            LinearLayout contentView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.home_item_dialog_layout,null);
            dialogView.addView(contentView);
            dialog = new CustomItemDialog(context, R.style.CustomDialogNewT);
            RecyclerView recyclerView = contentView.findViewById(R.id.home_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);

            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context,recyclerView,itemClickListener,dialog));


            contentView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            WindowManager.LayoutParams localLayoutParams = dialog.getWindow().getAttributes();
            localLayoutParams.x = 0;
            localLayoutParams.y = -1000;
            localLayoutParams.gravity = 80;
            WindowManager m = context.getWindowManager();
            Display d = m.getDefaultDisplay();
            int dialogWidth = (int) (d.getWidth() * 0.96);
            dialogView.setMinimumWidth(dialogWidth);
            contentView.setLayoutParams(new FrameLayout.LayoutParams(dialogWidth, FrameLayout.LayoutParams.WRAP_CONTENT));

            dialog.onWindowAttributesChanged(localLayoutParams);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            dialog.setContentView(dialogView);

            return dialog;
        }
    }

}
