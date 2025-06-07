package com.example.gametracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class register_fragment extends Fragment implements
        View.OnClickListener{

    private static final String TAG = "EmailPassword";

    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etNick;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_fragment, container, false);

        etEmail = view.findViewById(R.id.etEmailReg);
        etPassword = view.findViewById(R.id.etPasswdReg);
        etConfirmPassword = view.findViewById(R.id.etConfPasswdReg);
        etNick = view.findViewById(R.id.etNickReg);

        view.findViewById(R.id.bRegisterReg).setOnClickListener(this);
        view.findViewById(R.id.bLoginReg).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new login_fragment()).commit();
            }
        });

        return view;
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        //  showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification();
                            // dodanie uztkownika do bazy
                            String email = etEmail.getText().toString().trim();
                            User userr = new User(etNick.getText().toString().trim(),etEmail.getText().toString().trim());
                            db.collection("users").document(email).set(userr);
                            // koniec dodawania
                            Toast.makeText(getActivity(), "Wysłano link aktywacyjny.",
                                    Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, new login_fragment()).commit();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Niepoprawne dane.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;
        String nick = etNick.getText().toString().trim();
        if (TextUtils.isEmpty(nick)) {
            etEmail.setError("Wprowadź Nick.");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        //czy mail jest pusty
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Wprowadź Email.");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        String password = etPassword.getText().toString().trim();
        String confpassword = etConfirmPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(password) && !(password.equals(confpassword))) {
            etPassword.setError("Hasła nie są takie same.");
            etConfirmPassword.setError("Hasła nie są takie same.");
            valid = false;
        } else {
            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Wprowadź Hasło.");
                valid = false;
            } else {
                etPassword.setError(null);
            }
            if (TextUtils.isEmpty(password)) {
                etConfirmPassword.setError("Wprowadź Ponownie hasło.");
                valid = false;
            } else {
                etConfirmPassword.setError(null);
            }
        }

        //czy mail jest prawidłowy
        if(isValidEmailId(email)){
            etEmail.setError(null);
        }else{
            etEmail.setError("Niepoprawny Email");
            valid = false;
        }

        return valid;
    }//validateForm

    private boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }//isValidEmailId

    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.bRegisterReg) {
            createAccount(etEmail.getText().toString().trim(), etPassword.getText().toString().trim());
        }else if ((i == R.id.bLoginReg)){
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, new login_fragment()).commit();
        }
    }
}