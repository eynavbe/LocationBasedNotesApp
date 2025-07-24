package com.eynav.locationbasednotesapp;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpdateNoteActivity extends AppCompatActivity {
    String notePlace = "";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Note noteGet;
    Context context = this;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);
        Button btnAddUpdateNote = findViewById(R.id.btnAddUpdateNote);
        Button btnDeleteUpdateNote = findViewById(R.id.btnDeleteUpdateNote);
        TextView tvTitleAddUpdateNote = findViewById(R.id.tvTitleAddUpdateNote);
        Button etNoteDate = findViewById(R.id.etNoteDate);
        EditText etNoteTitle = findViewById(R.id.etNoteTitle);
        EditText etNoteBody = findViewById(R.id.etNoteBody);
        ImageView imExit = findViewById(R.id.imExit);
        imExit.setOnClickListener(l -> finish());
        String reason = getIntent().getStringExtra("reason");

        if (reason != null) {
            Log.d("UpdateNoteActivity: ", "reason: " + reason);
        }
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        if (reason != null) {
            if (reason.equals("add")) {
                tvTitleAddUpdateNote.setText("Add Note");
                btnAddUpdateNote.setText("add");
                String noteDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", mDay, mMonth + 1, mYear);
                etNoteDate.setText(noteDate);
            }
            if (reason.equals("edit")) {
                tvTitleAddUpdateNote.setText("Edit Note");
                btnAddUpdateNote.setText("edit");
                noteGet = getIntent().getParcelableExtra("Note");
                if (noteGet != null){
                    etNoteDate.setText(noteGet.getDate());
                    etNoteTitle.setText(noteGet.getTitle());
                    etNoteBody.setText(noteGet.getBody());
                }
            }
        }

        etNoteDate.setOnClickListener(k -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
                String noteDate1 = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);
                etNoteDate.setText(noteDate1);
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            notePlace = latitude + "," + longitude;
                        } else {
                            Toast.makeText(context, "Unable to locate location", Toast.LENGTH_SHORT).show();
                        }
                    });
        }


        btnAddUpdateNote.setOnClickListener(h -> {
            if (etNoteDate.getText().toString().isEmpty()) {
                Toast.makeText(context, "please choose note date", Toast.LENGTH_SHORT).show();
            } else if (etNoteTitle.getText().toString().isEmpty()) {
                Toast.makeText(context, "title empty", Toast.LENGTH_SHORT).show();
            } else if (etNoteBody.getText().toString().isEmpty()) {
                Toast.makeText(context, "body empty ", Toast.LENGTH_SHORT).show();
            } else {
                Note note = new Note(etNoteDate.getText().toString(), etNoteTitle.getText().toString(), etNoteBody.getText().toString(), notePlace);
                if (reason != null){
                    if (reason.equals("edit")) {
                        addNoteToFirebase(note,noteGet.getId());
                    }
                    if (reason.equals("add")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                        String currentDateTimeID = sdf.format(new Date());
                        addNoteToFirebase(note,currentDateTimeID);
                    }
                }
            }
        });

        btnDeleteUpdateNote.setOnClickListener(r -> {
                AlertDialog.Builder builderDelete = new AlertDialog.Builder(context)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    if (reason != null) {
                        if (reason.equals("add")) {
                            nextPage();
                        }else {
                            saveDeleteNote(noteGet);
                        }
                    }
                })
                .setNegativeButton("No", (dialogInterface, i) -> {

                });
        builderDelete.show();
            });
    }

    private void nextPage() {
        Intent intent = new Intent(UpdateNoteActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void addNoteToFirebase(Note note, String id) {
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> data = new HashMap<>();
            data.put("date", note.firebaseDate(note.getDate()));
            data.put("title", note.getTitle());
            data.put("body", note.getBody());
            data.put("place", note.getPlace());
            data.put("dateCreate", new Date());
            data.put("id", id);
            db.collection("users")
                    .document(uid)
                    .collection("notes")
                    .document(id)
                    .set(data)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "save", Toast.LENGTH_SHORT).show();
                        nextPage();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "error " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void saveDeleteNote(Note note) {
        if (user != null && note != null) {
            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uid).collection("notes").document(note.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("delete");
                        nextPage();
                    })
                    .addOnFailureListener(e -> System.out.println("Error delete document"));
        }
    }
}