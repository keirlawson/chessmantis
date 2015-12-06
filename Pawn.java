/**
 * Pawn: pawn chess piece subclass
 * @author Dominik Gotojuch
 * @version 1.2 (6/12/07)
 */

import java.math.*;
import java.util.*;

public class Pawn extends GenericPiece
{
	/**
	 * Constructor
	 * initializes the basic fields of the new Pawn
	 * and sets its colour
	 * @param boolean indication of piece's colour
	 */
	public Pawn (boolean colour)
	{
		this.value = 100;
		this.moves = 0;
		this.colour = colour;
		
		// DG - Setting Zobrist hash identifier
		this.hashID = 6; 
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
		int adelta = Math.abs(delta);
		if (!this.hasMoved())
		{
			if ((delta == 32 && this.colour) || (delta == -32 && !this.colour))		// DG Not moved piece allowed two field jump
				return true;
		}
		if (adelta == 16)			// DG Moved piece allowed single field jump
		{	
			if ((delta == 16 && this.colour) || (delta == -16 && !this.colour)) // DG Direction in which pawn is going checked
				return true;
			return false;
		}
						
		if (this.colour && (delta == 15 || delta == 17))			// DG Capturing move for white pawns
			return true;
					
		if (!this.colour && (delta == -15 || delta == -17))		// DG Capturing move for black pawns
			return true;
			
		return false;		// DG Illegal move
	}
	
	/**
	 * accessor
	 * @author TT
	 * @return a list of absolute deltas for the given piece
	 */
	public List<Integer> getDeltas()
	{
		List<Integer> temp = new LinkedList<Integer>();
		temp.add(16);
		if (!hasMoved())
			temp.add(32);
		temp.add(15);
		temp.add(17);
		return temp;
	}
}
