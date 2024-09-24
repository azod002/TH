package com.example.th.Database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.th.Database.Callbacks.OnContentClicked;
import com.example.th.Database.db.Entity.ContentDB;
import com.example.th.R;
import com.example.th.databinding.ContentViewHolderBinding;

import java.util.ArrayList;
import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private List<ContentDB> contentDB = new ArrayList<>();
    private OnContentClicked callback;

    public ContentAdapter(List<ContentDB> contentDB, OnContentClicked callback) {
        this.contentDB = contentDB;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_view_holder, parent, false);
        return new ContentViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        holder.update(contentDB.get(position));
    }

    @Override
    public int getItemCount() {
        return contentDB.size();
    }

    public void addNewContent(ContentDB DB) {
        contentDB.add(DB);
        notifyItemInserted(contentDB.size() - 1);
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {
        private ContentViewHolderBinding binding;
        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ContentViewHolderBinding.bind(itemView);

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentDB content = contentDB.get(getAdapterPosition());
                    callback.onJustClicked(content);
                }
            });
            binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ContentDB content = contentDB.get(getAdapterPosition());
                    contentDB.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    callback.onRemoveClicked(content);
                    return false;
                }
            });

        }

        public void update(ContentDB contentDB) {
            binding.textContent.setText(contentDB.getContent());
        }
    }

}
