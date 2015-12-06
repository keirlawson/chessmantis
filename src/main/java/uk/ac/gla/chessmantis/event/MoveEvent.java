package uk.ac.gla.chessmantis.event;

import uk.ac.gla.chessmantis.Moveable;
import uk.ac.gla.chessmantis.event.ChessEvent;

public class MoveEvent implements ChessEvent
{
	private Moveable move;
	
	public MoveEvent(Moveable move)
	{
		this.move = move;
	}
	
	public Moveable getMove()
	{
		return this.move;
	}
}
