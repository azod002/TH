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

public class MainActivity extends AppCompatActivity {

    // Объект View Binding
    private ActivityMainBinding binding;
    //private ContentAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private AppDatabase database;

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
}
