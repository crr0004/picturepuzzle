package me.tempus.picturepuzzle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
				Intent startGame = new Intent(thisContext, GameActivity.class);
				
				SharedPreferences preferences = getSharedPreferences(thisContext.getResources().getString(R.string.sharedPrefencesName), MODE_PRIVATE);
				startGame.putExtra("rowSize", preferences.getInt("rowSize", 3));
				startGame.putExtra("columnSize", preferences.getInt("columnSize", 3));
				
				/*
				 * Format of the grid when saved is that each position in the grid is given the piece that is there
				 * E.G 0 9 1 3 2 6
				 * Meaning that the first position (top left) has the piece that belongs in the last position, etc
				 */
				startGame.putExtra("gameGrid", preferences.getString("gameGrid", ""));
				startGame.putExtra("startTime", preferences.getLong("startTime", 0));
				
				startActivity(startGame);
					
			}
		});

	}

	
	private boolean isCachedGame() {
		SharedPreferences preferences = getSharedPreferences(this.getResources().getString(R.string.sharedPrefencesName), MODE_PRIVATE);
		return preferences.getString("gameGrid", "") != "" ? true : false;
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
