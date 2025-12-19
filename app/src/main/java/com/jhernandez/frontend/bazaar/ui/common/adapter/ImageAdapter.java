package com.jhernandez.frontend.bazaar.ui.common.adapter;

/*
 * Adapter for displaying a list of images.
 * Manages image addition, removal, and updates within a RecyclerView.
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.jhernandez.frontend.bazaar.databinding.ItemImageBinding;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

    private final List<Uri> images;
    private final OnImageRemovedListener listener;

    public void addImage(Uri image) {
        this.images.add(image);
        notifyItemInserted(images.size() - 1);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateImages(List<Uri> newImages) {
        if (newImages == null) { return; }
        this.images.addAll(newImages);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageBinding binding = ItemImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ImageHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        Context context = holder.itemView.getContext();
        holder.binding.removeItem.setOnClickListener(v -> {
            images.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, images.size());
            listener.onImageRemoved(position);
        });
        ViewUtils.showImageOnImageView(context, images.get(position), holder.binding.imageItem);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ImageHolder extends RecyclerView.ViewHolder {
        private final ItemImageBinding binding;

        public ImageHolder(@NonNull ItemImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnImageRemovedListener {
        void onImageRemoved(Integer position);
    }
}
