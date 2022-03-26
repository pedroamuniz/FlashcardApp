package com.example.flashcardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        String question = getIntent().getStringExtra("questionValue");
        String answer = getIntent().getStringExtra("answerValue");
        String wrongAnswer1 = getIntent().getStringExtra("wrongAnswerValue1");
        String wrongAnswer2 = getIntent().getStringExtra("wrongAnswerValue2");
        if (answer != null && question != null){
            ((EditText)findViewById(R.id.question_editText)).setText(question);
            ((EditText)findViewById(R.id.answer_editText)).setText(answer);
            ((EditText)findViewById(R.id.wrongAnswer1_editText)).setText(wrongAnswer1);
            ((EditText)findViewById(R.id.wrongAnswer2_editText)).setText(wrongAnswer2);
        }

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = ((EditText)findViewById(R.id.question_editText)).getText().toString();
                String answer = ((EditText)findViewById(R.id.answer_editText)).getText().toString();
                String wrongAnswer1 = ((EditText)findViewById(R.id.wrongAnswer1_editText)).getText().toString();
                String wrongAnswer2 = ((EditText)findViewById(R.id.wrongAnswer2_editText)).getText().toString();
                if (question.isEmpty() || answer.isEmpty()){
                    Toast.makeText(getApplicationContext() , "Must enter both question and answer!" , Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent data = new Intent();
                data.putExtra("questionValue" , question);
                data.putExtra("answerValue" , answer);
                data.putExtra("wrongAnswer1" , wrongAnswer1);
                data.putExtra("wrongAnswer2" , wrongAnswer2);
                setResult(RESULT_OK , data);
                finish();
            }
        });
    }
}