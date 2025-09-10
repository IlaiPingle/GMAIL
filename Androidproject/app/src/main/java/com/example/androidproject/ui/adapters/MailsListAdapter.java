package com.example.androidproject.ui.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;
import com.example.androidproject.data.models.Mail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MailsListAdapter extends RecyclerView.Adapter<MailsListAdapter.MailsViewHolder> {
    private final List<Mail> mailsList;
    private final OnMailsListAdapterListener listener;

    public MailsListAdapter(List<Mail> mailsList, OnMailsListAdapterListener listener) {
        this.mailsList = (mailsList != null) ? mailsList : new ArrayList<>();
        this.listener = listener;
        setHasStableIds(true);
    }

    public interface OnMailsListAdapterListener {
        void onMailClick(Mail mail);

        void onStarClick(Mail mail);
    }

    static class MailsViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender, tvSubject, tvBody, tvTime;
        ImageView imgStar;

        MailsViewHolder(View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.textSender);
            tvSubject = itemView.findViewById(R.id.textSubject);
            tvBody = itemView.findViewById(R.id.textPreview);
            tvTime = itemView.findViewById(R.id.textTime);
            imgStar = itemView.findViewById(R.id.imgStar);
        }

        void bind(Mail mail, OnMailsListAdapterListener listener) {
            tvSender.setText(mail.getSender());
            tvSubject.setText(mail.getSubject());
            tvBody.setText(mail.getBody());
            tvTime.setText(formatDate(mail.getCreatedAt()));


            List<String> labels = mail.getLabels();
            boolean isStarred = labels != null && labels.contains("starred");
            boolean isUnread = labels != null && labels.contains("unread");

            imgStar.setImageResource(isStarred ? R.drawable.ic_star_filled : R.drawable.ic_star);
            tvSender.setTypeface(null, isUnread ? Typeface.BOLD : Typeface.NORMAL);
            tvSubject.setTypeface(null, isUnread ? Typeface.BOLD : Typeface.NORMAL);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onMailClick(mail);
            });
            imgStar.setOnClickListener(v -> {
                if (listener != null) listener.onStarClick(mail);
            });
        }

        @NonNull
        private String formatDate(String createdAt) {
            try {
                if (createdAt != null && !createdAt.isEmpty()) {
                    return new Date(createdAt).toString();
                }
                return "Unknown Date";
            } catch (Exception e) {
                return "Invalid Date";
            }
        }
    }

    @NonNull
    @Override
    public MailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_email, parent, false);
        return new MailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MailsViewHolder holder, int position) {
        holder.bind(mailsList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        if (mailsList != null) {
            return mailsList.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        try {
            return Long.parseLong(mailsList.get(position).getId());
        } catch (Exception e) {
            return mailsList.get(position).getId().hashCode();
        }
    }

    public void setMails(List<Mail> newData) {
        final List<Mail> finalNewData = newData;
        if (newData == null) newData = new ArrayList<>();
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() { return mailsList.size(); }
            @Override public int getNewListSize() { return finalNewData.size(); }
            @Override public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return mailsList.get(oldItemPosition).getId()
                        .equals(finalNewData.get(newItemPosition).getId());
            }
            @Override public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Mail oldM = mailsList.get(oldItemPosition);
                Mail newM = finalNewData.get(newItemPosition);
                return oldM.equals(newM);
            }
        });

        mailsList.clear();
        mailsList.addAll(newData);
        diff.dispatchUpdatesTo(this);
    }

    public Mail getItemAt(int position) {
        return mailsList.get(position);
    }

    public void removeAt(int position) {
        if (position >= 0 && position < mailsList.size()) {
            mailsList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
