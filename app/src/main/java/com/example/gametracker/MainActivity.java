package com.example.gametracker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mlayaut;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mlayaut = (DrawerLayout) findViewById(R.id.main_activity);
        NavigationView menu = findViewById(R.id.menu);

        //sprawdzanie czy jest zalogowany
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user==null){
            menu.getMenu().setGroupVisible(R.id.menu_user,false);
            menu.getMenu().setGroupVisible(R.id.menu_anonim, true);
        }else{
            if (user.isEmailVerified()){
                menu.getMenu().setGroupVisible(R.id.menu_user,true);
                menu.getMenu().setGroupVisible(R.id.menu_anonim,false);
            }else{
                mAuth.signOut();
                this.recreate();
            }
        }
        menu.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle mtoggle=new ActionBarDrawerToggle(this,mlayaut,toolbar,R.string.open,R.string.close);
        mlayaut.addDrawerListener(mtoggle);
        mtoggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,
                    new home_fragment()).commit();
        }
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new home_fragment()).commit();
                break;
            case R.id.calendar:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new calendar_fragment()).commit();
                break;
            case R.id.login:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new login_fragment()).commit();
                break;
            case R.id.profil:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new profil_fragment()).commit();
                break;
            case R.id.friends:
                FirebaseUser user = mAuth.getCurrentUser();
                String mail = getString(R.string.firebase_status_fmt, user.getEmail());
                mail = mail.substring(15);
                db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("users").document(mail);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User userr = documentSnapshot.toObject(User.class);
                        if (userr!= null) {
                            String friendlist = userr.getFriendList();
                            Fragment fr = new friends_mylist_fragment();
                            Bundle b = new Bundle();
                            b.putString("friendList", friendlist);
                            getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, fr).commit();
                            fr.setArguments(b);
                        }
                    }
                });
                break;
            case R.id.message:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new message_fragment()).commit();
                break;
            case R.id.logout:
                mAuth.signOut();
                this.recreate();
                break;
            case R.id.exit:
                finish();
                System.exit(0);        }
        mlayaut.closeDrawer(GravityCompat.START);
        return true; }
    @Override
    public void onBackPressed(){
        if(mlayaut.isDrawerOpen(GravityCompat.START)){
            mlayaut.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();}
    }
}