package edu.niu.students.z1836771.kanjiflashcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FlashCards extends AppCompatActivity {

    //global variables
    KanjiDatabase db;
    ArrayList<String> definitionArrayList = new ArrayList<String>();
    ArrayList<String> kanjiArrayList = new ArrayList<String>();

    TextView flashCardTextView;
    Button flipButton, prevButton, nextButton;

    public int side = 0; //current side
    public int index = 0; //current index
    //public int asyncTaskCounter = 1;
    boolean waitForDownload = false;
    boolean allDownloadsCompleted = false;
    boolean useFirstBackButton = false; //don't allow user to use the previous button until downloads are completed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_cards);
        setTitle("Kanji Flashcards");

        FlashCards.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        db = new KanjiDatabase(this);

        //retrieve arraylists and side to start on from MainActivity
        Bundle extras = getIntent().getExtras();
        kanjiArrayList = extras.getStringArrayList("kanjiArrayList");
        definitionArrayList = extras.getStringArrayList("definitionArrayList");
        side = extras.getInt("def");

        //find views by id
        flashCardTextView = findViewById(R.id.flashcard_id);
        flipButton = findViewById(R.id.flipbutton_id);
        prevButton = findViewById(R.id.previousbutton_id);
        nextButton = findViewById(R.id.nextbutton_id);

        //make textview scrollable
        flashCardTextView.setMovementMethod(new ScrollingMovementMethod());

        if (side == 0) //set text size
            flashCardTextView.setTextSize(120);
        else
            flashCardTextView.setTextSize(25);
        //function displays the current index's kanji or definition
        displayFlashCards();

        //if flipButton is selected, change text, textsize, and set side to either 0 or 1, depending on the current side
        flipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (side == 0) { //definition side
                    flashCardTextView.setText(definitionArrayList.get(index));
                    flashCardTextView.setTextSize(25);
                    side = 1;
                } else if (side == 1) { //kanji side
                    flashCardTextView.setText(kanjiArrayList.get(index));
                    flashCardTextView.setTextSize(120);
                    side = 0;
                }
            }
        });

        //if prevButton is selected, index--, and displayFlashCards()
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index == 0) {
                    if (useFirstBackButton) { //if all downloads are done, can use previous button on first index
                        if (!waitForDownload) {
                            index--;
                            if (index == -1)
                                index = kanjiArrayList.size() - 1;
                            displayFlashCards();
                            //asyncTaskCounter = 0;
                        } else {
                            Toast.makeText(FlashCards.this, "You pressed the button too fast", Toast.LENGTH_SHORT).show();
                        }
                    } else //still downloading data
                        Toast.makeText(FlashCards.this, "Press the next button", Toast.LENGTH_SHORT).show();
                }
                else //can use previous button no matter what
                {
                    index--;
                    if (index == -1)
                        index = kanjiArrayList.size() - 1;
                    displayFlashCards();
                    //asyncTaskCounter = 0;
                }
            }
        });

        //if nextButton is selected index++ and displayFlashCards()
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!waitForDownload) {
                    index++;
                    if (index == kanjiArrayList.size())
                        index = 0;
                    displayFlashCards();
                    //asyncTaskCounter = 0;
                }
                else
                {
                    Toast.makeText(FlashCards.this, "You pressed the button too fast", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //set text to current side's kanji/definition
    public void displayFlashCards()
    {
        if (side == 0)
            flashCardTextView.setText(kanjiArrayList.get(index));
        else// if (side == 1)
            flashCardTextView.setText(definitionArrayList.get(index));
        //load next kanji
        if ((index+1) != kanjiArrayList.size() && (index+1) >= definitionArrayList.size()) {
            loadNextKanji();
        }
    }

    public void loadNextKanji()
    {
        waitForDownload = true;
        String kanji = kanjiArrayList.get(index+1);
        if (kanji.contains("々")) {
            definitionArrayList.add("Meaning:\nRepetition Kanji");
            waitForDownload = false;
            if (definitionArrayList.size() == kanjiArrayList.size()) {
                useFirstBackButton = true;
            }
            //asyncTaskCounter++;
        }
        else
        {
            //create URL with kanji symbol and execute asynctask
            String stringURL = "https://kanjiapi.dev/v1/kanji/" + kanjiArrayList.get(index+1);
            //System.out.println("TEST1: stringURL = " + stringURL);
            URL url = createURL(stringURL); //url
            //if url is not null, call api
            if (url != null) { //execute asynctask
                FlashCards.getKanji getkanji = new FlashCards.getKanji();
                getkanji.execute(url);
            }
        }
    }

    //if back key was pressed, start MainActivity.class
    @Override
    public boolean onKeyDown(int i, KeyEvent event)
    {
        if (i == KeyEvent.KEYCODE_BACK)
        {
            Intent intent = new Intent(FlashCards.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onKeyDown(i, event);
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
                        Toast.makeText(FlashCards.this,
                                "Unable to connect to kanjiapi.dev", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    return new JSONObject(builder.toString()); //return JSONObject
                }
                else //failed to connect
                {
                    Toast.makeText(FlashCards.this,
                            "Unable to connect to kanjiapi.dev", Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e) //failed to connect
            {
                Toast.makeText(FlashCards.this,
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
            //insert each item into definitionArrayList
            definitionArrayList.add(def);
            waitForDownload = false;
            if (definitionArrayList.size() == kanjiArrayList.size()) {
                useFirstBackButton = true;
            }
            //asyncTaskCounter++;
        }
        catch (Exception e) //failed
        {
            e.printStackTrace();
        }
    }

}


