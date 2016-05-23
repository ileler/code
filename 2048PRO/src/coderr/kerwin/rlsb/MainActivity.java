package coderr.kerwin.rlsb;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class MainActivity extends Activity {

    protected final static String TAG = "MainActivity";
    
    
    public static int statusBarHeight = -1;

    private GameView gameView;
    
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kitkatPpersonalizedSettings();

        setContentView(R.layout.temp);
        
        gameView = new GameView(this);
        ((ViewGroup) getRootView()).addView(gameView);
//        ((LinearLayout)(findViewById(R.id.viewly))).addView(gameView);
        if (savedInstanceState != null) {
        	if (savedInstanceState.getBoolean("hasState")) {
        		gameView.load();
        	}
        }
    }
	
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("hasState", true);
        gameView.save();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.save();
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	gameView.load();
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
    
    public View getRootView(){
    	return ((ViewGroup) (getWindow().getDecorView().findViewById(android.R.id.content))).getChildAt(0);
    }
    
}
