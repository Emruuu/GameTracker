package com.example.gametracker;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.text.SimpleDateFormat;


public class game_mylist_fragment extends Fragment {
    FirebaseStorage storage;
    StorageReference rootRef;
    Context context;

    private TextView title,desc,producer,publisher,date,genre,mode,platform;
    private ImageButton back,film;
    private String docname,link;
    private ImageView ivfront;

    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_mylist_fragment, container, false);

        storage = FirebaseStorage.getInstance();
        rootRef = storage.getReference();
        this.context = getContext();

        title = view.findViewById(R.id.tvTitleGameML);
        desc = view.findViewById(R.id.tvDescGameML);
        producer = view.findViewById(R.id.tvProducerGameML);
        publisher = view.findViewById(R.id.tvPublisherGameML);
        date = view.findViewById(R.id.tvDateGameML);
        genre = view.findViewById(R.id.tvGenreGameML);
        mode = view.findViewById(R.id.tvModeGameML);
        platform = view.findViewById(R.id.tvPlatformGameML);
        ivfront = view.findViewById(R.id.ivFrontGameML);

        // Odbieranie danych
        getParentFragmentManager().setFragmentResultListener("game", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                docname = result.getString("docid");
                link = result.getString("link");
                db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("gry").document(docname);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Game game = documentSnapshot.toObject(Game.class);
                        if (game!= null) {
                            title.setText(game.getTitle());
                            desc.setText(game.getDesc());
                            producer.setText(game.getProducer());
                            publisher.setText(game.getPublisher());
                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
                            date.setText(outputFormat.format(game.getRelaseDate()));
                            genre.setText(String.join(" ", game.getGenre()));
                            mode.setText(game.getMode());
                            platform.setText(game.getPlatform());
                            String background = game.getFront();
                            if (background!=null)
                                loadfront(background, ivfront);
                            else
                                loadfront("test.png", ivfront);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });// Koniec odbierania danych

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new profil_fragment()).commit();
            }
        });

        film = view.findViewById(R.id.ibTrailerGameML);
        film.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view2 = inflater.inflate(R.layout.yt_dialog,null);

                YouTubePlayerView youTubePlayerView = view2.findViewById(R.id.youtube_player_view);
                getLifecycle().addObserver(youTubePlayerView);
                youTubePlayerView.setEnableAutomaticInitialization(false);
                youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        youTubePlayer.loadVideo(link,0);
                        super.onReady(youTubePlayer);
                    }
                });
                dialogBuilder.setView(view2);
                AlertDialog dialog;
                dialog = dialogBuilder.create();
                dialog.show();
            }
        });

        return view;
    }

    public void loadfront(String path, ImageView imageView){
        //StorageReference storageReference = storage.getReference();  // jestem na root
        StorageReference iconRef = rootRef.child("games/"+path);
        Glide.with(context)
                .load(iconRef)
                .into(imageView);
    }//load
}