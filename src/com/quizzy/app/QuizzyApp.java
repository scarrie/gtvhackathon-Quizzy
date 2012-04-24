package com.quizzy.app;

import java.util.ArrayList;

import android.app.Application;

public class QuizzyApp extends Application {

	private static ArrayList<String> players = new ArrayList<String>();
	private static ArrayList<Integer> playerScore = new ArrayList<Integer>();
	private int playerCount = 2;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		for(int i=0; i<playerCount; i++) {
			players.add(i, "");
			playerScore.add(i, 0);
		}
	}

	public static void savePlayerName(int PlayerIndex, String name) {
		players.add(PlayerIndex, name);
	}
	
	public static void addPlayerScore(int playerIndex, int score) {
		int currentScore = playerScore.get(playerIndex);
		playerScore.set(playerIndex, currentScore + score);
	}

	public static int getScore(int playerIndex) {
		return playerScore.get(playerIndex);
	}

	public static String getPlayerName(int playerIndex) {
		return players.get(playerIndex);
	}

	public static void setScore(int i, int score) {
		playerScore.set(i, score);
	}
}
