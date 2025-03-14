package com.example.th;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.TimePickerDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RemoteNotify extends AppCompatActivity {

    private EditText editTextUID, editTextMessage;
    private Button selectTimeButton, buttonSend;
    private TextView textViewTime;
    private ListView listNotifications;

    private FirebaseFirestore firestore;
    private String currentUserUid;
    private Calendar selectedCalendar = Calendar.getInstance();

    private ArrayList<NotificationModel> notificationsList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ArrayList<String> adapterData = new ArrayList<>();

    private int notificationCounter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_notify);

        editTextUID = findViewById(R.id.editTextUID);
        editTextMessage = findViewById(R.id.editTextMessage);
        selectTimeButton = findViewById(R.id.select_time);
        textViewTime = findViewById(R.id.textViewTime);
        buttonSend = findViewById(R.id.buttonSend);
        listNotifications = findViewById(R.id.listNotifications);

        firestore = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, adapterData);
        listNotifications.setAdapter(adapter);

        selectTimeButton.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);
            TimePickerDialog timePicker = new TimePickerDialog(RemoteNotify.this,
                    (view, selectedHour, selectedMinute) -> {
                        selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        selectedCalendar.set(Calendar.MINUTE, selectedMinute);
                        selectedCalendar.set(Calendar.SECOND, 0);
                        String timeString = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                        textViewTime.setText("Выбранное время: " + timeString);
                    }, hour, minute, true);
            timePicker.show();
        });

        buttonSend.setOnClickListener(v -> {
            String targetUid = editTextUID.getText().toString().trim();
            String message = editTextMessage.getText().toString().trim();
            long scheduledTimeMillis = selectedCalendar.getTimeInMillis();
            long nowMillis = System.currentTimeMillis();

            if (targetUid.isEmpty() || message.isEmpty() || scheduledTimeMillis < nowMillis) {
                Toast.makeText(RemoteNotify.this, "Введите корректные данные и выберите время в будущем", Toast.LENGTH_SHORT).show();
                return;
            }

            NotificationModel notification = new NotificationModel(
                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    targetUid,
                    message,
                    new Timestamp(selectedCalendar.getTime()),
                    Timestamp.now()
            );

            firestore.collection("notifications")
                    .add(notification)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(RemoteNotify.this, "Уведомление запланировано", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(RemoteNotify.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        Timestamp twoDaysAgo = new Timestamp(new Date(System.currentTimeMillis() - 48 * 60 * 60 * 1000L));
        firestore.collection("notifications")
                .whereEqualTo("receiverUid", currentUserUid)
                .whereGreaterThan("createdAt", twoDaysAgo)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((QuerySnapshot snapshots, @Nullable com.google.firebase.firestore.FirebaseFirestoreException e) -> {
                    if (e != null) {
                        return;
                    }
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        NotificationModel model = dc.getDocument().toObject(NotificationModel.class);
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            long scheduledMillis = model.getScheduledTime().toDate().getTime();
                            if (scheduledMillis > System.currentTimeMillis()) {
                                scheduleLocalNotification(scheduledMillis, model.getMessage(), notificationCounter++);
                            } else {
                                scheduleLocalNotification(System.currentTimeMillis() + 1000, model.getMessage(), notificationCounter++);
                            }
                            notificationsList.add(model);
                            adapterData.add("От: " + model.getSenderUid() +
                                    "\nСообщение: " + model.getMessage() +
                                    "\nВремя: " + model.getScheduledTime().toDate().toString());
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void scheduleLocalNotification(long timeInMillis, String content, int notificationId) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("content", content);
        intent.putExtra("notificationId", notificationId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
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
}
