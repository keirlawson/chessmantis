package uk.ac.gla.chessmantis.piece; /**
 * uk.ac.gla.chessmantis.piece.Rook: rook chess piece subclass
 * @author Dominik Gotojuch
 * @version 1.2 (6/12/07)
 */

import java.util.*;

public class Rook extends GenericPiece
{
	
	/**
	 * Constructor
	 * initializes the basic fields of the new uk.ac.gla.chessmantis.piece.Rook
	 * and sets its colour
	 * @param boolean indication of piece's colour
	 */
	public Rook (boolean colour)
	{
		this.value = 500;
		this.moves = 0;
		this.colour = colour;
		
		// DG - Setting Zobrist hash identifier
		this.hashID = 3; 
	}

	/**
	 * Constructor
	 * @author TT
	 * instantiates uk.ac.gla.chessmantis.piece.Rook, where colour is not available
	 */
	public Rook ()
	{
		this.value = 300;
		this.moves = 0;
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
		if((delta & 15) == 0)		// DG Vertical movement
			return true;

		if (Math.abs(delta) < 8)	// DG Horizontal movement
			return true;

		return false;			// DG Illegal move
	}
	
	/**
	 * accessor
	 * @author TT
	 * @return a list of legal absolute deltas
	 */
	public List<Integer> getDeltas()
	{
		List<Integer> temp = new LinkedList<Integer>();
		for (int i = 1; i < 8; i++)
		{
			temp.add(i);
			temp.add(i * 16);
		}
		return temp;
	}
}
