public class IllegalMoveEvent implements ChessEvent
{
	private Moveable move;
	
	/** Constructs a new IllegalMoveEvent with the given Illegal Moveable move. */
	public IllegalMoveEvent(Moveable move)
	{
		this.move = move;
	}
	
	/** Returns the Illegal Move associates with this Event. */
	public Moveable getIllegalMove()
	{
		return this.move;
	}
}
