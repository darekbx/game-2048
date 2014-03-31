package com.game2048.view;

import java.util.ArrayList;
import java.util.List;

import com.game2048.model.CellItem;
import com.game2048.model.MoveItem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;

public class GameView extends GestureView {

	private static final int ROUND_CORNER = 10;
	
	private final Paint mBoardPaint;
	private final Paint mLockBoardPaint;
	private final Paint mEmptyCellPaint;
	private final Paint mCellPaint;
	private final CellItem[] mCells;
	
	private RectF mBoard;

	// animation variables
	private List<CellItem[]> mMoves;
	private Move mCurrentMove;
	private boolean mIsMoveAnimation;
	private boolean mIsFirstMove;
	private int[] mTempNumbers;
	private float mTime;
	private int mNewNumberIndex;
	private int mNewNumberValue;
	
	private static final int ANIMATION_DURATION = 12;
	private static final int ANIMATION_SPEED = 10;
	private static final int ADD_ANIMATION_SPEED = 8;
	
	public GameView(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		
		this.mBoardPaint = new Paint();
		this.mBoardPaint.setAntiAlias(true);
		this.mBoardPaint.setColor(Brushes.BOARD);
		
		this.mLockBoardPaint = new Paint();
		this.mLockBoardPaint.setAntiAlias(true);
		this.mLockBoardPaint.setStyle(Style.STROKE);
		this.mLockBoardPaint.setStrokeWidth(4);
		this.mLockBoardPaint.setColor(Brushes.BOARD_LOCK);
		
		this.mEmptyCellPaint = new Paint();
		this.mEmptyCellPaint.setAntiAlias(true);
		this.mEmptyCellPaint.setColor(Brushes.EMPTY_CELL);

		this.mCellPaint = new Paint();
		this.mCellPaint.setAntiAlias(true);
		this.mCellPaint.setTextSize(52);
		this.mCellPaint.setTextAlign(Align.CENTER);
		this.mCellPaint.setTypeface(Typeface.DEFAULT_BOLD);
		
		this.mCells = new CellItem[SIZE * SIZE];
		
		this.mNewNumberIndex = -1;
		this.mNewNumberValue = -1;
	}

	@Override
	public void draw(final Canvas canvas) {

		canvas.drawColor(Brushes.BACKGROUND);
		
		canvas.translate(PADDING, 0);
		canvas.save();
		
		this.drawBoard(canvas);
		this.drawEmptyCells(canvas);
		this.drawNumbers(canvas);
		
		if (this.mMoves != null) 
			this.drawAnimation(canvas);
		
		if (this.mNewNumberIndex != -1)
			this.drawAddAnimation(canvas);
		
		super.draw(canvas);
	}
	
	private void drawNumbers(Canvas canvas) {

		for (int y = 0; y < SIZE; y++) 
			for (int x = 0; x < SIZE; x++) {
		
				if (this.mNumbers[y * SIZE + x] == 0)
					continue;

				if (this.mMoves != null) {

					boolean skipItem = false;
					
					for (CellItem[] move : this.mMoves) {

						int skipIndex = 1;
						
						if (!this.mIsMoveAnimation) {
							
							if (this.mCurrentMove == Move.DOWN || this.mCurrentMove == Move.RIGHT) 
								skipIndex = 0;
						}
						
						if (move[skipIndex] != null && move[skipIndex].index == (y * SIZE + x)) {
							
							skipItem = true;
							continue;
						}
					}
					
					if (skipItem)
						continue;
				}
				
				this.drawCell(canvas, x, y);
			}
	}
	
	private void drawCell(Canvas canvas, int x, int y) {
		
		this.drawCell(canvas, this.mCells[y * SIZE + x], this.mNumbers[y * SIZE + x]);
	}
	
	private void drawCell(Canvas canvas, CellItem item, int value) {

		int color = Brushes.EMPTY_CELL;
		int fontColor = value > Brushes.LIGHT_NUMBER_MAX 
				? Brushes.NUMBER_LIGHT : Brushes.NUMBER_DARK;
		
		switch (value) {
		
			case 2: color = Brushes.CELLS[0]; break;
			case 4: color = Brushes.CELLS[1]; break;
			case 8: color = Brushes.CELLS[2]; break;
			case 16: color = Brushes.CELLS[3]; break;
			case 32: color = Brushes.CELLS[4]; break;
			case 64: color = Brushes.CELLS[5]; break;
			case 128: color = Brushes.CELLS[6]; break;
			case 256: color = Brushes.CELLS[7]; break;
			case 512: color = Brushes.CELLS[8]; break;
			case 1024: color = Brushes.CELLS[9]; break;
			case 2048: color = Brushes.CELLS[10]; break;
			case 4096: color = Brushes.CELLS[11]; break;
		}
		
		this.mCellPaint.setColor(color);
		canvas.drawRoundRect(item.rectangle, 
				ROUND_CORNER, ROUND_CORNER, this.mCellPaint);

		canvas.save();
		canvas.translate(item.rectangle.left, item.rectangle.top);
		
		this.mCellPaint.setColor(fontColor);
		canvas.drawText(String.valueOf(value),
				item.numberPosition.x, 
				item.numberPosition.y, 
				this.mCellPaint);
		
		canvas.restore();
	}
	
	private void drawBoard(Canvas canvas) {
	
		if (this.mBoard == null)
			this.initializeRegions();
		
		canvas.drawRoundRect(this.mBoard, ROUND_CORNER, ROUND_CORNER, this.mBoardPaint);
		
		if (this.mLock) {

			canvas.drawRoundRect(this.mBoard, ROUND_CORNER, ROUND_CORNER, this.mLockBoardPaint);
		}
	}
	
	private void drawEmptyCells(Canvas canvas) {
		
		for (CellItem cell : this.mCells)
			canvas.drawRoundRect(cell.rectangle, ROUND_CORNER, ROUND_CORNER, this.mEmptyCellPaint);
	}
	
	private void initializeRegions() {

		final int boardSize = this.getWidth() - PADDING * 2;
		final int top = (this.getHeight() - boardSize) - PADDING;
		final int cellSize = (boardSize / SIZE) - PADDING / SIZE;
		
		this.mBoard = new RectF(0, top, boardSize, top + boardSize);
		
		// initialize cells
		for (int y = 0; y < SIZE; y++) 
			for (int x = 0; x < SIZE; x++) {
				
				this.mCells[y * SIZE + x] = new CellItem(
						new RectF(
							cellSize * x + PADDING, 
							cellSize * y + top + PADDING,
							cellSize * (x + 1),
							cellSize * (y + 1) + top),
						new PointF(
							(cellSize - PADDING) / 2, 
							PADDING + cellSize / 2 + 5), 
							y * SIZE + x);
			}
	}

	private void drawAnimation(Canvas canvas) {

		if (this.mMoves.size() == 0)
			return;
		
		int movedItems = 0;
		
		float width = this.mCells[0].rectangle.width();
		float height = this.mCells[0].rectangle.height();
		
		for (CellItem[] move : this.mMoves) {

			if (move == null || move[1] == null)
				continue;
			
			boolean moved = false;
			RectF from = move[0].rectangle;
			RectF to = move[1].rectangle;
			RectF d = new RectF(from); // destination
			
			CellItem tempMove = move[1];
			
			if (this.mCurrentMove == Move.LEFT || this.mCurrentMove == Move.RIGHT) {
				
				if (from.left < to.left) {
					
					d.left = Math.min(to.left, this.ease(from.left, to.left));
				    moved = true;
				}
				else if (from.left > to.left) {

					d.left = Math.max(to.left, from.left - this.ease(0, from.left - to.left));
				    moved = true;
				}
			    
			    if (d.left == to.left)
			    	move[1] = null;
				
				if (moved) {
					
					d.right = d.left + width;
				    movedItems++;
				}
			}
			else {

				if (from.top < to.top) {
				
					d.top = Math.min(to.top, this.ease(from.top, to.top));
				    moved = true;
				}
				else if (from.top > to.top) {
					
					d.top = Math.max(to.top, from.top - this.ease(0, from.top - to.top));
					moved = true;
				}
			    
			    if (d.top == to.top)
			    	move[1] = null;
				
				if (moved) {
					
					d.bottom = d.top + height;
				    movedItems++;
				}
			}
			
			if (!this.mIsMoveAnimation) {

				this.drawCell(
						canvas, 
						new CellItem(to, tempMove.numberPosition, 2), 
						this.mTempNumbers[tempMove.index]);	
			}
			
			this.drawCell(
					canvas, 
					new CellItem(d, move[0].numberPosition, move[0].index), 
					this.mTempNumbers[move[0].index]);
		}

		try { Thread.sleep(ANIMATION_SPEED); } 
		catch (InterruptedException e) { }
		
		if (movedItems == 0) {

			this.mTime = 0;
			this.mMoves = null;
			this.invalidate();

			if (this.mIsMoveAnimation) {

				if (this.mIsFirstMove) this.moveAnimationCompletedFirst(this.mCurrentMove);
				else this.moveAnimationCompletedSecond(this.mCurrentMove);
			}
			else this.sumAnimationCompleted(this.mCurrentMove);
			
			return;
		}
		
		this.mTime++;
		this.invalidate();
	}
	
	private void drawAddAnimation(Canvas canvas) {
	
		canvas.save();
		
		final CellItem item = this.mCells[this.mNewNumberIndex];
		final float x = item.rectangle.left + item.rectangle.width() / 2;
		final float y = item.rectangle.top + item.rectangle.height() / 2;
		float scale = this.ease(0f, 1f);
		canvas.scale(scale, scale, x, y);

		this.drawCell(
				canvas, 
				new CellItem(item.rectangle, item.numberPosition, this.mNewNumberIndex), 
				this.mNewNumberValue);
		
		canvas.restore();

		try { Thread.sleep(ADD_ANIMATION_SPEED); } 
		catch (InterruptedException e) { }
		
		if (scale == 1f) {

			this.mTime = 0;
			
			this.addAnimationCompleted(this.mNewNumberIndex, this.mNewNumberValue);
			this.mNewNumberIndex = -1;
			this.mNewNumberValue = -1;
			this.invalidate();
			return;
		}

		this.mTime++;
		this.invalidate();
	}
	
	@Override
	protected void prepareMoveAnimation(boolean first, Move move, int[] tempNumbers, MoveItem[] moveInfo) {

		this.mIsFirstMove = first;
		this.mIsMoveAnimation = true;
		
		this.mTempNumbers = tempNumbers;
		this.mMoves = new ArrayList<CellItem[]>();
		this.mCurrentMove = move;
		
		int addedMoves = 0;
		
		for (int y = 0; y < SIZE; y++) 
			for (int x = 0; x < SIZE; x++) {

				if (moveInfo[y * SIZE + x] == null)
					continue;

				this.mMoves.add(new CellItem[] { 
						this.mCells[moveInfo[y * SIZE + x].from].clone(),
						this.mCells[moveInfo[y * SIZE + x].to].clone()
				});
				
				addedMoves++;
			}
		
		if (addedMoves == 0) {
		
			this.mMoves = null;
			this.mTempNumbers = null;
			
			if (first) this.moveAnimationCompletedFirst(move);
			else this.moveAnimationCompletedSecond(move);
			
			return;
		}

		this.mTime = 0;
		this.invalidate();
	}
	
	@Override
	protected void prepareSumAnimation(Move move, int[] tempNumbers, MoveItem[] sumInfo) {

		this.mIsMoveAnimation = false;
		
		this.mMoves = new ArrayList<CellItem[]>();
		this.mTempNumbers = tempNumbers;
		this.mCurrentMove = move;

		if (sumInfo.length == 0) {
		
			this.mMoves = null;
			this.mTempNumbers = null;
			this.sumAnimationCompleted(move);
			return;
		}

		for (MoveItem item : sumInfo) {
		
			final CellItem from = this.mCells[item.from].clone();
			final CellItem to = this.mCells[item.to].clone();
			
			this.mMoves.add(new CellItem[] { from, to });
		}
		
		this.mTime = 0;
		this.invalidate();
	}
	
	@Override
	protected void prepareAddAnimation(int index, int value) {

		this.mNewNumberIndex = index;
		this.mNewNumberValue = value;
		
		this.mTime = 0;
		this.invalidate();
	}
	
	/** Easing functions
	 * Source: http://gsgd.co.uk/sandbox/jquery/easing/jquery.easing.1.3.js
	 * param time Current time. t = time
	 * param start Start value. b = start
	 * param end End value.     c = end
	 * param duration Duration. d = duration
	 */
    private float ease(float b, float c) {
    	
    	float t = this.mTime;
    	float d = ANIMATION_DURATION;
    	
    	// easeInOutQuad
		if ((t /= d / 2) < 1)
			return c / 2 * t * t + b;
		
		return -c / 2 * ((--t) * (t - 2) - 1) + b;
    }
}