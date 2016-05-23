package coderr.kerwin.rlsb.theme;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import coderr.kerwin.rlsb.R;

public class DefaultTheme extends Theme{
	
	private int numCellTypes = 18;
	
	private int TEXT_BLACK;				
    private int TEXT_WHITE;
	
	private Paint paint;
	private Resources resources;
	private BitmapDrawable[] bitmapCell;
	private Drawable lightUpRectangle;
	private Drawable backgroundRectangle;
	private Drawable backgroundGridRectangle;
	
	public DefaultTheme(Resources resources){
		paint = new Paint();
		paint.setAntiAlias(true);		//去锯齿
        paint.setTypeface(Typeface.createFromAsset(resources.getAssets(), "ClearSans-Bold.ttf"));		//设置字体样式
		bitmapCell = new BitmapDrawable[numCellTypes];
		lightUpRectangle = resources.getDrawable(R.drawable.light_up_rectangle);
		backgroundRectangle = resources.getDrawable(R.drawable.background_rectangle);
		backgroundGridRectangle = resources.getDrawable(R.drawable.cell_rectangle);
		this.resources = resources;
		
		TEXT_WHITE = resources.getColor(R.color.text_white);
        TEXT_BLACK = resources.getColor(R.color.text_black);
	}
	
	@Override
	public void setLayout(int cellSize, int gridWidth){
		super.setLayout(cellSize, gridWidth);
		
        paint.setTextSize(cellSize);
        paint.setTextAlign(Paint.Align.CENTER);
        float textSize = cellSize * cellSize * 0.9f / Math.max(cellSize * 0.9f, paint.measureText(String.valueOf((int) Math.pow(2, bitmapCell.length - 1))));
		
        int[] cellRectangleIds = getCellRectangleIds();
        for (int xx = 1; xx < bitmapCell.length; xx++) {
            paint.setTextSize(textSize);
            Bitmap bitmap = Bitmap.createBitmap(cellSize, cellSize, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawDrawable(canvas, resources.getDrawable(cellRectangleIds[xx]), 0, 0, cellSize, cellSize);
            drawCellText(canvas, (int) Math.pow(2, xx), 0, 0);
            bitmapCell[xx] = new BitmapDrawable(resources, bitmap);
        }
	}
	
	private int[] getCellRectangleIds() {
        int[] cellRectangleIds = new int[numCellTypes];
        cellRectangleIds[0] = R.drawable.cell_rectangle;
        cellRectangleIds[1] = R.drawable.c0002;
        cellRectangleIds[2] = R.drawable.c0004;
        cellRectangleIds[3] = R.drawable.c0008;
        cellRectangleIds[4] = R.drawable.c0016;
        cellRectangleIds[5] = R.drawable.c0032;
        cellRectangleIds[6] = R.drawable.c0064;
        cellRectangleIds[7] = R.drawable.c0128;
        cellRectangleIds[8] = R.drawable.c0256;
        cellRectangleIds[9] = R.drawable.c0512;
        cellRectangleIds[10] = R.drawable.c1024;
        cellRectangleIds[11] = R.drawable.c2048;
        for (int xx = 12; xx < cellRectangleIds.length; xx++) {
            cellRectangleIds[xx] = R.drawable.c4096;
        }
        return cellRectangleIds;
    }

    private void drawCellText(Canvas canvas, int value, int sX, int sY) {
        int textShiftY = centerText(paint);
        if (value >= 8) {
            paint.setColor(TEXT_WHITE);
        } else {
            paint.setColor(TEXT_BLACK);
        }
        canvas.drawText("" + value, sX + getCellSize() / 2, sY + getCellSize() / 2 - textShiftY, paint);
    }
    
    @Override
    public Drawable getNewGameBtnUDraw(){
    	return lightUpRectangle;
    }
    
    @Override
	public Drawable getNewGameBtnODraw(){
		return backgroundRectangle;
	}

	@Override
	public Drawable getBackgroundDraw() {
		return backgroundRectangle;
	}

	@Override
	public Drawable getBackgroundGridDraw() {
		return backgroundGridRectangle;
	}

	@Override
	public BitmapDrawable[] getBitmapCell() {
		return bitmapCell;
	}
	
}
