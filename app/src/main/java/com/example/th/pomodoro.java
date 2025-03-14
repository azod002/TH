package com.example.th;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class pomodoro extends AppCompatActivity {
    private TextView timerTextView;
    private Button startButton;
    private Spinner methodSpinner;
    private CountDownTimer countDownTimer;
    private boolean isWorkPeriod = true;
    private int cycleCount = 0;

    private final long WORK_TIME_POMO = 25 * 60 * 1000;
    private final long SHORT_BREAK_TIME_POMO = 5 * 60 * 1000;
    private final long LONG_BREAK_TIME_POMO = 30 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        timerTextView = findViewById(R.id.timerTextView);
        startButton = findViewById(R.id.startButton);
        methodSpinner = findViewById(R.id.methodSpinner);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Pomodoro (25/5)", "90/30", "52/17"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        methodSpinner.setAdapter(spinnerAdapter);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long duration = getDurationBasedOnMethod();
                startTimer(duration);
                startButton.setEnabled(false);
            }
        });
    }

    private long getDurationBasedOnMethod() {
        int selectedMethod = methodSpinner.getSelectedItemPosition();
        long duration = 0;
        if (selectedMethod == 0) {
            if (isWorkPeriod) {
                duration = WORK_TIME_POMO;
            } else {
                duration = (cycleCount % 4 == 0 && cycleCount != 0) ? LONG_BREAK_TIME_POMO : SHORT_BREAK_TIME_POMO;
            }
        } else if (selectedMethod == 1) { // 90/30 режим
            long WORK_TIME_90 = 90 * 60 * 1000;
            long BREAK_TIME_90 = 30 * 60 * 1000;
            duration = isWorkPeriod ? WORK_TIME_90 : BREAK_TIME_90;
        } else if (selectedMethod == 2) { // 52/17 режим
            long WORK_TIME_52 = 52 * 60 * 1000;
            long BREAK_TIME_52 = 17 * 60 * 1000;
            duration = isWorkPeriod ? WORK_TIME_52 : BREAK_TIME_52;
        }
        return duration;
    }

    private void startTimer(long duration) {
        countDownTimer = new CountDownTimer(duration, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(formatTime(millisUntilFinished));
            }
            public void onFinish() {
                int selectedMethod = methodSpinner.getSelectedItemPosition();
                if (selectedMethod == 0) {
                    if (isWorkPeriod) {
                        cycleCount++;
                        Toast.makeText(pomodoro.this, "Рабочий интервал завершен! Перерыв начинается.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(pomodoro.this, "Перерыв окончен! Приступайте к работе.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (isWorkPeriod) {
                        Toast.makeText(pomodoro.this, "Рабочий период завершен! Перерыв начинается.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(pomodoro.this, "Перерыв окончен! Приступайте к работе.", Toast.LENGTH_SHORT).show();
                    }
                }
                isWorkPeriod = !isWorkPeriod;
                startButton.setEnabled(true);
                timerTextView.setText("00:00");
            }
        }.start();
    }

    private String formatTime(long millis) {
        int minutes = (int) (millis / 60000);
        int seconds = (int) ((millis % 60000) / 1000);
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
