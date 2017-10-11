package com.example.gopal.assignmenthtml;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Dilip on 11-10-2017.
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
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_row_product, parent, false));
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
        Picasso.with(mContext).load(htmlData.getImageUrl()).placeholder(ContextCompat.getDrawable(mContext, R.mipmap.ic_launcher)).into(holder.productImage);
        holder.productTitle.setText(htmlData.getProductName());
        holder.productPrice.setText(htmlData.getPriceWithDiscount());
        holder.productOldPrice.setText(htmlData.getPriceWithoutDiscount());
        if (htmlData.getDiscount() != null && !htmlData.getDiscount().isEmpty() && htmlData.getDiscount().length() <= 3) {
            holder.discount.setVisibility(View.VISIBLE);
            holder.discount.setText(htmlData.getDiscount());
        } else {
            holder.discount.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView productPrice;
        private final TextView productTitle;
        private final TextView productOldPrice;
        private final TextView discount;
        ImageView productImage;

        public ViewHolder(View itemView) {
            super(itemView);
            productImage = (ImageView) itemView.findViewById(R.id.iv_prod_image);
            productOldPrice = (TextView) itemView.findViewById(R.id.tv_prod_price_old);
            productOldPrice.setPaintFlags(productOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            productPrice = (TextView) itemView.findViewById(R.id.tv_prod_price);
            productTitle = (TextView) itemView.findViewById(R.id.tv_prod_title);
            discount = (TextView) itemView.findViewById(R.id.tv_discount);
        }

    }
}
