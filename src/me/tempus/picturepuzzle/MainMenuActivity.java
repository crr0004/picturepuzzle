package me.tempus.picturepuzzle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class MainMenuActivity extends Activity {

	private OnTouchListener mainMenuButtonBackgroundListener = new OnTouchListener() {			
		@Override
		public boolean onTouch(View view, MotionEvent event) {				
			final int action = event.getAction();
			switch (action){
			
			case MotionEvent.ACTION_DOWN:
				view.setBackgroundResource(R.drawable.mainmenu_buttonbackground_gradient_off);
				break;
			case MotionEvent.ACTION_UP:
				view.setBackgroundResource(R.drawable.mainmenu_buttonbackground_gradient_on);
				break;
			
			}
			
			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainmenu);

		/*
		 * Sets the buttons backgrounds to change when pressed
		 */
		Button enterGameButton = (Button)findViewById(R.id.enterGameButton);
		Button continueGameButton = (Button)findViewById(R.id.continueGameButton);
		Button statsButton = (Button)findViewById(R.id.statsButton);
		
		enterGameButton.setOnTouchListener(mainMenuButtonBackgroundListener );
		continueGameButton.setOnTouchListener(mainMenuButtonBackgroundListener);
		statsButton.setOnTouchListener(mainMenuButtonBackgroundListener);
				
		final Context thisContext = (Context)this;
		enterGameButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				//TODO NewGame dialog goes here
				//Has 3 list options 3x3 4x4 5x5
				AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
				builder.setTitle(R.string.difficultyPromt);
				
				builder.setItems(R.array.difficulties, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(thisContext, GameActivity.class);
						
						switch(which){
						case 0:
							intent.putExtra("rowSize", 3);
							intent.putExtra("columnSize", 3);
							break;
						case 1:
							intent.putExtra("rowSize", 4);
							intent.putExtra("columnSize", 4);
							break;
						case 2:
							intent.putExtra("rowSize", 5);
							intent.putExtra("columnSize", 5);
							break;
						}
						startActivity(intent);
					}
				});
				builder.create().show();
			}
		});
		
		statsButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent goToStatsActivity = new Intent(thisContext, StatsActivity.class);
				startActivity(goToStatsActivity);
			}
			
		});
		
		if(!isCachedGame()){
			continueGameButton.setVisibility(View.GONE);
		}
		
		continueGameButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				File cachedGame = new File(thisContext.getCacheDir(), thisContext.getResources().getString(R.string.cacheGameDir));
				if(cachedGame.exists()){
					try{
						FileInputStream in = new FileInputStream(cachedGame);
						Intent startGame = new Intent(thisContext, GameActivity.class);
						//startGame.putExtra("rowSize", in.read(buffer));
					}catch (FileNotFoundException e){
						Log.e("MainMenu", "Cached game has tried to opened when it does not exist");
					}finally{
						
					}
					
				}
			}
		});
	}

	
	private boolean isCachedGame() {
		File cache = new File(this.getCacheDir(), getResources().getString(R.string.cacheGameDir));
		return cache.exists();
	}


	@Override
	public void onResume(){
		
		View logoView = findViewById(R.id.headSpaceLogo);
		View buttons = findViewById(R.id.mainMenuButtons);
		
		Animation buttonUpAnimation = AnimationUtils.loadAnimation(this, R.anim.buttonmoveup);
		Animation logoFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.puzzlepiecefadein);
		
		buttons.startAnimation(buttonUpAnimation);
		logoView.startAnimation(logoFadeInAnimation);
		
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

}
