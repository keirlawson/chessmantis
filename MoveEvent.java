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
