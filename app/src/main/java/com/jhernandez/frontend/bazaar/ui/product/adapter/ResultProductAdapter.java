package com.jhernandez.frontend.bazaar.ui.product.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.databinding.ItemResultProductBinding;
import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/*
 * Adapter for displaying a list of products in search results.
 * It handles product item clicks and displays product details including discounts.
 */
@RequiredArgsConstructor
public class ResultProductAdapter extends RecyclerView.Adapter<ResultProductAdapter.ResultProductHolder> {

    private final List<Product> products;
    private final OnItemClickedListener listener;

    @SuppressLint("NotifyDataSetChanged")
    public void updateProducts(List<Product> products) {
        if (products == null) { return; }
        this.products.clear();
        this.products.addAll(products);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResultProductAdapter.ResultProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemResultProductBinding binding = ItemResultProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ResultProductHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultProductAdapter.ResultProductHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Product product = products.get(position);
        holder.binding.product.setOnClickListener(v -> listener.onItemClicked(product.id()));
        holder.binding.tvProductName.setText(product.name());
        if (product.hasDiscount()) {
            holder.binding.promo.setVisibility(View.VISIBLE);
            holder.binding.tvProductPricePaintFlags.setVisibility(View.VISIBLE);
            holder.binding.tvProductPricePaintFlags.setText(String.format(
                    context.getString(R.string.price_format),
                    product.price()));
            holder.binding.tvProductPricePaintFlags.setPaintFlags(
                    holder.binding.tvProductPricePaintFlags.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
            holder.binding.tvProductPrice.setText(String.format(
                    context.getString(R.string.price_format),
                    product.discountPrice()));
        } else {
            holder.binding.promo.setVisibility(View.GONE);
            holder.binding.tvProductPricePaintFlags.setVisibility(View.GONE);
            holder.binding.tvProductPrice.setText(String.format(
                    context.getString(R.string.price_format),
                    product.price()));
        }
        holder.binding.ratingBar.setRating(product.rating().floatValue());
        ViewUtils.showImageOnImageView(context, product.imagesUrl().get(0), holder.binding.productImage);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ResultProductHolder extends RecyclerView.ViewHolder {
        private final ItemResultProductBinding binding;

        public ResultProductHolder(ItemResultProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickedListener {
        void onItemClicked(Long productId);
    }
}

