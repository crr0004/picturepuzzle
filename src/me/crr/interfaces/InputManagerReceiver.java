package me.crr.interfaces;

import android.view.KeyEvent;
import android.view.MotionEvent;

public interface InputManagerReceiver {

	public void onTouch(MotionEvent e);
	public void onKeyPressed(KeyEvent e);
	
}
