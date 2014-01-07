package me.crr.picturepuzzle;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import me.crr.interfaces.AABB;

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
	public int initI;
	public int initJ;
	public float x;
	public float y;
	public final Rectangle rect;
	private final int[] textureRect;

	public Piece(int i, int j, int[] textureRect) {
		this.initI = i;
		this.initJ = j;
		this.i = i;
		this.j = j;
		this.x = i *pieceWidth;
		this.y = j *pieceHeight;
		this.rect = new Rectangle(i * pieceWidth, j * pieceHeight, pieceWidth, pieceHeight);
		this.textureRect = textureRect;  
	}
	
	public Piece(int i, int j, int initI, int initJ, int[] textureRect){
		this.i = i;
		this.j = j;
		this.rect = new Rectangle(i * pieceWidth, j * pieceHeight, pieceWidth, pieceHeight);
		this.textureRect = textureRect; 
		this.initI = initI;
		this.initJ = initJ;
	}

	@Override
	public Rectangle getRect(){
		rect.x = x;
		rect.y = y;
		return rect;
	}
	
	public void preDraw(GL10 gl) {}

	public void draw(GL10 gl) {

		if(textureRect != null){
		((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, 
				GL11Ext.GL_TEXTURE_CROP_RECT_OES, textureRect, 0);
		}
		((GL11Ext) gl).glDrawTexfOES((int)x, (int)y, 0, pieceWidth - xPadding, pieceHeight - yPadding);
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
	
	public void setI(int i){
		this.i = i;
		this.x = i * pieceWidth;
	}
	
	public void setJ(int j){
		this.j = j;
		this.y = j * pieceHeight;
	}
	
	@Override
	public String toString(){
		//TODO Change this to a string builder
		return "(" + i + ", " + j + ")" + " Width, Height: " + pieceHeight + ", " + pieceWidth + " (" + i*pieceWidth + ", " + j*pieceHeight + ")";
	}




}
