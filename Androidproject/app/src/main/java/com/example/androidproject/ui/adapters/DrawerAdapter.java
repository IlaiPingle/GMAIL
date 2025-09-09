package com.example.androidproject.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;
import com.example.androidproject.data.models.Label;

public class DrawerAdapter extends ListAdapter<DrawerItem, RecyclerView.ViewHolder> {
    private static final int VT_HEADER = 0;
    private static final int VT_SECTION = 1;
    private static final int VT_LABEL = 2;

    public final OnLabelClickListener listener;
    private String selectedLabel;

    public DrawerAdapter(OnLabelClickListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    public interface OnLabelClickListener {
        void onLabelClick(DrawerItem.LabelItem labelItem);
    }

    public void setSelectedLabel(String labelName) {
        this.selectedLabel = labelName;
        notifyDataSetChanged();
    }

    private static final DiffUtil.ItemCallback<DrawerItem> DIFF = new DiffUtil.ItemCallback<DrawerItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull DrawerItem oldItem, @NonNull DrawerItem newItem) {
            if (oldItem instanceof DrawerItem.HeaderItem &&
                    newItem instanceof DrawerItem.HeaderItem) {
                return true;
            }
            if (oldItem instanceof DrawerItem.SectionItem &&
                    newItem instanceof DrawerItem.SectionItem) {
                return true;

            }
            if (oldItem instanceof DrawerItem.LabelItem &&
                    newItem instanceof DrawerItem.LabelItem) {
                Label oldLabel = ((DrawerItem.LabelItem) oldItem).label;
                Label newLabel = ((DrawerItem.LabelItem) newItem).label;
                return oldLabel.getLabelName().equals(newLabel.getLabelName());
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull DrawerItem oldItem, @NonNull DrawerItem newItem) {
            if (oldItem instanceof DrawerItem.HeaderItem &&
                    newItem instanceof DrawerItem.HeaderItem) {
                return true;
            }
            if (oldItem instanceof DrawerItem.SectionItem &&
                    newItem instanceof DrawerItem.SectionItem) {
                return true;
            }
            if (oldItem instanceof DrawerItem.LabelItem &&
                    newItem instanceof DrawerItem.LabelItem) {
                Label oldLabel = ((DrawerItem.LabelItem) oldItem).label;
                Label newLabel = ((DrawerItem.LabelItem) newItem).label;
                return oldLabel.getLabelName().equals(newLabel.getLabelName());
            }
            return false;
        }
    };

    public static class StaticViews extends RecyclerView.ViewHolder {
        StaticViews(View itemView) {
            super(itemView);
        }
    }

    public static class LabelViewHolder extends RecyclerView.ViewHolder {
        View root;
        ImageView LabelIcon;
        TextView LabelName;

        LabelViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            LabelIcon = itemView.findViewById(R.id.imgLabel);
            LabelName = itemView.findViewById(R.id.tvLabel);
        }

        void bind(DrawerItem.LabelItem labelItem, boolean selected, OnLabelClickListener listener) {
            Label label = labelItem.label;
            LabelName.setText(label != null ? label.getLabelName() : "");
            int iconRes = getLabelIcon(label.getLabelName());
            LabelIcon.setImageResource(iconRes);
            root.setActivated(selected);
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onLabelClick(labelItem);
            });
        }

        private int getLabelIcon(String labelName) {
            switch (labelName) {
                case "inbox":
                    return R.drawable.ic_inbox;
                case "starred":
                    return R.drawable.ic_star_filled;
                case "sent":
                    return R.drawable.ic_sent;
                case "drafts":
                    return R.drawable.ic_drafts;
                case "spam":
                    return R.drawable.ic_spam;
                case "bin":
                    return R.drawable.ic_bin;
                default:
                    return R.drawable.ic_label;
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VT_HEADER)
            return new StaticViews(inflater.inflate(R.layout.drawer_header, parent, false));
        if (viewType == VT_SECTION)
            return new StaticViews(inflater.inflate(R.layout.labels_drawer_section, parent, false));
        return new LabelViewHolder(inflater.inflate(R.layout.label_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DrawerItem item = getItem(position);
        if (holder instanceof LabelViewHolder && item instanceof DrawerItem.LabelItem) {
            DrawerItem.LabelItem labelItem = (DrawerItem.LabelItem) item;
            boolean selected = labelItem.label != null &&
                    labelItem.label.getLabelName() != null &&
                    labelItem.label.getLabelName().equals(selectedLabel);
            ((LabelViewHolder) holder).bind(labelItem, selected, listener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        DrawerItem item = getItem(position);
        if (item.getType() == DrawerItem.Type.HEADER) return VT_HEADER;
        if (item.getType() == DrawerItem.Type.SECTION) return VT_SECTION;
        return VT_LABEL;
    }
}
