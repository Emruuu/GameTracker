package com.example.gametracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login_fragment extends Fragment implements
        View.OnClickListener{

    private static final String TAG = "EmailPassword";

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText etEmail;
    private EditText etPassword;
    private Button bActivate;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_fragment, container, false);

        mStatusTextView = view.findViewById(R.id.tvStatusLog);
        mDetailTextView = view.findViewById(R.id.tvDetailLog);
        etEmail = view.findViewById(R.id.etEmailLog);
        etPassword = view.findViewById(R.id.etPaswdLog);

        view.findViewById(R.id.bLoginLog).setOnClickListener(this);
        view.findViewById(R.id.bRegisterLog).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new home_fragment()).commit();
            }
        });

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        updateUI(null);
    }

    //logowanie

    private void LogIn(String email, String password) {
        Log.d(TAG, "Zalogowano:" + email);
        if (!validateForm()) {
            return;
        }

        // showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user.isEmailVerified()) {
                                updateUI(user);
                            }else {
                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        mStatusTextView.setText("Wysłano link aktywacyjny");
                                        mDetailTextView.setText("Sprawdź pocztę!");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mStatusTextView.setText("Błąd przy wysyłaniu");
                                    }
                                });
                                mAuth.signOut();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            //  Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Niepoprawne dane.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    //komunikaty

    private void updateUI(FirebaseUser user) {
        // hideProgressDialog();
        if ((user != null) && user.isEmailVerified()) {
            requireActivity().recreate();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, new home_fragment()).commit();
        } else {
            mStatusTextView.setText(null);
            mDetailTextView.setText(null);
        }
    }

    //walidacja

    private boolean validateForm() {
        boolean valid = true;

        String email = etEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Wprowadź Email.");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Wprowadź Hasło.");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.bLoginLog) {
            LogIn(etEmail.getText().toString().trim(), etPassword.getText().toString().trim());
        }else if ((i == R.id.bRegisterLog)){
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, new register_fragment()).commit();
        }
    }
}