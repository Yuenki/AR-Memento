package com.example.ar_memento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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

    private DrawerLayout mNavDrawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mNavDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mNavDrawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mNavDrawer.addDrawerListener(toggle);
        toggle.syncState();
        slide_up_button = (ImageButton) findViewById(R.id.imageButton);
        ImgButton  =findViewById(R.id.ImgButton);
        onResume();

    }

    @Override
    public void onBackPressed() {
        if(mNavDrawer.isDrawerOpen(GravityCompat.START)){
            mNavDrawer.closeDrawer(GravityCompat.START);
        } else{
            super.onBackPressed();

        }
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
