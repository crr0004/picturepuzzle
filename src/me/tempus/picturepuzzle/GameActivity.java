package me.tempus.picturepuzzle;

import me.tempus.interfaces.RenderHost;
import android.app.Activity;
import android.content.Intent;
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

public class GameActivity extends Activity implements OnClickListener, OnTouchListener, OnKeyListener {

	private final StringBuffer timeTextBuffer = new StringBuffer("00:00:00");
	private long upTime = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		Intent intent = getIntent();
		int rowSize = 3;
		int columnSize = 3;
		
		if(intent != null){
			rowSize = intent.getIntExtra("rowSize", 3);
			columnSize = intent.getIntExtra("columnSize", 3);
		}
		
		GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.glView);
		glSurfaceView.setFocusable(true);
		glSurfaceView.setOnClickListener(this);
		glSurfaceView.setOnTouchListener(this);
		glSurfaceView.setOnKeyListener(this);
		
		Render render = new Render(50);
		final PicturePuzzle puzzleGame = new PicturePuzzle(this, render, rowSize, columnSize, R.drawable.nintendo_characters_nintendo_512x512);
		render.setHost(puzzleGame);
		render.setRawScreenHeight(getWindowManager().getDefaultDisplay().getHeight());
		
		glSurfaceView.setRenderer(render);		
		new Thread(puzzleGame).start();

		final TextView timeTextField = (TextView)findViewById(R.id.timeTextField);
		final Handler handler = new Handler();
		
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
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		InputManager.addKeyEvent(v, keyCode, event);
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		InputManager.addMotionEvent(v, event);
		return true;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
	}

	
}
