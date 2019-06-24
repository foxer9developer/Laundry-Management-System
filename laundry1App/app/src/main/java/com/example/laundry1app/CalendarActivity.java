package com.example.laundry1app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CalendarView;


public class CalendarActivity extends AppCompatActivity {

    private static final String tag = "Calendar Activity";
    private CalendarView calendarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calender_layout);
        calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month+1) + "/" + year;
//                Log.d(tag, "onSelectingDayChange :date: " + date);
                Intent intent = new Intent(CalendarActivity.this, CheckoutActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });

    }
}
