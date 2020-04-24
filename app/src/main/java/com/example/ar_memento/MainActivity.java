package com.example.ar_memento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ImageButton slide_up_button;
    private DrawerLayout mNavDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNavDrawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mNavDrawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mNavDrawer.addDrawerListener(toggle);
        toggle.syncState();

        slide_up_button = findViewById(R.id.imageButton);
    }
@SuppressWarnings("StatementWithEmtpyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    switch (item.getItemId()) {
        // TODO: Check whether user is selecting home while in home.
        case R.id.nav_sel_home:
            startActivity(new Intent(this, MainActivity.class));
            break;
//        case R.id.nav_sel_add_image:
////            Fragment frag_addImage = new AddImageFragment();
////            transaction.add(R.id.navigation_container, frag_addImage)
////                    .addToBackStack(null)
////                    .commit();
//            Toast toast = Toast.makeText(this,
//                    "Under development!", Toast.LENGTH_SHORT);
//            toast.show();
//            break;
        case R.id.camera:
            startActivity(new Intent(this,CameraActivity.class));
            break;
        case R.id.add_notes:
            startActivity(new Intent(this, NotesActivity.class));
            break;
        case R.id.nav_sel_user_profile:
            Fragment frag_login= new LoginFragment();
            transaction.add(R.id.navigation_container, frag_login)
                    .addToBackStack(null)
                    .commit();
            break;

        case R.id.settings:
            startActivity(new Intent(this,SettingsActivity.class));
        }

        mNavDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if(mNavDrawer.isDrawerOpen(GravityCompat.START)){
//            mNavDrawer.closeDrawer(GravityCompat.START);
//        }
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navbarselections, menu);
        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId())
//        {
//            case R.id.settings:
//                startActivity(new Intent(this, SettingsActivity.class));
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    
    @Override
    protected void onResume() {
        super.onResume();
        final Intent scannerIntent = new Intent(this, ScannerActivity.class);

        slide_up_button.setOnClickListener(r -> startActivity(scannerIntent));
    }
}
