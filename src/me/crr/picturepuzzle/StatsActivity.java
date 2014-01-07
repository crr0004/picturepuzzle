package me.crr.picturepuzzle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.tempus.picturepuzzle.R;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

public class StatsActivity extends Activity {

	private List<WinTime> times = new ArrayList<WinTime>(5);
	private ListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);
		
		Button goMainMenu = (Button) findViewById(R.id.goToMainMenuButton);
		goMainMenu.setOnTouchListener(new OnTouchListener() {			
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
		});
		
		goMainMenu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent goToMainMenu = new Intent(StatsActivity.this, MainMenuActivity.class);
				startActivity(goToMainMenu);
			}
		});
		
		Intent starter = getIntent();
		
		ListView statsList = (ListView)findViewById(R.id.statsListView);
		adapter = new ArrayAdapter<WinTime>(this, android.R.layout.simple_list_item_1, times);
		statsList.setAdapter(adapter);
		
		if(starter != null){
			addTime((WinTime) starter.getSerializableExtra("me.crr.picturepuzzle.winTime"));
		}
		
		new GetTimes().execute(this);
	}

	@Override
	public void onStop(){
		super.onStop();
		
		String fileName = getResources().getString(R.string.statsFile);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(openFileOutput(fileName, MODE_PRIVATE).getFD()));
			StringBuilder output = new StringBuilder();
			for(WinTime time : this.times){
				output.append(time.getTime()).append("~").append(time.getName()).append('\n');
			}
			writer.write(output.toString());
		} catch (FileNotFoundException e) {
			Log.e("StatsActivityL56", "Stats file not found");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(writer != null){
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.stats, menu);
		return true;
	}

	public void addTime(WinTime time){
		if(time != null){
			times.add(time);
		}
	}
}

class GetTimes extends AsyncTask<StatsActivity, WinTime, Object>{

	private StatsActivity caller;
	
	@Override
	protected Object doInBackground(StatsActivity... callers) {
		this.caller = callers[0];
		
		BufferedReader reader = null;
		String fileName = caller.getResources().getString(R.string.statsFile);
		
		
		String currentLine;
		try {
			reader = new BufferedReader(new FileReader(caller.openFileInput(fileName).getFD()));
			currentLine = reader.readLine();
			while(currentLine != null){
				String[] timeData = currentLine.split("~");
				if(timeData.length == 2){
					publishProgress(new WinTime(Long.parseLong(timeData[0]), timeData[1]));
				}else if(timeData.length == 1){
					publishProgress(new WinTime(Long.parseLong(timeData[0]), "N/A"));
				}				
				currentLine = reader.readLine();
			}
		}catch(FileNotFoundException e){
			Log.d("StatsActivityL105", "Stats File Not found during reading");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(reader != null){
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		return null;
	}
	
	@Override
	protected void onProgressUpdate(WinTime...times){
		caller.addTime(times[0]);
	}
	
}
