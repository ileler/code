package coderr.kerwin.rlsb.theme;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public abstract class Theme {
	
	private int cellSize;
	private int gridWidth;
	private int numSquaresX;
	private int numSquaresY;
	
	public int centerText(Paint paint) {
		if(paint == null)	return -1;
		return (int) ((paint.descent() + paint.ascent()) / 2);
	}
	
	public void setLayout(int cellSize, int gridWidth){
		this.cellSize = cellSize;
		this.gridWidth = gridWidth;
	}
	
	public int getCellSize(){
		return cellSize;
	}
	
	public int getGridWidth(){
		return gridWidth;
	}
	
	public int getNumSquaresX(){
		return numSquaresX;
	}
	
	public int getNumSquaresY(){
		return numSquaresY;
	}
	
	public void drawDrawable(Canvas canvas, Drawable draw, Rect rect) {
        draw.setBounds(rect);
        draw.draw(canvas);
    }
	
	public void drawDrawable(Canvas canvas, Drawable draw, int startingX, int startingY, int endingX, int endingY) {
		drawDrawable(canvas, draw, new Rect(startingX, startingY, endingX, endingY));
    }
	
	public abstract Drawable getNewGameBtnUDraw();
	public abstract Drawable getNewGameBtnODraw();
	public abstract Drawable getBackgroundDraw();
	public abstract Drawable getBackgroundGridDraw();
	public abstract BitmapDrawable[] getBitmapCell();
	
}
