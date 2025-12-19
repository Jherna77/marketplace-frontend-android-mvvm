package com.jhernandez.frontend.bazaar.ui.common.adapter;

/*
 * Generic adapter for displaying a list of items.
 * Utilizes a binder interface to handle item-specific logic.
 */
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.jhernandez.frontend.bazaar.databinding.ItemListEditBinding;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemListEditAdapter<T> extends RecyclerView.Adapter<ItemListEditAdapter.ItemListEditHolder> {

    private final List<T> items;
    private final ItemBinder<T> binder;

    @SuppressLint("NotifyDataSetChanged")
    public void updateItems(List<T> newItems) {
        if (newItems == null) { return; }
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
//        notifyItemChanged(items.size());
    }

    @NonNull
    @Override
    public ItemListEditHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListEditBinding binding = ItemListEditBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemListEditHolder(binding);
    }

    @Override
    public void onBindViewHolder( ItemListEditHolder holder, int position) {
        T item = items.get(position);
        holder.binding.itemName.setText(binder.getItemTitle(item));
        holder.binding.itemName.setOnClickListener(v -> binder.onEditClicked(item));
        holder.binding.itemEdit.setOnClickListener(v -> binder.onEditClicked(item));
        holder.binding.itemName.setOnClickListener(v -> binder.onEditClicked(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemListEditHolder extends RecyclerView.ViewHolder {
        private final ItemListEditBinding binding;

        public ItemListEditHolder(ItemListEditBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface ItemBinder<T> {
        String getItemTitle(T item);
        void onEditClicked(T item);
    }

}
