package com.example.gametracker;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class recommended_games_fragment extends Fragment {

    private static final String TAG = "recommended_games_fragment";

    private GameAdapter adapter;
    private RecyclerView recyclerView;

    private String[] title;

    private String list;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommended_games_fragment, container, false);

        recyclerView = view.findViewById(R.id.rvRecomendedGames);
        list = getArguments().getString("myList");
        title = list.split(",");

        favlist();

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment fr = new profil_fragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, fr).commit();
            }
        });

        return view;
    }
    public void favlist(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("gry")
                .whereIn("title", Arrays.asList(title))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Integer> genresCount = new HashMap<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Game game = document.toObject(Game.class);
                            List<String> genres = game.getGenre();
                            for (String genre : genres) {
                                genresCount.merge(genre, 1, Integer::sum);
                            }
                        }
                        List<String> genresList = genresCount.entrySet().stream()
                                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                .limit(3)
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toList());
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String email = currentUser.getEmail();
                            db.collection("users").document(email).update("favList",genresList);
                        }
                        Log.d(TAG, "getting documents: "+genresList);
                        db.collection("gry")
                                .whereArrayContainsAny("genre", genresList)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        List<String> rekomendowane = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : task1.getResult()) {
                                            Game game = document.toObject(Game.class);
                                            rekomendowane.add(game.getTitle());
                                        }
                                        rekomendowane.removeAll(Arrays.asList(title));
                                        recommended(rekomendowane);
                                    }
                                });
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void recommended(List<String> rekomendowane){
        if (!(rekomendowane.isEmpty())) {
            Query query = FirebaseFirestore.getInstance()
                    .collection("gry")
                    .limit(5).whereIn("title", rekomendowane);

            FirestoreRecyclerOptions<Game> options = new FirestoreRecyclerOptions.Builder<Game>()
                    .setQuery(query, Game.class)
                    .build();

            adapter = new GameAdapter(options, getActivity().getApplicationContext());


            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            recyclerView.setAdapter(adapter);

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
            itemTouchHelper.attachToRecyclerView(recyclerView);

            adapter.startListening();
        }
    }

    public void onPause() {
        super.onPause();
        if(adapter!=null){
            adapter.stopListening();
        }
        if(adapter!=null) {
            adapter.stopListening();
        }
    }
}