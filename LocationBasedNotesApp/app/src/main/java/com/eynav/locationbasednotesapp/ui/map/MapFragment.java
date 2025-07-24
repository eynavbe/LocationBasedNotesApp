package com.eynav.locationbasednotesapp.ui.map;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.eynav.locationbasednotesapp.MainActivity;
import com.eynav.locationbasednotesapp.Note;
import com.eynav.locationbasednotesapp.NotesAllCallback;
import com.eynav.locationbasednotesapp.R;
import com.eynav.locationbasednotesapp.UpdateNoteActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap mMap;
    MapView mMapView;
    List<Note> noteList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
        ((MainActivity) getActivity()).getNotes(new NotesAllCallback() {
            @Override
            public void onNotesAll(List<Note> notes) {
                noteList.clear();
                noteList.addAll(notes);
                addNotesToMap();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(requireContext());
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        addNotesToMap();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Object tag = marker.getTag();
                if (tag != null) {
                    Note note = getNoteById(tag.toString(), noteList);
                    Intent intent= new Intent(getContext(), UpdateNoteActivity.class);
                    intent.putExtra("Note", note);
                    intent.putExtra("reason", "edit");
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    private void addNotesToMap() {
        if (mMap == null || noteList == null) return;
        for (Note note : noteList) {
            LatLng latLng = note.latLngNotePlace(note.getPlace());
            if (latLng != null) {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(note.getTitle())
                        .icon(getMarkerIcon()));
                marker.setTag(note.getId());
            }
        }
        if (!noteList.isEmpty()){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(noteList.get(0).latLngNotePlace(),7));
        }
    }

    public Note getNoteById(String id, List<Note> noteList) {
        for (Note note : noteList) {
            if (note.getId().equals(id)) {
                return note;
            }
        }
        return null;
    }

    private BitmapDescriptor getMarkerIcon() {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor("#e81431"), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }
}