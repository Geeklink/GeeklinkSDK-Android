package com.example.helloworld.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;
import com.example.helloworld.bean.SortModel;
import com.geeklink.smartpisdk.utils.CommonUtil;
import com.gl.LanguageType;

import me.yokeyword.indexablerv.IndexableAdapter;


public class BrandsListAdapter extends IndexableAdapter<SortModel> {

    private LayoutInflater mInflater;

    public BrandsListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateTitleViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_index_country, parent, false);
        return new IndexVH(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_search, parent, false);
        return new ContentVH(view);
    }

    @Override
    public void onBindTitleViewHolder(RecyclerView.ViewHolder holder, String indexTitle) {
        IndexVH vh = (IndexVH) holder;
        vh.tv.setText(indexTitle);
    }

    @Override
    public void onBindContentViewHolder(RecyclerView.ViewHolder holder, SortModel entity) {
        ContentVH vh = (ContentVH) holder;
        if (CommonUtil.getSystemLanguageType() == LanguageType.TRADITIONAL_CHINESE) {
            vh.tv.setText( CommonUtil.translateSimplied2Tradional(entity.getName()));
        } else {
            vh.tv.setText(entity.getName());
        }
    }


    private class IndexVH extends RecyclerView.ViewHolder {
        TextView tv;

        IndexVH(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_index);
        }
    }

    private class ContentVH extends RecyclerView.ViewHolder {
        TextView tv;

        ContentVH(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.title);
        }
    }
}
