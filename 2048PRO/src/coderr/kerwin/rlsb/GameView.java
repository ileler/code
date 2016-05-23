package coderr.kerwin.rlsb;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import coderr.kerwin.rlsb.model.AnimationCell;
import coderr.kerwin.rlsb.model.TileCell;
import coderr.kerwin.rlsb.theme.DefaultTheme;
import coderr.kerwin.rlsb.theme.Theme;

public class GameView extends View {
	
	private static final String WIDTH = "width";
	private static final String HEIGHT = "height";
	private static final String SCORE = "score";
	private static final String HIGH_SCORE = "high score temp";
	private static final String UNDO_SCORE = "undo score";
	private static final String CAN_UNDO = "can undo";
	private static final String UNDO_GRID = "undo";
	private static final String GAME_STATE = "game state";
	private static final String UNDO_GAME_STATE = "undo game state";

	private MainActivity mainActivity;
	private GameLogic gameLogic;
	private Theme theme;
	private Paint paint;
	
    //Internal variables
    public final int numCellTypes = 18;
    public boolean continueButtonEnabled = false;

    //Layout variables
    private int cellSize;			//单元格大小（正方形）
    private int gridWidth;			//网格宽度（单元格周围的网格）
    private float textSize = 0;			//文本大小
    private int TEXT_BLACK;				
    private int TEXT_WHITE;
    private Rect gameRect;
    private int textPaddingSize;	
    public int prevMaxValue;
    public int currMaxValue;

    //Assets
    private BitmapDrawable[] bitmapCell = new BitmapDrawable[numCellTypes];

    private Drawable lightUpRectangle;
    private Drawable fadeRectangle;
    private Bitmap background = null;
    private BitmapDrawable loseGameOverlay;
    private BitmapDrawable winGameContinueOverlay;
    private BitmapDrawable winGameFinalOverlay;

    //Icon variables
    public int sYIcons;
    public int sXNewGame;
    public int sXUndo;
    public int iconSize;

    //Timing
    long lastFPSTime = System.nanoTime();
    long currentTime = System.nanoTime();

    //Text
    float bodyTextSize;
    float gameOverTextSize;

    //Misc
    boolean refreshLastTime = true;

    //Intenal Constants
    static final int BASE_ANIMATION_TIME = 100000000;
    static final float MERGING_ACCELERATION = (float) - 0.5;
    static final float INITIAL_VELOCITY = (1 - MERGING_ACCELERATION) / 4;
    
    public GameView(MainActivity mainActivity) {
        this(mainActivity.getBaseContext());
        this.mainActivity = mainActivity;
    }

    private GameView(Context context) {
        super(context);
        paint = new Paint();
        Resources resources = context.getResources();
        //Loading resources
        gameLogic = new GameLogic(context, this);
        theme = new DefaultTheme(resources);
        try {
            //Getting assets
            lightUpRectangle = resources.getDrawable(R.drawable.light_up_rectangle);
            fadeRectangle = resources.getDrawable(R.drawable.fade_rectangle);
            TEXT_WHITE = resources.getColor(R.color.text_white);
            TEXT_BLACK = resources.getColor(R.color.text_black);
            Typeface font = Typeface.createFromAsset(resources.getAssets(), "ClearSans-Bold.ttf");
            paint.setTypeface(font);		//设置字体样式
            paint.setAntiAlias(true);		//去锯齿
        } catch (Exception e) {
            System.out.println("Error getting assets?");
        }
        setOnTouchListener(new InputListener(this));
        gameLogic.newGame();
    }
    
    public void save(){
    	SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mainActivity).edit();
    	TileCell[][] field = gameLogic.grid.field;
    	TileCell[][] undoField = gameLogic.grid.undoField;
    	editor.putInt(WIDTH, field.length);
    	editor.putInt(HEIGHT, field.length);
    	for (int xx = 0; xx < field.length; xx++) {
    		for (int yy = 0; yy < field[0].length; yy++) {
    			if (field[xx][yy] != null) {
    				editor.putInt(xx + " " + yy, field[xx][yy].getValue());
    			} else {
    				editor.putInt(xx + " " + yy, 0);
    			}
    			
    			if (undoField[xx][yy] != null) {
    				editor.putInt(UNDO_GRID + xx + " " + yy, undoField[xx][yy].getValue());
    			} else {
    				editor.putInt(UNDO_GRID + xx + " " + yy, 0);
    			}
    		}
    	}
    	editor.putLong(SCORE, gameLogic.score);
    	editor.putLong(HIGH_SCORE, gameLogic.highScore);
    	editor.putLong(UNDO_SCORE, gameLogic.lastScore);
    	editor.putBoolean(CAN_UNDO, gameLogic.canUndo);
    	editor.putInt(GAME_STATE, gameLogic.gameState);
    	editor.putInt(UNDO_GAME_STATE, gameLogic.lastGameState);
    	editor.commit();
    }
    
    public void load(){
    	//Stopping all animations
    	gameLogic.aGrid.cancelAnimationCells();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        for (int xx = 0; xx < gameLogic.grid.field.length; xx++) {
            for (int yy = 0; yy < gameLogic.grid.field[0].length; yy++) {
                int value = settings.getInt(xx + " " + yy, -1);
                if (value > 0) {
                	gameLogic.grid.field[xx][yy] = new TileCell(xx, yy, value);
                } else if (value == 0) {
                	gameLogic.grid.field[xx][yy] = null;
                }

                int undoValue = settings.getInt(UNDO_GRID + xx + " " + yy, -1);
                if (undoValue > 0) {
                	gameLogic.grid.undoField[xx][yy] = new TileCell(xx, yy, undoValue);
                } else if (value == 0) {
                	gameLogic.grid.undoField[xx][yy] = null;
                }
            }
        }

        gameLogic.score = settings.getLong(SCORE, gameLogic.score);
        gameLogic.highScore = settings.getLong(HIGH_SCORE, gameLogic.highScore);
        gameLogic.lastScore = settings.getLong(UNDO_SCORE, gameLogic.lastScore);
        gameLogic.canUndo = settings.getBoolean(CAN_UNDO, gameLogic.canUndo);
        gameLogic.gameState = settings.getInt(GAME_STATE, gameLogic.gameState);
        gameLogic.lastGameState = settings.getInt(UNDO_GAME_STATE, gameLogic.lastGameState);
    }
    
    /**
     * 通过屏幕大小计算布局
     * @param width		当前View的宽度
     * @param height	当前View的高度
     */
    private void calculateLayout(int width, int height) {
    	int boardMiddleX = 0;	//面板中心X值
        int boardMiddleY = 0;	//面板中心Y值
    	if(getResources().getConfiguration().orientation == 1){
    		//竖屏
    		boardMiddleX = width / 2;
    		boardMiddleY = (height - width) + boardMiddleX;
    	}else{
    		//横屏
    		boardMiddleY = height / 2;
    		boardMiddleX = (width - height) + boardMiddleY;
    	}
        cellSize = Math.min(width / (gameLogic.numSquaresX + 1), height / (gameLogic.numSquaresY + 1));	//计算方格大小
        gridWidth = cellSize / 7;		//计算网格线宽度
        

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(cellSize);
        textSize = cellSize * cellSize / Math.max(cellSize, paint.measureText("0000"));
        
        bodyTextSize = (int) (textSize / 1.5);
        gameOverTextSize = textSize * 2;
        textPaddingSize = (int) (textSize / 3);

        //Grid Dimensions
        double halfNumSquaresX = gameLogic.numSquaresX / 2d;
        double halfNumSquaresY = gameLogic.numSquaresY / 2d;

        
        gameRect = new Rect(
    		(int) (boardMiddleX - (cellSize + gridWidth) * halfNumSquaresX - gridWidth / 2),
    		(int) (boardMiddleY - (cellSize + gridWidth) * halfNumSquaresY - gridWidth / 2),
            (int) (boardMiddleX + (cellSize + gridWidth) * halfNumSquaresX + gridWidth / 2),
            (int) (boardMiddleY + (cellSize + gridWidth) * halfNumSquaresY + gridWidth / 2)
		);
        resyncTime();
    }
    
    public Rect getGameRect(){
    	return gameRect;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(background, 0, 0, paint);
        
//        mainActivity.setCurrScore(String.valueOf(gameLogic.score));
//    	mainActivity.setHighScore(String.valueOf(gameLogic.highScore));

        drawCells(canvas);
        if(currMaxValue > prevMaxValue){
//        	mainActivity.setCurrHigh(String.valueOf(currMaxValue));
        	int ti = -1;
        	switch(currMaxValue){
	        	case 2:
	        		ti = R.color.c0002;
	        		break;
	        	case 4:
	        		ti = R.color.c0004;
	        		break;
	        	case 8:
	        		ti = R.color.c0008;
	        		break;
	        	case 16:
	        		ti = R.color.c0016;
	        		break;
	        	case 32:
	        		ti = R.color.c0032;
	        		break;
	        	case 64:
	        		ti = R.color.c0064;
	        		break;
	        	case 128:
	        		ti = R.color.c0128;
	        		break;
	        	case 256:
	        		ti = R.color.c0256;
	        		break;
	        	case 512:
	        		ti = R.color.c0512;
	        		break;
	        	case 1024:
	        		ti = R.color.c1024;
	        		break;
	        	case 2048:
	        		ti = R.color.c2048;
	        		break;
	        	case 4096:
	        		ti = R.color.c4096;
	        		break;
        	}
        	if(ti > -1){
        		mainActivity.getRootView().setBackgroundColor(getResources().getColor(ti));
        	}
        }

        if (!gameLogic.isActive()) {
            drawEndGameState(canvas);
        }

        if (!gameLogic.canContinue()) {
        }

        //Refresh the screen if there is still an animation running
        if (gameLogic.aGrid.isAnimationActive()) {
            invalidate(gameRect);
            tick();
            //Refresh one last time on game end.
        } else if (!gameLogic.isActive() && refreshLastTime) {
            invalidate();
            refreshLastTime = false;
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        calculateLayout(width, height);
        theme.setLayout(cellSize, gridWidth);
        bitmapCell = theme.getBitmapCell();
        createBackgroundBitmap(width, height);
        createOverlays();
    }
    
    /**
     * 画背景
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        theme.drawDrawable(canvas, theme.getBackgroundDraw(), gameRect);
    }

    /**
     * 画16个单元格的背景
     * @param canvas
     */
    private void drawBackgroundGrid(Canvas canvas) {
        Drawable backgroundCell = theme.getBackgroundGridDraw();
        for (int xx = 0; xx < gameLogic.numSquaresX; xx++) {
            for (int yy = 0; yy < gameLogic.numSquaresY; yy++) {
                int sX = gameRect.left + gridWidth + (cellSize + gridWidth) * xx;
                int eX = sX + cellSize;
                int sY = gameRect.top + gridWidth + (cellSize + gridWidth) * yy;
                int eY = sY + cellSize;
                theme.drawDrawable(canvas, backgroundCell, sX, sY, eX, eY);
            }
        }
    }

    /**
     * 画单元格
     * @param canvas
     */
    private void drawCells(Canvas canvas) {
        paint.setTextAlign(Paint.Align.CENTER);
        // Outputting the individual cells
        for (int xx = 0; xx < gameLogic.numSquaresX; xx++) {
            for (int yy = 0; yy < gameLogic.numSquaresY; yy++) {
                int sX = gameRect.left + gridWidth + (cellSize + gridWidth) * xx;
                int eX = sX + cellSize;
                int sY = gameRect.top + gridWidth + (cellSize + gridWidth) * yy;
                int eY = sY + cellSize;

                TileCell currentTile = gameLogic.grid.getCellContent(xx, yy);
                if (currentTile != null) {
                    //Get and represent the value of the tile
                    int value = currentTile.getValue();
                    if(currMaxValue < value)	currMaxValue = value;
                    int index = log2(value);

                    //Check for any active animations
                    ArrayList<AnimationCell> aArray = gameLogic.aGrid.getAnimationCells(xx, yy);
                    boolean animated = false;
                    for (int i = aArray.size() - 1; i >= 0; i--) {
                        AnimationCell aCell = aArray.get(i);
                        //If this animation is not active, skip it
                        if (aCell.getAnimationType() == GameLogic.SPAWN_ANIMATION) {
                            animated = true;
                        }
                        if (!aCell.isActive()) {
                            continue;
                        }

                        if (aCell.getAnimationType() == GameLogic.SPAWN_ANIMATION) { // Spawning animation
                            double percentDone = aCell.getPercentageDone();
                            float textScaleSize = (float) (percentDone);

                            float cellScaleSize = cellSize / 2 * (1 - textScaleSize);
                            bitmapCell[index].setBounds((int) (sX + cellScaleSize), (int) (sY + cellScaleSize), (int) (eX - cellScaleSize), (int) (eY - cellScaleSize));
                            bitmapCell[index].draw(canvas);
                        } else if (aCell.getAnimationType() == GameLogic.MERGE_ANIMATION) { // Merging Animation
                            double percentDone = aCell.getPercentageDone();
                            float textScaleSize = (float) (1 + INITIAL_VELOCITY * percentDone
                                    + MERGING_ACCELERATION * percentDone * percentDone / 2);

                            float cellScaleSize = cellSize / 2 * (1 - textScaleSize);
                            bitmapCell[index].setBounds((int) (sX + cellScaleSize), (int) (sY + cellScaleSize), (int) (eX - cellScaleSize), (int) (eY - cellScaleSize));
                            bitmapCell[index].draw(canvas);
                        } else if (aCell.getAnimationType() == GameLogic.MOVE_ANIMATION) {  // Moving animation
                            double percentDone = aCell.getPercentageDone();
                            int tempIndex = index;
                            if (aArray.size() >= 2) {
                                tempIndex = tempIndex - 1;
                            }
                            int previousX = aCell.extras[0];
                            int previousY = aCell.extras[1];
                            int currentX = currentTile.getX();
                            int currentY = currentTile.getY();
                            int dX = (int) ((currentX - previousX) * (cellSize + gridWidth) * (percentDone - 1) * 1.0);
                            int dY = (int) ((currentY - previousY) * (cellSize + gridWidth) * (percentDone - 1) * 1.0);
                            bitmapCell[tempIndex].setBounds(sX + dX, sY + dY, eX + dX, eY + dY);
                            bitmapCell[tempIndex].draw(canvas);
                        }
                        animated = true;
                    }

                    //No active animations? Just draw the cell
                    if (!animated) {
                        bitmapCell[index].setBounds(sX, sY, eX, eY);
                        bitmapCell[index].draw(canvas);
                    }
                }
            }
        }
    }

    private void drawEndGameState(Canvas canvas) {
        double alphaChange = 1;
        continueButtonEnabled = false;
        for (AnimationCell animation : gameLogic.aGrid.getGlobalAnimationCells()) {
            if (animation.getAnimationType() == GameLogic.FADE_GLOBAL_ANIMATION) {
                alphaChange = animation.getPercentageDone();
            }
        }
        BitmapDrawable displayOverlay = null;
        if (gameLogic.gameWon()) {
            if (gameLogic.canContinue()) {
                continueButtonEnabled = true;
                displayOverlay = winGameContinueOverlay;
            } else {
                displayOverlay = winGameFinalOverlay;
            }
        } else if (gameLogic.gameLost()) {
            displayOverlay = loseGameOverlay;
        }

        if (displayOverlay != null) {
            displayOverlay.setBounds(gameRect);
            displayOverlay.setAlpha((int) (255 * alphaChange));
            displayOverlay.draw(canvas);
        }
    }

    private void createEndGameStates(Canvas canvas, boolean win, boolean showButton) {
        int width = gameRect.width();
        int length = gameRect.height();
        int middleX = width / 2;
        int middleY = length / 2;
        if (win) {
            lightUpRectangle.setAlpha(127);
            theme.drawDrawable(canvas, lightUpRectangle, 0, 0, width, length);
            lightUpRectangle.setAlpha(255);
            paint.setColor(TEXT_WHITE);
            paint.setAlpha(255);
            paint.setTextSize(gameOverTextSize);
            paint.setTextAlign(Paint.Align.CENTER);
            int textBottom = middleY - theme.centerText(paint);
            canvas.drawText(getResources().getString(R.string.you_win), middleX, textBottom, paint);
            paint.setTextSize(bodyTextSize);
            String text = showButton ? getResources().getString(R.string.go_on) :
                    getResources().getString(R.string.for_now);
            canvas.drawText(text, middleX, textBottom + textPaddingSize * 2 - theme.centerText(paint) * 2, paint);
        } else {
            fadeRectangle.setAlpha(127);
            theme.drawDrawable(canvas, fadeRectangle, 0, 0, width, length);
            fadeRectangle.setAlpha(255);
            paint.setColor(TEXT_BLACK);
            paint.setAlpha(255);
            paint.setTextSize(gameOverTextSize);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(getResources().getString(R.string.game_over), middleX, middleY - theme.centerText(paint), paint);
        }
    }

    private void createBackgroundBitmap(int width, int height) {
        background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(background);
        drawBackground(canvas);
        drawBackgroundGrid(canvas);
    }

    private void createOverlays() {
        Resources resources = getResources();
        //Initalize overlays
        Bitmap bitmap = Bitmap.createBitmap(gameRect.width(), gameRect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        createEndGameStates(canvas, true, true);
        winGameContinueOverlay = new BitmapDrawable(resources, bitmap);
        bitmap = Bitmap.createBitmap(gameRect.width(), gameRect.height(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        createEndGameStates(canvas, true, false);
        winGameFinalOverlay = new BitmapDrawable(resources, bitmap);
        bitmap = Bitmap.createBitmap(gameRect.width(), gameRect.height(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        createEndGameStates(canvas, false, false);
        loseGameOverlay = new BitmapDrawable(resources, bitmap);
    }

    private void tick() {
        currentTime = System.nanoTime();
        gameLogic.aGrid.tickAll(currentTime - lastFPSTime);
        lastFPSTime = currentTime;
    }

    public void resyncTime() {
        lastFPSTime = System.nanoTime();
    }

    private static int log2(int n) {
        if (n <= 0) throw new IllegalArgumentException();
        return 31 - Integer.numberOfLeadingZeros(n);
    }

	class InputListener implements View.OnTouchListener {

	    private static final int SWIPE_MIN_DISTANCE = 0;
	    private static final int SWIPE_THRESHOLD_VELOCITY = 25;
	    private static final int MOVE_THRESHOLD = 250;
	    private static final int RESET_STARTING = 10;

	    private float x;
	    private float y;
	    private float lastdx;
	    private float lastdy;
	    private float previousX;
	    private float previousY;
	    private float startingX;
	    private float startingY;
	    private int previousDirection = 1;
	    private int veryLastDirection = 1;
	    private boolean hasMoved = false;

	    GameView mView;

	    public InputListener(GameView view) {
	        super();
	        this.mView = view;
	    }

	    public boolean onTouch(View view, MotionEvent event) {
	        switch (event.getAction()) {

	            case MotionEvent.ACTION_DOWN:
	                x = event.getX();
	                y = event.getY();
	                startingX = x;
	                startingY = y;
	                previousX = x;
	                previousY = y;
	                lastdx = 0;
	                lastdy = 0;
	                hasMoved = false;
	                return true;
	            case MotionEvent.ACTION_MOVE:
	                x = event.getX();
	                y = event.getY();
	                if (mView.gameLogic.isActive()) {
	                    float dx = x - previousX;
	                    if (Math.abs(lastdx + dx) < Math.abs(lastdx) + Math.abs(dx) && Math.abs(dx) > RESET_STARTING
	                            &&  Math.abs(x - startingX) > SWIPE_MIN_DISTANCE) {
	                        startingX = x;
	                        startingY = y;
	                        lastdx = dx;
	                        previousDirection = veryLastDirection;
	                    }
	                    if (lastdx == 0) {
	                        lastdx = dx;
	                    }
	                    float dy = y - previousY;
	                    if (Math.abs(lastdy + dy) < Math.abs(lastdy) + Math.abs(dy) && Math.abs(dy) > RESET_STARTING
	                            && Math.abs(y - startingY) > SWIPE_MIN_DISTANCE) {
	                        startingX = x;
	                        startingY = y;
	                        lastdy = dy;
	                        previousDirection = veryLastDirection;
	                    }
	                    if (lastdy == 0) {
	                        lastdy = dy;
	                    }
	                    if (pathMoved() > SWIPE_MIN_DISTANCE * SWIPE_MIN_DISTANCE && !hasMoved) {
	                        boolean moved = false;
	                        //Vertical
	                        if (((dy >= SWIPE_THRESHOLD_VELOCITY && Math.abs(dy) >= Math.abs(dx)) || y - startingY >= MOVE_THRESHOLD) && previousDirection % 2 != 0) {
	                            moved = true;
	                            previousDirection = previousDirection * 2;
	                            veryLastDirection = 2;
	                            mView.gameLogic.move(2);
	                        } else if (((dy <= -SWIPE_THRESHOLD_VELOCITY && Math.abs(dy) >= Math.abs(dx)) || y - startingY <= -MOVE_THRESHOLD ) && previousDirection % 3 != 0) {
	                            moved = true;
	                            previousDirection = previousDirection * 3;
	                            veryLastDirection = 3;
	                            mView.gameLogic.move(0);
	                        }
	                        //Horizontal
	                        if (((dx >= SWIPE_THRESHOLD_VELOCITY && Math.abs(dx) >= Math.abs(dy)) || x - startingX >= MOVE_THRESHOLD) && previousDirection % 5 != 0) {
	                            moved = true;
	                            previousDirection = previousDirection * 5;
	                            veryLastDirection = 5;
	                            mView.gameLogic.move(1);
	                        } else if (((dx <= -SWIPE_THRESHOLD_VELOCITY  && Math.abs(dx) >= Math.abs(dy)) || x - startingX <= -MOVE_THRESHOLD) && previousDirection % 7 != 0) {
	                            moved = true;
	                            previousDirection = previousDirection * 7;
	                            veryLastDirection = 7;
	                            mView.gameLogic.move(3);
	                        }
	                        if (moved) {
	                            hasMoved = true;
	                            startingX = x;
	                            startingY = y;
	                        }
	                    }
	                }
	                previousX = x;
	                previousY = y;
	                return true;
	            case MotionEvent.ACTION_UP:
	                x = event.getX();
	                y = event.getY();
	                previousDirection = 1;
	                veryLastDirection = 1;
	                //"Menu" inputs
	                if (!hasMoved) {
	                    if (iconPressed(mView.sXNewGame, mView.sYIcons)) {
	                        mView.gameLogic.newGame();
	                    } else if (iconPressed(mView.sXUndo, mView.sYIcons)) {
	                        mView.gameLogic.revertUndoState();
	                    } else if (isTap(2) && inRange(mView.getGameRect().left, x, mView.getGameRect().right)
	                        && inRange(mView.getGameRect().top, x, mView.getGameRect().bottom) && mView.continueButtonEnabled) {
	                        mView.gameLogic.setEndlessMode();
	                    }
	                }
	        }
	        return true;
	    }

	    private float pathMoved() {
	        return (x - startingX) * (x - startingX) + (y - startingY) * (y - startingY);
	    }

	    private boolean iconPressed(int sx, int sy) {
	        return isTap(1) && inRange(sx, x, sx + mView.iconSize)
	                && inRange(sy, y, sy + mView.iconSize);
	    }

	    private boolean inRange(float starting, float check, float ending) {
	        return (starting <= check && check <= ending);
	    }

	    private boolean isTap(int factor) {
	        return pathMoved() <= mView.iconSize * factor;
	    }
	}

}