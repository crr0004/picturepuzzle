package me.tempus.picturepuzzle;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

public class Piece implements AABB, Drawable {

	//Global data
	//Static because each piece has to be the same dimensions
	private static int pieceWidth;
	private static int pieceHeight;
	private static int xPadding;
	private static int yPadding;


	//Piece specific
	public int i;
	public int j;
	public final int initI;
	public final int initJ;
	private final Rectangle rect;
	private final int[] textureRect;

	public Piece(int i, int j, int[] textureRect) {
		this.initI = i;
		this.initJ = j;
		this.i = i;
		this.j = j;
		this.rect = new Rectangle(i * pieceWidth, j * pieceHeight, pieceWidth, pieceHeight);
		this.textureRect = textureRect;  
	}
	/**
	 * Will update and return the area covered by this piece in the form of a rectangle
	 * See AABB for use in collision detection
	 * @return A rectangle representing the area covered by this piece
	 */
	public Rectangle getRect() {
		rect.x = i * pieceWidth;
		rect.y = j * pieceHeight;
		return rect;
	}

	public void preDraw(GL10 gl) {}

	public void draw(GL10 gl) {

		if(textureRect != null){
		((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, 
				GL11Ext.GL_TEXTURE_CROP_RECT_OES, textureRect, 0);
		}
		((GL11Ext) gl).glDrawTexfOES((i * pieceWidth), (j * pieceHeight), 0, pieceWidth - xPadding, pieceHeight - yPadding);
	}
	
	public void postDraw(GL10 gl) {}	

	public static void setPieceWidth(int pieceWidth) {
		Piece.pieceWidth = pieceWidth;
	}
	
	public static void setPieceHeight(int pieceHeight) {
		Piece.pieceHeight = pieceHeight;
	}
	
	public static void setxPadding(int xPadding) {
		Piece.xPadding = xPadding;
	}
	
	public static void setyPadding(int yPadding) {
		Piece.yPadding = yPadding;
	}
	
	public static int getPieceWidth() {
		return pieceWidth;
	}
	
	public static int getPieceHeight() {
		return pieceHeight;
	}
	
	public static int getxPadding() {
		return xPadding;
	}
	
	public static int getyPadding() {
		return yPadding;
	}
	
	@Override
	public String toString(){
		//TODO Change this to a string builder
		return "(" + i + ", " + j + ")" + " Width, Height: " + pieceHeight + ", " + pieceWidth + "(" + i*pieceWidth + ", " + j*pieceHeight + ")";
	}


}
