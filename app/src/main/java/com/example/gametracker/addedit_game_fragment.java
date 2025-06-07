package com.example.gametracker;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class addedit_game_fragment extends Fragment {

    private static final String TAG = "addedit_game";

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private EditText etTitle,etDesc,etProducer,etPublisher,etDate,etMode,etGenre,etPlatform,etTrailer;
    private ImageButton check,add,choosefront,chooseback;
    private String title,desc,producer,publisher,date,mode,platform,trailer;
    private List<String> genre;

    FirebaseFirestore db;

    FirebaseStorage storage;
    StorageReference storageReference;

    // Uri indicates, where the image will be picked from
    private Uri [] filePath = new Uri[2];
    private int licznik=0;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_addedit_game_fragment, container, false);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        etTitle = view.findViewById(R.id.etTitleAdd);
        etDesc = view.findViewById(R.id.etDescAdd);
        etProducer = view.findViewById(R.id.etProducerAdd);
        etPublisher = view.findViewById(R.id.etPublisherAdd);
        etDate = view.findViewById(R.id.etDateAdd);
        etMode = view.findViewById(R.id.etModeAdd);
        etGenre = view.findViewById(R.id.etGenreAdd);
        etPlatform = view.findViewById(R.id.etPlatformAdd);
        etTrailer = view.findViewById(R.id.etTrailerAdd);

        db = FirebaseFirestore.getInstance();

        // Wybieranie daty pod polem tekstowym

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getContext(),android.R.style.Theme,mDateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;
                Log.d(TAG, "onDataSet: mm/dd/yyyy: " + month + "/"+day + "/"+ year);
                String date = day + "."+month + "."+ year;
                etDate.setText(date);
            }
        };

        // Koniec wybierania daty.

        // Odbieranie danych
        getParentFragmentManager().setFragmentResultListener("add", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                etTitle.setText(result.getString("title"));
                etDesc.setText(result.getString("desc"));
                etProducer.setText(result.getString("producer"));
                etPublisher.setText(result.getString("publisher"));
                etDate.setText(result.getString("date"));
                etGenre.setText(result.getString("genre"));
                etMode.setText(result.getString("mode"));
                etPlatform.setText(result.getString("platform"));
                etTrailer.setText(result.getString("trailer"));
            }
        });// Koniec odbierania danych

        check = view.findViewById(R.id.ibCheckAdd);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pobierzdane();
                AppCompatActivity activity=(AppCompatActivity)view.getContext();
                Bundle dane = new Bundle();
                dane.putString("title", title);
                dane.putString("desc", desc);
                dane.putString("producer", producer);
                dane.putString("publisher", publisher);
                dane.putString("date", date);
                dane.putString("mode", mode);
                dane.putString("genre", String.join(" ", genre));
                dane.putString("platform", platform);
                dane.putString("trailer", trailer);
                activity.getSupportFragmentManager().setFragmentResult("check", dane);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new preview_fragment()).commit();
            }
        });

        add = view.findViewById(R.id.ibAddAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pobierzdane();
                boolean b = (title.equals("")) || (desc.equals("")) || (platform.equals("")) || (mode.equals("")) || (date.equals("")) || (String.join(" ", genre).equals("")) || (producer.equals("")) || (publisher.equals("")) || (trailer.equals(""));
                boolean f = (filePath[0]==null) || (filePath[1]==null);
                if (b) {
                    Toast.makeText(getActivity(), "Uzupełnij pola przed dodaniem gry.",
                            Toast.LENGTH_SHORT).show();
                }else if(f){
                    Toast.makeText(getActivity(), "Wybierz okładkę/tło.",
                            Toast.LENGTH_SHORT).show();
                }else{
                    String[] link;
                    if (trailer.contains("v=")) {
                        link = trailer.split("v=");
                        trailer = link[1];
                    } else if (trailer.contains("/")) {
                        link = trailer.split("/");
                        trailer = link[3];
                    }
                    try {
                        Date date1=new SimpleDateFormat("dd.MM.yyyy").parse(date);
                        Game game = new Game(title, desc, platform, mode, date1, genre, producer, publisher, title + "back", title + "front", trailer);
                        db.collection("gry").whereEqualTo("title",title).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                Toast.makeText(getActivity(), "Tytuł już istnieje.",
                                        Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                db.collection("gry").document(title).set(game);
                                uploadImage(etTitle.getText().toString().trim());
                            }
                        });

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        choosefront = view.findViewById(R.id.ibChooseFrontAdd);
        chooseback = view.findViewById(R.id.ibChooseBackgroundAdd);
        choosefront.setEnabled(false);

        chooseback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

        choosefront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
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
    public void refresh(){
        Fragment fr = new addedit_game_fragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, fr).commit();
    }

    private void pobierzdane(){
        title = etTitle.getText().toString().trim();
        desc = etDesc.getText().toString().trim();
        producer = etProducer.getText().toString().trim();
        publisher = etPublisher.getText().toString().trim();
        date = etDate.getText().toString().trim();
        mode = etMode.getText().toString().trim();
        genre = Arrays.asList((etGenre.getText().toString().trim()).split(" "));
        platform = etPlatform.getText().toString().trim();
        trailer = etTrailer.getText().toString().trim();
    }

    private void SelectImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Wybierz obrazek do wgrania..."),
                PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data)
    {
        super.onActivityResult(requestCode,
                resultCode,
                data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            Log.d(TAG,"Licznik: "+licznik);
            if(licznik==0) {
                filePath[0] = data.getData();
                licznik++;
                chooseback.setEnabled(false);
                choosefront.setEnabled(true);
            }else {
                filePath[1] = data.getData();
                choosefront.setEnabled(false);
            }
        }
    }

    private void uploadImage(String name) {
        for(int j=0;j<2;j++){
            if (filePath[j] != null) {
            ProgressDialog progressDialog
                    = new ProgressDialog(getContext());
            progressDialog.setTitle("Wgrywanie...");
            progressDialog.show();
                StorageReference ref;
            if(j==0)
                ref = storageReference.child("games/" + name+"back");
            else
                ref = storageReference.child("games/" + name+"front");

            ref.putFile(filePath[j])
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(getContext(),
                                                    "Propozycja wysłana!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getContext(),
                                            "Niepowodzenie " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Załadowano "
                                                    + (int)progress + "%");
                                }
                            });
            }
        }
    }
}