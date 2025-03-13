package com.example.th;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.th.Chat.CalenViewModel;
import com.example.th.Database.db.AppCalDB;
import com.example.th.Database.db.DBManager2;
import com.example.th.Database.db.Entity.CalendarDB;
import com.example.th.DayItem;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Calendarik extends AppCompatActivity {
    private RecyclerView recyclerViewCalendar;
    private CalendarMonthAdapter adapter;
    private List<DayItem> dayItems;
    private AppCalDB database;
    private CalenViewModel viewModel;
    private boolean isSelectingSevenDays = false;
    private Calendar currentMonthCalendar;

    // Элементы UI для чат-ответа и навигации
    private View chatContainer;
    private android.widget.Button btnGetChatResp, btnCloseChat, btnPrevMonth, btnNextMonth, btnback;
    private android.widget.TextView tvChatResponse, tvCurrentMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        database = DBManager2.getInstance(this).getDatabase();
        recyclerViewCalendar = findViewById(R.id.recyclerViewCalendar);
        chatContainer = findViewById(R.id.chatContainer);
        btnGetChatResp = findViewById(R.id.btnGetChatResp);
        btnCloseChat = findViewById(R.id.btnCloseChat);
        tvChatResponse = findViewById(R.id.tvChatResponse);
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        btnback = findViewById(R.id.back);


        recyclerViewCalendar.setLayoutManager(new GridLayoutManager(this, 7));
        currentMonthCalendar = Calendar.getInstance();
        updateMonthTitle();

        loadMonthDays();
        adapter = new CalendarMonthAdapter(dayItems, this,
                dayItem -> showDayDetailsDialog(dayItem),
                startDay -> selectSevenConsecutiveDays(startDay));
        recyclerViewCalendar.setAdapter(adapter);

        btnback.setOnClickListener(v ->{
            startActivity(new Intent(Calendarik.this, MainActivity.class));
        });
        btnGetChatResp.setOnClickListener(v -> {
            isSelectingSevenDays = true;
            adapter.setWeekSelectionMode(true);
            Toast.makeText(Calendarik.this, "Выберите стартовую дату для оптимизации недели", Toast.LENGTH_SHORT).show();
        });

        btnCloseChat.setOnClickListener(v -> {
            tvChatResponse.setVisibility(View.GONE);
            btnCloseChat.setVisibility(View.GONE);
            btnGetChatResp.setVisibility(View.VISIBLE);
        });

        viewModel = new ViewModelProvider(this).get(CalenViewModel.class);
        viewModel.getPromptResponse().observe(this, response -> {
            tvChatResponse.setText(response);
            tvChatResponse.setVisibility(View.VISIBLE);
            btnCloseChat.setVisibility(View.VISIBLE);
            btnGetChatResp.setVisibility(View.GONE);
        });

        recyclerViewCalendar.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeRight() {
                currentMonthCalendar.add(Calendar.MONTH, -1);
                updateMonthTitle();
                loadMonthDays();
                adapter.setDayItems(dayItems);
            }
            @Override
            public void onSwipeLeft() {
                // Переключаем календарь на следующий месяц
                currentMonthCalendar.add(Calendar.MONTH, 1);
                updateMonthTitle();
                loadMonthDays();
                adapter.setDayItems(dayItems);
            }
        });

    }

    private void updateMonthTitle() {
        String[] months = {
                "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
        };

        DateFormatSymbols symbols = new DateFormatSymbols(new Locale("ru"));
        symbols.setMonths(months);

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM", new Locale("ru"));
        sdf.setDateFormatSymbols(symbols);
        tvCurrentMonth.setText(sdf.format(currentMonthCalendar.getTime()));
    }

    private void loadMonthDays() {
        List<DayItem> updatedDayItems = new ArrayList<>();
        Calendar calendar = (Calendar) currentMonthCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int emptyCells = firstDayOfWeek - Calendar.MONDAY;
        if (emptyCells < 0) {
            emptyCells = 6;
        }
        for (int i = 0; i < emptyCells; i++) {
            updatedDayItems.add(new DayItem("", new ArrayList<>()));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        for (int day = 1; day <= maxDay; day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            String dateStr = sdf.format(calendar.getTime());
            List<CalendarDB> tasks = database.getContent1Dao().findByDate(dateStr);
            List<String> plans = new ArrayList<>();
            for (CalendarDB task : tasks) {
                plans.add(task.getPlans());
            }
            updatedDayItems.add(new DayItem(dateStr, plans));
        }
        dayItems = updatedDayItems;
        if (adapter != null) {
            adapter.setDayItems(updatedDayItems);
        }
    }

    private void showDayDetailsDialog(DayItem dayItem) {
        if (dayItem.getDate().isEmpty()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_day_details, null);
        builder.setView(dialogView);

        final android.widget.TextView tvDialogDate = dialogView.findViewById(R.id.tvDialogDate);
        final LinearLayout llDialogPlansContainer = dialogView.findViewById(R.id.llDialogPlansContainer);
        final android.widget.Button btnDialogAddPlan = dialogView.findViewById(R.id.btnDialogAddPlan);

        tvDialogDate.setText(dayItem.getDate());
        refreshDialogPlans(llDialogPlansContainer, dayItem);

        btnDialogAddPlan.setOnClickListener(v -> {
            final EditText input = new EditText(this);
            new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                    .setTitle("Добавить план для " + dayItem.getDate())
                    .setView(input)
                    .setPositiveButton("Ок", (dialog, which) -> {
                        String planText = input.getText().toString().trim();
                        if (!planText.isEmpty()) {
                            CalendarDB newTask = new CalendarDB(dayItem.getDate(), planText);
                            database.getContent1Dao().insert(newTask);
                            // Обновляем данные для выбранного дня
                            List<CalendarDB> tasks = database.getContent1Dao().findByDate(dayItem.getDate());
                            dayItem.getPlans().clear();
                            for (CalendarDB task : tasks) {
                                dayItem.addPlan(task.getPlans());
                            }
                            refreshDialogPlans(llDialogPlansContainer, dayItem);
                            loadMonthDays();
                            adapter.setDayItems(dayItems);
                        } else {
                            Toast.makeText(Calendarik.this, "Введите текст", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });

        builder.setNegativeButton("Закрыть", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void refreshDialogPlans(LinearLayout container, DayItem dayItem) {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        List<String> plans = dayItem.getPlans();
        for (int i = 0; i < plans.size(); i++) {
            View planView = inflater.inflate(R.layout.item_plan, container, false);
            android.widget.TextView tvPlanText = planView.findViewById(R.id.tvPlanText);
            android.widget.ImageButton btnDeletePlan = planView.findViewById(R.id.btnDeletePlan);
            tvPlanText.setText(plans.get(i));
            final int planIndex = i;
            btnDeletePlan.setOnClickListener(v -> {
                List<CalendarDB> tasks = database.getContent1Dao().findByDate(dayItem.getDate());
                if (planIndex < tasks.size()) {
                    CalendarDB taskToDelete = tasks.get(planIndex);
                    database.getContent1Dao().delete(taskToDelete);
                    List<CalendarDB> newTasks = database.getContent1Dao().findByDate(dayItem.getDate());
                    dayItem.getPlans().clear();
                    for (CalendarDB task : newTasks) {
                        dayItem.addPlan(task.getPlans());
                    }
                    refreshDialogPlans(container, dayItem);
                    loadMonthDays();
                    adapter.setDayItems(dayItems);
                }
            });
            container.addView(planView);
        }
    }

    private void selectSevenConsecutiveDays(DayItem startDay) {
        if (startDay.getDate().isEmpty()) return;
        ArrayList<String> selectedDates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(startDay.getDate()));
            for (int i = 0; i < 7; i++) {
                selectedDates.add(sdf.format(calendar.getTime()));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            tvChatResponse.setText("Загрузка...");
            tvChatResponse.setVisibility(View.VISIBLE);
            btnCloseChat.setVisibility(View.VISIBLE);
            btnGetChatResp.setVisibility(View.GONE);
            isSelectingSevenDays = false;
            adapter.setWeekSelectionMode(false);
            viewModel.fetchDataAndSendPrompt(selectedDates);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка обработки даты", Toast.LENGTH_SHORT).show();
        }
    }
}
