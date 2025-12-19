package com.jhernandez.frontend.bazaar.ui.common.adapter;

/*
 * Generic adapter for displaying a list of items with checkboxes.
 * Utilizes a binder interface to handle item-specific logic.
 */
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.jhernandez.frontend.bazaar.databinding.ItemListCheckBinding;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemListCheckAdapter<T> extends RecyclerView.Adapter<ItemListCheckAdapter.ItemListCheckHolder> {

    private final List<T> items;
    private final ItemBinder<T> binder;

    @SuppressLint("NotifyDataSetChanged")
    public void updateItems(List<T> newItems) {
        if (newItems == null) { return; }
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemListCheckHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListCheckBinding binding = ItemListCheckBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemListCheckHolder(binding);
    }

    @Override
    public void onBindViewHolder( ItemListCheckHolder holder, int position) {
        T item = items.get(position);
        holder.binding.itemName.setText(binder.getItemTitle(item));
        holder.binding.itemName.setOnClickListener(v -> binder.onCheckClicked(item));
        holder.binding.itemCheck.setOnClickListener(v -> binder.onCheckClicked(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemListCheckHolder extends RecyclerView.ViewHolder {
        private final ItemListCheckBinding binding;

        public ItemListCheckHolder(ItemListCheckBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface ItemBinder<T> {
        String getItemTitle(T item);
        void onCheckClicked(T item);
    }

}

