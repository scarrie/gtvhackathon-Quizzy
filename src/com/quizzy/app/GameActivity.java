package com.quizzy.app;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.quizzy.model.Question;
import com.quizzy.model.QuestionList;

public class GameActivity extends Activity implements AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnCompletionListener,
MediaPlayer.OnErrorListener {

	private static final String TAG = "Quizzy";
	private static final long MOVIE_LENGTH_MILLI = 5 * 1000;
	private VideoView videoView;
	private QuestionList questionList;
	private TextView txtQuestion;
	private Button btnAnswer1;
	private Button btnAnswer2;
	private Button btnNext;
	
	private TextView txtPlayer1Score;
	private TextView txtPlayer1Name;
	
	private TextView txtTimer;
	
	private Question mCurrentQuestion;
	private int currentQuestionIndex;
	private int totalQuestionCount;
	
	private VideoTimer videoTimer;
	private AnswerWindowTimerHandler answerWindowTimer;
	private int currentPlayerIndex;
	private TextView txtPlayer2Score;
	private TextView txtPlayer2Name;
	private TextView txtCorrectAnswer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_game);
		videoView = (VideoView) findViewById(R.id.videoView1);
		videoView.setOnCompletionListener(this);
		videoView.setOnErrorListener(this);

		txtQuestion = (TextView) findViewById(R.id.txtQuestion);
		btnAnswer1 = (Button) findViewById(R.id.btnAnswer1);
		btnAnswer2 = (Button) findViewById(R.id.btnAnswer2);
		btnNext = (Button) findViewById(R.id.btnNext);
		
		btnAnswer1.setOnClickListener(answerClickListener);
		btnAnswer2.setOnClickListener(answerClickListener);
		btnNext.setOnClickListener(nextButtonClickListener);
		
		txtPlayer1Score = (TextView) findViewById(R.id.txtPlayer1Score);
		txtPlayer1Name = (TextView) findViewById(R.id.txtPlayer1Name);
		txtPlayer2Score = (TextView) findViewById(R.id.txtPlayer2Score);
		txtPlayer2Name = (TextView) findViewById(R.id.txtPlayer2Name);
		txtTimer = (TextView) findViewById(R.id.txtTimer);
		txtCorrectAnswer = (TextView) findViewById(R.id.txtCorrectAnswer);
		
		txtPlayer1Name.setText(QuizzyApp.getPlayerName(0));
		txtPlayer2Name.setText(QuizzyApp.getPlayerName(1));
		
		currentPlayerIndex = 0;
		currentQuestionIndex = 0;
		loadData();
		
		answerWindowTimer = new AnswerWindowTimerHandler();
		videoTimer = new VideoTimer();

		playVideoForCurrentQuestion(currentQuestionIndex);
	}
	
	private OnClickListener answerClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			boolean isCorrectlyAnswered = false;
			
			if(v.getId() == R.id.btnAnswer1) {
				if(mCurrentQuestion.getCorrectanswer() == 0) {
					isCorrectlyAnswered = true;
				}
			}
			else {
				if(mCurrentQuestion.getCorrectanswer() == 1) {
					isCorrectlyAnswered = true;
				}
			}

			//update score
			if(isCorrectlyAnswered)
				QuizzyApp.addPlayerScore(currentPlayerIndex, 10);
			
			//if last question then goto summary activity
			if(currentQuestionIndex == totalQuestionCount-1) {
				startActivity(new Intent(GameActivity.this, GameSummaryActivity.class));
				return;
			}
			
			//move to player 2
			if(currentPlayerIndex == 0) {
				currentPlayerIndex = 1;
				setPlayer1(false);
				setPlayer2(true);
				answerWindowTimer.reset();
				
				findViewById(R.id.layoutPlayer1).setBackgroundResource(R.drawable.rounded_border);
				findViewById(R.id.layoutPlayer2).setBackgroundResource(R.drawable.rounded_border_highlight);
			}
			else {
				prepapreNextQuestion();
				
				//show current score
				txtPlayer1Score.setText(QuizzyApp.getScore(0) + "");
				txtPlayer2Score.setText(QuizzyApp.getScore(1) + "");
				
				//show correct answer
				Question q = questionList.getQuestion(currentQuestionIndex);
				txtCorrectAnswer.setText("Correct Answer: " + (q.getCorrectanswer() == 0 ? q.getAnswer1() : q.getAnswer2()));

				currentQuestionIndex++;
			}
		}
	};
	
	private View.OnClickListener nextButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			btnNext.setEnabled(false);
			btnAnswer1.setEnabled(true);
			btnAnswer2.setEnabled(true);
			
			txtQuestion.setText("");
			btnAnswer1.setText("Answer 1");
			btnAnswer2.setText("Answer 2");
			txtCorrectAnswer.setText("");
			
			findViewById(R.id.layoutPlayer1).setBackgroundResource(R.drawable.rounded_border);
			findViewById(R.id.layoutPlayer2).setBackgroundResource(R.drawable.rounded_border);
			
			playVideoForCurrentQuestion(currentQuestionIndex);
		}
	};
	
	private void renderQuestion(int index) {
		mCurrentQuestion = questionList.getQuestion(index);
		
		findViewById(R.id.layoutPlayer1).setBackgroundResource(R.drawable.rounded_border_highlight);
		btnAnswer1.setVisibility(View.VISIBLE);
		btnAnswer2.setVisibility(View.VISIBLE);
		btnAnswer1.setEnabled(true);
		btnAnswer2.setEnabled(true);
		
		
		txtQuestion.setText(mCurrentQuestion.getQuestion());
		btnAnswer1.setText(mCurrentQuestion.getAnswer1());
		btnAnswer2.setText(mCurrentQuestion.getAnswer2());
	}

	private void playVideoForCurrentQuestion(int index) {
		Question q = questionList.getQuestion(currentQuestionIndex);
		videoView.setVideoPath(q.getUrl());
		videoView.requestFocus();

		videoView.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				Thread videoThread = new Thread(new Runnable() {
					public void run() {

						videoView.start();
						
						answerWindowTimer.reset();
						videoTimer.sendEmptyMessageDelayed(0, MOVIE_LENGTH_MILLI);
						
					}
				});
				videoThread.start();
			}
		});	
	}
	
	private void loadData() {
		Gson gson = new Gson();
		questionList = null;
		try {
			questionList = gson.fromJson(Util.getStringFromStream(getAssets().open("data.json")), QuestionList.class);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		totalQuestionCount = questionList.size();
	}
	
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		
	}

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch(focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.i(TAG, "AF Gain");
                if (videoView != null)
                    videoView.resume();
                break;


            case AudioManager.AUDIOFOCUS_LOSS:
                Log.i(TAG, "AF Loss");
                if (videoView != null)
                    videoView.stopPlayback();
                	videoView = null;
                	this.finish(); // Let's move on.
                break;


            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.i(TAG, "AF Transient");
                if (videoView != null)
                    videoView.pause();
                break;
        }
    }

    private class VideoTimer extends Handler {

		@Override
		public void handleMessage(Message msg) {
			renderQuestion(currentQuestionIndex);
			
			answerWindowTimer.sendEmptyMessage(2);
		}
    }
    
    private final static int ANSWER_WINDOW_LENGTH = 10 * 1000;
    private int currentAnswerWindowLength = 0;
    private class AnswerWindowTimerHandler extends Handler {
    	
		@Override
		public void handleMessage(Message msg) {
			if(currentAnswerWindowLength <= ANSWER_WINDOW_LENGTH) {
				txtTimer.setText(((ANSWER_WINDOW_LENGTH - currentAnswerWindowLength)/1000) + " sec");
				answerWindowTimer.sendEmptyMessageDelayed(0, 1000);
			}
			else {
				if(currentPlayerIndex == 0) {
					currentPlayerIndex = 1;
					reset();
					setPlayer1(false);
					setPlayer2(true);
				}
				else {
					setPlayer2(false);
					prepapreNextQuestion();
//					currentQuestionIndex++;
				}
			}

			currentAnswerWindowLength += 1000;
		}

		public void reset() {
			currentAnswerWindowLength = 0;
			answerWindowTimer.sendEmptyMessageDelayed(0, 3000);
			
			
			//delay for 2nd player
		}
    }

    private void setPlayer1(boolean flag) {
    	
	}

	public void prepapreNextQuestion() {
		btnNext.setEnabled(true);
		btnAnswer1.setEnabled(false);
		btnAnswer2.setEnabled(false);
	}

	public void setPlayer2(boolean flag) {
		findViewById(R.id.layoutPlayer1).setBackgroundResource(R.drawable.rounded_border);
		findViewById(R.id.layoutPlayer2).setBackgroundResource(R.drawable.rounded_border_highlight);
	}
}