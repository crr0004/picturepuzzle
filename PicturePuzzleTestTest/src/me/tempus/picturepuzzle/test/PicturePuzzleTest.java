package me.tempus.picturepuzzle.test;

import me.tempus.picturepuzzle.PicturePuzzle;
import junit.framework.TestCase;

public class PicturePuzzleTest extends TestCase {

	public void testSetUpGrid() {
		fail("Not yet implemented");
	}

	public void testMovePieceTo() {
		fail("Not yet implemented");
	}

	public void testSwapPiece() {
		fail("Not yet implemented");
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
