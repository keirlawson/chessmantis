package uk.ac.gla.chessmantis.piece; /**
 * uk.ac.gla.chessmantis.piece.Knight: knight chess piece subclass
 * @author Dominik Gotojuch
 * @version 1.2 (6/12/07)
 */

import java.util.*;

public class Knight extends GenericPiece 
{
	/**
	 * Constructor
	 * initializes the basic fields of the new knight
	 * and sets its colour
	 * @param boolean indication of piece's colour
	 */
	public Knight (boolean colour)
	{
		this.value = 300;
		this.moves = 0;
		this.colour = colour;
		
		// DG - Setting Zobrist hash identifier
		this.hashID = 5; 
	}
	
	/**
	 * Constructor
	 * @author TT
	 * instantiates uk.ac.gla.chessmantis.piece.Knight, where colour is not available
	 */
	public Knight ()
	{
		this.value = 300;
		this.moves = 0;
	}
	
	/**
	 * Accessor
	 * @author TT
	 * @return a list of legal absolute deltas for the piece
	 */
	public List<Integer> getDeltas()
	{
		List<Integer> temp = new LinkedList<Integer>();
		temp.add(14);
		temp.add(18);
		temp.add(31);
		temp.add(33);
		return temp;
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
			case 14:			// DG Left-up and right-down moves 
				return true;
			case 18:			// DG Right-up and left-down moves
				return true;
			case 31:			// DG Up-left and down-right moves
				return true;
			case 33:			// DG Up-right and down-left moves
				return true;
			default:
				break;
		}
		return false;				// DG Illegal move
	}
}
