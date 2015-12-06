/**
 * Queen: queen chess piece subclass
 * @author Dominik Gotojuch
 * @version 1.2 (6/12/07)
 */

import java.math.*;
import java.util.*;

public class Queen extends GenericPiece 
{
	
	/**
	 * Constructor
	 * initializes the basic fields of the new Queen
	 * and sets its colour
	 * @param boolean indication of piece's colour
	 */
	public Queen (boolean colour)
	{
		this.value = 900;
		this.moves = 0;
		this.colour = colour;
		
		// DG - Setting Zobrist hash identifier
		this.hashID = 2; 
	}
	
	/**
	 * Constructor
	 * @author TT
	 * instantiates Queen, where colour is not available
	 */
	public Queen ()
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
		if (Math.abs(delta) < 8)	// DG Horizontal movement
			return true;
		if ((delta & 15) == 0)		// DG Vertical movement
			return true;
		if ((delta % 15) == 0)		// DG Left diagonal movement
			return true;
		if ((delta % 17) == 0)		// DG Right diagonal movement 
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
		for(int i = 1; i<8; i++)
		{
			temp.add(i);
			temp.add(16*i);
			temp.add(15*i);
			temp.add(17*i);
		}
		return temp;
	}
}
