package uk.ac.gla.chessmantis.event;

import uk.ac.gla.chessmantis.Moveable;
import uk.ac.gla.chessmantis.event.ChessEvent;

public class IllegalMoveEvent implements ChessEvent
{
	private Moveable move;
	
	/** Constructs a new uk.ac.gla.chessmantis.event.IllegalMoveEvent with the given Illegal uk.ac.gla.chessmantis.Moveable move. */
	public IllegalMoveEvent(Moveable move)
	{
		this.move = move;
	}
	
	/** Returns the Illegal uk.ac.gla.chessmantis.Move associates with this Event. */
	public Moveable getIllegalMove()
	{
		return this.move;
	}
}
