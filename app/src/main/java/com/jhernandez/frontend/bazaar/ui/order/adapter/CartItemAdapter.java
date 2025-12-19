package com.jhernandez.frontend.bazaar.ui.order.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.databinding.ItemCartProductBinding;
import com.jhernandez.frontend.bazaar.domain.model.Item;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;


/*
 * Adapter for displaying items in the shopping cart.
 * Handles item click, quantity change, and delete actions.
 */
@RequiredArgsConstructor
public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemHolder> {

    private final List<Item> items;
    private final OnProductActionListener listener;

    @SuppressLint("NotifyDataSetChanged")
    public void updateItems(List<Item> items) {
        if (items == null) return;
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartProductBinding binding = ItemCartProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CartItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemAdapter.CartItemHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Item item = items.get(position);
        Double itemPrice = item.getProduct().hasDiscount()
                ? item.getProduct().discountPrice()
                : item.getProduct().price();
        holder.binding.tvProductName.setText(item.getProduct().name());
        holder.binding.tvProductPrice.setText(String.format(
                    context.getString(R.string.price_format),
                    itemPrice));
        holder.binding.tvProductShipping.setText(String.format(
                context.getString(R.string.shipping_tv),
                item.getProduct().shipping()));
        holder.binding.tvQuantity.setText(String.format(
                context.getString(R.string.quantity_of_products),
                item.getQuantity()));
        holder.binding.tvStock.setText(String.format(
                context.getString(R.string.tv_stock),
                item.getProduct().stock()));
        ViewUtils.showImageOnImageView(context, item.getProduct().imagesUrl().get(0), holder.binding.productImage);

        holder.binding.llProductInfo.setOnClickListener(v -> listener.onItemClicked(item.getProduct().id()));
        holder.binding.btnDelete.setOnClickListener(v -> listener.onDeleteClicked(item.getId()));
        holder.binding.tvQuantity.setOnClickListener(v -> listener.onQuantityClicked(item.getId()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class CartItemHolder extends RecyclerView.ViewHolder {
        private final ItemCartProductBinding binding;

        public CartItemHolder(ItemCartProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnProductActionListener {
        void onItemClicked(Long productId);
        void onQuantityClicked(Long itemId);
        void onDeleteClicked(Long itemId);
    }
}
