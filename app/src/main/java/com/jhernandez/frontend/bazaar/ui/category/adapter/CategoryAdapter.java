package com.jhernandez.frontend.bazaar.ui.category.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.jhernandez.frontend.bazaar.databinding.ItemCategoryBinding;
import com.jhernandez.frontend.bazaar.domain.model.Category;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/*
 * Adapter for displaying categories in a RecyclerView.
 * Binds category data to the UI components and handles user interactions.
 */
@RequiredArgsConstructor
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    private final List<Category> categories;
    private final OnCategoryClickedListener listener;

    @SuppressLint("NotifyDataSetChanged")
    public void updateCategories(List<Category> categories) {
        if (categories == null) { return; }
        this.categories.clear();
        this.categories.addAll(categories);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Category category = categories.get(position);
        holder.binding.category.setOnClickListener(v -> listener.onCategoryClicked(category));
        holder.binding.tvCategoryName.setText(category.name());
        ViewUtils.showImageOnImageView(context, category.imageUrl(), holder.binding.categoryImage);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding binding;

        public CategoryHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnCategoryClickedListener {
        void onCategoryClicked(Category category);
    }

}
