package com.jhernandez.frontend.bazaar.ui.home.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.databinding.ItemCategorySectionBinding;
import com.jhernandez.frontend.bazaar.ui.home.HomeViewModel;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/*
 * Adapter for displaying category sections in the home screen.
 * Each section represents a category with its associated products.
 */
@RequiredArgsConstructor
public class CategorySectionAdapter extends RecyclerView.Adapter<CategorySectionAdapter.SectionViewHolder> {

    private final List<HomeViewModel.HomeCategory> categories;
    private final OnItemClickedListener listener;

    @SuppressLint("NotifyDataSetChanged")
    public void updateCategories(List<HomeViewModel.HomeCategory> categories) {
        if (categories == null) return;
        this.categories.clear();
        this.categories.addAll(categories);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategorySectionBinding binding = ItemCategorySectionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SectionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        HomeViewModel.HomeCategory category = categories.get(position);
        holder.binding.tvCategoryName.setText(String.format(
                context.getString(R.string.category_name),
                category.getCategoryName()));

        HomeProductAdapter productAdapter = new HomeProductAdapter(category.getCategoryProducts(), listener::onItemClicked);
        holder.binding.rvCategoryProducts.setAdapter(productAdapter);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategorySectionBinding binding;

        SectionViewHolder(ItemCategorySectionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickedListener {
        void onItemClicked(Long productId);
    }
}
