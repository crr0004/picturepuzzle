package me.tempus.picturepuzzle;

import android.os.SystemClock;

public class PieceTransitionAnimation {
	public int startingPosX;
	public int startingPosY;
	
	public int endPosX;
	public int endPosY;
	
	public Piece piece;

	private long startTime;
	private long endTime;
	
	//Used in the animation
	private long time = 0; 
	private float gradient = 0;
	private float currentTime;
	public PicturePuzzle caller;
	
	public PieceTransitionAnimation(PicturePuzzle caller) {
		this.caller = caller;
	}
	
	/**
	 * A method to set up the data for the transition but also serves as starting the animation
	 * @param pieceToAnimate
	 * @param endPiece
	 * @param time The duration of the transition
	 */
	public void setAnimationData(Piece pieceToAnimate, Piece endPiece, long time) {
		this.piece = pieceToAnimate;
		this.endPosX = endPiece.i * Piece.getPieceWidth();
		this.endPosY = endPiece.j * Piece.getPieceHeight();
		
		this.startingPosX = piece.i * Piece.getPieceWidth();
		this.startingPosY = piece.j * Piece.getPieceHeight();
		
		this.time = time;
		this.startTime = SystemClock.uptimeMillis();
		this.endTime = startTime + time;
		
	}

	/**
	 * 
	 * @return if the animation is finished or not
	 */
	public boolean update(){
		currentTime = SystemClock.uptimeMillis();
		
		if(piece == null)
			return false;
		
		if(currentTime < endTime && piece != null){			
			
			gradient = (currentTime - startTime)/time;
			piece.x = (((endPosX - startingPosX) * gradient) + startingPosX);
			piece.y = (((endPosY - startingPosY) * gradient) + startingPosY);
		}else{
			caller.swapPiece(piece, caller.getFreePiece(), caller.getPieces());
			piece = null;
			return true;
		}
		
		
		return false;
	}

	public void kill() {
		// TODO Auto-generated method stub
		caller.swapPiece(piece, caller.getFreePiece(), caller.getPieces());
		currentTime = endTime;
		piece = null;
	}
}
