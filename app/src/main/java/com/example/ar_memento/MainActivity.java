package com.example.ar_memento;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ImageButton slide_up_button;
    //Button ImgButton;
    Button scannerButton;

    public static FragmentManager fragmentManager;
    private DrawerLayout mNavDrawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // nav_drawer_layout includes activity_main.
        setContentView(R.layout.nav_drawer_layout);
        /*
FragmentManager fragmentManager = getSupportFragmentManager();
if(findViewById(R.id.navigation_view) !=null) {
    if(savedInstanceState !=null) {
        return;
    }
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    GalleryFragment galleryFragment = new GalleryFragment();
    fragmentTransaction.add(R.id.navigation_view,galleryFragment,null);
    fragmentTransaction.commit();
}
*/

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNavDrawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mNavDrawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mNavDrawer.addDrawerListener(toggle);
        toggle.syncState();


        slide_up_button = findViewById(R.id.imageButton);
        //Connects buttons to corresponding buttons in activity_main.
        //ImgButton  =findViewById(R.id.ImgButton);
//        scannerButton = findViewById(R.id.scannerButton);

    }
@SuppressWarnings("StatementWithEmtpyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.GalleryPic:

                getSupportFragmentManager().beginTransaction().replace(R.id.navigation_container, new GalleryFragment()).commit();
                break;
            case R.id.camera:
                //getSupportFragmentManager().beginTransaction().replace(R.id.navigation_container, new MessageFragment()).commit();
                startActivity(new Intent(this,CameraActivity.class));
                break;
            case  R.id.User:
                getSupportFragmentManager().beginTransaction().replace(R.id.navigation_container, new Login()).commit();
                break;

        }

        if(item.getItemId() == R.id.GalleryPic){
            Fragment frag = new GalleryFragment();
            getFragmentManager().popBackStack();
           // MainActivity.fragmentManager.beginTransaction().replace(R.id.navigation_view,null).addToBackStack(null).commit();

            getSupportFragmentManager().beginTransaction().add(R.id.navigation_container, frag).addToBackStack(null).commit();
        }

        mNavDrawer.closeDrawer(GravityCompat.START);
        return true;
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
        final Intent scannerIntent = new Intent(this, ScannerActivity.class);


//        scannerButton.setOnClickListener(s -> startActivity(scannerIntent));
        //ImgButton.setOnClickListener(v->startActivity(ImgPick));
        slide_up_button.setOnClickListener(r -> startActivity(scannerIntent));
    }
}
