package coderr.kerwin.rlsb;

import net.youmi.android.AdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import coderr.kerwin.rlsb.model.TileCell;

public class MainActivity extends ActionBarActivity {

    protected final static String TAG = "MainActivity";
    
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String SCORE = "score";
    public static final String HIGH_SCORE = "high score temp";
    public static final String UNDO_SCORE = "undo score";
    public static final String CAN_UNDO = "can undo";
    public static final String UNDO_GRID = "undo";
    public static final String GAME_STATE = "game state";
    public static final String UNDO_GAME_STATE = "undo game state";
    public static int statusBarHeight = -1;

    private MainView view;
    private AdView adView;
    private FrameLayout.LayoutParams adLayoutParams;
    
    /**
     * 获取状态栏高度
     * @return
     */
    private int getStatusBarHeight(){
    	Rect frame = new Rect();  
    	getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
    	int statusBarHeight = frame.top;  
    	int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();  
        int titleBarHeight = contentViewTop - statusBarHeight;
        Log.v("test", "=-init-= statusBarHeight=" + statusBarHeight  
                + " contentViewTop=" + contentViewTop + " titleBarHeight="  
                + titleBarHeight); 
        return statusBarHeight;
    }
    
    /**
     * 添加菜单按钮
     */
    private void addMenuBtn(){
    	if(statusBarHeight == -1)	statusBarHeight = getStatusBarHeight();
    	FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(     
        FrameLayout.LayoutParams.WRAP_CONTENT,     
        FrameLayout.LayoutParams.WRAP_CONTENT);   
        params.topMargin = statusBarHeight + 20; 
        params.rightMargin = 20;
        params.gravity = Gravity.TOP | Gravity.RIGHT;   
        addContentView(LayoutInflater.from(MainActivity.this).inflate(R.layout.menubtn, null), params);
    }
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kitkatPpersonalizedSettings();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = new MainView(getBaseContext());
        view.setFitsSystemWindows(true);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        view.hasSaveState = settings.getBoolean("save_state", false);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("hasState")) {
                load();
            }
        }
        setContentView(view);
        
        // 初始化接口，应用启动的时候调用
 		// 参数：appId, appSecret, 调试模式
 		AdManager.getInstance(this).init("ec58ce714bf2d399", "6d2780fb47a99c60", false);
 		// 实例化LayoutParams(重要)
 		adLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		// 设置广告条的悬浮位置
 		adLayoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT; // 这里示例为右下角
    }
    
    /**
	 * Android KITKAT 个性化设置
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void kitkatPpersonalizedSettings() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        	// 如果不希望 APP 的内容被上拉到状态列 (Status bar) 的话，要记得在介面 (Layout) XML 档中，最外面的那层，要再加上一个属性 fitsSystemWindows 为 true
        	// 在介面的根层加入 android:fitsSystemWindows="true" 这个属性，这样就可以让内容介面从 Action Bar 下方开始。
        	// Translucent status bar
        	getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        	// Translucent navigation bar
        	getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//		}
//        if(getActionBar() != null)	getActionBar().hide();
	}
	
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU) {
            //Do nothing
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            view.game.move(2);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            view.game.move(0);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            view.game.move(3);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            view.game.move(1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("hasState", true);
        save();
    }

    @Override
    protected void onPause() {
        super.onPause();
        save();
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	load();
    	if(getResources().getConfiguration().orientation == 1){
    		view.post(new Runnable() {  
                public void run() {  
                	addMenuBtn();
                }  
            });
    		// 实例化广告条
    		adView = new AdView(this, AdSize.FIT_SCREEN);
    		// 调用Activity的addContentView函数
    		addContentView(adView, adLayoutParams);
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);//option.xml定义在res/menu/目录下
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
            	
                break; //处理完以后这里也可以return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
	public void menuBtnClicked(View view){
		if(adView.isShown())	adView.setVisibility(View.GONE);
		else{
			// 实例化广告条
			adView = new AdView(this, AdSize.FIT_SCREEN);
			// 调用Activity的addContentView函数
	 		addContentView(adView, adLayoutParams);
		}
    }

    private void save() {
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
    	SharedPreferences.Editor editor = settings.edit();
    	TileCell[][] field = view.game.grid.field;
    	TileCell[][] undoField = view.game.grid.undoField;
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
    	editor.putLong(SCORE, view.game.score);
    	editor.putLong(HIGH_SCORE, view.game.highScore);
    	editor.putLong(UNDO_SCORE, view.game.lastScore);
    	editor.putBoolean(CAN_UNDO, view.game.canUndo);
    	editor.putInt(GAME_STATE, view.game.gameState);
    	editor.putInt(UNDO_GAME_STATE, view.game.lastGameState);
    	editor.commit();
    }

    private void load() {
        //Stopping all animations
        view.game.aGrid.cancelAnimationCells();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        for (int xx = 0; xx < view.game.grid.field.length; xx++) {
            for (int yy = 0; yy < view.game.grid.field[0].length; yy++) {
                int value = settings.getInt(xx + " " + yy, -1);
                if (value > 0) {
                    view.game.grid.field[xx][yy] = new TileCell(xx, yy, value);
                } else if (value == 0) {
                    view.game.grid.field[xx][yy] = null;
                }

                int undoValue = settings.getInt(UNDO_GRID + xx + " " + yy, -1);
                if (undoValue > 0) {
                    view.game.grid.undoField[xx][yy] = new TileCell(xx, yy, undoValue);
                } else if (value == 0) {
                    view.game.grid.undoField[xx][yy] = null;
                }
            }
        }

        view.game.score = settings.getLong(SCORE, view.game.score);
        view.game.highScore = settings.getLong(HIGH_SCORE, view.game.highScore);
        view.game.lastScore = settings.getLong(UNDO_SCORE, view.game.lastScore);
        view.game.canUndo = settings.getBoolean(CAN_UNDO, view.game.canUndo);
        view.game.gameState = settings.getInt(GAME_STATE, view.game.gameState);
        view.game.lastGameState = settings.getInt(UNDO_GAME_STATE, view.game.lastGameState);
    }
    
}
