package com.example.th;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.content.pm.PackageManager;
import android.Manifest;
import android.app.AlarmManager;
import android.provider.Settings;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import com.example.th.Database.db.AppDatabase;
import com.example.th.Database.db.DatabaseManager;
import com.example.th.Database.db.Entity.ContentDB;
import com.example.th.Firebase.MainData;
import com.example.th.Firebase.MainViewModel;
import com.example.th.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppDatabase database;
    private MainViewModel mainViewModel;
    private RecyclerView.LayoutManager layoutManager;
    private OkHttpClient client = new OkHttpClient();
    private static final int REQUEST_CODE_NOTIFICATION = 1001;

    private String getDate(){
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM");
        return sdf.format(c.getTime());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Если пользователь не залогинен, перенаправляем на регистрацию
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, Register.class));
            finish();
            return;
        }

        // Запрос разрешения для уведомлений (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_NOTIFICATION
                );
            }
        }

        // Проверка и запрос разрешения для точных будильников (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                // Показываем Toast-сообщение о необходимости включения разрешения
                Toast.makeText(this, "Необходимо включить разрешение для точных будильников", Toast.LENGTH_LONG).show();
                // Перенаправляем пользователя в настройки для включения разрешения
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }

        // Инициализируем локальную базу данных
        initDatabase();

        // Создаём MainViewModel с ключом "questions" и подписываемся на изменения данных
        mainViewModel = MainViewModel.getInstance(this, "questions");
        mainViewModel.getUsersLiveData().observe(this, mainData -> {
            if (mainData != null && mainData.getContentList() != null) {
                // Вставляем полученные данные в локальную БД
                for (ContentDB content : mainData.getContentList()) {
                    database.getContentDao().insert(content);
                }
            }
        });

        binding.title.setText(getDate());
        fetchQuote();
        initViews(binding.drawerLayout);
    }

    private void initViews(DrawerLayout drawerLayout) {
        binding.listButton.setOnClickListener(v -> toggleDrawer(drawerLayout, binding.leftDrawer));

        binding.quoteTextView.setOnClickListener(v -> fetchQuote());

        binding.history.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, History.class)));

        binding.calendar.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Calendarik.class));
            drawerLayout.closeDrawer(binding.leftDrawer);
        });

        binding.profileButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Profile.class));
            drawerLayout.closeDrawer(binding.leftDrawer);
        });

        binding.timeOptimizer.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, pomodoro.class));
            drawerLayout.closeDrawer(binding.leftDrawer);
        });

        binding.technicButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RemoteNotify.class));
            drawerLayout.closeDrawer(binding.leftDrawer);
        });

        binding.save.setOnClickListener(v -> {
            String userInput = binding.input.getText().toString();
            if (!userInput.isEmpty()){
                ContentDB content = new ContentDB(userInput);
                // Сохранение в локальную базу
                database.getContentDao().insert(content);
                // Сохранение в Firebase
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseManager firebaseManager = FirebaseManager.getInstance(MainActivity.this, uid);
                firebaseManager.saveNote(content);
                MyTextWidget.updateAllWidgets(this);
                Toast.makeText(this, "Успех?", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Введите текст", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleDrawer(DrawerLayout drawerLayout, android.view.View drawerView) {
        if (drawerLayout.isDrawerOpen(drawerView)) {
            drawerLayout.closeDrawer(drawerView);
        } else {
            drawerLayout.openDrawer(drawerView);
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(binding.leftDrawer)) {
            binding.drawerLayout.closeDrawer(binding.leftDrawer);
        } else {
            super.onBackPressed();
        }
    }

    private void initDatabase() {
        database = DatabaseManager.getInstance(this).getDatabase();
    }

    private void fetchQuote() {
        String url = "https://zenquotes.io/api/random";
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> binding.quoteTextView.setText("Ошибка загрузки цитаты."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            String englishQuote = parseQuote(responseData);
                            translateQuote(englishQuote);
                        } catch (JSONException e) {
                            binding.quoteTextView.setText("Ошибка обработки данных.");
                        }
                    });
                } else {
                    runOnUiThread(() -> binding.quoteTextView.setText("Ошибка на сервере."));
                }
            }
        });
    }

    private String parseQuote(String jsonData) throws JSONException {
        JSONArray jsonArray = new JSONArray(jsonData);
        JSONObject firstQuote = jsonArray.getJSONObject(0);
        String quote = firstQuote.getString("q");
        String author = firstQuote.getString("a");
        return "\"" + quote + "\"\n\n- " + author;
    }

    private void translateQuote(String englishQuote) {
        String translateUrl = "https://translate.google.com/m?hl=ru&sl=en&tl=ru&ie=UTF-8&prev=_m&q=" + englishQuote;
        Request request = new Request.Builder().url(translateUrl).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> binding.quoteTextView.setText("Ошибка перевода цитаты."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String htmlResponse = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            String translatedText = parseTranslatedText(htmlResponse);
                            binding.quoteTextView.setText(translatedText);
                        } catch (Exception e) {
                            binding.quoteTextView.setText("Ошибка обработки перевода.");
                        }
                    });
                } else {
                    runOnUiThread(() -> binding.quoteTextView.setText("Ошибка на сервере перевода."));
                }
            }
        });
    }

    private String parseTranslatedText(String htmlResponse) throws Exception {
        Document doc = Jsoup.parse(htmlResponse);
        Element translatedElement = doc.selectFirst("div.result-container");
        if (translatedElement != null) {
            return translatedElement.text();
        } else {
            throw new Exception("Переведенный текст не найден");
        }
    }
}
