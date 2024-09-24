package com.example.th;

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
                // Тут ContentDB = content в окне
            }
        });

        layoutManager = new LinearLayoutManager(this);

        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
    }
    private void initDatabase() {database = DatabaseManager.getInstance(this).getDatabase();}
}