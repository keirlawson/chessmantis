package uk.ac.gla.chessmantis; /**
 * 
 */

import uk.ac.gla.chessmantis.piece.*;

/**
 * @author icelook
 *
 */
public class PieceClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Piece p = new Pawn(BoardArray.WHITE);
		for (Integer i : p.getDeltas())
			System.out.println("uk.ac.gla.chessmantis.piece.Pawn delta: " + i);
		p = new Rook(true);
		for (Integer i : p.getDeltas())
			System.out.println("uk.ac.gla.chessmantis.piece.Rook delta: " + i);
		p = new Bishop(true);
		for (Integer i : p.getDeltas())
			System.out.println("Bishop delta: " + i);
		p = new Knight(true);
		for (Integer i : p.getDeltas())
			System.out.println("uk.ac.gla.chessmantis.piece.Knight delta: " + i);
		p = new King(true);
		for (Integer i : p.getDeltas())
			System.out.println("uk.ac.gla.chessmantis.piece.King delta: " + i);
		p = new Queen(true);
		for (Integer i : p.getDeltas())
			System.out.println("uk.ac.gla.chessmantis.piece.Queen delta: " + i);
		

	}

}
