package com.quizzy.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class GameSummaryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);
		
		TextView txt = (TextView) findViewById(R.id.txtWinner);
		
		int score = QuizzyApp.getScore(0);
		int score2 = QuizzyApp.getScore(1);
		
		txt.setText("Winner is: " + (score > score2 ? QuizzyApp.getPlayerName(0) : QuizzyApp.getPlayerName(1)));
	}
	

}
