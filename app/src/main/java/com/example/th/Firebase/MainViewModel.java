package com.example.th.Firebase;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.th.Database.db.Entity.ContentDB;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainViewModel {
    private static MainViewModel INSTANCE;
    private Context context;
    private String name;
    private DatabaseReference root;
    private MutableLiveData<MainData> liveData = new MutableLiveData<>();

    private MainViewModel(Context context, String name) {
        this.context = context;
        this.name = name;
        // Явно указываем URL вашей базы
        this.root = FirebaseDatabase.getInstance("https://pppr-c439f-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        initListeners();
    }

    private void initListeners() {
        // Слушаем изменения в узле "questions/{name}"
        root.child("questions").child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, ContentDB> contentMap = new HashMap<>();
                String nodeName = "";
                // Обрабатываем специальные ключи, если они присутствуют (например, "name", "id", "uidlist")
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    if ("name".equals(key)) {
                        nodeName = childSnapshot.getValue(String.class);
                    } else if ("id".equals(key) || "uidlist".equals(key)) {
                        // можно обработать по необходимости
                    } else {
                        ContentDB content = childSnapshot.getValue(ContentDB.class);
                        contentMap.put(childSnapshot.getKey(), content);
                    }
                }
                // Формируем контейнер данных с новым классом MainData
                MainData mainData = new MainData(new ArrayList<>(contentMap.values()), nodeName);
                liveData.postValue(mainData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибки при чтении данных (если требуется)
            }
        });
    }

    public void saveContent(ContentDB content) {
        // Здесь используем в качестве ключа идентификатор записи (преобразованный в строку)
        root.child("questions").child(name).child(String.valueOf(content.getId()))
                .setValue(content)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Успешно сохранено", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Ошибка сохранения: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public synchronized static MainViewModel getInstance(Context context, String name) {
        if (INSTANCE == null || !INSTANCE.name.equals(name)) {
            INSTANCE = new MainViewModel(context, name);
        }
        return INSTANCE;
    }

    public LiveData<MainData> getUsersLiveData() {
        return liveData;
    }
}
