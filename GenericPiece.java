/**
 * GenericPiece: abstract, parent class for all chess pieces
 * on board, implementing general methods of Piece interface
 * @author Dominik Gotojuch
 * @version 1.2 (6/12/07)
 */

import java.util.*;

public abstract class GenericPiece implements Piece 
{
	protected int hashID;
	protected short value;
	protected boolean colour;
	protected int moves;

	public abstract boolean isLegalMove(int delta);
	
	public abstract List<Integer> getDeltas();

	/**
	 * DG
	 * Accessor
	 * returns the int id of the piece for Zobrist hashing
	 */
	public int getID ()
	{
		return this.hashID;
	}
	
	/**
	 * Modifier
	 * reduces the number of moves done
	 */
	public void unsetMoved ()
	{
		// System.err.println("unsetMoved: " + this.moves);
		this.moves--;
		if (this.moves < 0)		// DG Safety check if goes below 0
			this.moves = 0;
		// System.err.println("unsetMoved: " + this.moves);
	}
	
	/**
	 * Modifier
	 * increments the number of moves done
	 */
	public void setMoved()
	{
		this.moves++;
	}

	/**
	 * Modifier
	 * @author TT
	 * @param colour sets colour of the piece to white iff true, black otherwise
	 */
	public void setColour(boolean colour)
	{
		this.colour = colour;
	}
	
	/**
	 * Accessor
	 * returns true if the piece has already been moved
	 * false otherwise
	 */
	public boolean hasMoved ()
	{
		return this.moves>0;
	}

	/**
	 * Accessor
	 * returns the chess piece class of the given piece
	 */
	public Class getPieceType()
	{
		return this.getClass();
	}

	/**
	 * Accessor
	 * returns ture if the piece is white and false
	 * if the piece is black
	 */
	public boolean isWhite()
	{
		return this.colour;
	}

	/**
	 * Accessor
	 * returns the value of the piece
	 */
	public int getValue()
	{
		return this.value;
	}

}
