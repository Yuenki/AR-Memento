package com.example.ar_memento;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ContentActivity extends AppCompatActivity {

    TextView nContent;
    NoteDatabase db;
    NoteData note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nContent = findViewById(R.id.contentOfNote);

        Intent i = getIntent();
        Long id = i.getLongExtra("ID", 0);

        db = new NoteDatabase(this);
        note = db.getNote(id);
        getSupportActionBar().setTitle(note.getTitle());
        nContent.setText(note.getContent());
        nContent.setMovementMethod(new ScrollingMovementMethod());


//        Toast.makeText(this, "ID -> " + note.getTitle(), Toast.LENGTH_SHORT).show();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               db.deleteNote(note.getId());
               Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
               startActivity(new Intent(getApplicationContext(), NotesActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.edit) {
            Toast.makeText(this, "Edit Note", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, EditActivity.class);
            i.putExtra("ID", note.getId());
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

}
