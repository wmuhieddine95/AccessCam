package com.example.accesscam.Models;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note implements Comparable<Note>{
    String title;
    String note;
    String lastModified;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public Note(String title, String note, String lastModified)
    {
        this.title = title;
        this.note = note;
        this.lastModified = lastModified;
    }
/*    public Note(String title, String note)
    {
        this.title = title;
        this.note = note;
     }*/
     public Note(){

     }

    @Override
    public int compareTo(Note o) {
        Date date1;
        Date date2;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy");
        try {
            date1 = formatter.parse(getLastModified());
            date2 = formatter.parse(o.getLastModified());
            return date1.compareTo(date2);
        } catch (ParseException e) {
            Log.d("ErrorNote","Unable to Sort");
        }
        return 0;
    }
}
