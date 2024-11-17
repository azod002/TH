package com.example.th.Database.Callbacks;

import com.example.th.Database.db.Entity.CalendarDB;
import com.example.th.Database.db.Entity.ContentDB;

public interface OnPlanClicked {
    public void onRemoveClicked(CalendarDB calendarDB);

    public void onJustClicked(CalendarDB calendarDB);
}
