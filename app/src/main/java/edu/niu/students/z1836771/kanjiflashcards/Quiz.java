package edu.niu.students.z1836771.kanjiflashcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Collections;
import java.util.Random;

public class Quiz extends AppCompatActivity {

    //global variables
    KanjiDatabase db;

    ArrayList<String> questionArrayList = new ArrayList<String>();
    ArrayList<String> allAnswersArrayList = new ArrayList<String>();
    int[] selectedAnswers; //array of all selected answers
    int[] scoreArray; //array of 0 or 1 for false or true
    String[][] answersArray; //3 dimensional array for the 4 answers for each question number

    TextView questionTextView, answerTextView, totalScoreTextView, correctincorrectTextView;
    RadioGroup quizRadioGroup;
    RadioButton radioButton0, radioButton1, radioButton2, radioButton3;
    Button selectButton, backButton, nextButton, homeButton, endBackButton, startOverButton;
    int startOnDef = 0; //start on definition
    boolean waitForDownload = false; //used to make sure all required data is loaded before displaying

    Bundle extras;

    int index = 0; //current index

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        setTitle("Kanji Quiz");

        Quiz.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setAllIds(); //find each view by id

        db = new KanjiDatabase(this);

        extras = getIntent().getExtras();
        startOnDef = extras.getInt("def");

        setTextViews(); //set text and text size

        if (startOnDef == 0) { //initialize array sizes
            selectedAnswers = new int[questionArrayList.size()];
            scoreArray = new int[questionArrayList.size()];
            answersArray = new String[questionArrayList.size()][4]; //3d array
        }
        else {
            selectedAnswers = new int[allAnswersArrayList.size()];
            scoreArray = new int[allAnswersArrayList.size()];
            answersArray = new String[allAnswersArrayList.size()][4]; //3d array
        }

        for (int i = 0; i < selectedAnswers.length; i++) { //default initialization of selectedAnswers and scoreArray
            selectedAnswers[i] = -1;
            scoreArray[i] = 0;
        }

        displayQuestion(); //display current index's question/answers
        buttonOnClickListeners(); //onclicklisteners for buttons
    }

    //find all views by id
    public void setAllIds()
    {
        questionTextView = findViewById(R.id.questionTextView_id);
        answerTextView = findViewById(R.id.answersTitleTextView_id);
        quizRadioGroup = findViewById(R.id.quizRadioGroup_id);
        selectButton = findViewById(R.id.quizSelectButton_id);
        backButton = findViewById(R.id.quizBackButton_id);
        nextButton = findViewById(R.id.quizNextButton_id);
        radioButton0 = findViewById(R.id.radioButton0_id);
        radioButton1 = findViewById(R.id.radioButton1_id);
        radioButton2 = findViewById(R.id.radioButton2_id);
        radioButton3 = findViewById(R.id.radioButton3_id);
        correctincorrectTextView = findViewById(R.id.correctincorrectTextView_id);

        questionTextView.setMovementMethod(new ScrollingMovementMethod()); //make textview scrollable
    }

    //set question answer text view, textsize, and get data from previous activity
    public void setTextViews()
    {
        if (startOnDef == 0) //study kanji, set questionArrayList to kanji, and answer's to definition
        {
            questionArrayList = extras.getStringArrayList("kanjiArrayList");
            allAnswersArrayList = extras.getStringArrayList("definitionArrayList");
            questionTextView.setTextSize(75);
            answerTextView.setText("   Meaning:");
        }
        else //study definition, set questionArrayList to definition, and answer's to kanji
        {
            questionArrayList = extras.getStringArrayList("definitionArrayList");
            allAnswersArrayList = extras.getStringArrayList("kanjiArrayList");
            questionTextView.setTextSize(15);
            answerTextView.setText("   Kanji:");
        }
    }

    //all onclicklisteners for buttons
    public void buttonOnClickListeners()
    {
        //if selectButton is selected, see if answer is correct or not
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = quizRadioGroup.getCheckedRadioButtonId(); //selected answer
                if (selectedId != -1) {
                    RadioButton radioButton = findViewById(selectedId);
                    String selectedRadioButton = radioButton.getText().toString();
                    selectedAnswers[index] = quizRadioGroup.indexOfChild(radioButton); //set selectedAnswer
                    if (allAnswersArrayList.get(index) == selectedRadioButton) { //correct answer
                        scoreArray[index] = 1; //1 correct answer
                        Toast.makeText(Quiz.this, "Correct!", Toast.LENGTH_LONG).show();
                        correctincorrectTextView.setText("Correct!"); //set text to show this answer is correct
                        correctincorrectTextView.setTextColor(Color.GREEN);
                    }
                    else { //incorrect answer
                        scoreArray[index] = 0; //0 incorrect answer
                        Toast.makeText(Quiz.this, "False! the correct answer is: " + allAnswersArrayList.get(index), Toast.LENGTH_LONG).show();
                        correctincorrectTextView.setText("Incorrect!"); //set text to show this answer is incorrect
                        correctincorrectTextView.setTextColor(Color.RED);
                    }
                }
                else //no answer selected
                {
                    Toast.makeText(Quiz.this, "Please select an answer.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //if backButton is selected index-- and displayQuestion
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!waitForDownload) {
                    if (index != 0) {
                        index--;
                        displayQuestion();
                    }
                }
                else
                {
                    Toast.makeText(Quiz.this, "You pressed the button too fast", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //if nextButton is selected, index++ and display question
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!waitForDownload)
                {
                    if (index != questionArrayList.size()-1) {
                        index++;
                        displayQuestion();
                    }
                    else //reached end of quiz, set layout to activity_quiz_end
                    {
                        setContentView(R.layout.activity_quiz_end); //end quiz layout
                        //find views by id
                        totalScoreTextView = findViewById(R.id.totalScoreTextView_id);
                        homeButton = findViewById(R.id.endHomeButton_id);
                        endBackButton = findViewById(R.id.endBackButton_id);
                        startOverButton = findViewById(R.id.startOverButton_id);

                        //calculate total score
                        totalScoreTextView.setText(calculateTotalScore());

                        //if home button is selected, start MainActivity
                        homeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Quiz.this, MainActivity.class);
                            startActivity(intent);
                            }
                        });

                        //if back button is selected, load activity_quiz layout, set ids, text views, onclicklisteners, and displayQuestion
                        endBackButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setContentView(R.layout.activity_quiz);
                            setAllIds();
                            setTextViews();
                            buttonOnClickListeners();
                            displayQuestion();
                            }
                        });

                        //if start over button is selected, load activity_quiz layout, set everything, and start index at 0
                        startOverButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setContentView(R.layout.activity_quiz);
                            setAllIds();
                            setTextViews();
                            buttonOnClickListeners();
                            index = 0;
                            for (int i = 0; i < questionArrayList.size(); i++) //default initialization
                            {
                                selectedAnswers[i] = -1;
                                scoreArray[i] = 0;
                            }
                            displayQuestion();
                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(Quiz.this, "You pressed the button too fast", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //calculate total score
    public String calculateTotalScore()
    {
        double score = 0.0;
        for (int i = 0; i < questionArrayList.size(); i++) //loop through scoreArray and add 1 each time there is a correct answer
        {
            if (scoreArray[i] == 1)
                score++;
        }
        int scorePercent = (int)(score / questionArrayList.size() * 100); //percentage
        String totalScore = "Total Score:\n";
        totalScore += scorePercent + "%";
        return totalScore;
    }

    //build answers 3d array
    public void buildAnswersArray()
    {
        answersArray[index][0] = allAnswersArrayList.get(index); //correct answer
        ArrayList<Integer> randomNumbers = new ArrayList<Integer>(); //array of numbers 0 - size()
        for (int j = 0; j < allAnswersArrayList.size(); j++) {
            randomNumbers.add(j);
        }
        randomNumbers.remove(index); //remove correct answer
        Collections.shuffle(randomNumbers); //shuffle to make random
        for (int j = 1; j < 4; j++) //add 3 to answersArray[i][j]
            {
                int num = randomNumbers.get(j-1);
                answersArray[index][j] = allAnswersArrayList.get(num);
            }
        ArrayList<String> arrayList = new ArrayList<String>();
        for (int j = 0; j < 4; j++) //add answers to temp arraylist
            arrayList.add(answersArray[index][j]);
        Collections.shuffle(arrayList); //shuffle arraylist
        for (int j = 0; j < 4; j++) //set answers
            answersArray[index][j] = arrayList.get(j);
    }

    //display the current index's question, answers, and selected answer
    public void displayQuestion()
    {
        buildAnswersArray();
        //set question and answers
        questionTextView.setText(questionArrayList.get(index));
        radioButton0.setText(answersArray[index][0]);
        radioButton1.setText(answersArray[index][1]);
        radioButton2.setText(answersArray[index][2]);
        radioButton3.setText(answersArray[index][3]);

        quizRadioGroup.clearCheck();

        if (selectedAnswers[index] != -1) //set selected answer's radio button check if exists
        {
            if (selectedAnswers[index] == 1)
                radioButton0.setChecked(true);
            else if (selectedAnswers[index] == 3)
                radioButton1.setChecked(true);
            else if (selectedAnswers[index] == 5)
                radioButton2.setChecked(true);
            else if (selectedAnswers[index] == 7)
                radioButton3.setChecked(true);
        }
        //set Correct or Incorrect for selected answer
        if (selectedAnswers[index] != -1 && scoreArray[index] == 1)
        {
            correctincorrectTextView.setText("Correct!");
            correctincorrectTextView.setTextColor(Color.GREEN);
        }
        else if (selectedAnswers[index] != -1 && scoreArray[index] == 0)
        {
            correctincorrectTextView.setText("Incorrect!");
            correctincorrectTextView.setTextColor(Color.RED);
        }
        else //no selected answer
        {
            correctincorrectTextView.setText("");
        }

        if (startOnDef == 0) {
            if ((index + 1) != questionArrayList.size() && (index + 1) >= allAnswersArrayList.size()) { //need to download data
                loadNextKanji();
            }
        }
        else
        {
            if ((index + 1) != allAnswersArrayList.size() && (index + 1) >= questionArrayList.size()) { //need to download data
                loadNextKanjiDefinition();
            }
        }
    }

    //execute asynctask to download next kanji
    public void loadNextKanji()
    {
        waitForDownload = true;
        String kanji = questionArrayList.get(index+1);
        if (kanji.contains("々")) { //contains repetition kanji //fixfixfixfixfixfixfixfix
            allAnswersArrayList.add("Meaning:\nRepetition Kanji");
            waitForDownload = false;
        }
        else
        {
            //create URL with kanji symbol and execute asynctask
            String stringURL = "https://kanjiapi.dev/v1/kanji/" + questionArrayList.get(index+1);
            //System.out.println("TEST1: stringURL = " + stringURL);
            URL url = createURL(stringURL); //url
            //if url is not null, call api
            if (url != null) { //execute asynctask
                Quiz.getKanji getkanji = new Quiz.getKanji();
                getkanji.execute(url);
            }
        }
    }

    //executes asynctask to download the next kanji
    public void loadNextKanjiDefinition()
    {
        waitForDownload = true;
        String kanji = allAnswersArrayList.get(index+1);
        if (kanji.contains("々")) { //contains repetition kanji
            questionArrayList.add("Meaning:\nRepetition Kanji");
            waitForDownload = false;
        }
        else
        {
            //create URL with kanji symbol and execute asynctask
            String stringURL = "https://kanjiapi.dev/v1/kanji/" + allAnswersArrayList.get(index+1);
            //System.out.println("TEST1: stringURL = " + stringURL);
            URL url = createURL(stringURL); //url
            //if url is not null, call api
            if (url != null) { //execute asynctask
                Quiz.getKanji getkanji = new Quiz.getKanji();
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
            Intent intent = new Intent(Quiz.this, MainActivity.class);
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
                        Toast.makeText(Quiz.this,
                                "Unable to connect to kanjiapi.dev", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    return new JSONObject(builder.toString()); //return JSONObject
                }
                else //failed to connect
                {
                    Toast.makeText(Quiz.this,
                            "Unable to connect to kanjiapi.dev", Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e) //failed to connect
            {
                Toast.makeText(Quiz.this,
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
            String def = "Meanings: ";
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
            //insert each item into definitionArrayList
            if (startOnDef == 0)
                allAnswersArrayList.add(def);
            else
                questionArrayList.add(def);

            waitForDownload = false;
        }
        catch (Exception e) //failed
        {
            e.printStackTrace();
        }
    }
}