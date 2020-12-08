package com.demo.customview.recycler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.demo.customview.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerCacheAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static List<Integer> viewHolderHashCodeList = new ArrayList<>();
    private final List<String> dataList;

    public RecyclerCacheAdapter() {
        this.dataList = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            dataList.add("测试.." + i);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerCacheViewHolder holder = new RecyclerCacheViewHolder(parent);
        Log.e("gxd", "onCreateViewHolder...ViewHolder count = " + viewHolderHashCodeList.size());
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.e("gxd", "onBindViewHolder...ViewHolder index = " + (viewHolderHashCodeList.indexOf(holder.hashCode()) + 1) + "...position = " + (position + 1));
        if (holder instanceof RecyclerCacheViewHolder) {
            RecyclerCacheViewHolder myViewHolder = (RecyclerCacheViewHolder) holder;
            myViewHolder.bindData(dataList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    static class RecyclerCacheViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvAge;

        public RecyclerCacheViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_cache, parent, false));
            viewHolderHashCodeList.add(hashCode());
            tvName = itemView.findViewById(R.id.tv_name);
            tvAge = itemView.findViewById(R.id.tv_age);
        }

        public void bindData(String data) {
            tvName.setText(data);
            tvAge.setText(String.valueOf(viewHolderHashCodeList.indexOf(this.hashCode()) + 1));
            itemView.setBackgroundColor(getAdapterPosition() % 2 == 0 ? 0x33666666 : 0xFFFFFFFF);
        }
    }

}
