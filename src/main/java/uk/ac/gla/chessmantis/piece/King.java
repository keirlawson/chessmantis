package uk.ac.gla.chessmantis.piece; /**
 * uk.ac.gla.chessmantis.piece.King: king chess piece subclass
 * @author Dominik Gotojuch
 * @version 1.2 (6/12/07)
 */

import java.util.*;

public class King extends GenericPiece 
{
	/**
	 * Constructor
	 * initializes the basic fields of the new king
	 * and sets its colour
	 * @param boolean indication of piece's colour
	 */
	public King (boolean colour)
	{
		this.value = 1000;
		this.moves = 0;
		this.colour = colour;
		
		// DG - Setting Zobrist hash identifier
		this.hashID = 1; 
	}
	

	/**
	 * Accessor
	 * checks if the shift from one position to another
	 * is a legal move for the chess piece
	 * @param path travelled by the piece on the chessboard
	 * @return true if move is valid, false if otherwise
	 */
	public boolean isLegalMove(int delta) 
	{
		switch (Math.abs(delta))
		{
			case 1:					// DG Horizontal movement
				return true;
			case 15:				// DG Left diagonal movement
				return true;
			case 16:				// DG Vertical movement
				return true;
			case 17:				// DG Right diagonal movement
				return true;
			case 2:
				if (!hasMoved())// AM Needs to ensure delta is only available when castling is a possibility
					return true;
			case -2:
				if (!hasMoved())// AM Needs to ensure delta is only available when castling is a possibility
					return true;
			default:
				break;
		}
		return false;				// DG Illegal move
	}
	
	/**
	 * accessor
	 * @author TT
	 * @return a list of legal deltas
	 */
	public List<Integer> getDeltas()
	{
		List<Integer> temp = new LinkedList<Integer>();
		temp.add(1);
		temp.add(2);
		temp.add(15);
		temp.add(16);
		temp.add(17);
		return temp;
	}
}
