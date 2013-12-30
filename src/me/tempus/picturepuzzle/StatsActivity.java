package me.tempus.picturepuzzle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class StatsActivity extends Activity {

	private List<Long> times = new ArrayList<Long>(5);
	private ListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);
		
		Intent starter = getIntent();
		
		ListView statsList = (ListView)findViewById(R.id.statsListView);
		adapter = new ArrayAdapter<Long>(this, android.R.layout.simple_list_item_1, times);
		statsList.setAdapter(adapter);
		
		if(starter != null){
			addTime(starter.getLongExtra("winTime", -1));
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
			for(Long time : this.times){
				output.append(time).append('\n');
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

	public void addTime(Long time){
		if(time != -1){
			times.add(time);
		}
	}
}

class GetTimes extends AsyncTask<StatsActivity, Object, Object>{

	@Override
	protected Object doInBackground(StatsActivity... caller) {
		
		BufferedReader reader = null;
		String fileName = caller[0].getResources().getString(R.string.statsFile);
		
		
		String currentLine;
		try {
			reader = new BufferedReader(new FileReader(caller[0].openFileInput(fileName).getFD()));
			currentLine = reader.readLine();
			while(currentLine != null){
				caller[0].addTime(Long.parseLong(currentLine));
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
	
}
