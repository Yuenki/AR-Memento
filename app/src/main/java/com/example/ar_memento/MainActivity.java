package com.example.ar_memento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ImageButton slide_up_button;
    Button ImgButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slide_up_button = (ImageButton) findViewById(R.id.imageButton);
        ImgButton  =findViewById(R.id.Imgbutton);
        onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Intent cameraIntent = new Intent(this, CameraActivity.class);
        final Intent ImgPick = new Intent(this,ImagePickActivity.class);

        ImgButton.setOnClickListener(v->startActivity(ImgPick));
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                startActivity(cameraIntent);
//            }
//        });
        slide_up_button.setOnClickListener(r -> startActivity(cameraIntent));
    }
}
