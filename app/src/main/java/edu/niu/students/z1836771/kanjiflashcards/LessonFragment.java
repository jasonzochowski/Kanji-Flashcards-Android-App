package edu.niu.students.z1836771.kanjiflashcards;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class LessonFragment extends Fragment {

    //global variables
    KanjiDatabase db;

    ArrayList<Integer> selectedItems = new ArrayList<Integer>();
    ArrayList<Integer> selectedKanji = new ArrayList<Integer>();
    ArrayList<String> kanjiArrayList = new ArrayList<String>();
    ArrayList<String> allKanjiArrayList = new ArrayList<String>();
    ArrayList<String> definitionArrayList = new ArrayList<String>();
    ArrayList<String> lessonArrayList = new ArrayList<String>();

    CheckBox randomCheckBox, startOnDefCheckBox, selectAllCheckbox;
    Button studyButton;
    Spinner selectTypeSpinner;
    ListView listView;

    int asyncTaskCounter = 0;
    int totalTasks = 1;
    int startOnDef = 0;
    boolean containsRepetitionKanji = false;
    String selectedType;

    Intent intent;

    String[] spinnerItems = {"Flashcards", "Quiz"}; //spinner text

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson, container, false);

        getActivity().setTitle("Select Lesson");

        db = new KanjiDatabase(getContext());
        allKanjiArrayList = db.retrieveKanji();

        lessonArrayList = db.retrieveChapters(); //retrieve chapters for list item arraylist

        selectedItems.clear();
        selectedKanji.clear();

        //find each view by id
        randomCheckBox = view.findViewById(R.id.lessonrandomcheckbox_id);
        startOnDefCheckBox = view.findViewById(R.id.defCheckBox_id);
        studyButton = view.findViewById(R.id.lessonstudybutton_id);
        listView = view.findViewById(R.id.lessonlistview_id);
        selectTypeSpinner = view.findViewById(R.id.selectTypeSpinner_id);
        selectAllCheckbox = view.findViewById(R.id.selectAllCheckBox);

        //set listview's arrayadapter and choice mode
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.rowlayout, R.id.check_id, lessonArrayList);
        listView.setAdapter(adapter);

        //set spinner's arrayadapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerItems);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectTypeSpinner.setAdapter(spinnerArrayAdapter);


        //if listview is selected, add/remove from selectedItems
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int selectedItem = i;//((TextView)view).getText().toString();
                if (selectedItems.contains(selectedItem))
                {
                    selectedItems.remove(Integer.valueOf(selectedItem));
                }
                else
                {
                    selectedItems.add(selectedItem);
                }
            }
        });

        //select all items
        selectAllCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectAllCheckbox.isChecked()) //check all
                {
                    selectedItems.clear();
                    for (int i = 0; i < lessonArrayList.size(); i++) {
                        listView.setItemChecked(i, true);
                        selectedItems.add(i);
                    }
                }
                else //uncheck all
                {
                    selectedItems.clear();
                    for (int i = 0; i < lessonArrayList.size(); i++) {
                        listView.setItemChecked(i, false);
                    }
                }
            }
        });

        //if listview is long clicked, show alert message, and if yes, add/remove from favorites
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                final int lessonNumber = i+3; //starts at lesson 3
                if (!db.isFavoriteChapter(lessonNumber)) { //lesson is not favorite
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to add lesson " + lessonNumber + " to favorites?"); //alert message
                    //if yes was selected
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //add all of lesson's kanji to favorites and refresh fragment
                            db.updateFavoritesLesson(lessonNumber, 1);
                            dialogInterface.dismiss();
                            Toast.makeText(getContext(), "Added lesson " + lessonNumber + " to favorites.", Toast.LENGTH_LONG).show();
                        }
                    });
                    //if no was selected
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    //create and show alert
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else //lesson is favorite
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to remove lesson " + lessonNumber + " from favorites?"); //alert message
                    //if yes was selected
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //delete all favorite kanji from lesson and refresh fragment
                            db.updateFavoritesLesson(lessonNumber, 0);
                            dialogInterface.dismiss();
                            Toast.makeText(getContext(), "Removed lesson " + lessonNumber + " from favorites.", Toast.LENGTH_LONG).show();
                        }
                    });
                    //if no was selected
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    //create and show alert
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                return false;
            }
        });

        //if study button is selected, get selected spinner, checkboxes, and selectedItems and start either FlashCards or Quiz
        studyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make sure selectedKanji is clear before adding to it
                HashSet<Integer> hashSet = new HashSet<Integer>();
                hashSet.addAll(selectedItems);
                selectedItems.clear();
                selectedItems.addAll(hashSet);
                selectedKanji.clear();

                if (selectedItems.isEmpty()) { //no lesson selected
                    Toast.makeText(getContext(), "Please select the Lesson", Toast.LENGTH_SHORT).show();
                } else {

                    for (int i = 0; i < selectedItems.size(); i++) //go through selected lessons and add each lesson's kanji's id's to selectedKanji arraylist
                    {
                        ArrayList<Integer> chapter = db.retrieveChapter(selectedItems.get(i)+3);
                        for (int j = 0; j < chapter.size(); j++)
                        {
                            selectedKanji.add(chapter.get(j));
                        }
                    }

                    if (randomCheckBox.isChecked()) { //randomize selected kanji
                        Collections.shuffle(selectedKanji);
                    } else {
                        Collections.sort(selectedKanji);
                    }
                    if (startOnDefCheckBox.isChecked())
                        startOnDef = 1;
                    else
                        startOnDef = 0;

                    selectedType = selectTypeSpinner.getSelectedItem().toString();
                    if (selectedType == "Flashcards") //start FlashCards
                        intent = new Intent(getContext(), FlashCards.class);
                    else //start Quiz
                    {
                        intent = new Intent(getContext(), Quiz.class);
                        totalTasks = 4;
                    }

                    for (int i = 0; i < selectedKanji.size(); i++)
                        kanjiArrayList.add(allKanjiArrayList.get(selectedKanji.get(i)));

                    for (int i = 0; i < totalTasks; i++) {
                        String kanji = kanjiArrayList.get(i);
                        if (kanji.contains("々")) {
                            containsRepetitionKanji = true; //contains repetition kanji
                            asyncTaskCounter++;
                            if (asyncTaskCounter == totalTasks) {
                                if (selectedType == "Flashcards")
                                    definitionArrayList.add(kanjiArrayList.indexOf("々"), "Meaning:\nRepetition Kanji");
                                else if (selectedType == "Quiz")
                                    definitionArrayList.add(kanjiArrayList.indexOf("々"), "Meaning: Repetition Kanji");
                                Bundle extras = new Bundle();
                                extras.putStringArrayList("kanjiArrayList", kanjiArrayList);
                                extras.putStringArrayList("definitionArrayList", definitionArrayList);
                                extras.putInt("def", startOnDef);
                                intent.putExtras(extras);
                                startActivity(intent);
                            }
                        }
                        else {
                            //create URL with kanji symbol and execute asynctask
                            String stringURL = "https://kanjiapi.dev/v1/kanji/" + kanjiArrayList.get(i);
                            URL url = createURL(stringURL); //url
                            //if url is not null, call api
                            if (url != null) { //execute asynctask
                                LessonFragment.getKanji getkanji = new LessonFragment.getKanji();
                                getkanji.execute(url);
                            }
                        }
                }
            }
        }
    });

        return view;
}


    //create url
    private URL createURL(String stringurl)
    {
        try
        {
            return new URL(stringurl);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

//parse through JSON data
public class getKanji extends AsyncTask<URL, Void, JSONObject>
{
    @Override //build JSONObject
    protected JSONObject doInBackground(URL...params)
    {
        HttpURLConnection connection = null;
        try //open connection
        {
            connection = (HttpURLConnection) params[0].openConnection();
            int response = connection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) //connection was successful
            {
                StringBuilder builder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                ))
                {
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        builder.append(line); //build line used in jsonobject
                    }
                }
                catch (IOException e) //failed to connect
                {
                    Toast.makeText(getContext(),
                            "Unable to connect to kanjiapi.dev", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                return new JSONObject(builder.toString()); //return JSONObject
            }
            else //failed to connect
            {
                Toast.makeText(getContext(),
                        "Unable to connect to kanjiapi.dev", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e) //failed to connect
        {
            Toast.makeText(getContext(),
                    "Unable to connect to kanjiapi.dev", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        finally {
            connection.disconnect(); //disconnect from api
        }
        return null;
    }
    @Override
    protected void onPostExecute(JSONObject kanji)
    {
        convertJSONtoArrayList(kanji);
    }
}

    //convert JSON to an ArrayList
    private void convertJSONtoArrayList(JSONObject kanji)
    {
        try
        {
            //build string for definitionArrayList
            JSONArray meanings = kanji.getJSONArray("meanings");
            JSONArray kunreadings = kanji.getJSONArray("kun_readings");
            JSONArray onreadings = kanji.getJSONArray("on_readings");
            String def = "";
            String selectedSpinner = selectTypeSpinner.getSelectedItem().toString();
            if (selectedSpinner == "Flashcards") {
                def += "Meanings:\n";
                for (int k = 0; k < meanings.length(); k++) {
                    def += meanings.get(k).toString() + "\n";
                }
                def += "\nKun Readings:\n";
                for (int k = 0; k < kunreadings.length(); k++) {
                    def += kunreadings.get(k).toString() + "\n";
                }
                def += "\nOn Readings:\n";
                for (int k = 0; k < onreadings.length(); k++) {
                    def += onreadings.get(k).toString() + "\n";
                }
            }
            else if (selectedSpinner == "Quiz")
            {
                def = "Meanings: ";
                for (int k = 0; k < meanings.length(); k++) {
                    def += meanings.get(k).toString() + ", ";
                }
                def = def.replaceAll(", $", "\nKun Readings:");
                //def += "Kun Readings: ";
                for (int k = 0; k < kunreadings.length(); k++) {
                    def += kunreadings.get(k).toString() + ", ";
                }
                def = def.replaceAll(", $", "\nOn Readings: ");
                //def += "On Readings: ";
                for (int k = 0; k < onreadings.length(); k++) {
                    def += onreadings.get(k).toString() + ", ";
                }
                def = def.replaceAll(", $", "");
            }
            //insert each item into definitionArrayList
            definitionArrayList.add(def);
            asyncTaskCounter++;
            if (asyncTaskCounter == totalTasks) { //start activity (all tasks completed)
                if (containsRepetitionKanji) //add repetition kanji to list after tasks are completed
                {
                    if (selectedType == "Flashcards")
                        definitionArrayList.add(kanjiArrayList.indexOf("々"), "Meaning:\nRepetition Kanji");
                    else if (selectedType == "Quiz")
                        definitionArrayList.add(kanjiArrayList.indexOf("々"), "Meaning: Repetition Kanji");
                }
                Bundle extras = new Bundle();
                extras.putStringArrayList("kanjiArrayList", kanjiArrayList);
                extras.putStringArrayList("definitionArrayList", definitionArrayList);
                extras.putInt("def", startOnDef);
                intent.putExtras(extras);
                startActivity(intent);
            }
        }
        catch (Exception e) //failed
        {
            e.printStackTrace();
        }
    }
}