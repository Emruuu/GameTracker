package com.example.gametracker;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

public class friends_fragment extends Fragment {
    private static final String TAG = "friends_fragment";

    private EditText etszukaj;
    private ImageButton ibszukaj, ibfaq;
    RecyclerView recyclerViewfriend;
    RecyclerView recyclerViewRecommend;

    private UserAdapter Recommendedadapter,adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends_fragment, container, false);

        recyclerViewfriend = view.findViewById(R.id.rvSearchFriend);
        recyclerViewRecommend = view.findViewById(R.id.rvRecommendFriend);

        etszukaj = view.findViewById(R.id.etSearchFriendSearch);
        ibszukaj = view.findViewById(R.id.ibSearchFriendSearch);
        ibszukaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchButtonClick();
            }
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            DocumentReference usersRef = db.collection("users").document(email);
            db.collection("users")
                    .document(email)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            favlist(user.getMyList(),email);
                            List<String> FavList = user.getFavList();
                            List<String> friendslist = new ArrayList<>(Arrays.asList(user.getFriendList().split(";")));
                            friendslist.add(user.getEmail());
                            db.collection("users")
                                    .whereArrayContainsAny("favList", FavList)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            List<String> rekomendowani = new ArrayList<>();
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                User user1 = document.toObject(User.class);
                                                List<String> userfav = user1.getFavList();
                                                List<String> common = new ArrayList<String>(FavList);
                                                common.retainAll(userfav);
                                                int count = common.size();
                                                int ilosc = FavList.size()+userfav.size();;
                                                double ratio = (double) count * 2 / ilosc;
                                                if (ratio > 0.5) {
                                                    rekomendowani.add(user1.getEmail());
                                                }
                                            }
                                            rekomendowani.removeAll(friendslist);
                                            recommended(rekomendowani);
                                        }
                                    });
                        }


                    });
        }


        String list = getArguments().getString("friendList");

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment fr = new friends_mylist_fragment();
                Bundle b = new Bundle();
                b.putString("friendList",list);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, fr).commit();
                fr.setArguments(b);
            }
        });

        return view;
    }
    public void onSearchButtonClick() {
        Query query = FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("nick", etszukaj.getText().toString().trim());

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new UserAdapter(options, getActivity().getApplicationContext());

        recyclerViewfriend.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerViewfriend.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToAddFriend(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerViewfriend);

        adapter.startListening();
    }
    public void favlist(String list, String email){
        String[] title;
        title = list.split(",");
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
                        db.collection("users").document(email).update("favList",genresList);
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
                                    }
                                });
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
    public void recommended(List<String> rekomendowani){
        if (!(rekomendowani.isEmpty())) {
            Query query = FirebaseFirestore.getInstance()
                    .collection("users")
                    .limit(5).whereIn("email", rekomendowani);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();



        Recommendedadapter = new UserAdapter(options, getActivity().getApplicationContext());

        recyclerViewRecommend.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerViewRecommend.setAdapter(Recommendedadapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToAddFriend(Recommendedadapter));
        itemTouchHelper.attachToRecyclerView(recyclerViewRecommend);

        Recommendedadapter.startListening();
        }
    }

    public void onPause() {
        super.onPause();
        if(adapter!=null){
            adapter.stopListening();
        }
        if(Recommendedadapter!=null) {
            Recommendedadapter.stopListening();
        }
    }

}