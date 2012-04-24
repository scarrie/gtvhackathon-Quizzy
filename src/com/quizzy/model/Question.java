package com.quizzy.model;

public class Question {
	private String url;
	private int length;
	private String question;
	private String answer1;
	private String answer2;
	private int correctanswer;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer1() {
		return answer1;
	}
	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}
	public String getAnswer2() {
		return answer2;
	}
	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}
	public int getCorrectanswer() {
		return correctanswer;
	}
	public void setCorrectanswer(int correctanswer) {
		this.correctanswer = correctanswer;
	}
	@Override
	public String toString() {
		return "Question [url=" + url + ", length=" + length + ", question="
				+ question + ", answer1=" + answer1 + ", answer2=" + answer2
				+ ", correctanswer=" + correctanswer + "]";
	}
	
	
}
