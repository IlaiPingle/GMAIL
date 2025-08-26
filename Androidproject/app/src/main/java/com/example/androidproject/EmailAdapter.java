// EmailAdapter.java
package com.example.androidproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.EmailViewHolder> {

    private List<EmailItem> emails;

    public EmailAdapter(List<EmailItem> emails) {
        this.emails = emails;
    }

    @NonNull
    @Override
    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_email, parent, false);
        return new EmailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
        EmailItem email = emails.get(position);
        holder.tvAvatar.setText(email.getSender().substring(0, 1));
        holder.tvSender.setText(email.getSender());
        holder.tvSubject.setText(email.getSubject());
        holder.tvPreview.setText(email.getPreview());
        holder.tvTime.setText(email.getTime());
    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    static class EmailViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvSender, tvSubject, tvPreview, tvTime;

        public EmailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_avatar);
            tvSender = itemView.findViewById(R.id.tv_sender);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvPreview = itemView.findViewById(R.id.tv_preview);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}