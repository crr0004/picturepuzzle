package me.tempus.picturepuzzle;

import javax.microedition.khronos.opengles.GL10;

public interface Drawable {

	public abstract void preDraw(GL10 gl);
	
	public abstract void draw(GL10 gl);
	
	public abstract void postDraw(GL10 gl);
}
