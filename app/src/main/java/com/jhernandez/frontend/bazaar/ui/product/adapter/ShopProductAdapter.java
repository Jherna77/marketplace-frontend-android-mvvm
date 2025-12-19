package com.jhernandez.frontend.bazaar.ui.product.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.databinding.ItemShopProductBinding;
import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/*
 * Adapter for displaying a list of products in the shop management interface.
 * It handles edit button clicks and displays product details.
 */
@RequiredArgsConstructor
public class ShopProductAdapter extends RecyclerView.Adapter<ShopProductAdapter.ShopProductHolder> {

    private final List<Product> products;
    private final OnEditClickedListener listener;

    @SuppressLint("NotifyDataSetChanged")
    public void updateProducts(List<Product> products) {
        if (products == null) { return; }
        this.products.clear();
        this.products.addAll(products);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShopProductAdapter.ShopProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemShopProductBinding binding = ItemShopProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ShopProductHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopProductAdapter.ShopProductHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Product product = products.get(position);
        holder.binding.itemEdit.setOnClickListener(v -> listener.onEditClicked(product.id()));
        holder.binding.tvProductName.setText(product.name());
        holder.binding.tvProductPrice.setText(String.format(
                context.getString(R.string.price_format),
                product.price()));
        holder.binding.tvSold.setText(String.format(
                context.getString(R.string.tv_sold),
                product.sold()));
        holder.binding.ratingBar.setRating(product.rating().floatValue());
        ViewUtils.showImageOnImageView(context, product.imagesUrl().get(0), holder.binding.productImage);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ShopProductHolder extends RecyclerView.ViewHolder {
        private final ItemShopProductBinding binding;

        public ShopProductHolder(ItemShopProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnEditClickedListener {
        void onEditClicked(Long productId);
    }
}
