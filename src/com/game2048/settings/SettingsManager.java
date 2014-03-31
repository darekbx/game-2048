package com.game2048.settings;

import com.game2048.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {

	private Context mContext;
	
	public SettingsManager(Context context) {
	
		this.mContext = context;
	}

	public int getMoves() {

		return this.getPreferences().getInt(this.mContext.getString(R.string.moves_key), 0);
	}
	
	public void increaseMoves() {

		int timesPlayed = this.getMoves();
		timesPlayed++;
		
		final SharedPreferences.Editor editor = this.getPreferences().edit();
		editor.putInt(this.mContext.getString(R.string.moves_key), timesPlayed);
		editor.commit();
	}

	public int getTimesPlayed() {

		return this.getPreferences().getInt(this.mContext.getString(R.string.times_played_key), 0);
	}
	
	public void increaseTimesPlayed() {

		int timesPlayed = this.getTimesPlayed();
		timesPlayed++;
		
		final SharedPreferences.Editor editor = this.getPreferences().edit();
		editor.putInt(this.mContext.getString(R.string.times_played_key), timesPlayed);
		editor.commit();
	}
	
	public int getBestScore() {

		return this.getPreferences().getInt(this.mContext.getString(R.string.best_score_key), 0);
	}
	
	public void setBestScore(int score) {

		final SharedPreferences.Editor editor = this.getPreferences().edit();
		editor.putInt(this.mContext.getString(R.string.best_score_key), score);
		editor.commit();
	}
	
	private SharedPreferences getPreferences() {
		
		return this.mContext.getSharedPreferences(
				this.mContext.getString(R.string.app_name), 
				Activity.MODE_PRIVATE);
	}
}