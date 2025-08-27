package com.example.androidproject.ui.email;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;
import com.example.androidproject.model.EmailItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying email items in a RecyclerView.
 * - Stable circular avatar colors (based on sender)
 * - Read/unread bold styling
 * - Star toggle
 * - Search filtering (sender/subject/preview)
 */
public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.EmailViewHolder> implements Filterable {

    private final List<EmailItem> emailList;     // filtered list
    private final List<EmailItem> allEmails;     // full list for filtering
    private final OnEmailClickListener listener;
    private final int[] colors = {
            Color.parseColor("#F44336"), // Red 500
            Color.parseColor("#E91E63"), // Pink 500
            Color.parseColor("#9C27B0"), // Purple 500
            Color.parseColor("#3F51B5"), // Indigo 500
            Color.parseColor("#2196F3"), // Blue 500
            Color.parseColor("#009688"), // Teal 500
            Color.parseColor("#4CAF50"), // Green 500
            Color.parseColor("#FF9800"), // Orange 500
            Color.parseColor("#795548")  // Brown 500
    };

    /**
     * Interface for handling email item clicks.
     * Implemented by the hosting activity/fragment to respond to item selections.
     */
    public interface OnEmailClickListener {
        void onEmailClick(int position);
    }

    // Constructor
    public EmailAdapter(List<EmailItem> emailList, OnEmailClickListener listener) {
        this.emailList = new ArrayList<>(emailList);
        this.allEmails = new ArrayList<>(emailList);
        this.listener = listener;
    }

    /**
     * Creates new ViewHolder objects as needed.
     * Inflates the item layout and initializes the ViewHolder.
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_email, parent, false);
        return new EmailViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder at the specified position.
     * Sets text, images, click listeners, and styles based on email properties.
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
        EmailItem email = emailList.get(position);

        holder.textSender.setText(email.getSender());
        holder.textSubject.setText(email.getSubject());
        holder.textPreview.setText(email.getPreview());
        holder.textTime.setText(email.getTime());

        holder.imgStar.setImageResource(email.isStarred() ?
                R.drawable.ic_star_filled :
                R.drawable.ic_star_outline);

        holder.imgStar.setOnClickListener(view -> {
            boolean next = !email.isStarred();
            email.setStarred(next);
            holder.imgStar.setImageResource(next ?
                    R.drawable.ic_star_filled :
                    R.drawable.ic_star_outline);
        });

        // Sender initial
        String sender = email.getSender() != null && !email.getSender().isEmpty() ? email.getSender() : "?";
        holder.textSenderInitial.setText(sender.substring(0, 1).toUpperCase());

        // Stable circular color using sender hash and keeping the oval shape
        int colorIndex = Math.abs(sender.hashCode()) % colors.length;
        int tintColor = colors[colorIndex];
        if (holder.textSenderInitial.getBackground() instanceof GradientDrawable) {
            GradientDrawable bg = (GradientDrawable) holder.textSenderInitial.getBackground();
            bg.setColor(tintColor);
        } else {
            // Fallback if background isn't the expected drawable
            holder.textSenderInitial.setBackgroundColor(tintColor);
        }

        // Read/unread styling
        if (!email.isRead()) {
            holder.textSender.setTypeface(null, Typeface.BOLD);
            holder.textSubject.setTypeface(null, Typeface.BOLD);
        } else {
            holder.textSender.setTypeface(null, Typeface.NORMAL);
            holder.textSubject.setTypeface(null, Typeface.NORMAL);
        }

        // Click to open
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) listener.onEmailClick(pos);
            }
        });

    }

    /**
     * Sets the full list of email items and refreshes the adapter.
     * Clears any existing items and adds the new ones.
     * @param items The new list of email items to display.
     */
    public void setItems(List<EmailItem> items) {
        allEmails.clear();
        allEmails.addAll(items);
        emailList.clear();
        emailList.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * Gets the email item at the specified position.
     * Useful for accessing item details in click handlers.
     * @param position The position of the item in the adapter.
     * @return The EmailItem at the given position.
     */
    public EmailItem getItem(int position) {
        return emailList.get(position);
    }

    // Total number of items
    @Override
    public int getItemCount() {
        return emailList.size();
    }

    /**
     * Provides filtering capabilities for the email list.
     * Filters based on sender, subject, or preview text matching the query.
     * Case-insensitive and trims whitespace.
     * Updates the displayed list and notifies the adapter of changes.
     * @return A Filter object for performing filtering operations.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String q = constraint == null ? "" : constraint.toString().trim().toLowerCase();
                List<EmailItem> filtered = new ArrayList<>();
                if (q.isEmpty()) {
                    filtered.addAll(allEmails);
                } else {
                    for (EmailItem e : allEmails) {
                        if ((e.getSender() != null && e.getSender().toLowerCase().contains(q)) ||
                                (e.getSubject() != null && e.getSubject().toLowerCase().contains(q)) ||
                                (e.getPreview() != null && e.getPreview().toLowerCase().contains(q))) {
                            filtered.add(e);
                        }
                    }
                }
                FilterResults res = new FilterResults();
                res.values = filtered;
                res.count = filtered.size();
                return res;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                emailList.clear();
                emailList.addAll((List<EmailItem>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    /**
     * ViewHolder for email item views.
     * Includes references to all subviews for easy access.
     * Handles view binding in onBindViewHolder.
     */
    static class EmailViewHolder extends RecyclerView.ViewHolder {
        TextView textSenderInitial, textSender, textSubject, textPreview, textTime;
        ImageView imgStar;

        EmailViewHolder(View itemView) {
            super(itemView);
            textSenderInitial = itemView.findViewById(R.id.textSenderInitial);
            textSender = itemView.findViewById(R.id.textSender);
            textSubject = itemView.findViewById(R.id.textSubject);
            textPreview = itemView.findViewById(R.id.textPreview);
            textTime = itemView.findViewById(R.id.textTime);
            imgStar = itemView.findViewById(R.id.imgStar);
        }
    }
}