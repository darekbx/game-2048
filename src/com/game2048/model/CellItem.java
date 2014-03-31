package com.game2048.model;

import android.graphics.PointF;
import android.graphics.RectF;

public class CellItem {

	public RectF rectangle;
	public PointF numberPosition;
	public int index;
	
	public CellItem(RectF rectangle, PointF numberPosition, int index) {
		
		this.rectangle = rectangle;
		this.numberPosition = numberPosition;
		this.index = index;
	}
	
	public CellItem clone() {

		return new CellItem(new RectF(this.rectangle), 
				new PointF(this.numberPosition.x, this.numberPosition.y), 
				this.index);
	}
}