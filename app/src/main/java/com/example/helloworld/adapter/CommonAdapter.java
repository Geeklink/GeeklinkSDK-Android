package com.example.helloworld.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.adapter.holder.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yao Ming on 2018/1/22.
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mDatas = new ArrayList<>();//必须这样声明初始化。否则容易报空指针异常
    protected LayoutInflater mInflater;
    private int count;
    private static final String TAG = "CommonAdapter";


    public CommonAdapter(Context context, int layoutId, List<T> datas)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mDatas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType)
    {
        count ++;
        ViewHolder viewHolder = ViewHolder.createViewHolder(mContext, parent, mLayoutId);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        convert(holder, mDatas.get(position),position);
    }

    public abstract void convert(ViewHolder holder, T t,int position);

    @Override
    public int getItemCount()
    {
        if(mDatas == null){
           return 0;
        }else {
            return mDatas.size();
        }
    }


    public void refreshData(List<T> datas){
        mDatas = datas;
        notifyDataSetChanged();
    }

    public T getItem(int position){
        if(position < mDatas.size()){
            return mDatas.get(position);
        }
        return null;
    }

    public void setDatas(List<T> datas){
         mDatas = datas;
    }
}
