package com.example.th;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.th.Database.CalendarAdapter;
import com.example.th.Database.Callbacks.OnPlanClicked;
import com.example.th.Database.db.DBManager2;
import com.example.th.Database.db.DatabaseManager;
import com.example.th.Database.db.AppCalDB;
import com.example.th.Database.db.Entity.CalendarDB;
import com.example.th.databinding.ActivityCalendarBinding;
import java.util.Calendar;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Calendarik extends AppCompatActivity {
    private ActivityCalendarBinding binding;
    private CalendarView calendarView;
    private AppCalDB database;
    private CalendarAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initDatabase();



        binding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
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
                                System.out.println(userInput);
                                CalendarDB content = new CalendarDB(selectedDate, userInput);
                                database.getContent1Dao().insert(content);
                                adapter.addNewPlans(content);
                                input.setText("");
                            }
                        });
                AlertDialog dialog = builder.create();

                binding.Buttplus.setOnClickListener(r -> {
                    dialog.show();
                });


                //Toast.makeText(Calendarik.this, "Дата выбрана: " + selectedDate, Toast.LENGTH_SHORT).show();
            }
        });

        binding.Buttclose.setOnClickListener( v -> {
            binding.selectedDateView.setVisibility(View.GONE);
            binding.Buttplus.setVisibility(View.GONE);
            binding.Buttclose.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.GONE);
            binding.calendarView.setVisibility(View.VISIBLE);
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

            }
        });

        layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
    }

    private void initDatabase() {database = DBManager2.getInstance(this).getDatabase();}
}