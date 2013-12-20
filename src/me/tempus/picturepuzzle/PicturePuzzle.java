package me.tempus.picturepuzzle;

import java.util.ArrayList;
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
			loadGrid(grid, rowSize, columnSize);
		}else{
			setUpGrid(rowSize, columnSize, picture.width, picture.height);
		}
		long currentTime = 0;
		long delta = 0;
		while(!done){
			currentTime = SystemClock.currentThreadTimeMillis();
			
			activePieces[0] = getActivePiece(freePiece.i, freePiece.j + 1);
			activePieces[1] = getActivePiece(freePiece.i, freePiece.j - 1);
			activePieces[2] = getActivePiece(freePiece.i + 1, freePiece.j);
			activePieces[3] = getActivePiece(freePiece.i - 1, freePiece.j);
			render.addDrawables(pieces);
			InputManager.processInput(this);
			if (render != null)				
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
		freePiece.i = 0;
		freePiece.j = 0;
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
	public void setUpGrid(int rowPieces, int columnPieces,
			int textureWidth, int textureHeight) {
		Log.d("PicturePuzzle", "RowPices, ColumnPieces" + rowPieces + ", " + columnPieces);
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

		shuffPieces(pieces);
	}

	/**
	 * Loads the game grid given a setup of coordinates
	 * @param grid The formatted string so that each position maps a grid position. Free set of cords is the free piece
	 * E.G 0 9 1 4 2 5 Meaning that the first position has the last piece in it
	 */
	private void loadGrid(String grid, int rowSize, int columnSize) {
		
		pieces = new ArrayList<Piece>(rowSize * columnSize);
		Piece.setPieceWidth(render.getScreenWidth() / rowSize);
		Piece.setPieceHeight(render.getScreenHeight() / columnSize);
		Piece.setxPadding(2);
		Piece.setyPadding(2);
		
		final int tPieceWidth = picture.width / (rowSize);
		final int tPieceHeight = picture.height / (columnSize);
		
		String[] positions = grid.split(" ");
		for(int k = 1; k < positions.length/2; k++){
			int position = Integer.parseInt(positions[(k*2)]);
			int[] pieceCords = getCoordinates(Integer.parseInt(positions[(k*2)+1]), rowSize);
			int i = pieceCords[0];
			int j = pieceCords[1];
			Piece piece = new Piece(i, j, new int[] {
					i * tPieceWidth, picture.height - (j * tPieceHeight),
					tPieceWidth, -tPieceHeight });
			pieces.add(position, piece);			
		}
		pieces.set(Integer.parseInt(positions[1]), null);
		freePiece.getRect().height = Piece.getPieceHeight();
		freePiece.getRect().width = Piece.getPieceWidth();
		
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
	public void movePieceTo(Piece piece, int i, int j) {
		final int freePos = getPositionInArray(freePiece.i, freePiece.j,
				rowSize);
		final int posTry = getPositionInArray(i, j, rowSize);
		if (posTry == freePos) {
			swapPiece(piece, freePiece);
			//pieces.set(getPositionInArray(piece.i, piece.j, rowSize), null);
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
		final Piece p = pieces.get(pos1);
		pieces.set(pos1, pieces.get(pos2));
		pieces.set(pos2, p);
		final int i1 = p1.i;
		final int j1 = p1.j;
		p1.i = p2.i;
		p1.j = p2.j;
		p2.i = i1;
		p2.j = j1;

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
		int[] coords = new int[2];
		if(position < rowSize){
			coords[0] = 0;
			coords[1] = position;
		}else{
			coords[1] = position - rowSize;
			coords[0] = (position - coords[1]) / rowSize;
		}
		return coords;
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
				if(piece != null)
					//Log.d("PicturePuzzle", piece.toString());
				pieceSelected = true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (piece != null) {
				final float x = event.getRawX();
				final float y = render.getRawScreenHeight() - event.getY();

				if (Rectangle.pointIntersects(x, y, freePiece.getRect())) {
					movePieceTo(piece, freePiece.i, freePiece.j);
				}
			}
			pieceSelected = false;
			piece = null;
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_CANCEL:
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void frameStarted(GL10 gl) {
		// TODO Auto-generated method stub
		render.bindTexture(gl, picture.textureID);
	}

	@Override
	public void frameEnded(GL10 gl) {
		// TODO Auto-generated method stub
		
	}

	public long getStartTime() {
		// TODO Auto-generated method stub
		return startTime ;
	}
	
	public int getRowSize() {
		// TODO Auto-generated method stub
		return rowSize;
	}

	public int getColumnSize() {
		// TODO Auto-generated method stub
		return columnSize;
	}

	public String getGrid() {
		// TODO Auto-generated method stub
		StringBuilder grid = new StringBuilder();
		grid.append("-1 ").append(getPositionInArray(freePiece.i, freePiece.j, rowSize)).append(" ");
		for(int i = 0; i < pieces.size(); i++){
			Piece currentPiece = pieces.get(i);
			grid.append(i).append(" ").append(getPositionInArray(currentPiece.i, currentPiece.j, rowSize)).append(" ");
		}
		return grid.toString();
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
