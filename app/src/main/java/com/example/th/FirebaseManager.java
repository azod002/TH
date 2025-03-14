package com.example.th;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.th.Database.db.Entity.ContentDB;
import com.example.th.Database.db.Entity.CalendarDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseManager {
    private static FirebaseManager INSTANCE;
    private Context context;
    private String uid;
    private DatabaseReference root;

    private FirebaseManager(Context context, String uid) {
        this.context = context;
        this.uid = uid;
        root = FirebaseDatabase.getInstance("https://thv01-5dfba-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
    }

    public static synchronized FirebaseManager getInstance(Context context, String uid) {
        if (INSTANCE == null || !INSTANCE.uid.equals(uid)) {
            INSTANCE = new FirebaseManager(context, uid);
        }
        return INSTANCE;
    }

    public void saveNote(ContentDB note) {
        root.child("users").child(uid).child("notes").child(note.getContent())
                .setValue(note)
                .addOnSuccessListener(unused ->
                        Toast.makeText(context, "Заметка сохранена в Firebase", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Ошибка сохранения заметки: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void deleteNote(ContentDB note) {
        root.child("users").child(uid).child("notes").child(note.getContent())
                .removeValue()
                .addOnSuccessListener(unused ->
                        Toast.makeText(context, "Заметка удалена из Firebase", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Ошибка удаления заметки: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    public void fetchNotes(ValueEventListener listener) {
        root.child("users").child(uid).child("notes").addListenerForSingleValueEvent(listener);
    }

    public void savePlan(CalendarDB plan) {
        root.child("users").child(uid).child("plans").child(plan.getPlans())
                .setValue(plan)
                .addOnSuccessListener(unused -> Toast.makeText(context, "План сохранён в Firebase", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Ошибка сохранения плана: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void deletePlan(CalendarDB plan) {
        root.child("users").child(uid).child("plans").child(plan.getPlans())
                .removeValue()
                .addOnSuccessListener(unused -> Toast.makeText(context, "План удалён из Firebase", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Ошибка удаления плана: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void fetchPlans(ValueEventListener listener) {
        root.child("users").child(uid).child("plans").addListenerForSingleValueEvent(listener);
    }
}
