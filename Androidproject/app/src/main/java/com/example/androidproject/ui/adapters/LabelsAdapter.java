package com.example.androidproject.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;
import com.example.androidproject.data.models.Label;

import java.util.List;

public class LabelsAdapter extends RecyclerView.Adapter<LabelsAdapter.LabelViewHolder> {
    private List<Label> labels;
    private OnLabelClickListener onLabelClickListener;

    public LabelsAdapter(List<Label> labels, OnLabelClickListener listener) {
        this.labels = labels;
        this.onLabelClickListener = listener;
    }

    public interface OnLabelClickListener {
        void onLabelClick(Label label);
    }

    public static class LabelViewHolder extends RecyclerView.ViewHolder {
        TextView LabelName;
        ImageView LabelIcon;

        public LabelViewHolder(View itemView) {
            super(itemView);
            LabelName = itemView.findViewById(R.id.label_name);
            LabelIcon = itemView.findViewById(R.id.label_icon);
        }

        void bind(Label label, OnLabelClickListener listener) {
            LabelName.setText(label.getName());
            LabelIcon.setImageResource();
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLabelClick(label);
                }
            });
        }
    }

    @Override
    public LabelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.label_item, parent, false);
        return new LabelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LabelViewHolder holder, int position) {
        holder.bind(labels.get(position), onLabelClickListener);
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }
}
