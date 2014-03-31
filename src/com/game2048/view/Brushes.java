package com.game2048.view;

import android.graphics.Color;

public class Brushes {

	public static final int LIGHT_NUMBER_MAX = 4;
	
	public static final int BACKGROUND = Color.rgb(250, 248, 239);
	public static final int BACKGROUND_OVERLAY = Color.argb(200, 250, 248, 239);
	public static final int BOARD = Color.rgb(187, 173, 160);
	public static final int BOARD_LOCK = Color.rgb(186, 93, 93);
	public static final int NUMBER_DARK = Color.rgb(119, 119, 101);
	public static final int NUMBER_LIGHT = Color.rgb(249, 246, 242);
	public static final int EMPTY_CELL = Color.rgb(204, 192, 179);
	
	public static final int[] CELLS = new int[] {
		Color.rgb(238, 228, 218), // 2
		Color.rgb(237, 224, 200), // 4
		Color.rgb(242, 177, 121), // 8
		Color.rgb(245, 149, 99),  // 16
		Color.rgb(246, 124, 95),  // 32
		Color.rgb(246, 94, 59),   // 64
		Color.rgb(237, 207, 114), // 128
		Color.rgb(237, 204, 97),  // 256
		Color.rgb(236, 200, 80),  // 512
		Color.rgb(242, 192, 57),  // 1024
		Color.rgb(186, 89, 51),   // 2048
		Color.rgb(58, 59, 51),    // 4096
	};
}