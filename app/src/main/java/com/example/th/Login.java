package com.example.th;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.th.Database.db.AppCalDB;
import com.example.th.Database.db.AppDatabase;
import com.example.th.Database.db.DatabaseManager;
import com.example.th.Database.db.DBManager2;
import com.example.th.Database.db.Entity.CalendarDB;
import com.example.th.Database.db.Entity.ContentDB;
import com.example.th.FirebaseManager;
import com.example.th.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private AppCalDB database1;
    private AppDatabase database2;
    private ActivityLoginBinding binding;
    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();
    }

    private void initViews() {
        binding.save.setOnClickListener(v -> {
            String login = binding.login.getText().toString();
            String password = binding.password.getText().toString();
            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Введите текст", Toast.LENGTH_SHORT).show();
                return;
            }
            login(login, password);
        });
        binding.back.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Register.class));
            finish();
        });
    }

    private void login(String login, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(login, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();

                                database2 =  DatabaseManager.getInstance(Login.this).getDatabase();
                                database1 = DBManager2.getInstance(Login.this).getDatabase();

                                FirebaseManager firebaseManager = FirebaseManager.getInstance(Login.this, uid);

                                firebaseManager.fetchNotes(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot noteSnapshot : snapshot.getChildren()) {
                                            ContentDB note = noteSnapshot.getValue(ContentDB.class);
                                            if (note != null) {
                                                database2.getContentDao().insert(note);
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e(TAG, "Ошибка загрузки заметок: " + error.getMessage());
                                    }
                                });

                                firebaseManager.fetchPlans(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot planSnapshot : snapshot.getChildren()) {
                                            CalendarDB plan = planSnapshot.getValue(CalendarDB.class);
                                            if (plan != null) {
                                                database1.getContent1Dao().insert(plan);
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e(TAG, "Ошибка загрузки планов: " + error.getMessage());
                                    }
                                });
                                startActivity(new Intent(Login.this, MainActivity.class));
                                finish();
                            }
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
