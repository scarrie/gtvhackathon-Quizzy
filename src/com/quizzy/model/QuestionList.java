package com.quizzy.model;

import java.util.ArrayList;

public class QuestionList {
	@Override
	public String toString() {
		return "QuestionList [questions=" + questions + "]";
	}

	public ArrayList<Question> questions = new ArrayList<Question>();

	public ArrayList<Question> getQuestions() {
		return questions;
	}

	public Question getQuestion(int i) {
		return questions.get(i);
	}

	public int size() {
		return questions.size();
	}
	
}
