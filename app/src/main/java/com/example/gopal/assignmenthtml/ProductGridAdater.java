package com.example.gopal.assignmenthtml;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gopal.assignmenthtml.databinding.ListRowProductBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Gopal on 11-10-2017.
 */

public class ProductGridAdater extends RecyclerView.Adapter<ProductGridAdater.ViewHolder> {
    private final ArrayList<HtmlData> mProductList;
    private Context mContext;
    private final RecycleViewListener listener;

    public ProductGridAdater(RecycleViewListener listener, ArrayList<HtmlData> productList) {
        this.listener = listener;
        mProductList = productList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        ListRowProductBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_product, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        HtmlData htmlData = mProductList.get(position);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, (int) v.getTag());
            }
        });
        Picasso.with(mContext).load(htmlData.getImageUrl()).placeholder(ContextCompat.getDrawable(mContext, R.mipmap.ic_launcher)).into(holder.binding.ivProdImage);
        holder.bind(htmlData);
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ListRowProductBinding binding;

        public ViewHolder(ListRowProductBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(HtmlData data) {
            binding.setHtmlData(data);
            if (data.isDiscountVisibility())
                binding.tvDiscount.setVisibility(View.VISIBLE);
            else
                binding.tvDiscount.setVisibility(View.GONE);
            binding.tvProdPriceOld.setPaintFlags(binding.tvProdPriceOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.executePendingBindings();
        }

    }
}
