package com.example.ar_memento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class Edit extends AppCompatActivity {

    Toolbar toolbar;
    EditText noteTitle, noteDetails;
    Calendar c;
    String todaysDate;
    String currentTime;
    NoteDatabase db;
    NoteData note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent i = getIntent();
        Long id = i.getLongExtra("ID", 0);
        db = new NoteDatabase(this);
        note =  db.getNote(id);


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(note.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noteTitle = findViewById(R.id.nTitle);
        noteDetails = findViewById(R.id.noteDetails);

        noteTitle.setText(note.getTitle());
        noteDetails.setText(note.getContent());

        noteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    getSupportActionBar().setTitle(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //get current time and date
        c = Calendar.getInstance();
        //month/date/year
        todaysDate = c.get(Calendar.YEAR)+ "/" + (c.get(Calendar.MONTH)+1)+ "/" + c.get(Calendar.DAY_OF_MONTH);
        currentTime = pad(c.get(Calendar.HOUR))+ ":" + pad(c.get(Calendar.MINUTE));

        Log.d("calendar","Date and Time:   " + todaysDate + " and" + currentTime);
    }

    private String pad(int i) {
        if (i < 10)
            return "0" + i;
        return String.valueOf(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.delete) {
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        if(item.getItemId() == R.id.save) {
            note.setTitle(noteTitle.getText().toString());
            note.setContent(noteDetails.getText().toString());
            int id = db.editNote(note);
            if (id == note.getId()) {
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                goToNotes();
            }
            else if (item.getItemId() == R.id.delete) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToNotes() {
        Intent i = new Intent(this, Notes.class);
        startActivity((i));
    }

}

