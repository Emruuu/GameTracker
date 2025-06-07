package com.example.gametracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class profil_fragment extends Fragment {
    private static final String TAG = "profil_fragment";
    private ImageView avatar;
    private TextView nick;
    private TextView email;
    private ImageButton mylist;
    private ImageButton friends;
    private ImageButton recomend;
    private ImageButton add;

    private String gamelist,friendlist;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Context context;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profil_fragment, container, false);

        email = view.findViewById(R.id.tvEmailProfil);
        nick = view.findViewById(R.id.tvNickProfil);
        avatar = view.findViewById(R.id.ivAvatarProfil);
        this.context = getContext();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String mail = getString(R.string.firebase_status_fmt, user.getEmail());
        mail = mail.substring(15);
        email.setText(mail);

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(mail);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User userr = documentSnapshot.toObject(User.class);
                if (userr!= null) {
                    nick.setText(userr.getNick());
                    gamelist = userr.getMyList();
                    friendlist = userr.getFriendList();
                    StorageReference avatarref = storage.getReference().child("avatars/"+userr.getAvatar());
                    Glide.with(context)
                            .load(avatarref)
                            .into(avatar);
                }
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        nick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditNickDialog();
            }
        });

        friends = view.findViewById(R.id.ibFriendsProfil);
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fr = new friends_mylist_fragment();
                Bundle b = new Bundle();
                b.putString("friendList", friendlist);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, fr).commit();
                fr.setArguments(b);
            }
        });

        recomend = view.findViewById(R.id.ibRecomendGamesProfil);
        recomend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fr = new recommended_games_fragment();
                Bundle b = new Bundle();
                b.putString("myList", gamelist);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, fr).commit();
                fr.setArguments(b);
            }
        });

        mylist = view.findViewById(R.id.ibMylistProfil);
        mylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fr = new mylist_fragment();
                Bundle b = new Bundle();
                b.putString("myList", gamelist);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, fr).commit();
                fr.setArguments(b);

            }
        });

        add = view.findViewById(R.id.ibAddGameProfil);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, new addedit_game_fragment()).commit();
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new home_fragment()).commit();
            }
        });

        return view;
    }

    private void showEditNickDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.edit_nick_dialog_title);

        // Dodanie pola EditText do okna dialogowego
        final EditText nickEditText = new EditText(getContext());
        nickEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        nickEditText.setText(nick.getText().toString().trim());
        builder.setView(nickEditText);

        // Dodanie przycisku "Anuluj"
        builder.setNegativeButton(R.string.cancel_button_label, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        // Dodanie przycisku "Zapisz"
        builder.setPositiveButton(R.string.save_button_label, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newNick = nickEditText.getText().toString();

                // Zapis nowego nicku użytkownika w bazie danych Firebase
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String email = currentUser.getEmail();
                    DocumentReference usersRef = db.collection("users").document(email);
                    usersRef.update("nick", newNick);
                }
                // Wyświetlenie nowego nicku użytkownika w TextView
                nick.setText(newNick);
            }
        });

        builder.show();
    }


    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Wybierz avatar");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_choose_avatar, null);
        builder.setView(dialogView);

        final RecyclerView recyclerView = dialogView.findViewById(R.id.rvAvatar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        final AvatarAdapter adapter = new AvatarAdapter(new ArrayList<String>(), null, getActivity());
        recyclerView.setAdapter(adapter);
        adapter.setSelectedPosition(0);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Pobieranie avatarów...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final List<String> avatarFileNames = new ArrayList<>();
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child("avatars").listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            avatarFileNames.add(item.getName());
                        }
                        adapter.updateData(avatarFileNames, storageRef);
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Błąd pobierania avatarów", Toast.LENGTH_SHORT).show();
                    }
                });

        builder.setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
            String newAvatar = adapter.getSelectedAvatarFileName();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String email = currentUser.getEmail();
                    DocumentReference usersRef = db.collection("users").document(email);
                    usersRef.update("avatar", newAvatar)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Powiadom użytkownika o zapisaniu nowego avatara
                                    Toast.makeText(context, "Zapisano nowy avatar", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Obsłuż błąd zapisywania avatara
                                    Log.d("Błąd zapisywania avatara", e.getMessage());
                                }
                            });
                }

            }
        });

        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // obsługa kliknięcia przycisku "Anuluj"
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}