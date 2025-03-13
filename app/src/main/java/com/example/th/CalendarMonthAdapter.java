package com.example.th;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.th.DayItem;
import java.util.List;

public class CalendarMonthAdapter extends RecyclerView.Adapter<CalendarMonthAdapter.DayViewHolder> {

    private List<DayItem> dayItems;
    private OnDayClickListener dayClickListener;
    private OnWeekSelectListener weekSelectListener;
    private boolean weekSelectionMode = false;
    private Context context;

    public interface OnDayClickListener {
        void onDayClicked(DayItem dayItem);
    }

    public interface OnWeekSelectListener {
        void onWeekSelected(DayItem startDay);
    }

    public CalendarMonthAdapter(List<DayItem> dayItems, Context context,
                                OnDayClickListener dayClickListener,
                                OnWeekSelectListener weekSelectListener) {
        this.dayItems = dayItems;
        this.context = context;
        this.dayClickListener = dayClickListener;
        this.weekSelectListener = weekSelectListener;
    }

    public void setDayItems(List<DayItem> newItems) {
        this.dayItems = newItems;
        notifyDataSetChanged();
    }

    public void setWeekSelectionMode(boolean mode) {
        this.weekSelectionMode = mode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        DayItem dayItem = dayItems.get(position);
        holder.bind(dayItem);
    }

    @Override
    public int getItemCount() {
        return dayItems.size();
    }

    class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvSummary1, tvSummary2, tvSummary3;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvSummary1 = itemView.findViewById(R.id.tvSummary1);
            tvSummary2 = itemView.findViewById(R.id.tvSummary2);
            tvSummary3 = itemView.findViewById(R.id.tvSummary3);

            itemView.setOnClickListener(v -> {
                DayItem dayItem = dayItems.get(getAdapterPosition());
                if (dayItem.getDate().isEmpty()) return;
                if (weekSelectionMode) {
                    if (weekSelectListener != null) {
                        weekSelectListener.onWeekSelected(dayItem);
                    }
                } else {
                    if (dayClickListener != null) {
                        dayClickListener.onDayClicked(dayItem);
                    }
                }
            });
        }

        public void bind(DayItem dayItem) {
            if (dayItem.getDate().isEmpty()) {
                tvDate.setText("");
                tvSummary1.setText("");
                tvSummary2.setText("");
                tvSummary3.setText("");
                return;
            }
            String[] parts = dayItem.getDate().split("\\.");
            String day = parts[0];
            tvDate.setText(day);

            List<String> plans = dayItem.getPlans();
            tvSummary1.setText(plans.size() > 0 ? plans.get(0) : "");
            tvSummary2.setText(plans.size() > 1 ? plans.get(1) : "");
            tvSummary3.setText(plans.size() > 2 ? plans.get(2) : "");
        }
    }
}
