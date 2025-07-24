package com.eynav.locationbasednotesapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.eynav.locationbasednotesapp.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/*
 Main screen Screen
 */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    FirebaseAuth mAuth;
    List<Note> noteList = new ArrayList<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    static boolean welcomeShown = false;

    /*
    Logout option.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
         if (id == R.id.action_logout) {
             welcomeShown = false;
             FirebaseAuth.getInstance().signOut();
             Intent intent = new Intent(MainActivity.this, LoginActivity.class);
             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
             startActivity(intent);
             return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        /*
         Bottom navigation menu will contain: List mode, Map mode.
         */
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_map,
                R.id.navigation_list)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        /*
        After login the user should be logged-in in the next time he opens the application.
        */
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }else{

            WelcomeMessageUser(user.getDisplayName(),user.getEmail());

        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }

    }

    /*
       Welcome message to the user.
    */
    private void WelcomeMessageUser(String name, String email) {
        if (!welcomeShown) {
            welcomeShown = true;
            Snackbar snackbar = Snackbar.make(binding.getRoot(), "Welcome " + name, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
            params.gravity = Gravity.TOP;
            snackbarView.setLayoutParams(params);
            snackbar.show();
        }
//            new AlertDialog.Builder(this)
//                    .setTitle("Welcome")
//                    .setMessage(name)
//                    .setPositiveButton("ok", (dialog, which) -> {
//                        dialog.dismiss();
//                    })
//                    .setCancelable(false)
//                    .show();
    }

    public void getNotes(NotesAllCallback callback) {
        if (!noteList.isEmpty()) {
            callback.onNotesAll(noteList);
            return;
        }
        readNotesFromFirebase(callback);
    }
    /*
    Get All notes, All notes sorted by date of creation.
     */
    private void readNotesFromFirebase(NotesAllCallback callback) {
        noteList.clear();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uid).collection("notes")
                    .orderBy("dateCreate", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Timestamp timestamp = document.getTimestamp("date");
                                    String title = String.valueOf(document.getData().get("title"));
                                    String body = String.valueOf(document.getData().get("body"));
                                    String place = String.valueOf(document.getData().get("place"));
                                    String id = String.valueOf(document.getData().get("id"));
                                    Timestamp dateCreate = document.getTimestamp("dateCreate");
                                    Note note = new Note("", title, body, place,"", id);
                                    note.setDate(note.firebaseDateToString(timestamp));
                                    note.setDateCreate(note.firebaseDateToString(dateCreate));
                                    noteList.add(note);
                                }
                                callback.onNotesAll(noteList);

                            } else {
                                System.out.println("Error getting documents.");
                            }
                        }
                    });
        }
    }
}