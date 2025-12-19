package com.jhernandez.frontend.bazaar.ui.home.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.databinding.ItemHomeProductBinding;
import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/*
 * Adapter for displaying products in the home screen.
 * Each product item shows details like name, price, rating, and image.
 */
@RequiredArgsConstructor
public class HomeProductAdapter extends RecyclerView.Adapter<HomeProductAdapter.HomeProductHolder> {

    private final List<Product> products;
    private final OnItemClickedListener listener;

    public void updateProducts(List<Product> products) {
        if (products == null) return;
        this.products.clear();
        this.products.addAll(products);
        notifyItemChanged(products.size());
    }

    @NonNull
    @Override
    public HomeProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHomeProductBinding binding = ItemHomeProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HomeProductHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeProductHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Product product = products.get(position);
        holder.binding.product.setOnClickListener(v -> listener.onItemClicked(product.id()));
        holder.binding.tvProductName.setText(product.name());
        if (product.hasDiscount()) {
            holder.binding.promo.setVisibility(View.VISIBLE);
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
            holder.binding.tvProductPrice.setText(String.format(
                    context.getString(R.string.price_format),
                    product.price()));
        }
        holder.binding.tvSold.setText(String.format(
                context.getString(R.string.tv_sold),
                product.sold()));
        holder.binding.ratingBar.setRating(product.rating().floatValue());
        ViewUtils.showImageOnImageView(context, product.imagesUrl().get(0),holder.binding.productImage);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class HomeProductHolder extends RecyclerView.ViewHolder {
        private final ItemHomeProductBinding binding;

        public HomeProductHolder(ItemHomeProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickedListener {
        void onItemClicked(Long productId);
    }
}
