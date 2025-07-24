package com.eynav.locationbasednotesapp;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.google.android.gms.maps.model.LatLng;

public class Note implements Parcelable{
    String date;
    String title;
    String body;
    String place;
    String id;
    String dateCreate;

    public Note(String date, String title, String body, String place,String dateCreate, String id) {
        this.date = date;
        this.title = title;
        this.body = body;
        this.place = place;
        this.dateCreate = dateCreate;
        this.id = id;

    }
    public Note(String date, String title, String body, String place,String dateCreate) {
        this.date = date;
        this.title = title;
        this.body = body;
        this.place = place;
        this.dateCreate = dateCreate;

    }
    public Note(String date, String title, String body, String place) {
        this.date = date;
        this.title = title;
        this.body = body;
        this.place = place;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
    public Timestamp firebaseDate(String dateGet){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(dateGet);
            if (date != null) {
                return new Timestamp(date);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return null;

    }
    public String firebaseDateToString(Timestamp timestamp){
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    public LatLng latLngNotePlace() {
        return latLngNotePlace(this.place);
    }
    public LatLng latLngNotePlace(String place) {
        if (place != null && place.contains(",")) {
            String[] parts = place.split(",");
            if (parts.length == 2) {
                try {
                    double latitude = Double.parseDouble(parts[0]);
                    double longitude = Double.parseDouble(parts[1]);
                    return new LatLng(latitude, longitude);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(date);
        parcel.writeString(title);
        parcel.writeString(body);
        parcel.writeString(place);
        parcel.writeString(id);
        parcel.writeString(dateCreate);
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    private Note(Parcel in) {
        date = in.readString();
        title= in.readString();
        body= in.readString();
        place= in.readString();
        id= in.readString();
        dateCreate= in.readString();
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    @Override
    public String toString() {
        return "Note{" +
                "date='" + date + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", place='" + place + '\'' +
                ", id='" + id + '\'' +
                ", dateCreate='" + dateCreate + '\'' +
                '}';
    }
}