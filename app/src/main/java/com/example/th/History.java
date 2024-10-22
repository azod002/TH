package com.example.th;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.th.Database.Callbacks.OnContentClicked;
import com.example.th.Database.ContentAdapter;
import com.example.th.Database.db.AppDatabase;
import com.example.th.Database.db.DatabaseManager;
import com.example.th.Database.db.Entity.ContentDB;
import com.example.th.databinding.ActivityHistoryBinding;

import java.util.Calendar;
import java.util.List;

public class History extends AppCompatActivity {
    private ActivityHistoryBinding binding;
    private ContentAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private AppDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        initDatabase();
        initRecyclerView();


    }

    private void initRecyclerView() {
        List<ContentDB> contentDBList = database.getContentDao().findAll();
        adapter = new ContentAdapter(contentDBList, new OnContentClicked() {
            @Override
            public void onRemoveClicked(ContentDB contentDB) {
                database.getContentDao().delete(contentDB);
            }

            @Override
            public void onJustClicked(ContentDB contentDB) {
                System.out.println(contentDB);
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(History.this,
                        (view, year, month, dayOfMonth) -> {
                            TimePickerDialog timePickerDialog = new TimePickerDialog(History.this,
                                    (timeView, hourOfDay, minute) -> {
                                        calendar.set(Calendar.YEAR, year);
                                        calendar.set(Calendar.MONTH, month);
                                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar.set(Calendar.MINUTE, minute);
                                        calendar.set(Calendar.SECOND, 0);
                                        calendar.set(Calendar.MILLISECOND, 0);

                                        long selectedTime = calendar.getTimeInMillis();
                                        long currentTime = System.currentTimeMillis();

                                        if (selectedTime < currentTime) {
                                            Toast.makeText(History.this, "Выберите время в будущем", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        int notificationId = (int) System.currentTimeMillis();

                                        scheduleNotification(History.this, selectedTime, contentDB.getContent(), notificationId);
                                        Toast.makeText(History.this, "Уведомление запланировано", Toast.LENGTH_SHORT).show();
                                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                            timePickerDialog.show();
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        layoutManager = new LinearLayoutManager(this);

        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
    }

    private void scheduleNotification(Context context, long timeInMillis, String content, int notificationId) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("content", content);
        intent.putExtra("notificationId", notificationId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }
        }
    }




    private void initDatabase() {database = DatabaseManager.getInstance(this).getDatabase();}
}