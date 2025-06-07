package com.example.gametracker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

public class message_fragment extends Fragment {

    private TextView tvUser, tvTittle;
    private EditText etMessage, etUser, etTittle;
    private Button bsend;
    private RadioButton rbApka, rbUser, rbPremiera;
    Context context;

    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_fragment, container, false);

        etMessage = view.findViewById(R.id.etMsgMessage);
        etTittle = view.findViewById(R.id.etMsgTittle);
        etUser = view.findViewById(R.id.etMsgUser);

        tvUser = view.findViewById(R.id.tvMsgUser);
        tvTittle = view.findViewById(R.id.tvMsgTittle);

        rbApka = view.findViewById(R.id.rbMsgApka);
        rbUser = view.findViewById(R.id.rbMsgUser);
        rbPremiera = view.findViewById(R.id.rbMsgPremiera);

        rbApka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etTittle.setVisibility(View.INVISIBLE);
                tvTittle.setVisibility(View.INVISIBLE);
                etUser.setVisibility(View.INVISIBLE);
                tvUser.setVisibility(View.INVISIBLE);
            }
        });

        rbUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etTittle.setVisibility(View.INVISIBLE);
                tvTittle.setVisibility(View.INVISIBLE);
                etUser.setVisibility(View.VISIBLE);
                tvUser.setVisibility(View.VISIBLE);
            }
        });

        rbPremiera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etUser.setVisibility(View.INVISIBLE);
                tvUser.setVisibility(View.INVISIBLE);
                etTittle.setVisibility(View.VISIBLE);
                tvTittle.setVisibility(View.VISIBLE);
            }
        });

        bsend = view.findViewById(R.id.bMsgPrzeslij);

        db = FirebaseFirestore.getInstance();

        bsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typ = "";
                String addon = "";
                if (rbApka.isChecked()) typ="Aplikacja";
                if (rbUser.isChecked()){
                    typ="Użytkownik";
                    addon = etUser.getText().toString().trim();
                }
                if (rbPremiera.isChecked()){
                    typ="Premiera";
                    addon = etTittle.getText().toString().trim();
                }

                String message = etMessage.getText().toString().trim();


                if(!rbApka.isChecked()&&!rbPremiera.isChecked()&&!rbUser.isChecked()){
                    Toast.makeText(view.getContext(), "Zaznacz czego dotyczy zgłoszenie!",
                            Toast.LENGTH_SHORT).show();
                }else if(message.length()<10){
                    Toast.makeText(view.getContext(), "Twoja wiadomość jest pusta lub za krótka",
                            Toast.LENGTH_SHORT).show();
                }else if(rbUser.isChecked()||rbPremiera.isChecked()) {
                    if(rbUser.isChecked()&&etUser.getText().toString().trim().length()<4) {
                        Toast.makeText(view.getContext(), "Uzupełnij nazwe użytkownika!",
                                Toast.LENGTH_SHORT).show();
                    }else if(rbPremiera.isChecked()&&etTittle.getText().toString().trim().length()<4){
                        Toast.makeText(view.getContext(), "Uzupełnij tytuł premiery!",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        Message msg = new Message(typ,addon,message);
                        db.collection("wiadomosci").add(msg);
                        Toast.makeText(view.getContext(), "Twoja wiadomość została wysłana!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Message msg = new Message(typ,addon,message);
                    db.collection("wiadomosci").add(msg);
                    Toast.makeText(view.getContext(), "Twoja wiadomość została wysłana!",
                            Toast.LENGTH_SHORT).show();
                }
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

}