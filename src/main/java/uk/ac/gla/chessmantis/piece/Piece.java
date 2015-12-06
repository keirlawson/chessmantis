package uk.ac.gla.chessmantis.piece; /**
 * uk.ac.gla.chessmantis.piece.Piece: interface for all kinds of chess pieces
 * present on the chess board
 * @author Dominik Gotojuch & Tamerlan Tajadinov
 * @version 1.4 (04/02/08)
 */

import java.util.*;

public interface Piece 
{
	boolean isLegalMove(int delta);
	boolean hasMoved ();
	void setMoved ();
	/**
	 * 
	 * @return True: uk.ac.gla.chessmantis.piece.Piece is white, False: uk.ac.gla.chessmantis.piece.Piece is black
	 */
	boolean isWhite();
	int getValue();
	void unsetMoved();
	List<Integer> getDeltas();
	void setColour(boolean colour);
	int getID();

}


