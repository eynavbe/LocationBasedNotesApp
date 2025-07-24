package com.eynav.locationbasednotesapp.ui.list;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.eynav.locationbasednotesapp.R;
import com.eynav.locationbasednotesapp.Note;
import com.eynav.locationbasednotesapp.UpdateNoteActivity;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesAdapterHolder>{
    List<Note> noteList;
    Context context;

    public NotesAdapter(List<Note> noteList, Context context) {
        this.noteList = noteList;
        this.context = context;
    }

    @NonNull
    @Override
    public NotesAdapter.NotesAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.note_list_card_view, parent, false);
        return new NotesAdapter.NotesAdapterHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.NotesAdapterHolder holder, int position) {
        Note note = noteList.get(position);
        holder.tvNoteDate.setText(note.getDate());
        holder.tvNoteTitle.setText(note.getTitle());
        holder.note = note;
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class NotesAdapterHolder extends RecyclerView.ViewHolder {
        Note note;
        TextView tvNoteDate,tvNoteTitle;
        LinearLayout cartNote;
        public NotesAdapterHolder(@NonNull View itemView) {
            super(itemView);
            cartNote = itemView.findViewById(R.id.cartNote);
            tvNoteDate = itemView.findViewById(R.id.tvNoteDate);
            tvNoteTitle = itemView.findViewById(R.id.tvNoteTitle);
            itemView.setOnClickListener(l -> {
                Intent intent= new Intent(itemView.getContext(), UpdateNoteActivity.class);
                intent.putExtra("Note", note);
                intent.putExtra("reason", "edit");
                itemView.getContext().startActivity(intent);
            });
        }
    }
}