package me.tempus.picturepuzzle;

import me.tempus.interfaces.InputManagerReceiver;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class InputManager {

	
	public static final FixedArray<MotionEvent> events = new FixedArray<MotionEvent>(50);
	
	public static final FixedArray<KeyEvent> keyEvents = new FixedArray<KeyEvent>(15);
	
	/**
	 * Will go through all the touch and events that have been posted and push them onto the callback supplied
	 * @param host The callback
	 */
	public static void processInput(InputManagerReceiver host){
		/**
		 * TODO Could be optimized
		 * Put it all in one loop, it will reduced iterations and checks
		 */
		final int size = events.contentIndex+1;
		final Object[] eventsContents = events.getContents();
		for(int i = 0; i < size; i++){
			final MotionEvent event = (MotionEvent)eventsContents[i];
			if(event != null){
				host.onTouch(event);
				events.remove(i, true);
			}
		}
		events.reset();
		final int keySize = keyEvents.contentIndex+1;
		final Object[] keyEventContents = keyEvents.getContents();
		for(int i = 0; i < keySize; i++){
			final KeyEvent event = (KeyEvent)keyEventContents[i];
			if(event != null){
				host.onKeyPressed(event);
				keyEvents.remove(i, true);
			}
		}
		keyEvents.reset();
	}
	
	
	/**
	 * Adds a motion even to the list, if there are too many events, it will not add
	 * @param v
	 * @param event
	 */
	public static void addMotionEvent(View v, MotionEvent event){
		events.add(event);
	}
	
	/**
	 * Add keyEvent to key events list
	 * @param v View
	 * @param keyCode The key code
	 * @param event The actual event
	 */
	public static void addKeyEvent(View v, int keyCode, KeyEvent event){
		
		keyEvents.add(event);
	}
	
	/**
	 * Adds a event to the Velocity, basically redundant and pointless
	 * @param event
	 */
	//@Deprecated
	public static void addVelocityEvent(MotionEvent event){
		//if(event != null)
			//vTracker.addMovement(event);
	}
}
