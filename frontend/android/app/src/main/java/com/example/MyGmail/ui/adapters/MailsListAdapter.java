package com.example.MyGmail.ui.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MyGmail.R;
import com.example.MyGmail.data.models.Mail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class MailsListAdapter extends RecyclerView.Adapter<MailsListAdapter.MailsViewHolder> {
    private final List<Mail> mailsList;
    private final OnMailsListAdapterListener listener;

    public MailsListAdapter(List<Mail> mailsList, OnMailsListAdapterListener listener) {
        this.mailsList = (mailsList != null) ? new ArrayList<>(mailsList) : new ArrayList<>();
        this.listener = listener;
        setHasStableIds(true);
    }

    public interface OnMailsListAdapterListener {
        void onMailClick(Mail mail);

        void onStarClick(Mail mail);
    }

    /** ViewHolder holds the views for a single mail row. */
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
                boolean newStar = !(labels != null && labels.contains("starred"));
                imgStar.setImageResource(newStar ? R.drawable.ic_star_filled : R.drawable.ic_star);
                if (mail.getLabels() == null) mail.setLabels(new ArrayList<>());
                if (newStar) {
                    if (!mail.getLabels().contains("starred")) mail.getLabels().add("starred");
                } else {
                    mail.getLabels().remove("starred");
                }
                if (listener != null) listener.onStarClick(mail);
            });
        }


        @NonNull
        private String formatDate(String createdAt) {
            try {
                SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
                iso.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date d = iso.parse(createdAt);
                if (d == null) return "";
                SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
                SimpleDateFormat dateFmt = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                return dateFmt.format(d);
            } catch (Exception e) {
                return "";
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
        return (mailsList != null) ? mailsList.size() : 0;
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
        final List<Mail> sortedNew = (newData != null) ? new ArrayList<>(newData) : new ArrayList<>();
        sortedNew.sort((a, b) -> Long.compare(getEpochMillis(b), getEpochMillis(a)));
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return mailsList.size();
            }

            @Override
            public int getNewListSize() {
                return sortedNew.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return mailsList.get(oldItemPosition).getId()
                        .equals(sortedNew.get(newItemPosition).getId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Mail oldM = mailsList.get(oldItemPosition);
                Mail newM = sortedNew.get(newItemPosition);
                return oldM.equals(newM);
            }
        });
        mailsList.clear();
        mailsList.addAll(sortedNew);
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
//  ===== Helpers =====
    /**
     * Returns epoch millis for sorting:
     * prefers updatedAt; if missing, uses createdAt; if both missing/unparseable → Long.MIN_VALUE.
     */
    private static long getEpochMillis(Mail m) {
        String iso = firstNonEmpty(m.getUpdatedAt(), m.getCreatedAt());
        if (iso == null) return Long.MIN_VALUE;
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
            f.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = f.parse(iso);
            return (d != null) ? d.getTime() : Long.MIN_VALUE;
        } catch (Exception ignore) {
            return Long.MIN_VALUE;
        }
    }

    private static String firstNonEmpty(String a, String b) {
        if (a != null && !a.isEmpty()) return a;
        return (b != null && !b.isEmpty()) ? b : null;
    }
}
