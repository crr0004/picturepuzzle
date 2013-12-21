package me.tempus.picturepuzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import me.tempus.interfaces.InputManagerReceiver;
import me.tempus.interfaces.RenderHost;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PicturePuzzle implements Runnable, RenderHost, InputManagerReceiver {

	private final Piece freePiece = new Piece(0, 0, new int[] { 0, 0, 0,
			0 });

	private final Piece[] activePieces = new Piece[4];

	private int rowSize = 3;
	private int columnSize = 3;

	private static boolean pieceSelected = false;
	private static Piece piece = null;
	private List<Piece> pieces;

	private Render render;

	private boolean done = false;

	private long startTime = 0;

	private int pictureID = -1;

	private GameActivity gameActivity;

	private Texture picture;

	private String grid;

	public PicturePuzzle(GameActivity gameActivity, Render render, int rowSize, int columnSize, int pictureID){
		this.render = render;
		this.rowSize = rowSize;
		this.columnSize = columnSize;
		this.pictureID  = pictureID;
		this.gameActivity = gameActivity;
	}

	public PicturePuzzle(GameActivity gameActivity2, Render render2,
			int rowSize2, int columnSize2, String grid,
			int nintendoCharactersNintendo512x512) {
		this(gameActivity2, render2, rowSize2, columnSize2, nintendoCharactersNintendo512x512);
		this.grid = grid;
	}

	public void init() {
		// Inflate the layout for this fragment
		startTime = SystemClock.uptimeMillis();
		
	}
	

	public void run() {
		
		init();
		waitThis();
		if(grid != null){
			pieces = loadGrid(grid, picture, render, freePiece, rowSize, columnSize);
		}else{
			setUpGrid(rowSize, columnSize, picture.width, picture.height);
			shuffPieces(pieces);
		}
		long currentTime = 0;
		long delta = 0;
		while(!done){
			currentTime = SystemClock.currentThreadTimeMillis();
			
			activePieces[0] = getActivePiece(freePiece.i, freePiece.j + 1);
			activePieces[1] = getActivePiece(freePiece.i, freePiece.j - 1);
			activePieces[2] = getActivePiece(freePiece.i + 1, freePiece.j);
			activePieces[3] = getActivePiece(freePiece.i - 1, freePiece.j);
			
			
			InputManager.processInput(this);
			render.addDrawables(pieces);
			//if (render != null)				
				render.beginFrame();
			delta = SystemClock.currentThreadTimeMillis() - currentTime;
			if(delta < 32){ // 32 for 30 frames for second
				try {
					synchronized (this) {
						wait(32 - delta);
					}					
				} catch (InterruptedException e) {}
			}
		}
	}

	public void onResume() {
		render.notifyThis();
	}

	public void onPause() {
		render.notifyThis();
	}

	/**
	 * Method to set all the references and variables to null Helps the GC clean
	 * up
	 */
	public void destroySelf() {
		
		render = null;
		piece = null;
		pieces = null;
		//freePiece.i = 0;
		//freePiece.j = 0;
		activePieces[0] = null;
		activePieces[1] = null;
		activePieces[2] = null;
		activePieces[3] = null;
	}

	public void setUpButtons(View view) {

		final PicturePuzzle host = this; // Dirty work around for accessing within a button onClick
		
		Button resetButton = (Button) view
				.findViewById(R.id.resestButton);

		resetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				host.shuffPieces(pieces);
				startTime = SystemClock.uptimeMillis();
			}
		});
	}

	public void preUpdate(GameActivity game, Render render) {
		
	}


	/**
	 * Created to prevent array out of bounds when getting the different active
	 * pieces
	 * 
	 * @param i
	 * @param j
	 * @return The active piece
	 */
	public Piece getActivePiece(int i, int j) {
		if(i < 0 || j < 0) return null;
		final int pos = getPositionInArray(i, j, rowSize);
		if (pieces == null)
			return null;
		if (pos >= 0 && pos < pieces.size()) {

			return pieces.get(pos);
		}
		return null;

	}

	/**
	 * Sets up the grid of pieces for the image
	 * 
	 * @param textureWidth
	 * @param textureHeight
	 */
	private void setUpGrid(int rowPieces, int columnPieces,
			int textureWidth, int textureHeight) {
		Log.d("PicturePuzzle", "RowPieces, ColumnPieces" + rowPieces + ", " + columnPieces);
		pieces = new ArrayList<Piece>(rowPieces * columnPieces);
		Piece.setPieceWidth(render.getScreenWidth() / rowPieces);
		Piece.setPieceHeight(render.getScreenHeight() / columnPieces);
		Piece.setxPadding(2);
		Piece.setyPadding(2);
		
		final int tPieceWidth = textureWidth / (rowPieces);
		final int tPieceHeight = textureHeight / (columnPieces);
		
		for (int i = 0; i < rowPieces; i++) {
			for (int j = 0; j < columnPieces; j++) {
				final int position = getPositionInArray(i, j, rowPieces);
				final Piece piece = new Piece(i, j, new int[] {
						i * tPieceWidth, textureHeight - (j * tPieceHeight),
						tPieceWidth, -tPieceHeight });
				pieces.add(position, piece);
			}
		}
		pieces.set(getPositionInArray(0, 0, rowPieces), null);
		freePiece.getRect().height = Piece.getPieceHeight();
		freePiece.getRect().width = Piece.getPieceWidth();

		
	}

	/**
	 * Loads the game grid given a setup of coordinates
	 * @param grid The formatted string so that each position maps a grid position. Free set of cords is the free piece
	 * E.G 0 9:1 1 4:3 2 5:4 Meaning that the first position has the last piece in it which belongs in the first position
	 */
	private List<Piece> loadGrid(String grid, Texture picture, Render render, Piece freePiece, int rowSize, int columnSize) {
		
		setUpGrid(rowSize, columnSize, picture.width, picture.height);
		
		String[] posistions = grid.split(" ");
		for(int k = 0; k < posistions.length; k++){
			if(posistions[k].length() == 2){
				swapPiece(pieces.get(k), freePiece);
			}else{
				swapPiece(pieces.get(k), pieces.get(Integer.parseInt(posistions[k])));
			}
		}
		
		return pieces;
		
	}
	
	/**
	 * To check if the pieces are in the correct places The method will take
	 * care of the case when the game is won
	 * 
	 * @param piece Piece array of the current state of the pieces
	 */
	private void checkIfComplete(List<Piece> pieces) {
		Piece currentPiece = null;
		for (int i = 0; i < pieces.size(); i++) {
			currentPiece = pieces.get(i);
			if (currentPiece != null) {
				if (getPositionInArray(currentPiece.initI, currentPiece.initJ,
						rowSize) != i) {
					return;
				}
			}
		}
		showWinGameDialog();
	}

	private boolean validGrid(List<Piece> pieces, int rowSize){
		for(int i = 0; i < pieces.size(); i++){
			Piece p = pieces.get(i);
			if(p != null){
				int piecePos = getPositionInArray(p.i, p.j, rowSize);
				if(piecePos == getPositionInArray(freePiece.i, freePiece.j, rowSize)){
					return false;
				}
				for(int k = 0; k < pieces.size(); k++){
					
					if(i != k){
						Piece c = pieces.get(k);
						if(c != null){
							if(piecePos == getPositionInArray(c.i, c.j, rowSize)){
								return false;
							}
						}
					}
				}
			}
		}
		
		return true;
	}
	
	private void showWinGameDialog() {
		// TODO Auto-generated method stub
		Runnable winGame = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				AlertDialog.Builder winGameDialog = new AlertDialog.Builder(gameActivity);
				winGameDialog.setTitle(R.string.wingamedialogtitle);
				winGameDialog.setMessage(R.string.wingame);
				winGameDialog.create().show();
			}
		};
		gameActivity.runOnUiThread(winGame);
	}

	/**
	 * 
	 * @param piece
	 *            The current Piece to move
	 * @param i
	 *            Row position to move to
	 * @param j
	 *            Column position to move to
	 */
	public void movePieceTo(Piece piece, Piece freePiece) {
		final int freePos = getPositionInArray(freePiece.i, freePiece.j,
				rowSize);
		final int posTry = getPositionInArray(piece.i, piece.j, rowSize);
		if (posTry == freePos) {
			swapPiece(piece, freePiece);
			checkIfComplete(pieces);
		}
	}

	/**
	 * Swap two pieces positions
	 * 
	 * @param p1
	 *            Piece one
	 * @param p2
	 *            Piece two
	 */
	public void swapPiece(Piece p1, Piece p2) {
		if(p1 == null || p2 == null) return;
		
		final int pos1 = getPositionInArray(p1.i, p1.j, rowSize);
		final int pos2 = getPositionInArray(p2.i, p2.j, rowSize);
		
		if(pos1 == pos2) return;
		
		final Piece p = pieces.get(pos1);
		pieces.set(pos1, pieces.get(pos2));
		pieces.set(pos2, p);
		final int i1 = p1.i;
		final int j1 = p1.j;
		p1.i = p2.i;
		p1.j = p2.j;
		p2.i = i1;
		p2.j = j1;
		
		if(!validGrid(pieces, this.getRowSize())){
			Log.e("PicturePuzzleL357", "Grid is not valid");
		}

	}

	/**
	 * 
	 * @param i
	 *            Zero indexed row position
	 * @param j
	 *            Zero indexed Column position
	 * @param n
	 *            Row size
	 * @return The position in the array
	 */
	public static int getPositionInArray(int i, int j, int n) {
		return ((n * i) + j);
	}
	
	public static int[] getCoordinates(int position, int rowSize){
		int[] cords = new int[2];		
		cords[0] = (int)(position / rowSize);
		cords[1] = position - (rowSize * cords[0]);
		return cords;
	}

	/**
	 * Called each frame to process all the touch events the MainActivity thread
	 * as given the game thread
	 * 
	 * @param activePieces
	 *            The pieces surrounding the empty slot
	 * @param event
	 *            The touch event
	 * @return A piece if it's being touched
	 */
	public Piece processTouchDownEvent(Piece[] activePieces, MotionEvent event) {
		if (event == null) {
			return null;
		}
		final int size = activePieces.length;
		final float x = event.getRawX();
		final float y = render.getRawScreenHeight() - event.getRawY();
		Piece activePiece = null;
		for (int i = 0; i < size; i++) {
			final Piece currentPiece = activePieces[i];
			if (currentPiece != null) {
				final boolean value = Rectangle.pointIntersects(x, y,
						currentPiece.getRect());
				if (value) {
					activePiece = currentPiece;
				}
			}
		}
		return activePiece;
	}

	/**
	 * 
	 * @param list
	 *            The list of objects to be shuffled
	 * @return Returns the shuffled version of the list
	 */
	public List<Piece> shuffPieces(List<Piece> list) {

		final Random random = new Random();
		for (int i = list.size() - 1; i > 0; i--) {
			final int swapIndex = random.nextInt(i - 1 + 1) + 1;
			if (list.get(swapIndex) != null && list.get(i) != null) {
				swapPiece(list.get(i), list.get(swapIndex));
			}
		}

		final int swapIndex = random.nextInt((list.size() - 1) - 1 + 1) + 1;
		swapPiece(freePiece, list.get(swapIndex));

		return list;
	}

	/**
	 * Resets the current game
	 * Shuffles the pieces and puts the start time back to current phones uptime
	 */
	public void resetGame() {
		startTime = SystemClock.uptimeMillis();
		shuffPieces(pieces);
				
	}
	
	@Override
	public void onKeyPressed(KeyEvent event) {

	}

	/**
	 * Method to handle touch events being sent to this screen
	 */
	public void onTouch(MotionEvent event) {

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			if (!pieceSelected) {
				piece = processTouchDownEvent(activePieces, event);
				//if(piece != null)
					//Log.d("PicturePuzzle", piece.toString());
				pieceSelected = true;
			}
			break;
		case MotionEvent.ACTION_UP:
			piece = null;
			pieceSelected = false;
			break;
		case MotionEvent.ACTION_MOVE:
			if (piece != null) {
				final float x = event.getRawX();
				final float y = render.getRawScreenHeight() - event.getY();

				if (Rectangle.pointIntersects(x, y, freePiece.getRect())) {
					//movePieceTo(piece, freePiece);
					swapPiece(piece, freePiece);
					//checkIfComplete(pieces);
					this.pieces.isEmpty();
					piece = null;
					pieceSelected = false;
				}
				
			}			
			break;
		case MotionEvent.ACTION_CANCEL:
			piece = null;
			pieceSelected = false;
			break;

		}
	}

	@Override
	public void screenChanged(GL10 gl) {		
		
		final Texture picture = Render.loadBitmap(gl, (Context)gameActivity, pictureID);
		this.picture = picture;
		this.notifyThis();
	}

	@Override
	public void screenCreated(GL10 gl) {
		
	}

	@Override
	public void frameStarted(GL10 gl) {
		render.bindTexture(gl, picture.textureID);
	}

	@Override
	public void frameEnded(GL10 gl) {
		
	}

	public long getStartTime() {
		return startTime ;
	}
	
	public int getRowSize() {
		return rowSize;
	}

	public int getColumnSize() {
		return columnSize;
	}

	/**
	 * Creates a string representing the current state of the game's grid. Formated like below
	 * E.G 0 9:1 1 4:3 2 5:4 Meaning that the first position has the last piece in it which belongs in the first position
	 * @param pieces The list of the current pieces of the game
	 * @param freePiece The piece representing the null piece in the list
	 * @param rowSize The size of how pieces are in each row of the grid
	 * @return A string representing the games grid
	 */
	public String getGrid(List<Piece> pieces, Piece freePiece, int rowSize) {
		StringBuilder grid = new StringBuilder();
		
		for(int i = 0; i < pieces.size(); i++){
			Piece currentPiece = pieces.get(i);
			if(currentPiece != null){
				grid.append(getPositionInArray(currentPiece.initI, currentPiece.initJ, rowSize)).append(" ");
			}else{
				//Log.d("PicturePuzzle", freePiece.toString());
				grid.append("n").append(getPositionInArray(freePiece.initI, freePiece.initJ, rowSize)).append(" ");
			}
		}
		grid.deleteCharAt(grid.length()-1); //Burns the space put at the end
		return grid.toString();
	}
	

	public List<Piece> getPieces() {
		return pieces;
	}

	public Piece getFreePiece() {
		// TODO Auto-generated method stub
		return freePiece;
	}
	
	public String toString() {
		return "PicturePuzzle";
	}
	/**
	 * A sync and catch way of waiting the thread. Just cleaner code
	 */
	public void waitThis(){
		try{
			synchronized (this) {
				this.wait();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * A sync and catch way of notifying the thread. Just cleaner code
	 */
	public void notifyThis(){
		try{
			synchronized (this) {
				this.notify();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
