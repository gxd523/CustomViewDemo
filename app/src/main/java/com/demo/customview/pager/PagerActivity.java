package com.demo.customview.pager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.demo.customview.R;
import com.demo.customview.base.BaseActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by guoxiaodong on 2020/6/19 14:00
 */
public class PagerActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        RecyclerView recyclerView = findViewById(R.id.activity_pager_recycler_view);
        PagerLayoutManager layoutManager = new PagerLayoutManager(3, 5, PagerLayoutManager.OrientationType.VERTICAL);
//        layoutManager.setAllowContinuousScroll(true);
//        layoutManager.setOrientationType(PagerGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

//        new PagerGridSnapHelper().attachToRecyclerView(recyclerView);
//        new PagerSnapHelper().attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(new RecyclerView.Adapter<PagerViewHolder>() {
            @NonNull
            @Override
            public PagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new PagerViewHolder(parent);
            }

            @Override
            public void onBindViewHolder(@NonNull PagerViewHolder holder, int position) {
                holder.setData(position);
            }

            @Override
            public int getItemCount() {
                return 50;
            }
        });
    }

    private static class PagerViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTv;

        public PagerViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pager, parent, false));
            titleTv = itemView.findViewById(R.id.adapter_pager_title_tv);
            titleTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    v.setBackgroundColor(hasFocus ? 0x66FF00FF : 0xFF666666);
                }
            });
        }

        public void setData(int position) {
            titleTv.setText(String.valueOf(position));
        }
    }
}
