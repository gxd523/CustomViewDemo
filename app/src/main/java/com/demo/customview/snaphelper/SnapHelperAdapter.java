package com.demo.customview.snaphelper;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.customview.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by chenzhimao on 17-7-6.
 */

public class SnapHelperAdapter extends RecyclerView.Adapter<SnapHelperAdapter.GalleryViewHolder> {
    int[] dataArray = new int[]{
            R.drawable.jdzz,
            R.drawable.ccdzz,
            R.drawable.dfh,
            R.drawable.dlzs,
            R.drawable.sgkptt,
            R.drawable.ttxss,
            R.drawable.zmq,
            R.drawable.zzhx
    };

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GalleryViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(final GalleryViewHolder holder, int position) {
        holder.mImageView.setImageResource(dataArray[position % dataArray.length]);
        holder.mTextView.setText(String.format("第%s个Item", position));

    }

    @Override
    public int getItemCount() {
        return 200;
    }

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView mTextView;

        public GalleryViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_gallery, parent, false));
            mImageView = (ImageView) itemView.findViewById(R.id.image);
            mTextView = (TextView) itemView.findViewById(R.id.tv_num);
        }
    }
}
