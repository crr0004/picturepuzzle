package me.tempus.picturepuzzle;


public class Rectangle {

	public int width;
	public int height;
	public float x;
	public float y;
	
	/**
	 * @param width
	 * @param height
	 * @param x
	 * @param y
	 */
	public Rectangle(int x, int y, int width, int height) {
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Tests if the (x,y) point is inside the rectangle
	 * @param x
	 * @param y
	 * @param rect
	 * @return If (x, y) is inside rectangle
	 */
	public static boolean pointIntersects(float x, float y, Rectangle rect){
		if(x >= rect.x && x <= rect.x + rect.width){
			if(y >= rect.y && y <= rect.y + rect.height){
				return true;
			}
		}
		return false;
	}
	
}
