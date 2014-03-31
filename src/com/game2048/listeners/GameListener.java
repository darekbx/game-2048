package com.game2048.listeners;

public interface GameListener {

	public void scoreUpdate(int score);
	public void gameOver();
	public void move();
}