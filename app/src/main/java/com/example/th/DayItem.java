package com.example.th;

import com.example.th.Database.db.Entity.CalendarDB;
import java.util.List;

public class DayItem {
    private String date;
    private List<CalendarDB> plans;

    public DayItem(String date, List<CalendarDB> plans) {
        this.date = date;
        this.plans = plans;
    }

    public String getDate() {
        return date;
    }

    public List<CalendarDB> getPlans() {
        return plans;
    }

    public void addPlan(CalendarDB plan) {
        plans.add(plan);
    }
}
