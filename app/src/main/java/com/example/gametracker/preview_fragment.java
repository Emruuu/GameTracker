package com.example.gametracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class preview_fragment extends Fragment {

    private TextView tvtitle;
    private TextView tvdesc;
    private TextView tvproducer;
    private TextView tvpublisher;
    private TextView tvdate;
    private TextView tvgenre;
    private TextView tvmode;
    private TextView tvplatform;
    private ImageButton back;
    private String title,desc,producer,publisher,date,mode,platform,trailer;
    List<String> genre;

    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preview_fragment, container, false);

        tvtitle = view.findViewById(R.id.tvTitlePrev);
        tvdesc = view.findViewById(R.id.tvDescPrev);
        tvproducer = view.findViewById(R.id.tvProducerPrev);
        tvpublisher = view.findViewById(R.id.tvPublisherPrev);
        tvdate = view.findViewById(R.id.tvDatePrev);
        tvgenre = view.findViewById(R.id.tvGenrePrev);
        tvmode = view.findViewById(R.id.tvModePrev);
        tvplatform = view.findViewById(R.id.tvPlatformPrev);

        // Odbieranie danych
        getParentFragmentManager().setFragmentResultListener("check", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                title = result.getString("title");
                desc = result.getString("desc");
                producer = result.getString("producer");
                publisher = result.getString("publisher");
                date = result.getString("date");

                genre = Arrays.asList(result.getString("genre").split(" "));
                mode = result.getString("mode");
                platform = result.getString("platform");
                trailer = result.getString("trailer");
                tvtitle.setText(title);
                tvdesc.setText(desc);
                tvproducer.setText(producer);
                tvpublisher.setText(publisher);
                tvdate.setText(date);
                tvgenre.setText(String.join(" ", genre));
                tvmode.setText(mode);
                tvplatform.setText(platform);
            }
        });// Koniec odbierania danych

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AppCompatActivity activity=(AppCompatActivity)view.getContext();
                Bundle dane = new Bundle();
                dane.putString("title", title);
                dane.putString("desc", desc);
                dane.putString("producer", producer);
                dane.putString("publisher", publisher);
                dane.putString("date", date);
                dane.putString("mode", mode);
                dane.putString("genre", String.join(" ", genre));
                dane.putString("platform", platform);
                dane.putString("trailer", trailer);
                activity.getSupportFragmentManager().setFragmentResult("add", dane);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new addedit_game_fragment()).commit();
            }
        });

        return view;
    }
}