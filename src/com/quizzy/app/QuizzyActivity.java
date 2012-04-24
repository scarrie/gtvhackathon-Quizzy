package com.quizzy.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class QuizzyActivity extends Activity {
    /** Called when the activity is first created. */
	
	Button btnPlay;
	private EditText player1;
	private EditText player2;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        btnPlay = (Button) findViewById(R.id.btnPlay);
        player1 = (EditText) findViewById(R.id.player1);
        player2 = (EditText) findViewById(R.id.player2);

        player1.setText(QuizzyApp.getPlayerName(0));
        player2.setText(QuizzyApp.getPlayerName(1));
    }
    
    public void playButtonClickListener(View view) {
    	savePlayerName();
    	startGame();
    }

	private void startGame() {
		QuizzyApp.setScore(0, 0);
		QuizzyApp.setScore(1, 0);
		startActivity(new Intent(this, GameActivity.class));
	}

	private void savePlayerName() {
		QuizzyApp.savePlayerName(0, player1.getText().toString());
		QuizzyApp.savePlayerName(1, player2.getText().toString());
		
	}
}