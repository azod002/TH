package com.example.th;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.example.th.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    private static final String TAG = "Register";
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
    }

    private void initViews() {
        binding.save.setOnClickListener(v -> {
            Pair<String, String> pair = getUserData();
            if (pair == null) return;
            register(pair);
        });

        binding.already.setOnClickListener(v -> {
            Intent a;
            a = new Intent(Register.this, Login.class);
            startActivity(a);
            finish();
        });
    }

    private void register(Pair<String, String> pair) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(pair.first, pair.second)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            startActivity(new Intent(Register.this, MainActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private Pair<String, String> getUserData() {
        String login = binding.login.getText().toString();
        String password = binding.password.getText().toString();
        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Введите текст", Toast.LENGTH_SHORT).show();
            return null;
        }
        return Pair.create(login, password);
    }
}