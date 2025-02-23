package com.example.th;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.th.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;

public class Profile extends AppCompatActivity {
    private ActivityProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.uid.setText(FirebaseAuth.getInstance().getUid().toString());

        binding.back.setOnClickListener(v ->{
            startActivity(new Intent(Profile.this, MainActivity.class));
        });

        binding.copy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", FirebaseAuth.getInstance().getUid().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Текст скопирован", Toast.LENGTH_SHORT).show();
        });

    }
}