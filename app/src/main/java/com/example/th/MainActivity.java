package com.example.th;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.th.Database.ContentAdapter;
import com.example.th.Database.db.AppDatabase;
import com.example.th.Database.db.DatabaseManager;
import com.example.th.Database.db.Entity.ContentDB;
import com.example.th.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    // Объект View Binding
    private ActivityMainBinding binding;
    //private ContentAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private AppDatabase database;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent a;
            a = new Intent(MainActivity.this, Register.class);
            startActivity(a);
            finish();
        }


        final EditText input = new EditText(MainActivity.this);
        initDatabase();
        fetchQuote();
        initViews(binding.drawerLayout);

    }

    private void initViews(DrawerLayout drawerLayout) {

        binding.menub.setOnClickListener(v -> {
            toggleDrawer(drawerLayout, binding.leftDrawer);
        });
        binding.menuButton1.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, History.class));
            drawerLayout.closeDrawer(binding.leftDrawer);
        });
        binding.menuButton2.setOnClickListener(v -> {
            drawerLayout.closeDrawer(binding.leftDrawer);
        });
        binding.menuButton3.setOnClickListener(v -> {
            drawerLayout.closeDrawer(binding.leftDrawer);
        });
        binding.save.setOnClickListener(v -> {
            String userInput = binding.input.getText().toString();
            ContentDB content = new ContentDB(userInput);
            System.out.println(content.getContent());
            database.getContentDao().insert(content);
            Toast.makeText(this, "Успех?", Toast.LENGTH_SHORT).show();
        });
    }

    private void toggleDrawer(DrawerLayout drawerLayout, View drawerView) {
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

    private void initDatabase() {database = DatabaseManager.getInstance(this).getDatabase();}

    private void fetchQuote() {
        String url = "https://zenquotes.io/api/random"; // API для получения случайной цитаты

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
                            // Парсим цитату и передаем на перевод
                            String englishQuote = parseQuote(responseData);
                            translateQuote(englishQuote);  // Переводим цитату на русский
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
        // Парсинг ответа, берем цитату
        JSONArray jsonArray = new JSONArray(jsonData);
        JSONObject firstQuote = jsonArray.getJSONObject(0);
        String quote = firstQuote.getString("q");
        String author = firstQuote.getString("a");

        return "\"" + quote + "\"\n\n- " + author;
    }

    private void translateQuote(String englishQuote) {
        // URL запроса к веб-интерфейсу Google Translate
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
                            // Парсинг HTML ответа с помощью Jsoup
                            String translatedText = parseTranslatedText(htmlResponse);

                            // Отображаем переведённую цитату
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
        // Парсим HTML для извлечения переведённого текста
        Document doc = Jsoup.parse(htmlResponse);
        Element translatedElement = doc.selectFirst("div.result-container");  // Ищем div с переведенным текстом
        if (translatedElement != null) {
            return translatedElement.text();
        } else {
            throw new Exception("Переведенный текст не найден");
        }
    }

}
