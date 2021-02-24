package edu.niu.students.z1836771.kanjiflashcards;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WebsiteFragment extends Fragment {

    //global variables
    Button website1Button, website2Button, website3Button, website4Button, website5Button;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_website, container, false);
        getActivity().setTitle("Select Website");

        //set each view by id
        website1Button = view.findViewById(R.id.websiteButton1_id);
        website2Button = view.findViewById(R.id.websiteButton2_id);
        website3Button = view.findViewById(R.id.websiteButton3_id);
        website4Button = view.findViewById(R.id.websiteButton4_id);
        website5Button = view.findViewById(R.id.websiteButton5_id);

        //if first button is selected, open Genki-Online Kanji list website
        website1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://genki.japantimes.co.jp/self/genki-kanji-list-linked-to-wwkanji"));
                getActivity().startActivity(intent);
            }
        });

        //if second button is selected, open Genki-Online Home website
        website2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://genki.japantimes.co.jp/"));
                getActivity().startActivity(intent);
            }
        });

        //if third button is selected, open Jisho website
        website3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://jisho.org/"));
                getActivity().startActivity(intent);
            }
        });

        //if fourth button is selected, open Japanese Ammo with Misa YouTube channel website
        website4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/channel/UCBSyd8tXJoEJKIXfrwkPdbA"));
                getActivity().startActivity(intent);
            }
        });

        //if fifth button is selected, open JapanesePod101 YouTube channel website
        website5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/channel/UC0ox9NuTHYeRys63yZpBFuA"));
                getActivity().startActivity(intent);
            }
        });

        return view;
    }
}
