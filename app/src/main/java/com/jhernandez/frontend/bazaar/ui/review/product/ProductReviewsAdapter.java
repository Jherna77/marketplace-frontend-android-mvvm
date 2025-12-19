package com.jhernandez.frontend.bazaar.ui.review.product;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.databinding.ItemProductReviewBinding;
import com.jhernandez.frontend.bazaar.domain.model.Review;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/*
 * Adapter for displaying a list of product reviews.
 * It binds review data to the RecyclerView items.
 */
@RequiredArgsConstructor
public class ProductReviewsAdapter extends RecyclerView.Adapter<ProductReviewsAdapter.ProductReviewtHolder> {

    private final List<Review> reviews;

    @SuppressLint("NotifyDataSetChanged")
    public void updateReviews(List<Review> reviews) {
        this.reviews.clear();
        this.reviews.addAll(reviews);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductReviewsAdapter.ProductReviewtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductReviewBinding binding = ItemProductReviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductReviewtHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductReviewsAdapter.ProductReviewtHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Review review = reviews.get(position);
        holder.binding.tvReviewAuthor.setText(review.author());
        holder.binding.tvReviewDate.setText(String.format(
                context.getString(R.string.string_format), review.reviewDate()
        ));
        holder.binding.ratingBar.setRating(review.rating());
        holder.binding.tvComment.setText(review.comment());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ProductReviewtHolder extends RecyclerView.ViewHolder {
        private final ItemProductReviewBinding binding;

        public ProductReviewtHolder(ItemProductReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
