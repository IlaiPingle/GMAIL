package com.example.MyGmail.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MyGmail.R;
import com.example.MyGmail.data.models.Label;

public class DrawerAdapter extends ListAdapter<DrawerItem, RecyclerView.ViewHolder> {
    private static final int VT_HEADER = 0;
    private static final int VT_SECTION = 1;
    private static final int VT_LABEL = 2;
    private static final int VT_ACTION = 3;

    public interface OnItemClickListener {
        void onLabelClick(DrawerItem.LabelItem labelItem);
        void onCreateLabelClick();
        void onManageLabelsClick();
    }
    private final OnItemClickListener listener;
    private String selectedLabel;

    public DrawerAdapter(OnItemClickListener listener) {
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
            if (oldItem.getType() != newItem.getType()) return false;
            if (oldItem instanceof DrawerItem.LabelItem && newItem instanceof DrawerItem.LabelItem) {
                Label a = ((DrawerItem.LabelItem) oldItem).label;
                Label b = ((DrawerItem.LabelItem) newItem).label;
                return a != null && b != null && a.getName().equals(b.getName());
            }
            if (oldItem instanceof DrawerItem.ActionItem && newItem instanceof DrawerItem.ActionItem) {
                return ((DrawerItem.ActionItem) oldItem).action == ((DrawerItem.ActionItem) newItem).action;
            }
            return true;
        }

        @Override
        public boolean areContentsTheSame(@NonNull DrawerItem oldItem, @NonNull DrawerItem newItem) {
            if (oldItem instanceof DrawerItem.LabelItem && newItem instanceof DrawerItem.LabelItem) {
                Label a = ((DrawerItem.LabelItem) oldItem).label;
                Label b = ((DrawerItem.LabelItem) newItem).label;
                return a.getName().equals(b.getName());
            }
            if (oldItem instanceof DrawerItem.ActionItem && newItem instanceof DrawerItem.ActionItem) {
                DrawerItem.ActionItem A = (DrawerItem.ActionItem) oldItem;
                DrawerItem.ActionItem B = (DrawerItem.ActionItem) newItem;
                return A.action == B.action && A.title.equals(B.title) && A.iconRes == B.iconRes;
            }
            return true;
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

        void bind(DrawerItem.LabelItem labelItem, boolean selected, OnItemClickListener listener) {
            Label label = labelItem.label;
            LabelName.setText(label != null ? label.getName() : "");
            LabelIcon.setImageResource(getLabelIcon(label.getName()));
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
    static class ActionViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tv;
        ActionViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgAction);
            tv  = itemView.findViewById(R.id.tvAction);
        }
        void bind(DrawerItem.ActionItem ai, OnItemClickListener listener) {
            img.setImageResource(ai.iconRes);
            tv.setText(ai.title);
            itemView.setOnClickListener(v -> {
                if (listener == null) return;
                if (ai.action == DrawerItem.ActionItem.Action.CREATE) listener.onCreateLabelClick();
                else listener.onManageLabelsClick();
            });
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
        if (viewType == VT_ACTION)
            return new ActionViewHolder(inflater.inflate(R.layout.drawer_action_item, parent, false));
        return new LabelViewHolder(inflater.inflate(R.layout.label_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DrawerItem item = getItem(position);
        if (holder instanceof LabelViewHolder && item instanceof DrawerItem.LabelItem) {
            DrawerItem.LabelItem labelItem = (DrawerItem.LabelItem) item;
            boolean selected = labelItem.label != null &&
                    labelItem.label.getName() != null &&
                    labelItem.label.getName().equals(selectedLabel);
                ((LabelViewHolder) holder).bind(labelItem, selected, listener);
        } else if (holder instanceof ActionViewHolder && item instanceof DrawerItem.ActionItem) {
            ((ActionViewHolder) holder).bind((DrawerItem.ActionItem) item, listener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        DrawerItem item = getItem(position);
        if (item.getType() == DrawerItem.Type.HEADER) return VT_HEADER;
        if (item.getType() == DrawerItem.Type.SECTION) return VT_SECTION;
        if (item.getType() == DrawerItem.Type.ACTION) return VT_ACTION;
        return VT_LABEL;
    }
}
