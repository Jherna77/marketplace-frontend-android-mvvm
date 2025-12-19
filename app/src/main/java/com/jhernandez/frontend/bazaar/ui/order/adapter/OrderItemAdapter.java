package com.jhernandez.frontend.bazaar.ui.order.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.databinding.ItemOrderProductBinding;
import com.jhernandez.frontend.bazaar.domain.model.Item;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/*
 * Adapter for displaying items in an order.
 * Handles leave review action.
 */
@RequiredArgsConstructor
public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemHolder> {

    private final List<Item> items;
    private final onItemClickedListener listener;

    @SuppressLint("NotifyDataSetChanged")
    public void updateItems(List<Item> items) {
        if (items == null) return;
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderProductBinding binding = ItemOrderProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Item item = items.get(position);

        holder.binding.tvProductName.setText(item.getProduct().name());
        holder.binding.tvProductPrice.setText(String.format(
                context.getString(R.string.price_format),
                item.getSalePrice()));
        holder.binding.tvProductShipping.setText(String.format(
                context.getString(R.string.shipping_tv),
                item.getSaleShipping()));
        holder.binding.tvQuantity.setText(String.format(
                context.getString(R.string.quantity_of_products),
                item.getQuantity()));
        ViewUtils.showImageOnImageView(context, item.getProduct().imagesUrl().get(0), holder.binding.productImage);
        holder.binding.tvLeaveReview.setOnClickListener(v -> listener.onLeaveReviewClicked(item.getProduct().id()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class OrderItemHolder extends RecyclerView.ViewHolder {
        private final ItemOrderProductBinding binding;

        public OrderItemHolder(ItemOrderProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface onItemClickedListener {
        void onLeaveReviewClicked(Long productId);
    }
}

