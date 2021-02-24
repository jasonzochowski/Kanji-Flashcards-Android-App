package edu.niu.students.z1836771.kanjiflashcards;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class InfoFragment extends Fragment {

    //global variables
    TextView infoTextView;
    Button startButton;
    BottomNavigationView bottomNavigationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        getActivity().setTitle("Kanji Flashcards");

        //find views by id
        infoTextView = view.findViewById(R.id.infoTextView_id);
        startButton = view.findViewById(R.id.startFragmentButton_id);
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationBar_id);

        //make info text scrollable
        infoTextView.setMovementMethod(new ScrollingMovementMethod());
        infoTextView.setText("Welcome to the Kanji Flashcards app!\n" +
                "To begin, either select one of the bottom navigation buttons, or \n" +
                "click the \"start\" button below.\n\n" +
                "The first (home) button lists all of the Kanji symbols and their lesson number,\n" +
                "the second (list) button lists all of the lessons. The third (star) button lists all of the\n" +
                "favorites. In order to add a favorite, press and hold on any Kanji or Lesson\n" +
                "you want and then select \"yes\". The fourth (web) button shows other useful online\n" +
                "resources. The fifth (info) button shows information on the app.\n" +
                "Select all of the Kanji you want to study and select either flashcards, or quiz, " +
                "and start studying!\n\n" +
                "This app uses kanjiapi.dev for the Kanji definitions, and uses the lesson \n" +
                "order of the Genki text book to order the Kanji.\n\n" +
                "Online resources include:\n" +
                "https://kanjiapi.dev/\n" +
                "http://genki.japantimes.co.jp/self/genki-kanji-list-linked-to-wwkanji\n" +
                "http://genki.japantimes.co.jp/\n" +
                "https://jisho.org/\n" +
                "https://www.youtube.com/channel/UCBSyd8tXJoEJKIXfrwkPdbA\n" +
                "https://www.youtube.com/channel/UC0ox9NuTHYeRys63yZpBFuA");

        //start KanjiFragment
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomNavigationView.setSelectedItemId(R.id.nav_kanji);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new KanjiFragment()).commit();
            }
        });

        return view;
    }
}
