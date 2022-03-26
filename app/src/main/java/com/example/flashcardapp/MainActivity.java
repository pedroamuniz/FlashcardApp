package com.example.flashcardapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    final boolean[] isShowingAnswers = {false};
    boolean wrongAnswers = false;
    FlashcardDatabase flashcardDatabase;
    List<Flashcard> allFlashcards;
    int currentCardDisplayedIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flashcardDatabase = new FlashcardDatabase(getApplicationContext());
        initFlashcard();

        TextView flashcardQuestion = findViewById(R.id.flashcard_question);
        TextView flashcardAnswer = findViewById(R.id.flashcard_answer);

        View.OnClickListener flipFlashcard = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int questionVisibility = flashcardQuestion.getVisibility();
                int answerVisibility = flashcardAnswer.getVisibility();

                flashcardQuestion.setVisibility(answerVisibility);
                flashcardAnswer.setVisibility(questionVisibility);

            }
        };
        flashcardAnswer.setOnClickListener(flipFlashcard);
        flashcardQuestion.setOnClickListener(flipFlashcard);



        findViewById(R.id.toggle_choices_visibility).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView possibleAnswer1 = findViewById(R.id.possible_answer1);
                TextView possibleAnswer2 = findViewById(R.id.possible_answer2);
                TextView possibleAnswer3 = findViewById(R.id.possible_answer3);
                possibleAnswer1.setVisibility(4 - possibleAnswer1.getVisibility());
                possibleAnswer2.setVisibility(4 - possibleAnswer2.getVisibility());
                possibleAnswer3.setVisibility(4 - possibleAnswer3.getVisibility());
                if (isShowingAnswers[0])
                    ((ImageView)view).setImageResource(R.drawable.icon_show);
                else ((ImageView)view).setImageResource(R.drawable.icon_hide);
                isShowingAnswers[0] = !isShowingAnswers[0];
            }
        });
        findViewById(R.id.reset_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetActivity();
            }
        });

        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , AddCardActivity.class);
                MainActivity.this.startActivityForResult(intent, 200);

            }
        });
        findViewById(R.id.edit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , AddCardActivity.class);
                Flashcard currentFlashcard = allFlashcards.get(currentCardDisplayedIndex);
                intent.putExtra("questionValue" , currentFlashcard.getQuestion());
                intent.putExtra("answerValue" , currentFlashcard.getAnswer());
                intent.putExtra("wrongAnswerValue1" , currentFlashcard.getWrongAnswer1());
                intent.putExtra("wrongAnswerValue2" , currentFlashcard.getWrongAnswer2());
                MainActivity.this.startActivityForResult(intent, 100);

            }
        });

        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCardDisplayedIndex++;
//                setRandomQuestion();
                ResetActivity();
            }
        });
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCardDisplayedIndex--;
//                setRandomQuestion();
                ResetActivity();
            }
        });
        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashcardDatabase.deleteCard(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                allFlashcards.remove(currentCardDisplayedIndex);

                if (currentCardDisplayedIndex >= allFlashcards.size()) currentCardDisplayedIndex = allFlashcards.size()-1;
//                setRandomQuestion();
                ResetActivity();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            Snackbar.make(findViewById(R.id.flashcard_question),
                    "Card successfully created",
                    Snackbar.LENGTH_SHORT)
                    .show();
            String questionValue = data.getExtras().getString("questionValue");
            String answerValue = data.getExtras().getString("answerValue");
            String wrongAnswer1 = data.getExtras().getString("wrongAnswer1");
            String wrongAnswer2 = data.getExtras().getString("wrongAnswer2");

            Flashcard newFlashcard = new Flashcard(questionValue, answerValue , wrongAnswer1 , wrongAnswer2);
            if (requestCode == 200) {
                flashcardDatabase.insertCard(newFlashcard);
                allFlashcards.add(newFlashcard);
            }
            else if (requestCode == 100){
                Flashcard cardEdited = allFlashcards.get(currentCardDisplayedIndex);
                cardEdited.setQuestion(questionValue);
                cardEdited.setAnswer(answerValue);
                cardEdited.setWrongAnswer1(wrongAnswer1);
                cardEdited.setWrongAnswer2(wrongAnswer2);
                flashcardDatabase.updateCard(cardEdited);
                allFlashcards.set(currentCardDisplayedIndex, newFlashcard);
            }
            ResetActivity();

        }
    }
    public void setPossibleAnswerOrder (TextView [] possibleAnswers){
        Random random = new Random();
        int correctAnswerIndex = random.nextInt(3);

        Flashcard currentFlashCard = allFlashcards.get(currentCardDisplayedIndex);
        String [] list = {currentFlashCard.getAnswer() , currentFlashCard.getWrongAnswer1() , currentFlashCard.getWrongAnswer2()};
        int listIndex = 1; // the answer is at index 0
        for (int i = 0; i < 3; i++){
            if (i == correctAnswerIndex){
                possibleAnswers[i].setText(list[0]);
                possibleAnswers[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.setBackgroundColor(getColor(R.color.lime));
                    }
                });
            }
            else{
                possibleAnswers[i].setText(list[listIndex++]);
                possibleAnswers[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.setBackgroundColor(getColor(R.color.red));
                        possibleAnswers[correctAnswerIndex].setBackgroundColor(getColor(R.color.lime));
                    }
                });
            }
        }
    }


    // Set the flashcard to default
    // All possible answers (INVISIBLE by default)
    // Update the next and back button
    public void ResetActivity(){ // Reset to default
        TextView possibleAnswer1 = findViewById(R.id.possible_answer1);
        TextView possibleAnswer2 = findViewById(R.id.possible_answer2);
        TextView possibleAnswer3 = findViewById(R.id.possible_answer3);
        possibleAnswer1.setVisibility(View.INVISIBLE);
        possibleAnswer2.setVisibility(View.INVISIBLE);
        possibleAnswer3.setVisibility(View.INVISIBLE);
        possibleAnswer1.setBackground(getDrawable(R.drawable.possible_answer_background));
        possibleAnswer2.setBackground(getDrawable(R.drawable.possible_answer_background));
        possibleAnswer3.setBackground(getDrawable(R.drawable.possible_answer_background));

        findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
        findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.flashcard_question)).setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
        ((TextView)findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());


        ((ImageView)findViewById(R.id.toggle_choices_visibility)).setImageResource(R.drawable.icon_show);
        if (allFlashcards.get(currentCardDisplayedIndex).getWrongAnswer1().isEmpty() ||
                allFlashcards.get(currentCardDisplayedIndex).getWrongAnswer2().isEmpty()) wrongAnswers = false;
        else wrongAnswers = true;
        if (wrongAnswers){
            TextView [] possibleAnswers = {possibleAnswer1 , possibleAnswer2 , possibleAnswer3};
            setPossibleAnswerOrder(possibleAnswers);
        }
        ((ImageView)findViewById(R.id.toggle_choices_visibility)).setVisibility(wrongAnswers ? View.VISIBLE : View.INVISIBLE);
        isShowingAnswers[0] = false;

        updateNextAndBackBottom();
        ((ImageView)findViewById(R.id.delete_button)).setVisibility((allFlashcards.size() <= 1) ? View.INVISIBLE : View.VISIBLE);
    }

    public void setRandomQuestion (){
        Random random = new Random();
        int temp = random.nextInt(allFlashcards.size());
        if (temp == currentCardDisplayedIndex) // in case the random question is the same
            temp = (temp + 1) % allFlashcards.size();
        currentCardDisplayedIndex = temp;
    }
    public void updateNextAndBackBottom(){
        ((ImageView)findViewById(R.id.next_button)).setVisibility(currentCardDisplayedIndex < allFlashcards.size()-1 ? View.VISIBLE : View.INVISIBLE);
        ((ImageView)findViewById(R.id.back_button)).setVisibility(currentCardDisplayedIndex > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    public void initFlashcard(){
        flashcardDatabase.initFirstCard();
        allFlashcards = flashcardDatabase.getAllCards();

        ResetActivity();

    }

}