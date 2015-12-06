/**
 * Bishop: bishop chess piece subclass
 * @author Dominik Gotojuch
 * @version 1.2 (6/12/07)
 */

import java.util.*;

public class Bishop extends GenericPiece
{

	/**
	 * Constructor
	 * initializes the basic fields of the new Bishop
	 * and sets its colour
	 * @param boolean indication of piece's colour
	 */
	public Bishop (boolean colour)
	{
		this.value = 325;
		this.moves = 0;
		this.colour = colour;
		
		// DG - Setting Zobrist hash identifier
		this.hashID = 4; 
	}
	
	/**
	 * Constructor
	 * @author TT
	 * instantiates Bishop, where colour is not available
	 */
	public Bishop ()
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
		if (((delta % 15) == 0) || ((delta % 17) == 0))	// DG Right/Left diagonal movement
			return true;
		return false;					// DG Illegal move
	}
	
	/**
	 * accessor
	 * @author TT
	 * @return List of absolute values of all possible deltas
	 */
	public List<Integer> getDeltas()
	{
		List<Integer> temp = new LinkedList<Integer>();
		for (int i = 1; i < 8; i++)
		{
			temp.add(15*i);
			temp.add(17*i);
		}
		return temp;
	}
}
