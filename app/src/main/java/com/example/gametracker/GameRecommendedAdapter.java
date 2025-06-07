package com.example.gametracker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class GameRecommendedAdapter extends FirestoreRecyclerAdapter<Game, GameHolder> {
    FirebaseStorage storage;
    StorageReference rootRef;
    Context context;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public GameRecommendedAdapter(@NonNull FirestoreRecyclerOptions<Game> options, Context context) {
        super(options);
        storage = FirebaseStorage.getInstance();
        rootRef = storage.getReference();
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull GameHolder holder, int position, @NonNull Game model) {
        // Onclick i wysłanie danych do game_fragment
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity=(AppCompatActivity)view.getContext();
                game_fragment gameFragment= new game_fragment();
                Bundle dane = new Bundle();
                int pos = holder.getAdapterPosition();
                String documentId = getSnapshots().getSnapshot(pos).getId();
                dane.putString("docid", documentId);
                dane.putString("link", model.getTrailer());
                activity.getSupportFragmentManager().setFragmentResult("game", dane);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, gameFragment).addToBackStack(null).commit();
            }
        }); // Koniec Onclick

        holder.tvData.setText(String.valueOf(model.getRelaseDate()));
        holder.tvTitle.setText(String.valueOf(model.getTitle()));
        holder.tvPlatform.setText(String.valueOf(model.getPlatform()));
        holder.tvGenre.setText(String.join(" ", model.getGenre()));
        String background = model.getBackground();
        if (background!=null)
            loadBackground(background, holder.ivBg);
        else
            loadBackground("test.png", holder.ivBg);
    }

    @NonNull
    @Override
    public GameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_home, parent, false);

        return new GameHolder(view);
    }

    public void loadBackground(String path, ImageView imageView){
        //StorageReference storageReference = storage.getReference();  // jestem na root
        StorageReference iconRef = rootRef.child("games/"+path);
        Glide.with(context)
                .load(iconRef)
                .into(imageView);
    }//load
}
