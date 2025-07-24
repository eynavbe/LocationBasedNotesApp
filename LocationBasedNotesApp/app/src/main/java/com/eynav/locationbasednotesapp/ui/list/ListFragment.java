package com.eynav.locationbasednotesapp.ui.list;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.eynav.locationbasednotesapp.MainActivity;
import com.eynav.locationbasednotesapp.R;
import com.eynav.locationbasednotesapp.UpdateNoteActivity;
import com.eynav.locationbasednotesapp.databinding.FragmentListBinding;
import com.eynav.locationbasednotesapp.Note;
import com.eynav.locationbasednotesapp.NotesAllCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

/*
Notes view, will contain the following view modes:
    Note List: All notes sorted by date of creation.
 */
public class ListFragment extends Fragment {
    FloatingActionButton fabAddNote;
    Context context;
    List<Note> noteList = new ArrayList<>();
    NotesAdapter notesAdapter;
    RecyclerView rvNotes;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentListBinding binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        fabAddNote = view.findViewById(R.id.fabAddNote);
        rvNotes = view.findViewById(R.id.rvNotes);
        rvNotes.setLayoutManager(new LinearLayoutManager(context));
        notesAdapter = new NotesAdapter(noteList, context);
        rvNotes.setAdapter(notesAdapter);

        getNotesList();
        addNewNote();
    }

    private void getNotesList() {
        ((MainActivity) getActivity()).getNotes(new NotesAllCallback(){
            @Override
            public void onNotesAll(List<Note> notes) {
                noteList.clear();
                noteList.addAll(notes);
                if (noteList.isEmpty()){
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Notes")
                            .setMessage("You have no saved notes.")
                            .create();

                    dialog.show();
                    new Handler(Looper.getMainLooper()).postDelayed(dialog::dismiss, 3000);
                }
                notesAdapter.notifyDataSetChanged();

            }
        });
    }

    private void addNewNote() {
        fabAddNote.setOnClickListener(l -> {
            Intent intent = new Intent(getContext(), UpdateNoteActivity.class);
            intent.putExtra("reason", "add");
            startActivity(intent);
        });
    }
}