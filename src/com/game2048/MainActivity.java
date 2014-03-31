package com.game2048;

import com.game2048.listeners.GameListener;
import com.game2048.settings.SettingsManager;
import com.game2048.view.GameView;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.app.Activity;

public class MainActivity extends Activity {

	private static final int INTERVAL = 1000;
	
	private Handler mHandler = new Handler();
	private TextView mTimeTextView;
	private int mTimeElapsed = 0;
	private int mAllMoves = 0;
	private int mMoves = 0;
	
	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {

			MainActivity.this.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {

					MainActivity.this.tick();
				}
			});

			MainActivity.this.mHandler.postDelayed(this, INTERVAL);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_main);
		
		final SettingsManager settingsManager = new SettingsManager(this);
		final int bestScore = settingsManager.getBestScore();
		final int timesPlayed = settingsManager.getTimesPlayed();
		
		final GameView gameView = (GameView)this.findViewById(R.id.game_view);
		final TextView scoreView = (TextView)this.findViewById(R.id.score_text_view);
		final TextView bestView = (TextView)this.findViewById(R.id.best_text_view);
		final TextView allMovesView = (TextView)this.findViewById(R.id.all_moves_text_view);
		final TextView movesView = (TextView)this.findViewById(R.id.moves_text_view);
		TextView playedView = (TextView)this.findViewById(R.id.played_text_view);
		
		this.mTimeTextView = (TextView)this.findViewById(R.id.time_text_view);
		
		this.mAllMoves = settingsManager.getMoves();
		allMovesView.setText(String.valueOf(this.mAllMoves));
		
		gameView.setOnScoreListener(new GameListener() {

			@Override
			public void scoreUpdate(final int score) {

				scoreView.setText(String.valueOf(score));
				
				if (score > bestScore) {

					bestView.setText(String.valueOf(score));
					settingsManager.setBestScore(score);
				}
			}

			@Override
			public void gameOver() {
				
				MainActivity.this.stopTimer();
			}

			@Override
			public void move() {

				movesView.setText(String.valueOf(++MainActivity.this.mMoves));
				allMovesView.setText(String.valueOf(++MainActivity.this.mAllMoves));
				
				settingsManager.increaseMoves();
			}
		});
		
		bestView.setText(String.valueOf(bestScore));
		playedView.setText(String.valueOf(timesPlayed));
		
		settingsManager.increaseTimesPlayed();

		this.mHandler.post(this.mRunnable);
	}
	
	@Override
	protected void onDestroy() {
		
		this.mHandler.removeCallbacks(this.mRunnable);
		
		super.onDestroy();
	}
	
	private void tick() {
		
		this.mTimeElapsed++;
		
		if (this.mTimeElapsed < 60) {
		
			this.mTimeTextView.setText(
					this.getString(R.string.time_format_s, this.mTimeElapsed));
		}
		else {
			
			int minutes = (int)((double)this.mTimeElapsed / 60d);
			int seconds = this.mTimeElapsed - (60 * minutes);
			
			this.mTimeTextView.setText(
					this.getString(R.string.time_format_sm, minutes, seconds));
		}
	}
	
	private void stopTimer() {

		this.mHandler.removeCallbacks(this.mRunnable);
	}
}