package me.tempus.picturepuzzle.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import me.tempus.picturepuzzle.PicturePuzzle;
import me.tempus.picturepuzzle.Piece;

public class PicturePuzzleTest extends TestCase {

	public void testSetUpGrid() {
		fail("Not yet implemented");
	}

	public void testMovePieceTo() {
		fail("Not yet implemented");
	}

	public void testSwapPiece() {
		List<Piece> pieces = new ArrayList<Piece>();
		int[] p1c = new int[]{0,1};
		int[] p2c = new int[]{0,2};
		int rowSize = 3;
		int pos1 = PicturePuzzle.getPositionInArray(p1c[0], p1c[1], rowSize);
		int pos2 = PicturePuzzle.getPositionInArray(p2c[0], p2c[1], rowSize);
		Piece piece1 = new Piece(p1c[0], p1c[1], null);
		Piece piece2 = new Piece(p2c[0], p2c[1], null);
		pieces.add(null);
		pieces.add(piece1);
		pieces.add(piece2);
		
		PicturePuzzle test = new PicturePuzzle(null, null, rowSize, rowSize, 0);
		//test.swapPiece(piece1, piece2, pieces);
		
		//assertTrue(String.format("Piece 1 pos was %d not $d", PicturePuzzle.getPositionInArray(newPiece1.initI, j, n)))
	}

	public void testGetPositionInArray() {
		fail("Not yet implemented");
	}

	public void testGetCoordinates() {
		int rowSize = 3;
		int x = 0;
		int y = 2;
		int pos = 2;
		int[] cords = PicturePuzzle.getCoordinates(pos, rowSize);
		assertTrue(String.format("Pos of %d, rowSize %d, x was %d not %d", pos, rowSize, cords[0], x)
				, cords[0] == x);
		assertTrue(String.format("Pos of %d, rowSize %d, y was %d not %d", pos, rowSize, cords[1], y),
				cords[1] == y);
	}

	public void testGetGrid() {
		fail("Not yet implemented");
	}

}
