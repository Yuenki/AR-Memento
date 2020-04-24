package com.example.ar_memento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnNext;

    int position = 0;
    Button btnGetStarted;

    Animation btnAnim;


    protected void OnCreate(Bundle savedInstanceState) {
        if (restorePrefData()) {

            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
            finish();

        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_getstarted);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);

        List<ScreenItem> mlist = new ArrayList<>();
        mlist.add(new ScreenItem("Memento Welcome", "An App for note taking with an augmented reality twist", R.drawable.img1));
        mlist.add(new ScreenItem("Personalized Code", "Allowing you to be able to scan and register your own symbols or drawing to bring out your object and notes", R.drawable.img3));
        mlist.add(new ScreenItem("Share your Work", "Share your notes and object with friends using a personalized ar recognizable image", R.drawable.img2));
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mlist);
        screenPager.setAdapter(introViewPagerAdapter);

        tabIndicator.setupWithViewPager(screenPager);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = screenPager.getCurrentItem();
                if (position < mlist.size()){

                        position++;
                        screenPager.setCurrentItem(position);
                }
                if (position == mlist.size()-1){

                    loadLastScreen();

                }

            }
        });



        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mlist.size() - 1) {
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);

                savePrefsData();
                finish();




            }
        });

    }
    private boolean restorePrefData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myprefs", MODE_PRIVATE);
        Boolean isIntroActivityOpenedBefore = pref.getBoolean("isTutOpened", false);
        return isIntroActivityOpenedBefore;

    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myprefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isTutOpened", true);
        editor.commit();
    }

    private void loadLastScreen() {

        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);

        btnGetStarted.setAnimation(btnAnim);
    }

}
