package com.example.gametracker;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class calendar_fragment extends Fragment {
    private static final String TAG = "calendar_fragment";

    private CalendarView mCalendarView;

    GameAdapter adapter;
    RecyclerView recyclerView;
    String date;

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar_fragment, container, false);

        recyclerView = view.findViewById(R.id.rvcalendar);
        mCalendarView = view.findViewById(R.id.calendarView);

        //odbieranie daty i zmiana obecnie wyswietlanej
        if(getArguments()!=null){
            date = getArguments().getString("data");
            Log.d(TAG, "onSelectedDayChar: dd/mm/yyyy: "+date);
            try {
                mCalendarView.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(date).getTime(), true, true);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            date = String.valueOf(currentDate.format(formatter));
            Log.d(TAG, "onSelectedDay: dd/mm/yyyy: "+date);

        }

        //kalendarz i obluga
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                date = day+"/"+(month+1)+"/"+year;
                Fragment fr = new calendar_fragment();
                Bundle bundle = new Bundle();
                bundle.putString("data",date);
                fr.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, fr).commit();
            }
        });
        //wyswietlanie gier
        Date data;
        Timestamp timestamp;
        Timestamp endTimestamp;
            try {
                data = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                timestamp = new Timestamp(data);
                endTimestamp = new Timestamp(timestamp.getSeconds() + TimeUnit.DAYS.toSeconds(1),0);
            } catch (ParseException e) {
                timestamp = Timestamp.now();
                endTimestamp = new Timestamp(timestamp.getSeconds() + TimeUnit.DAYS.toSeconds(1),0);
                e.printStackTrace();
            }
        Log.d(TAG, "Startowy time: "+timestamp);
        Log.d(TAG, "Koncowy time: "+endTimestamp);
        Query query = FirebaseFirestore.getInstance()
                .collection("gry")
                .whereGreaterThanOrEqualTo("relaseDate", timestamp)
                .whereLessThanOrEqualTo("relaseDate", endTimestamp)
                .whereEqualTo("accepted", true);


        FirestoreRecyclerOptions<Game> options = new FirestoreRecyclerOptions.Builder<Game>()
                .setQuery(query, Game.class)
                .build();



        adapter = new GameAdapter(options, requireActivity().getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));

        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new home_fragment()).commit();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}