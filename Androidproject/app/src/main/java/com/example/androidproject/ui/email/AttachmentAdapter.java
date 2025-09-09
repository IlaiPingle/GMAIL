//package com.example.androidproject.ui.email;
//
//import android.net.Uri;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.androidproject.R;
//import java.util.List;
//
///**
// * Adapter for displaying email attachments in a RecyclerView.
// * - Shows image previews for image attachments
// * - Shows generic file icon for non-image attachments
// * - Allows removal of attachments via a callback interface
// */
//class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.VH> {
//
//    interface OnRemove {
//        void remove(int position);
//    }
//
//    interface GetName {
//        String of(Uri uri);
//    }
//
//    interface IsImage {
//        boolean test(Uri uri);
//    }
//
//    private final List<Uri> data;
//    private final OnRemove onRemove;
//    private final GetName getName;
//    private final IsImage isImage;
//
//    // Constructor
//    AttachmentAdapter(List<Uri> data, OnRemove onRemove, GetName getName, IsImage isImage) {
//        this.data = data;
//        this.onRemove = onRemove;
//        this.getName = getName;
//        this.isImage = isImage;
//    }
//
//    /**
//     * ViewHolder class for attachment items.
//     * Holds references to the preview image, file name, and remove button.
//     * Uses item_attachment.xml layout.
//     */
//    static class VH extends RecyclerView.ViewHolder {
//        ImageView preview, remove;
//        TextView name;
//        VH(@NonNull View itemView) {
//            super(itemView);
////            preview = itemView.findViewById(R.id.imagePreview);
//            remove = itemView.findViewById(R.id.buttonRemove);
//            name = itemView.findViewById(R.id.textFileName);
//        }
//    }
//
//    @NonNull
//    @Override
//    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attachment, parent, false);
//        return new VH(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull VH h, int position) {
//        Uri uri = data.get(position);
//        h.name.setText(getName.of(uri));
//        if (isImage.test(uri)) {
//            h.preview.setImageURI(uri);
//        } else {
////            h.preview.setImageResource(R.drawable.ic_file);
//        }
//        h.remove.setOnClickListener(v -> onRemove.remove(h.getBindingAdapterPosition()));
//    }
//
//    @Override
//    public int getItemCount() {
//        return data.size();
//    }
////