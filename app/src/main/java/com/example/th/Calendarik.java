package com.example.th;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.th.Chat.CalenViewModel;
import com.example.th.Database.CalendarAdapter;
import com.example.th.Database.Callbacks.OnPlanClicked;
import com.example.th.Database.db.DBManager2;
import com.example.th.Database.db.DatabaseManager;
import com.example.th.Database.db.AppCalDB;
import com.example.th.Database.db.Entity.CalendarDB;
import com.example.th.databinding.ActivityCalendarBinding;

import java.util.ArrayList;
import java.util.Calendar;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarOutputStream;

public class Calendarik extends AppCompatActivity {
    private boolean isSelectingSevenDays = false;
    private ArrayList<String> selectedDates = new ArrayList<>();
    private ActivityCalendarBinding binding;
    private CalendarView calendarView;
    private AppCalDB database;
    private CalendarAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private CalenViewModel viewModel;
    private String Chatresponse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initDatabase();



        binding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                if (isSelectingSevenDays){
                    selectSevenConsecutiveDays(year, month, dayOfMonth);
                } else {
                Calendar calendar = Calendar.getInstance();
                System.out.println(year);
                System.out.println(month);
                System.out.println(dayOfMonth);
                calendar.set(year, month, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String selectedDate = sdf.format(calendar.getTime());

                //binding.selectedDateView.setText("Вы выбрали дату: " + selectedDate);
                binding.selectedDateView.setVisibility(android.view.View.VISIBLE);
                binding.Buttplus.setVisibility(android.view.View.VISIBLE);
                binding.Buttclose.setVisibility(android.view.View.VISIBLE);
                binding.recyclerView.setVisibility(android.view.View.VISIBLE);
                binding.calendarView.setVisibility(View.GONE);

                initRecyclerView(selectedDate);
                final EditText input = new EditText(Calendarik.this);

                AlertDialog.Builder builder = new AlertDialog.Builder(Calendarik.this);
                builder.setMessage("")
                        .setTitle("Введите текст")
                        .setView(input)
                        .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String userInput = input.getText().toString();
                                if (!(userInput.isEmpty())){
                                System.out.println(userInput);
                                CalendarDB content = new CalendarDB(selectedDate, userInput);
                                database.getContent1Dao().insert(content);
                                adapter.addNewPlans(content);
                                input.setText("");} else {
                                    Toast.makeText(Calendarik.this, "Введите текст", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                AlertDialog dialog = builder.create();

                binding.Buttplus.setOnClickListener(r -> {
                    dialog.show();
                });


                //Toast.makeText(Calendarik.this, "Дата выбрана: " + selectedDate, Toast.LENGTH_SHORT).show();
            }
        }
    });

        binding.Buttclose.setOnClickListener( v -> {
            binding.selectedDateView.setVisibility(View.GONE);
            binding.Buttplus.setVisibility(View.GONE);
            binding.Buttclose.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.GONE);
            binding.calendarView.setVisibility(View.VISIBLE);
        });

        binding.GetChatResp.setOnClickListener(v -> {
            isSelectingSevenDays = true;
            selectedDates.clear();
            Toast.makeText(Calendarik.this, "Выберите стартовую дату", Toast.LENGTH_SHORT).show();
        });

    }

    private void initRecyclerView(String date) {
        List<CalendarDB> contentDBList1 = database.getContent1Dao().findByDate(date);

        adapter = new CalendarAdapter(contentDBList1, new OnPlanClicked() {
            @Override
            public void onRemoveClicked(CalendarDB calendarDB) {
                database.getContent1Dao().delete(calendarDB);
            }

            @Override
            public void onJustClicked(CalendarDB calendarDB) {
                // Сюда можно вставить уведомлениия...
            }
        });

        layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
    }

    private void selectSevenConsecutiveDays(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);

        selectedDates.clear();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            String date = sdf.format(calendar.getTime());
            selectedDates.add(date);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        binding.calendarView.setVisibility(View.GONE);
        binding.GetChatResp.setVisibility(View.GONE);
        binding.ChatResponse.setVisibility(View.VISIBLE);
        binding.Buttcl.setVisibility(View.VISIBLE);
        binding.ChatResponse.setText("Загрузка...");
        viewModel = new ViewModelProvider(this).get(CalenViewModel.class);

        viewModel.getPromptResponse().observe(this, response -> binding.ChatResponse.setText(response));



        viewModel.fetchDataAndSendPrompt(selectedDates);

        binding.Buttcl.setOnClickListener(v -> {
            binding.ChatResponse.setVisibility(View.GONE);
            binding.Buttcl.setVisibility(View.GONE);
            binding.calendarView.setVisibility(View.VISIBLE);
            binding.GetChatResp.setVisibility(View.VISIBLE);
        });



        //binding.selectedDatesView.setText(datesBuilder.toString());
        //binding.selectedDatesView.setVisibility(View.VISIBLE);
        /*
        StringBuilder datesBuilder = new StringBuilder("Выбранные даты:\n");
        for (String date : selectedDates) {
            datesBuilder.append(date).append("\n");
            System.out.println(date);
        }*/
        isSelectingSevenDays = false;

        Toast.makeText(Calendarik.this, "7 дней выбрано", Toast.LENGTH_SHORT).show();
    }

    private String formatDate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private void initDatabase() {database = DBManager2.getInstance(this).getDatabase();}
}