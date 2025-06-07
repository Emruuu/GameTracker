package com.example.gametracker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class UserAdapter extends FirestoreRecyclerAdapter<User, UserHolder> {
    Context context;
    FirebaseFirestore db;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UserAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull User model) {
        holder.tvnick.setText(String.valueOf(model.getNick()));
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference iconRef = storage.getReference().child("avatars/"+model.getAvatar());
        Glide.with(context)
                .load(iconRef)
                .into(holder.ivavatar);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity=(AppCompatActivity)view.getContext();
                String friendList=String.valueOf(model.getMyList());
                Fragment fr = new mylist_fragment();
                Bundle b = new Bundle();
                b.putString("myList", friendList);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, fr).commit();
                fr.setArguments(b);
            }
        });
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_friend, parent, false);

        return new UserHolder(view);
    }
    public void onitemswipedelete(int adapterPosition){
        notifyItemChanged(adapterPosition);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user!=null){
            if (user.isEmailVerified()){
                User useradapter = getItem(adapterPosition);
                String email = useradapter.getEmail();

                db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(user.getEmail()));
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user1 = documentSnapshot.toObject(User.class);
                        user1.setFriendList(user1.getFriendList().replace(";"+email,""));
                        db.collection("users").document(Objects.requireNonNull(user.getEmail())).set(user1);
                        Toast.makeText(context.getApplicationContext(), "Znajomy został usunięty",
                               Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
    public void onitemswipeadd(int adapterPosition){
        notifyItemChanged(adapterPosition);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user!=null){
            if (user.isEmailVerified()){
                User useradapter = getItem(adapterPosition);
                String email = useradapter.getEmail();

                db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(user.getEmail()));
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user1 = documentSnapshot.toObject(User.class);
                        assert user1 != null;
                        if (!(user1.getFriendList().contains(email))) {
                            user1.setFriendList(user1.getFriendList() + ";" + email);
                            db.collection("users").document(Objects.requireNonNull(user.getEmail())).set(user1);
                            Toast.makeText(context.getApplicationContext(), "Znajomy został dodany do listy",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context.getApplicationContext(), "Znajomy już jest na twojej liście.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}
// Do zrobienia:
// Możliwość dodawania znajomych (lista sugerowanych).