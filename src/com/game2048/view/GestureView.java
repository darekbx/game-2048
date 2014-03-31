package com.game2048.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.game2048.R;
import com.game2048.listeners.GameListener;
import com.game2048.listeners.SumListener;
import com.game2048.listeners.SwipeListener;
import com.game2048.logic.MoveLogic;
import com.game2048.model.MoveItem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class GestureView 
	extends View 
	implements GestureDetector.OnGestureListener, SwipeListener {
	
	public enum Move {
		
		LEFT,
		RIGHT,
		UP,
		DOWN
	}
	
	private static final int SWIPE_MIN_DISTANCE = 100;
	private static final int SWIPE_MAX_OFF_PATH = 100;
	private static final int SWIPE_THRESHOLD_VELOCITY = 60;

	private final GestureDetector mGestureDetector;
	private final Random mRandom;
	private GameListener mGameListener;
	private boolean mGameStarted = false;
	private boolean mGameOver = false;
	private boolean mMovedOrSumed;

	protected static final int PADDING = 10;
	public static final int SIZE = 4;
	
	protected int[] mNumbers;
	protected boolean mLock = false;
	protected int mScore = 0;

	public GestureView(Context context, AttributeSet attrs) {
		
		super(context, attrs);

		this.mGestureDetector = new GestureDetector(this.getContext(), this);
		this.mRandom = new Random();
		this.mNumbers = new int[SIZE * SIZE];
	}
	
	public void setOnScoreListener(GameListener listener) {
	
		this.mGameListener = listener;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
	
		super.onDraw(canvas);
		
		if (this.mGameOver)
			this.gameOver(canvas);
		
		if (!this.mGameStarted) {

			this.addRandomNumber();
			this.invalidate();
			
			this.mGameStarted = true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		this.mGestureDetector.onTouchEvent(event);
		
		return true;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

		float dX = e2.getX() - e1.getX();
		float dY = e1.getY() - e2.getY();

		if (Math.abs(dY) < SWIPE_MAX_OFF_PATH 
				&& Math.abs(velocityX) >= SWIPE_THRESHOLD_VELOCITY 
				&& Math.abs(dX) >= SWIPE_MIN_DISTANCE) {

			if (dX > 0) this.swipeRight();
			else this.swipeLeft();

			return true;

		} 
		else if (Math.abs(dX) < SWIPE_MAX_OFF_PATH
				&& Math.abs(velocityY) >= SWIPE_THRESHOLD_VELOCITY 
				&& Math.abs(dY) >= SWIPE_MIN_DISTANCE) {

			if (dY > 0) this.swipeUp();
			else this.swipeDown();
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void swipeUp() { this.move(Move.UP); }

	@Override
	public void swipeDown() { this.move(Move.DOWN); }

	@Override
	public void swipeLeft() { this.move(Move.LEFT); }

	@Override
	public void swipeRight() { this.move(Move.RIGHT); }
	
	private void move(final Move move) {

		if (this.mLock)
			return;
		
		if (this.checkIsGameOver()) {
		
			this.mGameOver = true;
			this.invalidate();
			return;
		}
		
		this.mLock = true;
		this.mMovedOrSumed = false;
		
		final int[] a = this.mNumbers.clone();
		MoveLogic.sort(this.mNumbers, move);
		
		final MoveItem[] diff = MoveLogic.moveDifference(move, a.clone(), this.mNumbers);
		
		for (int i = 0; i < SIZE * SIZE; i++)
			if (diff[i] != null) {
			
				this.mMovedOrSumed = true;
				break;
			}
			
		this.mGameListener.move();
		this.prepareMoveAnimation(true, move, a.clone(), diff);
	}
	
	private void addRandomNumber() {
	
		final List<Integer> freePositions = this.freePositions();
		final int freePositionsSize = freePositions.size();
		
		if (freePositionsSize == 0) {

			if (!this.canMove())
				this.mGameOver = true;
			
			this.invalidate();
			return;
		}
		
		final int random = this.mRandom.nextInt(freePositionsSize);
		final int index = freePositions.get(random);
		final int value = (this.mRandom.nextInt(2) + 1) * 2;
		
		this.prepareAddAnimation(index, value);
	}
	
	private boolean checkIsGameOver() {
		
		if (this.freePositions().size() > 0)
			return false;
		
		if (this.canMove())
			return false;

		return true;
	}
	
	private List<Integer> freePositions() {

		final List<Integer> freePositions = new ArrayList<Integer>();

		for (int y = 0; y < SIZE; y++) 
			for (int x = 0; x < SIZE; x++)
				if (this.mNumbers[y * SIZE + x] == 0)
					freePositions.add(y * SIZE + x);
		
		return freePositions;
	}
	
	private boolean canMove() {
		
		boolean canMove = false;
		
		for (int y = 0; y < SIZE; y++)
			for (int x = 0; x < SIZE - 1; x++)
				if (this.mNumbers[y * SIZE + x] == this.mNumbers[y * SIZE + (x + 1)]) {
				
					canMove = true;
					break;
				}	

		for (int x = 0; x < SIZE; x++)
			for (int y = 0; y < SIZE - 1; y++)
				if (this.mNumbers[y * SIZE + x] == this.mNumbers[(y + 1) * SIZE + x]) {
				
					canMove = true;
					break;
				}
		
		return canMove;
	}
	
	protected abstract void prepareMoveAnimation(boolean first, Move move, int[] tempNumbers, MoveItem[] moveInfo);
	protected abstract void prepareSumAnimation(Move move, int[] tempNumbers, MoveItem[] sumInfo);
	protected abstract void prepareAddAnimation(int index, int value);
	
	protected void moveAnimationCompletedFirst(Move move) {
		
		final List<MoveItem> sumInfo = new ArrayList<MoveItem>();
		final int[] temp = this.mNumbers.clone();
		
		this.mScore += MoveLogic.sum(this.mNumbers, move, new SumListener() {

			@Override
			public void sumed(MoveItem item) {
				
				GestureView.this.mMovedOrSumed = true;
				sumInfo.add(item);
			}
		});

		this.prepareSumAnimation(move, temp, sumInfo.toArray(new MoveItem[0]));
	}
	
	protected void sumAnimationCompleted(Move move) {

		final int[] a = this.mNumbers.clone();
		MoveLogic.sort(this.mNumbers, move);

		this.prepareMoveAnimation(false, move, a.clone(), 
				MoveLogic.moveDifference(move, a, this.mNumbers));
	}
	
	protected void moveAnimationCompletedSecond(Move move) {

		this.mGameListener.scoreUpdate(this.mScore);
		
		if (!this.mMovedOrSumed) {
			
			this.mLock = false;
			this.invalidate();
			return;
		}
		
		this.addRandomNumber();
	}
	
	protected void addAnimationCompleted(int index, int value) {

		this.mNumbers[index] = value;

		this.mLock = false;
		this.invalidate();
	}
	
	private void gameOver(Canvas canvas) {

		this.mGameListener.gameOver();

		final int boardSize = this.getWidth() - PADDING * 2;
		final int top = (this.getHeight() - boardSize) - PADDING;
		
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setColor(Brushes.BACKGROUND_OVERLAY);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(52);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		
		canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);
		
		paint.setColor(Brushes.NUMBER_DARK);
		canvas.drawText(this.getContext().getString(R.string.game_over), 
				this.getWidth() / 2, top + (boardSize / 2) + 18, paint);
	}

	@Override
	public boolean onDown(MotionEvent e) { return false; }

	@Override
	public boolean onSingleTapUp(MotionEvent e) { return false; }
	
	@Override
	public void onLongPress(MotionEvent e) { }

	@Override
	public void onShowPress(MotionEvent e) { }

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) { return false; }
}