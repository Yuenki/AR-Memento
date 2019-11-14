package com.example.ar_memento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ImageButton slide_up_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slide_up_button = (ImageButton) findViewById(R.id.imageButton);
        onResume();
    }
    protected void onResume() {
        super.onResume();
        final Intent cameraIntent = new Intent(this, CameraActivity.class);
        slide_up_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(cameraIntent);
            }
        });
    }
}
