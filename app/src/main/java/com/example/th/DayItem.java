package com.example.th;
import java.util.List;


public class DayItem {
    private String date;
    private List<String> plans;

    public DayItem(String date, List<String> plans) {
        this.date = date;
        this.plans = plans;
    }

    public String getDate() {
        return date;
    }

    public List<String> getPlans() {
        return plans;
    }

    public void addPlan(String plan) {
        plans.add(plan);
    }
}


