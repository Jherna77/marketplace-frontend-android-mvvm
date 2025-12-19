package com.jhernandez.frontend.bazaar.ui.common.adapter;

/*
 * Adapter for displaying a list of images in a slider format.
 * Manages image updates within a RecyclerView.
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.databinding.ItemImageSliderBinding;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageSliderHolder> {

    private final List<String> images;

    @SuppressLint("NotifyDataSetChanged")
    public void updateImages(List<String> images) {
        if (images == null) { return; }
        this.images.clear();
        this.images.addAll(images);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageSliderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageSliderBinding binding = ItemImageSliderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ImageSliderHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderHolder holder, int position) {
        Context context = holder.itemView.getContext();
        holder.binding.tvCountImage.setText(String.format(
                context.getString(R.string.tv_image_count),
                position + 1, images.size()));
        ViewUtils.showImageOnImageView(context,
                images.get(position),
                holder.binding.sivImage);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ImageSliderHolder extends RecyclerView.ViewHolder {
        private final ItemImageSliderBinding binding;

        public ImageSliderHolder(ItemImageSliderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
