package com.example.th.Database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.th.Database.Callbacks.OnContentClicked;
import com.example.th.Database.Callbacks.OnPlanClicked;
import com.example.th.Database.db.Entity.CalendarDB;
import com.example.th.Database.db.Entity.ContentDB;
import com.example.th.R;
import com.example.th.databinding.ContentViewHolderBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ContentViewHolder> {
    private List<CalendarDB> calendarDB = new ArrayList<>();
    private OnPlanClicked callback;

    public CalendarAdapter(List<CalendarDB> calendarDB, OnPlanClicked callback) {
        this.calendarDB = calendarDB;
        this.callback = callback;
    }

    @NonNull
    @Override
    public CalendarAdapter.ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_view_holder, parent, false);
        return new CalendarAdapter.ContentViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.ContentViewHolder holder, int position) {
        holder.update(calendarDB.get(position));
    }

    @Override
    public int getItemCount() {return calendarDB.size();}

    public void addNewPlans(CalendarDB DB) {
        calendarDB.add(DB);
        notifyItemInserted(calendarDB.size() - 1);
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {
        private ContentViewHolderBinding binding;
        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ContentViewHolderBinding.bind(itemView);

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CalendarDB plan = calendarDB.get(getAdapterPosition());
                    callback.onJustClicked(plan);
                }
            });
            binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    CalendarDB plan = calendarDB.get(getAdapterPosition());
                    calendarDB.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    callback.onRemoveClicked(plan);
                    return false;
                }
            });
        }

        public void update(CalendarDB calendarDB) {
            binding.textContent.setText(calendarDB.getPlans());
        }
    }
}
