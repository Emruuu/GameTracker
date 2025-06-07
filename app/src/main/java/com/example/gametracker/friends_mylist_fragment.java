package com.example.gametracker;

import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class friends_mylist_fragment extends Fragment {

    private ImageButton search, add;
    private UserAdapter adapter;
    private EditText etsearch;
    String filtr="";
    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends_mylist_fragment, container, false);


        recyclerView = view.findViewById(R.id.rvFriendsMylist);

        String list = getArguments().getString("friendList");
        String[] znajomy = list.split(";");

        Query query = FirebaseFirestore.getInstance()
                .collection("users")
                .limit(50).whereIn("email", Arrays.asList(znajomy));

        wyswietl(query);

        add = view.findViewById(R.id.ibFriendsAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fr = new friends_fragment();
                Bundle b = new Bundle();
                b.putString("friendList", list);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, fr).commit();
                fr.setArguments(b);
            }
        });

        etsearch = view.findViewById(R.id.etFriendsSearch);
        search = view.findViewById(R.id.ibFriendsSearch);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etsearch.getText().toString().equals("")) {
                    etsearch.setError("Wprowadź wyszukiwaną frazę.");
                } else {
                    filtr = etsearch.getText().toString().trim();
                    Query query = FirebaseFirestore.getInstance()
                            .collection("users")
                            .limit(50).whereEqualTo("nick", filtr);
                    wyswietl(query);
                }
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new profil_fragment()).commit();
            }
        });

        return view;
    }
        public void wyswietl(Query query){
            FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                    .setQuery(query, User.class)
                    .build();

            adapter = new UserAdapter(options, getActivity().getApplicationContext());

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            recyclerView.setAdapter(adapter);

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteFriend(adapter));
            itemTouchHelper.attachToRecyclerView(recyclerView);

            adapter.startListening();
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