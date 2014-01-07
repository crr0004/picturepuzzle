package me.crr.picturepuzzle;

import me.tempus.picturepuzzle.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Button;

public class GameActivity extends Activity implements OnClickListener, OnTouchListener, OnKeyListener {

	private final StringBuffer timeTextBuffer = new StringBuffer("00:00:00");
	private long upTime = 0;
	PicturePuzzle puzzleGame;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		Intent intent = getIntent();
		int rowSize = 3;
		int columnSize = 3;
		String grid = null;
		long startTime = SystemClock.uptimeMillis();
		
		if(savedInstanceState != null){
			rowSize = savedInstanceState.getInt("me.crr.picturepuzzle.rowSize", 3);
			columnSize = savedInstanceState.getInt("me.crr.picturepuzzle.columnSize", 3);
			grid = savedInstanceState.getString("me.crr.picturepuzzle.gameGrid");
			startTime = savedInstanceState.getLong("me.crr.picturepuzzle.startTime", SystemClock.uptimeMillis());
		}else if(intent != null){
			rowSize = intent.getIntExtra("me.crr.picturepuzzle.rowSize", 3);
			columnSize = intent.getIntExtra("me.crr.picturepuzzle.columnSize", 3);
			grid = intent.getStringExtra("me.crr.picturepuzzle.gameGrid");
			startTime = intent.getLongExtra("me.crr.picturepuzzle.startTime", SystemClock.uptimeMillis());
		}
		
		
		GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.glView);
		glSurfaceView.setFocusable(true);
		glSurfaceView.setOnClickListener(this);
		glSurfaceView.setOnTouchListener(this);
		glSurfaceView.setOnKeyListener(this);
		
		Render render = new Render(50);
		
		if(grid != ""){			
			puzzleGame = new PicturePuzzle(this, render, rowSize, columnSize, grid, R.drawable.nintendo_characters_nintendo_512x512);
		}else{
			puzzleGame = new PicturePuzzle(this, render, rowSize, columnSize, R.drawable.nintendo_characters_nintendo_512x512);
		}
		puzzleGame.setStartTime(startTime);
		
		render.setHost(puzzleGame);
		render.setRawScreenHeight(getWindowManager().getDefaultDisplay().getHeight());
		
		glSurfaceView.setRenderer(render);		
		new Thread(puzzleGame).start();

		final TextView timeTextField = (TextView)findViewById(R.id.timeTextField);
		final Handler handler = new Handler();
		
		((Button)findViewById(R.id.resestButton)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				puzzleGame.resetGame();				
			}
		});
		
		Runnable setText = new Runnable() {

			@Override
			public void run() {
				  upTime = SystemClock.uptimeMillis() - puzzleGame.getStartTime();
				  timeTextBuffer.delete(0, timeTextBuffer.length());
				  timeTextBuffer.append((upTime/(1000*60*60)%60)).append(":")
				  .append((int)(upTime/(1000*60)%60)).append(":")
				  .append((int)(upTime/1000)%60); //Seconds
				  
				  timeTextField.setText(timeTextBuffer);
				 
				 handler.postDelayed(this, 1000);
				 
			}
		};
		handler.post(setText);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}

	@Override
	public void onPause(){
		super.onPause();
		SharedPreferences prefences = getSharedPreferences(this.getResources().getString(R.string.sharedPrefencesName), MODE_PRIVATE);
		SharedPreferences.Editor editor = prefences.edit();
		
		editor.putBoolean("me.crr.picturepuzzle.continue", true);
		
		editor.commit();
	}
	
	@Override
	public void onSaveInstanceState(Bundle save){
		super.onSaveInstanceState(save);
		
		save.putInt("me.crr.picturepuzzle.rowSize", puzzleGame.getRowSize());
		save.putInt("me.crr.picturepuzzle.columnSize", puzzleGame.getColumnSize());
		save.putString("me.crr.picturepuzzle.gameGrid", puzzleGame.getGrid(puzzleGame.getPieces(), puzzleGame.getFreePiece(), puzzleGame.getRowSize()));
		save.putLong("me.crr.picturepuzzle.startTime", puzzleGame.getStartTime());
	}
	
	@Override
	public void onStop(){
		super.onStop();
		SharedPreferences prefences = getSharedPreferences(this.getResources().getString(R.string.sharedPrefencesName), MODE_PRIVATE);
		SharedPreferences.Editor editor = prefences.edit();
		
		editor.putInt("me.crr.picturepuzzle.rowSize", puzzleGame.getRowSize());
		editor.putInt("me.crr.picturepuzzle.columnSize", puzzleGame.getColumnSize());
		editor.putString("me.crr.picturepuzzle.gameGrid", puzzleGame.getGrid(puzzleGame.getPieces(), puzzleGame.getFreePiece(), puzzleGame.getRowSize()));
		editor.putLong("sme.crr.picturepuzzle.tartTime", puzzleGame.getStartTime());
		
		editor.commit();
		puzzleGame.setDone(true);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		
		InputManager.addKeyEvent(v, keyCode, event);
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		InputManager.addMotionEvent(v, event);
		synchronized (this) {
			try {
				wait(16);//Prevents the touch events from flooding my handler
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public void onClick(View view) {
	}

	
}
