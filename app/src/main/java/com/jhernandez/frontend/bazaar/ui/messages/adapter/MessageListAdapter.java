package com.jhernandez.frontend.bazaar.ui.messages.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jhernandez.frontend.bazaar.databinding.ItemMessageListBinding;
import com.jhernandez.frontend.bazaar.domain.model.Message;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

import java.util.List;

import lombok.RequiredArgsConstructor;


/*
 * Adapter for displaying messages in a list.
 * Handles message click and delete actions.
 */
@RequiredArgsConstructor
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageListHolder> {

    private final List<Message> messages;
    private final OnMessageActionListener listener;

    @SuppressLint("NotifyDataSetChanged")
    public void updateMessages(List<Message> newMessages) {
        if (newMessages == null) {
            return;
        }
        this.messages.clear();
        this.messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMessageListBinding binding = ItemMessageListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MessageListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Message message = messages.get(position);

        holder.binding.msgTitle.setText(ViewUtils.messageFormatter(message));
        holder.binding.msgTitle.setTypeface(null, message.seen()
                ? Typeface.NORMAL
                : Typeface.BOLD);
        holder.binding.icMessageSeen.setVisibility(message.seen() ? View.VISIBLE : View.GONE);
        holder.binding.icMessage.setVisibility(message.seen() ? View.GONE : View.VISIBLE);

        holder.binding.msgTitle.setOnClickListener(v -> listener.onMessageClicked(message.id()));
        holder.binding.deleteMessage.setOnClickListener(v -> listener.onDeleteClicked(message.id()));

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageListHolder extends RecyclerView.ViewHolder {
        private final ItemMessageListBinding binding;

        public MessageListHolder(ItemMessageListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnMessageActionListener {
        void onMessageClicked(Long messageId);
        void onDeleteClicked(Long messageId);
    }

}
