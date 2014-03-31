package com.game2048.logic;

import static com.game2048.view.GestureView.SIZE;

import com.game2048.listeners.SumListener;
import com.game2048.model.MoveItem;
import com.game2048.view.GestureView.Move;

public class MoveLogic {
	
	public static int sum(int[] numbers, Move move, SumListener listener) {

		int score = 0;

		for (int i = 0; i < SIZE; i++) {
			
			if (move == Move.UP || move == Move.LEFT) {
	
				for (int b = 1; b < SIZE; b++) {
					
					if (move == Move.UP) {
	
						if (numbers[b * SIZE + i] == 0)
							continue;
						
						if (numbers[b * SIZE + i] == numbers[(b - 1) * SIZE + i]) {
							
							numbers[(b - 1) * SIZE + i] += numbers[b * SIZE + i];
							numbers[b * SIZE + i] = 0;
	
							listener.sumed(new MoveItem(b * SIZE + i, (b - 1) * SIZE + i));
							score += numbers[(b - 1) * SIZE + i];
						}
					}
					else if (move == Move.LEFT) {
	
						if (numbers[i * SIZE + b] == 0)
							continue;
						
						if (numbers[i * SIZE + b] == numbers[i * SIZE + (b - 1)]) {
	
							numbers[i * SIZE + (b - 1)] += numbers[i * SIZE + b];
							numbers[i * SIZE + b] = 0;
	
							listener.sumed(new MoveItem(i * SIZE + b, i * SIZE + (b - 1)));
							score += numbers[i * SIZE + (b - 1)];
						}
					}
				}
			}
			else if (move == Move.DOWN || move == Move.RIGHT) {
	
				for (int b = SIZE - 2; b >= 0; b--) {
					
					if (move == Move.DOWN) {
	
						if (numbers[b * SIZE + i] == 0)
							continue;
						
						if (numbers[b * SIZE + i] == numbers[(b + 1) * SIZE + i]) {
	
							numbers[(b + 1) * SIZE + i] += numbers[b * SIZE + i];
							numbers[b * SIZE + i] = 0;
							
							listener.sumed(new MoveItem(b * SIZE + i, (b + 1) * SIZE + i));
							score += numbers[(b + 1) * SIZE + i];
						}
					}
					else if (move == Move.RIGHT) {
	
						if (numbers[i * SIZE + b] == 0)
							continue;
						
						if (numbers[i * SIZE + b] == numbers[i * SIZE + (b + 1)]) {
	
							numbers[i * SIZE + (b + 1)] += numbers[i * SIZE + b];
							numbers[i * SIZE + b] = 0;
	
							listener.sumed(new MoveItem(i * SIZE + b,i * SIZE + (b + 1)));
							score += numbers[i * SIZE + (b + 1)];
						}
					}
				}
			}
		}
		
		return score;
	}
	
	public static void sort(int[] numbers, Move move) {

		for (int i = 0; i < SIZE; i++)
			for (int a = 0; a < SIZE; a++)
				for (int b = 1; b < SIZE - a; b++) {
					
					boolean conditionA = false;
					boolean conditionB = false;
	
					switch (move) {
					
						case UP:
							
							if (numbers[b * SIZE + i] != 0 && numbers[(b - 1) * SIZE + i] == 0)
								conditionA = true;
							
							break;
						
						case DOWN:
							
							if (numbers[b * SIZE + i] == 0 && numbers[(b - 1) * SIZE + i] != 0)
								conditionA = true;
							
							break;
							
						case LEFT:
	
							if (numbers[i * SIZE + b] != 0 && numbers[i * SIZE + (b - 1)] == 0)
								conditionB = true;
							
							break;
							
						case RIGHT:
	
							if (numbers[i * SIZE + b] == 0 && numbers[i * SIZE + (b - 1)] != 0)
								conditionB = true;
							
							break;
					}
	
					if (conditionA) {
	
						int temp = numbers[(b - 1) * SIZE + i];
						numbers[(b - 1) * SIZE + i] = numbers[b * SIZE + i];
						numbers[b * SIZE + i] = temp;
					}
					
					if (conditionB) {
	
						int temp = numbers[i * SIZE + (b - 1)];
						numbers[i * SIZE + (b - 1)] = numbers[i * SIZE + b];
						numbers[i * SIZE + b] = temp;
					}
				}
	}
	
	public static MoveItem[] moveDifference(Move move, int[] a, int[] b) {

		final MoveItem[] items = new MoveItem[SIZE * SIZE];

		switch (move) {
				
			case LEFT:
				for (int y = 0; y < SIZE; y++)
					for (int bx = 0; bx < SIZE; bx++) {

						if (b[y * SIZE + bx] == 0) break;
						if (b[y * SIZE + bx] == a[y * SIZE + bx]) continue;

						for (int ax = bx; ax < SIZE; ax++)
							if (a[y * SIZE + ax] == b[y * SIZE + bx]) {
								
								items[y * SIZE + ax] = new MoveItem(y * SIZE + ax, y * SIZE + bx);
								a[y * SIZE + ax] = 0;
								break;
							}
					}
				break;
		
			case RIGHT:
				for (int y = 0; y < SIZE; y++)
					for (int bx = SIZE - 1; bx >= 0; bx--) {
						
						if (b[y * SIZE + bx] == 0) break;
						if (b[y * SIZE + bx] == a[y * SIZE + bx]) continue;
						
						for (int ax = bx; ax >= 0; ax--)
							if (a[y * SIZE + ax] == b[y * SIZE + bx]) {
							
								items[y * SIZE + ax] = new MoveItem(y * SIZE + ax, y * SIZE + bx);
								a[y * SIZE + ax] = 0;
								break;
							}
					}
				break;
				
			case UP:
				for (int x = 0; x < SIZE; x++) 
					for (int by = 0; by < SIZE; by++) {
						
						if (b[by * SIZE + x] == 0) break;
						if (b[by * SIZE + x] == a[by * SIZE + x]) continue;
						
						for (int ay = by; ay < SIZE; ay++)
							if (a[ay * SIZE + x] == b[by * SIZE + x]) {
							
								items[ay * SIZE + x] = new MoveItem(ay * SIZE + x, by * SIZE + x);
								a[ay * SIZE + x] = 0;
								break;
							}
					}
					
				break;
				
			case DOWN:
				for (int x = 0; x < SIZE; x++)
					for (int by = SIZE - 1; by >= 0; by--) {
						
						if (b[by * SIZE + x] == 0) break;
						if (b[by * SIZE + x] == a[by * SIZE + x]) continue;
						
						for (int ay = by; ay >= 0; ay--)
							if (a[ay * SIZE + x] == b[by * SIZE + x]) {
							
								items[ay * SIZE + x] = new MoveItem(ay * SIZE + x, by * SIZE + x);
								a[ay * SIZE + x] = 0;
								break;
							}
					}
				break;
		}
		
		return items;
	}
}