/********************************************************************
 CSCI 428 - Final Assignment - Spring 2020
 Programmer(s): Jason Zochowski
 Z-ID: Z1836771
 Section: 1
 Due Date & Time: 5/4/2020 11:59PM
 Purpose: This app is a study app that uses a database of Kanji (Japanese symbols), a bottom navigation bar.
 Each fragment in the navigation bar displays the Kanji in a different way. The first one, shows all Kanji symbols, and
 lesson number, the second is just the lesson number, and the third is favorite Kanji with it's lesson number only.
 The user can select as many Kanji/Lessons as they want and can select between flashcards, or quiz. The user can also
 randomize the selected items, or study the definition instead of the Kanji. After the user selects the study button,
 it loads a splash screen to download and parse through JSON data from kanjiapi.dev API. This app, uses all of the selected
 Kanji, and for each one, adds the definition from the API into a definition array list. This app also uses the lesson order
 of the Genki text book for studying Japanese.
 *********************************************************************/
package edu.niu.students.z1836771.kanjiflashcards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {

    //bottom navigation bar
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //find bottomNavigationView by id and set listener to navListener
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavigationBar_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        //start InfoFragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new InfoFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.nav_info);
    }

    //start selected fragment on bottomNavigationView select
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch(item.getItemId())
                    {
                        //selected item
                        case R.id.nav_kanji:
                            selectedFragment = new KanjiFragment();
                            break;
                        case R.id.nav_lesson:
                            selectedFragment = new LessonFragment();
                            break;
                        case R.id.nav_favorites:
                            selectedFragment = new FavoritesFragment();
                            break;
                        case R.id.nav_website:
                            selectedFragment = new WebsiteFragment();
                            break;
                        case R.id.nav_info:
                            selectedFragment = new InfoFragment();
                            break;
                    }
                    //start selected fragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };
}
