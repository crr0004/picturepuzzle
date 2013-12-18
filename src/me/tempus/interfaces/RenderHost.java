package me.tempus.interfaces;

import javax.microedition.khronos.opengles.GL10;

public interface RenderHost {

	/**
	 * Called at the end of the screen being changed
	 */
	public void screenChanged(GL10 gl);
	/**
	 * Called at the end of the screen being created
	 */
	public void screenCreated(GL10 gl);
	/**
	 * Called when the frame is starting drawing
	 */
	public void frameStarted(GL10 gl);
	/**
	 * Called when the frame has ended drawing
	 */
	public void frameEnded(GL10 gl);
}
