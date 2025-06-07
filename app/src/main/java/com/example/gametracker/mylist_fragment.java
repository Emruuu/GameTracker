package com.example.gametracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class mylist_fragment extends Fragment {
    private static final String TAG = "mylist_fragment";

    private GameMLAdapter adapter;
    private RecyclerView recyclerView;
    private String[] title;

    private String list;

    Button blast, bnextmonth, bnext6month,bsoon, bthisyear,bnextyear;
    ImageButton ibszukaj;
    EditText etszukaj;

    String filtr="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mylist_fragment, container, false);

        blast = view.findViewById(R.id.bLastMyList);
        bnextmonth = view.findViewById(R.id.bNextMonthMyList);
        bnext6month = view.findViewById(R.id.bNextMonthqMyList);
        bsoon = view.findViewById(R.id.bSoonMyList);
        bthisyear = view.findViewById(R.id.bThisYearMyList);
        bnextyear = view.findViewById(R.id.bNextYearMyList);
        ibszukaj = view.findViewById(R.id.ibSzukajMylist);
        etszukaj = view.findViewById(R.id.etSearchMyList);

        recyclerView = view.findViewById(R.id.rvMyList);

        list = getArguments().getString("myList");
        title = list.split(",");

        blast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtr = "last";
                refresh(filtr);
            }
        });
        bnextmonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtr = "nextmonth";
                refresh(filtr);
            }
        });
        bnext6month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtr = "next6month";
                refresh(filtr);
            }
        });
        bsoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtr = "soon";
                refresh(filtr);
            }
        });
        bthisyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtr = "thisyear";
                refresh(filtr);
            }
        });
        bnextyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtr = "nextyear";
                refresh(filtr);
            }
        });
        ibszukaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etszukaj.getText().toString().equals("")){
                    etszukaj.setError("Wprowadź wyszukiwaną frazę.");
                }else{
                    filtr = etszukaj.getText().toString().trim();
                    refresh(filtr);
                }
            }
        });
        if(getArguments().getString("filtr")!=null) {
            filtr = getArguments().getString("filtr");
        }
        Query query = null;
        if(filtr.equals("")){
            query = FirebaseFirestore.getInstance()
                    .collection("gry").whereEqualTo("accepted", true )
                    .orderBy("relaseDate", Query.Direction.DESCENDING)
                    .limit(50).whereIn("title", Arrays.asList(title));
            wyswietl(query);
        }else if(filtr.equals("last")) {
            query = FirebaseFirestore.getInstance()
                    .collection("gry")
                    .whereLessThanOrEqualTo("relaseDate",Timestamp.now()).whereEqualTo("accepted", true )
                    .orderBy("relaseDate", Query.Direction.DESCENDING)
                    .limit(50).whereIn("title", Arrays.asList(title));
            wyswietl(query);
        }else if(filtr.equals("nextmonth")) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 1);
            java.util.Date dt = cal.getTime();
            query = FirebaseFirestore.getInstance()
                    .collection("gry")
                    .whereGreaterThanOrEqualTo("relaseDate",Timestamp.now())
                    .whereEqualTo("accepted", true )
                    .whereLessThanOrEqualTo("relaseDate",new Timestamp(dt))
                    .orderBy("relaseDate")
                    .limit(50).whereIn("title", Arrays.asList(title));
            wyswietl(query);
        }else if(filtr.equals("next6month")) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 6);
            java.util.Date dt = cal.getTime();
            query = FirebaseFirestore.getInstance()
                    .collection("gry")
                    .whereGreaterThanOrEqualTo("relaseDate",Timestamp.now())
                    .whereEqualTo("accepted", true )
                    .whereLessThanOrEqualTo("relaseDate",new Timestamp(dt))
                    .orderBy("relaseDate")
                    .limit(50).whereIn("title", Arrays.asList(title));
            wyswietl(query);
        }else if (filtr.equals("soon")){
            query = FirebaseFirestore.getInstance()
                    .collection("gry")
                    .whereGreaterThanOrEqualTo("relaseDate",Timestamp.now())
                    .whereEqualTo("accepted", true )
                    .orderBy("relaseDate")
                    .limit(50).whereIn("title", Arrays.asList(title));
            wyswietl(query);
        }else if (filtr.equals("thisyear")){
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR),1,1);
            java.util.Date dt = calendar.getTime();
            calendar.set(calendar.get(Calendar.YEAR)+1,1,1);
            java.util.Date dt2 = calendar.getTime();
            query = FirebaseFirestore.getInstance()
                    .collection("gry")
                    .whereGreaterThanOrEqualTo("relaseDate",new Timestamp(dt))
                    .whereLessThanOrEqualTo("relaseDate",new Timestamp(dt2))
                    .whereEqualTo("accepted", true )
                    .orderBy("relaseDate")
                    .limit(50).whereIn("title", Arrays.asList(title));
            wyswietl(query);
        }else if (filtr.equals("nextyear")){
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR)+1,1,1);
            java.util.Date dt = calendar.getTime();
            calendar.set(calendar.get(Calendar.YEAR)+2,1,1);
            java.util.Date dt2 = calendar.getTime();
            query = FirebaseFirestore.getInstance()
                    .collection("gry")
                    .whereGreaterThanOrEqualTo("relaseDate",new Timestamp(dt))
                    .whereLessThanOrEqualTo("relaseDate",new Timestamp(dt2))
                    .whereEqualTo("accepted", true )
                    .orderBy("relaseDate")
                    .limit(50).whereIn("title", Arrays.asList(title));
            wyswietl(query);
        }else {
            String gatunki = "strzelanka,strategia,zręcznościowa,przygodowa,fabularna,symulacja,sportowa,logiczna,edukacyjna";
            if (gatunki.contains(filtr)){
                query = FirebaseFirestore.getInstance()
                        .collection("gry")
                        .whereArrayContains("genre", filtr)
                        .whereEqualTo("accepted", true)
                        .orderBy("relaseDate", Query.Direction.DESCENDING)
                        .whereIn("title", Arrays.asList(title))
                        .limit(50);
                wyswietl(query);
            } else {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("gry").whereIn("title", Arrays.asList(title)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> wyszukiwanie = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Game game = document.toObject(Game.class);
                                String title=game.getTitle().toLowerCase();
                                if(title.contains(filtr.toLowerCase())){
                                    wyszukiwanie.add(game.getTitle());
                                }
                            }
                            if (!wyszukiwanie.isEmpty()){
                                Query query = FirebaseFirestore.getInstance()
                                        .collection("gry")
                                        .whereIn("title",wyszukiwanie)
                                        .whereEqualTo("accepted", true )
                                        .orderBy("relaseDate", Query.Direction.DESCENDING)
                                        .limit(50);
                                wyswietl(query);
                            }else{
                                Toast.makeText(getActivity(), "Brak szukanego tytułu.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new profil_fragment()).commit();
            }
        });

        return view;
    }

    public void refresh(String dane){
        Fragment fr = new mylist_fragment();
        Bundle bundle = new Bundle();
        bundle.putString("filtr",dane);
        bundle.putString("myList",list);
        fr.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, fr).commit();
    }

    public void wyswietl(Query query){
        FirestoreRecyclerOptions<Game> options = new FirestoreRecyclerOptions.Builder<Game>()
                .setQuery(query, Game.class)
                .build();


        adapter = new GameMLAdapter(options, getActivity().getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(adapter);
        adapter.startListening();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback2(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
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